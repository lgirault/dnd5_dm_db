package dnd5_dm_db
package model

object WeaponProperty {
  def fromString(str :String) : WeaponProperty =
    str.toLowerCase match {
      case "finesse" => Finesse
      case "heavy" => Heavy
      case "light" => Light
      case "loading" => Loading
      case "reach" => Reach
      case "two-handed" => TwoHanded
      case "ammunition"
        | "thrown"
        | "versatile"
        | "special" => error(s"weapon property $str needs args")
      case _ => error(s"unknown weapon property $str")
    }
}
sealed abstract class WeaponProperty

case object Finesse extends WeaponProperty
case object Heavy extends WeaponProperty
case object Light extends WeaponProperty
case object Loading extends WeaponProperty
case object Reach extends WeaponProperty
case object TwoHanded extends WeaponProperty

trait RangeProperty {
  val regularRange : DnDLength
  val maxRange : DnDLength
}

case class Ammunition(regularRange : DnDLength, maxRange : DnDLength) extends WeaponProperty with RangeProperty
case class Thrown(regularRange : DnDLength, maxRange : DnDLength) extends WeaponProperty with RangeProperty
case class Versatile(twoHandDmg : Die) extends WeaponProperty

case class Special(text : String) extends WeaponProperty


object WeaponFamily {
  def fromString(str : String) =
    str.toLowerCase match {
      case "simple" => Simple
      case "martial" => Martial
      case _ => error(s"unknown weapon family $str")
    }
}
sealed abstract class WeaponFamily
case object Simple extends WeaponFamily
case object Martial extends WeaponFamily


object WeaponKind {
  def fromString(str : String) =
    str.toLowerCase match {
      case "melee" => Melee
      case "ranged" => Ranged
      case _ => error(s"unknown weapon kind $str")
    }
}

sealed abstract class WeaponKind
case object Melee extends WeaponKind
case object Ranged extends WeaponKind

case class Weapon
( name : Local,
  family : WeaponFamily,
  kind : WeaponKind,
  //cost
  damage : Die,
  damageType: DamageType,
  //weight
  properties : Seq[WeaponProperty]
) {

  def rangeOption : Option[RangeProperty] =
    (properties find {_.isInstanceOf[RangeProperty]}).asInstanceOf[Option[RangeProperty]]


}
