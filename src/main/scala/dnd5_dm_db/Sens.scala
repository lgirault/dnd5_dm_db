package dnd5_dm_db

import scala.xml.Node

sealed abstract class Sens
case class PassivePerception(value : Int) extends Sens
case class BlindSight(radius : DnDLength) extends Sens
case class DarkVision(radius : DnDLength) extends Sens
case class Tremorsense(radius : DnDLength) extends Sens
case class TrueSight(range : DnDLength) extends Sens

object Sens{
  def fromXml(node : Node) : Seq[Sens] = {

    def optionSens(name : String, build : DnDLength => Sens) =
      (node \ name).toNodeOption map (n => build(DnDLength.fromXml(n)))


    val senses : Seq[Option[Sens]] = Seq(
      (node \ "passivePerception").textOption map (v => PassivePerception(v.toInt)),
      optionSens("blindsight", BlindSight.apply),
      optionSens("darkvision", DarkVision.apply),
      optionSens("tremorsense", Tremorsense.apply),
      optionSens("truesight", TrueSight.apply)
    )

    senses.flatten
  }

}