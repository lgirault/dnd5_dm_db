package dnd5_dm_db
package model

sealed abstract class DnDTime
case class RegularAction(i : Int) extends DnDTime
case class BonusAction(i : Int) extends DnDTime
case class Minute(i: Int) extends DnDTime
case class Hour(i : Int) extends DnDTime
case class Round(i : Int) extends DnDTime
case class UpTo(t : DnDTime) extends DnDTime
case class Reaction(trigger : Local) extends DnDTime
case object Instant extends DnDTime

sealed abstract class AreaOfEffect
case class Line(length : DnDLength) extends AreaOfEffect
case class Sphere(radius : DnDLength) extends AreaOfEffect
case class Cube(sideLength : DnDLength) extends AreaOfEffect
//Cylinder

sealed abstract class DnDLength
case class Feet(i:Int) extends DnDLength
case object Touch extends DnDLength
case object Self extends DnDLength




