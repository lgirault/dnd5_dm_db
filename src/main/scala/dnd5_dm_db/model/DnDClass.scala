package dnd5_dm_db.model

import dnd5_dm_db._

sealed abstract class DnDClass
case object Barbarian extends DnDClass
case object Bard extends DnDClass
case object Cleric extends DnDClass
case object Druid extends DnDClass
case object Fighter extends DnDClass
case object Monk extends DnDClass
case object Paladin extends DnDClass
case object Ranger extends DnDClass
case object Rogue extends DnDClass
case object Sorcerer extends DnDClass
case object Warlock extends DnDClass
case object Wizard extends DnDClass

object DnDClass {
  def fromString(str : String) : DnDClass = str match {
    case "Barbarian" => Barbarian
    case "Bard" => Bard
    case "Cleric" => Cleric
    case "Druid" => Druid
    case "Fighter" => Fighter
    case "Monk" => Monk
    case "Paladin" => Paladin
    case "Ranger" => Ranger
    case "Rogue" => Rogue
    case "Sorcerer" => Sorcerer
    case "Warlock" => Warlock
    case "Wizard" => Wizard
    case _ =>  error(s"unknown class $str")

  }
}