package dnd5_dm_db
package model

sealed abstract class Sens
case class PassivePerception(value : Int) extends Sens
case class BlindSight(radius : DnDLength) extends Sens
case class DarkVision(radius : DnDLength) extends Sens
case class Tremorsense(radius : DnDLength) extends Sens
case class TrueSight(range : DnDLength) extends Sens

