package dnd5_dm_db
package model

import dnd5_dm_db.lang.Lang

case class Monster
( name : Local,
  size : Size,
  typ : MonsterType,
  alignment: Alignment,
  armorClass : Int,
  armorDesc : Option[Local],
  hitPoint : Die,
  speed : Seq[Speed],
  abilities: Abilities,
  savingThrows : Seq[(Ability, Int)],
  skills : Seq[(Skill, Int)],
  vulnerabilities : Seq[DamageType],
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
  description : Option[Local],
  source : Option[Source])

object MonsterHtmlGen {
  def toHtml
  ( spells : Map[String, Spell]) : ToHtml[Monster] = new ToHtml[Monster] {
    def toHtml(m : Monster)(implicit lang : Lang)  : String =
      monsterToHtml(m, spells)(lang)
  }

  def titledSeq[A](title:String, s : Seq[A])(f : A => String) : String = {
    if(s.isEmpty) ""
    else s map f mkString(s"<div><b>$title :</b> ", ", ","</div>")
  }

  def monsterToHtml
  ( m : Monster,
    spells : Map[String, Spell])
  ( implicit lang : Lang) : String = {
    val sepMonster =
      """<img src="images/sep-monster.gif" alt="" class="sep-monster" width="95%" height="4" />"""

    val (avgHp, hpDie) = (m.hitPoint.average, m.hitPoint)

    val saveStr = titledSeq(lang.savingThrows, m.savingThrows){
      case (ab, bonus) =>
        lang.ability_short(ab) + ":" + Die.bonus_str(bonus)
    }
    val skillStr = titledSeq(lang.skills, m.skills) {
      case (s, v) => lang.skill(s) + " " + Die.bonus_str(v)
    }


    val dmgVulnStr = titledSeq(lang.damageVulnerabilites, m.vulnerabilities)(lang.damageType)
    val dmgImmunStr = titledSeq(lang.damageImmunities, m.damageImmunities)(lang.damageType)
    val condImmunStr = titledSeq(lang.conditionImmunities, m.conditionImmunities)(lang.conditions)
    val resistancesStr = titledSeq(lang.resistance, m.resistances)(lang.damageType)
    val sensStr = titledSeq(lang.senses, m.sensList)(lang.sens)
    val langStr =
      if(m.languages.nonEmpty)
        titledSeq(lang.languages, m.languages)(lang.language)
      else s"<div><b>${lang.languages}</b>: --</div>"

    val armorDesc = m.armorDesc map {s => s"(${s.value})"} getOrElse ""

    def subSection[A](title : String, elts : Seq[A])(formatter : A => String) : String =
      if(elts.nonEmpty)
        s"""<div class="rub">$title</div>
           |${elts map formatter mkString ""}
         """.stripMargin
      else ""

    val actionsStr = subSection("ACTIONS", m.actions)(Action.toHtml)
    val reActionsStr = subSection("REACTIONS", m.reactions)(Action.toHtml)

    s"""<div class="bloc">
       |   <div class="name">${m.name.value}</div>
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
       |    $dmgVulnStr
       |    $dmgImmunStr
       |    $condImmunStr
       |    $resistancesStr
       |    $sensStr
       |    $langStr
       |    <div><b>${lang.challengeRanking}</b> ${m.challengeRanking} (${m.xp} ${lang.xp})</div>
       |    </div><!-- /red -->
       |    $sepMonster
       |    ${m.traits map Trait.toHtml mkString ""}
       |    ${m.spellCasting.map{SpellCasting.toHtml(spells, m.name.value, _)}.getOrElse("")}
       |    $actionsStr
       |    $reActionsStr
       |    ${Source.toHtml(m.source)}
       |</div> <!-- /block -->""".stripMargin
  }

}

