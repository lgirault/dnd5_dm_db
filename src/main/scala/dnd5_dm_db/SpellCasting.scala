package dnd5_dm_db


import scala.xml.Node

case class SpellCasting
( casterLevel : Int,
  clazz : DnDClass,
  ability : Ability,
  saveDifficultyClass : Int,
  attackBonus : Int,
  spells : Seq[SpellList]
  )

object SpellCasting {

  def fromXml(node : Node) : SpellCasting = {
    SpellCasting(
      node \ "casterLevel",
      DnDClass.fromString(node \ "class"),
      Ability.fromString(node \ "ability"),
      node \ "dc",
      node \ "attackBonus",
      node \ "spells" \ "spellList" map SpellList.fromNode)
  }

  def toHtml
  ( spells : Map[String, Spell],
    monster : String,
    spellCasting : SpellCasting)
  (implicit lang : Lang): String = {
    s"""<div>
        | <b><em>${lang.spells}</em></b> ${lang.spellCastingText(monster, spellCasting)}
        | ${spellCasting.spells.map(SpellList.toHtml(spells,_)).mkString("")}
        |</div>""".stripMargin
  }
}


case class SpellList
( level : Int,
  slots : Option[Int],
  spells : Seq[String])

object SpellList{
  def fromNode(spellList : Node) : SpellList =
    SpellList(
      singleAttribute( spellList, "level").toInt,
      singleOptionAttribute(spellList, "slots") map (_.toInt),
      (spellList \ "spell").theSeq map (_.text)
    )

  def toHtml
  ( spells : Map[String, Spell],
    spellList: SpellList)
  (implicit lang : Lang): String = {
    s"<div>${lang.spellLvl(spellList.level)} (${lang.slots(spellList.slots)}) : " +
    spellList.spells.map(s => Spell.toHtmlWithClass(spells(s),"innerBlock")).mkString("") +
    "</div>"
  }

}
