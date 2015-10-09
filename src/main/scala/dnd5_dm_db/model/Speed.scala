package dnd5_dm_db
package model

sealed abstract class Speed {
  val speed: DnDLength
}
case class Regular(speed: DnDLength) extends Speed
case class Burrow(speed: DnDLength) extends Speed
case class Climb(speed: DnDLength) extends Speed
case class Fly(speed: DnDLength) extends Speed
case class Swim(speed: DnDLength) extends Speed