package dnd5_dm_db.model

import dnd5_dm_db._

import scala.xml.Node

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
case class AnyRace(typ : MonsterType) extends MonsterType
case class TaggedType(typ : MonsterType, tags : Seq[String]) extends MonsterType

object MonsterType {

  def fromNode(identity : Node) : MonsterType =
    fromString(identity \ "type",
      (identity \ "typeTags" \ "race").toNodeOption map (_.text))

  def fromString(tstr : String, sr : Option[String])  : MonsterType = {
    val t = tstr match {
      case "Aberration" => Aberration
      case "Beast" => Beast
      case "Celestial" => CelestialType
      case "Construct" => Construct
      case "Dragon" => Dragon
      case "Elemental" => Elemental
      case "Fey" => Fey
      case "Fiend" => Fiend
      case "Giant" => GiantType
      case "Humanoid" => Humanoid
      case "Monstrosity" => Monstrosity
      case "Ooze" => Ooze
      case "Plant" => Plant
      case "Undead" => Undead
      case _ => error(s"unknown monster type $tstr")
    }
    sr match {
      case None => t
      case Some("any") => AnyRace(t)
      case Some(r) => TaggedType(t, Seq(r))
    }
  }
}
