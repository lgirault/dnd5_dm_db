package dnd5_dm_db

import dnd5_dm_db.lang.Lang

import scala.xml.Node



case class Source(src : String, page : Option[Int]){
  override def toString =
    src + (page map (p => s" ( p.$p )") getOrElse "")
}
object Source {
  def fromXml(node : Node): Source = {
    Source(node.text, singleOptionAttribute(node, "page") map (_.toInt))
  }
  def toHtml(ssource : Option[Source]) ( implicit lang : Lang) : String = {
    s"""<div class="source">${lang.source} : ${ssource.getOrElse(lang.unknown)}</div>"""
  }
}

case class Monster
( name : String,
  size : Size,
  typ : MonsterType,
  alignment: Alignment,
  armorClass : Int,
  armorDesc : Option[String],
  hitPoint : (Int, Die),
  speed : Seq[Speed],
  abilities: Abilities,
  savingThrows : Seq[(Ability, Int)],
  skills : Seq[(Skill, Int)],
  damageImmunities : Seq[DamageType],
  conditionImmunities : Seq[Condition],
  resistances : Seq[DamageType],
  sensList : Seq[Sens],
  languages : Seq[Language],
  challengeRanking : ChallengeRanking,
  xp : Int,
  traits : Seq[Trait],
  spellCasting: Option[SpellCasting],
  actions : Seq[Action],
  reactions : Seq[Action],
  description : Option[String],
  source : Option[Source])

object Monster{

  def fromXmlToHml
  ( spells : Map[String, Spell],
    traits : Map[String, Trait]) : FromXmlToHtml[Monster] = new FromXmlToHtml[Monster] {
    def fromXml(n : Node)(implicit lang : Lang) : Monster =
      Monster.fromXml(n, traits)(lang)
    def toHtml(m : Monster)(implicit lang : Lang)  : String =
      Monster.toHtml(m, spells)(lang)

  }

  def fromXml(monster : Node,
              traitsMap : Map[String, Trait])(implicit lang : Lang) : Monster = {
    val identity = (monster \ "identity").toNode
    val abilities = Abilities fromXml (monster \ "abilities").toNode
    val skillMisc = (monster \ "skillMisc").toNode

    val damageImmunities = skillMisc \ "damageImmunities" \ "immune"  map {
      n => DamageType.fromString(n.text)
    }
    val conditionImmunities = skillMisc \ "conditionImmunities" \ "immune"  map {
        n => Condition.fromString(n.text)
      }
    val damageResistances =  skillMisc \ "resistances" \ "resist" map {
      n => DamageType.fromString(n.text)
    }

    val traits = {
      val ts =  monster \ "traits" \ "trait" map Trait.fromXml

      val sShared = (monster \ "traits" \ "shared").toNodeOption

      val sts = sShared map {
        shared =>
          val name = shared \ lang.id
          shared \ "id" map (n => traitsMap(n.text).withMonsterName(name) )
      } getOrElse Seq()

      ts ++: sts
    }

    val spellCasting = (monster \ "spellcasting").toNodeOption
    val actions = monster \ "actions" \ "action" map Action.fromXml
    val reactions = monster \ "reactions" \ "action" map Action.fromXml


    val ac = (identity \ "ac").toNode

    Monster(
      identity \ "name" \ lang.id,
      Size.fromString(identity \ "size"),
      MonsterType.fromNode(identity),
      Alignment fromXml (identity\"alignment").toNode,
      ac \ "value",
      ac \ "desc" \ lang.id,
      Die fromXml (identity \ "hp").toNode,
      Speed fromXml (identity \ "speeds").toNode,
      abilities,
      (skillMisc \ "savingThrows" \ "save") map Ability.savingThrowFromXml,
      (skillMisc \ "skills" \ "skill") map Skill.fromXml,
      damageImmunities,
      conditionImmunities,
      damageResistances,
      Sens fromXml (skillMisc \ "senses").toNode,
      (skillMisc\"languages").toNodeOption map Language.fromXml getOrElse Seq(), // language
      ChallengeRanking.fromString(skillMisc \ "cr"),
      skillMisc \ "xp",
      traits,
      spellCasting map SpellCasting.fromXml,
      actions,
      reactions,
      monster \ "description" \ lang.id,
      (monster \ "source" ).toNodeOption map Source.fromXml )
  }


  def titledSeq[A](title:String, s : Seq[A])(f : A => String) : String = {
    if(s.isEmpty) ""
    else s map f mkString(s"<div><b>$title :</b> ", ", ","</div>")
  }
  def toHtml
  ( m : Monster,
    spells : Map[String, Spell])
  ( implicit lang : Lang) : String = {
    val sepMonster =
      """<img src="images/sep-monster.gif" alt="" class="sep-monster" width="95%" height="4" />"""

    val (avgHp, hpDie) = m.hitPoint

    val saveStr = titledSeq(lang.savingThrows, m.savingThrows){
      case (ab, bonus) =>
        lang.ability_short(ab) + ":" + Die.bonus_str(bonus)
    }
    val skillStr = titledSeq(lang.skills, m.skills) {
      case (s, v) => lang.skill(s) + " " + Die.bonus_str(v)
    }

    val dmgImmunStr = titledSeq(lang.damageImmunities, m.damageImmunities)(lang.damageType)
    val condImmunStr = titledSeq(lang.conditionImmunities, m.conditionImmunities)(lang.conditions)
    val resistancesStr = titledSeq(lang.resistance, m.resistances)(lang.damageType)
    val sensStr = titledSeq(lang.senses, m.sensList)(lang.sens)
    val langStr =
      if(m.languages.nonEmpty)
           titledSeq(lang.languages, m.languages)(lang.language)
      else s"<div><b>${lang.languages}</b>: --</div>"

    val armorDesc = m.armorDesc map {s => s"($s)"} getOrElse ""

    def subSection[A](title : String, elts : Seq[A])(formatter : A => String) : String =
      if(elts.nonEmpty)
        s"""<div class="rub">$title</div>
           |${elts map formatter mkString ""}
         """.stripMargin
    else ""

    val actionsStr = subSection("ACTIONS", m.actions)(Action.toHtml)
    val reActionsStr = subSection("REACTIONS", m.reactions)(Action.toHtml)

    s"""<div class="bloc">
      |   <div class="name">${m.name}</div>
      |   <div class="type sansSerif">
      |        <em>${lang.monsterTypeAndSize(m.size, m.typ)}, ${lang.alignment(m.alignment)}</em>
      |     </div>
      |    $sepMonster
      |    <div class="red">
      |        <b>${lang.armorClass} :</b> ${m.armorClass} $armorDesc<br/>
      |        <b>${lang.hp} :</b> $avgHp ($hpDie) <br/>
      |        <b>${lang.speed} :</b> ${m.speed map lang.speedLength mkString ","} <br/>
      |    $sepMonster
      |     ${Abilities.toHtml(m.abilities)}
      |    $sepMonster
      |    $saveStr
      |    $skillStr
      |    $dmgImmunStr
      |    $condImmunStr
      |    $resistancesStr
      |    $sensStr
      |    $langStr
      |    <div>${m.challengeRanking} (${m.xp} ${lang.xp})</div>
      |    </div><!-- /red -->
      |    $sepMonster
      |    ${m.traits map Trait.toHtml mkString ""}
      |    ${m.spellCasting.map{SpellCasting.toHtml(spells, m.name, _)}.getOrElse("")}
      |    $actionsStr
      |    $reActionsStr
      |    ${Source.toHtml(m.source)}
      |</div> <!-- /block -->""".stripMargin
  }

}