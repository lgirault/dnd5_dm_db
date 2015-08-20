package dnd5_dm_db.lang

import dnd5_dm_db.{MagicSchool, AreaOfEffect, Components}

trait SpellText {
  val castingTime : String

  val components : String

  val component : Components => String

  val duration : String

  val higherLevels : String

  val sAreaOfEffect : Option[AreaOfEffect] => String

  val magicSchool : MagicSchool => String


}
