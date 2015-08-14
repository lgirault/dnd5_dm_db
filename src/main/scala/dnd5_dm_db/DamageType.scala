package dnd5_dm_db

sealed abstract class DamageType
case object Acid extends DamageType
case object Bludgeoning extends DamageType
case object Cold extends DamageType
case object Fire extends DamageType
case object Force extends DamageType
case object Lightning extends DamageType
case object Necrotic extends DamageType
case object Piercing extends DamageType
case object Poison extends DamageType
case object Psychic extends DamageType
case object Radiant extends DamageType
case object Slashing extends DamageType
case object Thunder extends DamageType

object DamageType {
  def fromString(str : String) : DamageType = str match {
    case "Acid" => Acid
    case "Bludgeoning" => Bludgeoning
    case "Cold" => Cold
    case "Fire" => Fire
    case "Force" => Force
    case "Lightning" => Lightning
    case "Necrotic" => Necrotic
    case "Piercing" => Piercing
    case "Poison" => Poison
    case "Psychic" => Psychic
    case "Radiant" => Radiant
    case "Slashing" => Slashing
    case "Thunder" => Thunder
    case _ => error(s"unknown damage type : $str")
  }

}
