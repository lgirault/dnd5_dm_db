package dnd5_dm_db

object MagicSchool {
  def fromString(str : String) : MagicSchool = str match {
    case "abjuration" => Abjuration
    case "conjuration" => Conjuration
    case "divination" => Divination
    case "enchantment" => Enchantment
    case "evocation" => Evocation
    case "illusion" => Illusion
    case "necromancy" => Necromancy
    case "transmutation" => Transmutation
    case _ => error(s"unknown magic school $str")
  }
}

sealed abstract class MagicSchool
case object Abjuration extends MagicSchool
case object Conjuration extends MagicSchool
case object Divination extends MagicSchool
case object Enchantment extends MagicSchool
case object Evocation extends MagicSchool
case object Illusion extends MagicSchool
case object Necromancy extends MagicSchool
case object Transmutation extends MagicSchool

