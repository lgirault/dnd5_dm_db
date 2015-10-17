package dnd5_dm_db
package model

sealed abstract class MonsterType
case object Aberration extends MonsterType
case object Beast extends MonsterType
case object CelestialType extends MonsterType
case object Construct extends MonsterType
case object Dragon extends MonsterType
case object Elemental extends MonsterType
case object Fey extends MonsterType
case object Fiend extends MonsterType
case object GiantType extends MonsterType
case object Humanoid extends MonsterType
case object Monstrosity extends MonsterType
case object Ooze extends MonsterType
case object Plant extends MonsterType
case object Undead extends MonsterType

object MonsterType {
  def fromString(str : String) : MonsterType = str.toLowerCase match {
      case "aberration" => Aberration
      case "beast" => Beast
      case "celestial" => CelestialType
      case "construct" => Construct
      case "dragon" => Dragon
      case "elemental" => Elemental
      case "fey" => Fey
      case "fiend" => Fiend
      case "giant" => GiantType
      case "humanoid" => Humanoid
      case "monstrosity" => Monstrosity
      case "ooze" => Ooze
      case "plant" => Plant
      case "undead" => Undead
      case _ => error(s"unknown monster type $str")
    }
}

sealed abstract class TypeTag

sealed abstract class Race extends TypeTag
case object Human extends Race
case object Goblinoid extends Race
case object Kobold extends Race
case object Orc extends Race
case object HalfDragon extends Race
case object Shapechanger extends Race
case object Troglodyte extends Race

case object AnyRace extends Race

object Race {
  def fromString(str : String) :  Race = str.toLowerCase match {
    case "human" => Human
    case "goblinoid" => Goblinoid
    case "kobold" => Kobold
    case "orc" => Orc
    case "half-dragon" => HalfDragon
    case "shapechanger" => Shapechanger
    case "troglodyte" => Troglodyte
    case "any" => AnyRace
    case _ => error(s"unknown race $str")
  }
}