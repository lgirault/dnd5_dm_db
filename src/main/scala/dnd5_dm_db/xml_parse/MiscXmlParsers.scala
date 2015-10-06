package dnd5_dm_db.xml_parse
import dnd5_dm_db.model._
import dnd5_dm_db.xml_parse.DnDMeasuresXmlParser._
import Utils._

import scala.xml.Node



object MiscXmlParsers {

  def damageFromXml(node : Node) : DamageType = {
    val dt = DamageType fromString node.text
    if(optionBooleanAttribute(node, "nonMagic"))
      NonMagic(dt)
    else dt
  }


  def sensFromXml(node : Node) : Seq[Sens] = {

    def optionSens(name : String, build : DnDLength => Sens) =
      (node \ name).toNodeOption map (n => build(lengthFromXml(n)))


    val senses : Seq[Option[Sens]] = Seq(
      optionSens("blindsight", BlindSight.apply),
      optionSens("darkvision", DarkVision.apply),
      optionSens("tremorsense", Tremorsense.apply),
      optionSens("truesight", TrueSight.apply),
      (node \ "passivePerception").textOption map (v => PassivePerception(v.toInt))

    )

    senses.flatten
  }

}
