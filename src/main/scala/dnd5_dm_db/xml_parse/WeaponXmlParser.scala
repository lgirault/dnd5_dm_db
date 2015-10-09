package dnd5_dm_db.xml_parse

import dnd5_dm_db.FromXml
import dnd5_dm_db.model._
import Utils._

import scala.xml.Node


object WeaponXmlParser extends FromXml[Weapon]
  with DnDMeasuresXmlParser{


  def range(node : Node) : (DnDLength, DnDLength) =
     (lengthFromXml((node \ "regular").toNode),
       lengthFromXml((node \ "max").toNode))


  def propertyFromXml(n : Node) : WeaponProperty =
    singleAttribute(n, "name").toLowerCase match{
      case "ammunition" =>
        val (reg, max) =  range(n)
        Ammunition(reg, max)
      case "thrown" =>
        val (reg, max) =  range(n)
        Thrown(reg, max)
      case "versatile" =>
        Versatile(Die.fromString(singleAttribute(n, "die")))
      case name => WeaponProperty.fromString(name)
    }

  def fromXml(n : Node) : Weapon ={
    val dmg = (n \ "damage").toNode
    Weapon(
      LocalXmlParser.localFromXml(n \ "name"),
      WeaponFamily fromString singleAttribute(n, "family"),
      WeaponKind fromString singleAttribute(n, "kind"),
      Die fromString singleAttribute(dmg, "die"),
      DamageType fromString singleAttribute(dmg, "type"),
      (n \ "property").theSeq map propertyFromXml
    )
  }


}
