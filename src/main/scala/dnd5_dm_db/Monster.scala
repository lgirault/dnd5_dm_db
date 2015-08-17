package dnd5_dm_db

import scala.xml.Node





case class Trait(name : String, description : String)
object Trait {
  def fromXml(node : Node)(implicit lang : Lang) : Trait =
    Trait (node \ lang.id \ "name",
      node \ lang.id \ "description" )
  def toHtml(t : Trait) =
    s"<div><b>${t.name}:</b> ${t.description}</div>"
}

sealed abstract class MonsterType
case class AnyRace(typ : String) extends MonsterType
case class OneRace(typ : String, race : String) extends MonsterType
case class NoRace(typ : String) extends MonsterType

object MonsterType {
  def fromNode(identity : Node) : MonsterType =
    fromString(identity \ "type",
      (identity \ "race").toNodeOption map (_.text))

  def fromString(t : String, sr : Option[String])  : MonsterType =
    sr match {
      case None => NoRace(t)
      case Some("any") => AnyRace(t)
      case Some(r) => OneRace(t, r)
    }
}


case class Monster
( category : String,
  name : String,
  size : Size,
  typ : MonsterType,
  alignment: Alignment,
  armorClass : Int,
  hitPoint : (Int, Die),
  speed : DnDLength,
  abilities: Abilities,
  skills : Seq[(Skill, Int)],
  sensList : Seq[Sens],
  languages : Seq[Language],
  challengeRanking : Float,
  xp : Int,
  traits : Seq[Trait],
  spellCasting: Option[SpellCasting],
  actions : Seq[Action],
  source : Option[String])

object Monster{

  def fromXmlToHml(spells : Map[String, Spell]) : FromXmlToHtml[Monster] = new FromXmlToHtml[Monster] {
    def fromXml(n : Node)(implicit lang : Lang) : Monster =
      Monster.fromXml(n)(lang)
    def toHtml(m : Monster)(implicit lang : Lang)  : String =
      Monster.toHtml(m, spells)(lang)

  }

  def fromXml(monster : Node)(implicit lang : Lang) : Monster = {
    val identity = (monster \ "identity").toNode
    val abilities = Abilities fromXml (monster \ "abilities").toNode
    val skillMisc = monster \ "skillMisc"
    val traits = monster \ "traitList" \ "trait"
    val spellCasting = (monster \ "spellcasting").toNodeOption
    val actionList = monster \ "actionList"

    Monster(identity \ "category",
      identity \ "name" \ lang.id,
      Size.fromString(identity \ "size"),
      MonsterType.fromNode(identity),
      Alignment.fromString(identity\"alignment"),
      identity \ "ac",
      (identity \ "hp", Die.fromString(identity \ "hd")),
      DnDLength fromXml (identity \ "speed").toNode,
      abilities,
      (skillMisc \ "skills" \ "skill").theSeq map Skill.fromXml,
      Sens fromXml (skillMisc \ "senses").toNode,
      Language fromXml (skillMisc\"languages").toNode, // language
      (skillMisc \ "cr").text.toFloat,
      skillMisc \ "xp",
      traits map Trait.fromXml,
      spellCasting map SpellCasting.fromXml,
      actionList \ "action" map Action.fromXml,
      monster \ "source" )
  }

  def toHtml
  ( m : Monster,
    spells : Map[String, Spell])
  ( implicit lang : Lang) : String = {
    val sepMonster =
      """<img src="images/sep-monster.gif" alt="" class="sep-monster" width="95%" height="4" />"""

    val (avgHp, hpDie) = m.hitPoint

    s"""<div class="bloc">
      |   <div class="name">${m.name}</div>
      |   <div class="type sansSerif">
      |        <em>${lang.monsterType(m.size, m.typ)}, ${lang.alignment(m.alignment)}</em>
      |     </div>
      |    $sepMonster
      |    <div class="red">
      |        <b>${lang.armorClass} :</b> ${m.armorClass} <br/>
      |        <b>${lang.hp} :</b> $avgHp ($hpDie) <br/>
      |        <b>${lang.speed} :</b> ${lang.length(m.speed)} <br/>
      |    $sepMonster
      |     ${Abilities.toHtml(m.abilities)}
      |    $sepMonster
      |    ${Skill.toHtml(m.skills)}
      |    ${Sens.toHtml(m.sensList)}
      |    ${Language.toHtml(m.languages)}
      |    <div>${m.challengeRanking} (${m.xp} ${lang.xp})</div>
      |    </div><!-- /red -->
      |    $sepMonster
      |    ${m.traits map Trait.toHtml mkString ""}
      |    ${m.spellCasting.map{SpellCasting.toHtml(spells, m.name, _)}.getOrElse("")}
      |    <div class="rub">ACTIONS</div>
      |    ${m.actions map Action.toHtml mkString ""}
      |    <div><em>${lang.source} : ${m.source.getOrElse(lang.unknown)}</em></div>
      |</div> <!-- /block -->""".stripMargin
  }

}