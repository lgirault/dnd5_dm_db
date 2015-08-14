package dnd5_dm_db

import scala.xml.Node

sealed abstract class DnDTime
case class RegularAction(i : Int) extends DnDTime
case class BonusAction(i : Int) extends DnDTime
case class Minute(i: Int) extends DnDTime
case class Hour(i : Int) extends DnDTime
case class UpTo(t : DnDTime) extends DnDTime
case object Instant extends DnDTime

object DnDTime {
  def fromXml(node : Node): DnDTime = {
    val t : DnDTime =
      singleAttribute(node, "unit") match {
        case "minute" => Minute(node.text.toInt)
        case "hour" => Hour(node.text.toInt)
        case "action" => RegularAction(node.text.toInt)
        case "bonusAction" => BonusAction(node.text.toInt)
        case "instant" => Instant
        case attr => error(s"unknown time unit $attr")
      }

    singleOptionAttribute(node, "upTo") match {
      case Some("true") => UpTo(t)
      case attr => t
    }
  }
}


sealed abstract class DnDLength
case class Feet(i:Int) extends DnDLength
case object Contact extends DnDLength

object DnDLength {
  def fromXml(node : Node): DnDLength =
    singleAttribute(node, "unit") match {
      case "feet" => Feet(node.text.toInt)
      case "contact" => Contact
      case attr => error(s"unknown length unit $attr")
    }
}


