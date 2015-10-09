package dnd5_dm_db
package model

import dnd5_dm_db.xml_parse.Utils
import dnd5_dm_db.lang.Lang

case class SpellCasting
( casterLevel : Int,
  clazz : DnDClass,
  ability : Ability,
  saveDifficultyClass : Int,
  attackBonus : Int,
  spells : Seq[SpellList]
  )

object SpellCasting {

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

  def toHtml
  ( spells : Map[String, Spell],
    spellList: SpellList)
  (implicit lang : Lang): String = {
    s"<div>${lang.spellLvl(spellList.level)} (${lang.slots(spellList.slots)}) : " +
    spellList.spells.map(s => Spell.toHtmlWithClass(spells(s),"innerBlock")).mkString("") +
    "</div>"
  }

}
