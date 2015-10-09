package dnd5_dm_db.xml_parse

import dnd5_dm_db._
import dnd5_dm_db.model._
import dnd5_dm_db.xml_parse.Utils._

import scala.xml.Node

import LocalXmlParser._
trait DnDMeasuresXmlParser {

  def timeFromXml(node : Node): DnDTime = {
    val n =
      singleOptionAttribute(node, "value") map (_.toInt) getOrElse 1

    val t : DnDTime =
      singleAttribute(node, "unit") match {
        case "minute" => Minute(n)
        case "hour" => Hour(n)
        case "action" => RegularAction(n)
        case "bonusAction" => BonusAction(n)
        case "instant" => Instant
        case "round" => Round(n)
        case "reaction" => Reaction(localFromXml(node))
        case attr => error(s"unknown time unit $attr")
      }

    singleOptionAttribute(node, "upTo") match {
      case Some("true") => UpTo(t)
      case attr => t
    }
  }

  def lengthFromXml(node : Node): DnDLength =
    singleAttribute(node, "unit") match {
      case "feet" => Feet(singleAttribute(node, "value").toInt)
      case "touch" => Touch
      case "self" => Self
      case attr => error(s"unknown length unit $attr")
    }


  def areaOfEffectFromXml(node : Node): AreaOfEffect = {
    lazy val singleValue = lengthFromXml(node)

    singleAttribute(node, "type") match {
      case "line" => Line(singleValue)
      case "sphere" => Sphere(singleValue)
      case "cube" => Cube(singleValue)
      case t => error(s"$t unknown kind of area of effect")
    }
  }
}
