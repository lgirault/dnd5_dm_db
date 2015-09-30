package dnd5_dm_db.model

import dnd5_dm_db._
import dnd5_dm_db.lang.Lang

import scala.xml.Node

sealed abstract class Components
case object Verbose extends Components
case object Somatic extends Components
case class Material(cpts : String) extends Components

object Components {
  def fromXml(node : Node, lang : Lang): List[Components] =
    List[Option[Components]](
      singleAttribute(node, "verbose") match {
        case "true" => Some(Verbose)
        case _ => None
      },
      singleAttribute(node, "somatic") match {
        case "true" => Some(Somatic)
        case _ => None
      },
      singleAttribute(node, "material") match {
        case "true" => Some(Material(node \ lang.id))
        case _ => None
      }).flatten
}