package dnd5_dm_db

import scala.xml.Node



sealed abstract class Speed {
  val speed: DnDLength
}
object Speed {


  def fromXml(speeds : Node) : Seq[Speed] = {

    def extract(name : String, builder : DnDLength => Speed) : Option[Speed] =
      (speeds \ name ).toNodeOption  map { n =>
        builder( DnDLength fromXml n )}


    Seq(extract("speed", Regular.apply),
      extract("burrow", Regular.apply),
      extract("climb", Regular.apply),
      extract("fly", Regular.apply),
      extract("swim", Regular.apply)).flatten
    }


}

case class Regular(speed: DnDLength) extends Speed
case class Burrow(speed: DnDLength) extends Speed
case class Climb(speed: DnDLength) extends Speed
case class Fly(speed: DnDLength) extends Speed
case class Swim(speed: DnDLength) extends Speed




case class Trait(name : String, description : String)
object Trait extends FromXmlToHtml[Trait]{

  def sharedTraitFromXml
  (traits : Map[String, Trait])
  (node : Node)
  (implicit lang : Lang) : Trait = {
    val t = traits(node \ "id")
    val monster = (node \ lang.id).text

    t.copy(description = t.description
      .replaceAllLiterally("[Monster]", monster)
      .replaceAllLiterally("[monster]", monster.toLowerCase))
  }

  def fromXml(node : Node)(implicit lang : Lang) : Trait =
    Trait (node \ lang.id \ "name",
      node \ lang.id \ "description" )
  def toHtml(t : Trait)(implicit lang: Lang): String =
    s"<div><b>${t.name}:</b> ${t.description}</div>"


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
  resistances : Seq[DamageType],
  sensList : Seq[Sens],
  languages : Seq[Language],
  challengeRanking : Float,
  xp : Int,
  traits : Seq[Trait],
  spellCasting: Option[SpellCasting],
  actions : Seq[Action],
  description : Option[String],
  source : Option[String])

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
    val skillMisc = monster \ "skillMisc"

    val traits = {
      val ts =  monster \ "traits" \ "trait" map Trait.fromXml
      val sts = monster \ "traits" \ "sharedTrait" map
        Trait.sharedTraitFromXml(traitsMap)

      ts ++: sts
    }

    val spellCasting = (monster \ "spellcasting").toNodeOption
    val actionList = monster \ "actionList"

    Monster(
      identity \ "name" \ lang.id,
      Size.fromString(identity \ "size"),
      MonsterType.fromNode(identity),
      Alignment fromXml (identity\"alignment").toNode,
      identity \ "ac",
      identity \ "acDesc" \ lang.id,
      Die fromXml (identity \ "hp").toNode,
      Speed fromXml (identity \ "speeds").toNode,
      abilities,
      (skillMisc \ "savingThrows" \ "save") map Ability.savingThrowFromXml,
      (skillMisc \ "skills" \ "skill") map Skill.fromXml,
      (skillMisc \ "resistances" \ "resist").theSeq map {n =>DamageType.fromString(n.text)},
      Sens fromXml (skillMisc \ "senses").toNode,
      Language fromXml (skillMisc\"languages").toNode, // language
      (skillMisc \ "cr").text.toFloat,
      skillMisc \ "xp",
      traits,
      spellCasting map SpellCasting.fromXml,
      actionList \ "action" map Action.fromXml,
      monster \ "description" \ lang.id,
      monster \ "source" )
  }


  def titledSeq[A](title:String, s : Seq[A])(f : A => String) : String = {
    if(s.isEmpty) ""
    else s map f mkString(s"<div><b>$title</b>: ", ", ","</div>")
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
    val sensStr = titledSeq(lang.senses, m.sensList)(lang.sens)
    val langStr = titledSeq(lang.languages, m.languages)(lang.language)

    val armorDesc = m.armorDesc map {s => s"($s)"} getOrElse("")

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
      |    $sensStr
      |    $langStr
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