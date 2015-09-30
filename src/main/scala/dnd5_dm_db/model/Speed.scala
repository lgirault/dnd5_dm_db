package dnd5_dm_db.model

import scala.xml.Node

sealed abstract class Speed {
  val speed: DnDLength
}
object Speed {


  def fromXml(speeds : Node) : Seq[Speed] = {

    def extract(name : String, builder : DnDLength => Speed) : Option[Speed] =
      (speeds \ name ).toNodeOption  map { n =>
        builder( DnDLength fromXml n )}


    Seq(extract("speed", Regular.apply),
      extract("burrow", Regular.apply),
      extract("climb", Regular.apply),
      extract("fly", Regular.apply),
      extract("swim", Regular.apply)).flatten
  }


}

case class Regular(speed: DnDLength) extends Speed
case class Burrow(speed: DnDLength) extends Speed
case class Climb(speed: DnDLength) extends Speed
case class Fly(speed: DnDLength) extends Speed
case class Swim(speed: DnDLength) extends Speed