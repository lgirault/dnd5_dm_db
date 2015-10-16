package dnd5_dm_db
package model

case class SpellCasting
( casterLevel : Int,
  clazz : DnDClass,
  ability : Ability,
  saveDifficultyClass : Int,
  attackBonus : Int,
  spells : Seq[SpellList]
  )


case class SpellList
( level : Int,
  slots : Option[Int],
  spells : Seq[Spell])


