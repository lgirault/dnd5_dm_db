package dnd5_dm_db.model

import dnd5_dm_db._

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
  def fromString(str : String) : DamageType = str.toLowerCase match {
    case "acid" => Acid
    case "bludgeoning" => Bludgeoning
    case "cold" => Cold
    case "fire" => Fire
    case "force" => Force
    case "lightning" => Lightning
    case "necrotic" => Necrotic
    case "piercing" => Piercing
    case "poison" => Poison
    case "psychic" => Psychic
    case "radiant" => Radiant
    case "slashing" => Slashing
    case "thunder" => Thunder
    case _ => error(s"unknown damage type : $str")
  }

}
