package dnd5_dm_db.xml_parse

import java.io.File

import dnd5_dm_db.{NLSeq, FromXml}
import dnd5_dm_db.lang.{Lang, langFromString}
import dnd5_dm_db.model._
import Utils._
import sbt.PathFinder

import scala.xml.{XML, Node}


object ParseSeq {

  def apply[A]
  ( fileFinder : PathFinder)
  (implicit builder : FromXml[A], lang : Lang)
  : NLSeq[A] =
    fileFinder.getPaths map { path =>
      try {
        val f = XML.loadFile(path)
        val name = new File(path).getName stripSuffix ".xml"
        (name, lang.id, builder.fromXml(f))
      } catch {
        case e : Exception =>
          println(path)
          throw e
      }
    }
}

object MonsterNameXmlParser extends FromXml[Local] {
  def fromXml(node : Node) : Local =
    NameXmlParser fromXml (node \ "identity")

}
object NameXmlParser extends FromXml[Local]{
  def fromXml(node : Node) : Local =
    LocalXmlParser.localFromXml(node \ "name")
}

object LocalXmlParser {

  def localFromXml(node : Node) : Local = {
    val seq = (node \ "local" ) map  {
      n => (langFromString(singleAttribute(n, "id")), n.text)
    }
    Local(seq.toMap)
  }
}

object TraitXmlParser extends FromXml[Trait] {
  def fromXml(node : Node) : Trait =
    Trait (LocalXmlParser localFromXml (node \ "name"),
      LocalXmlParser localFromXml (node \ "description" ))
}

trait MiscXmlParsers extends DnDMeasuresXmlParser{

  def sourceFromXml(node : Node): Source =
    Source(node.text, singleOptionAttribute(node, "page") map (_.toInt))


  def damageFromXml(node : Node) : DamageType =
    if (optionBooleanAttribute(node, "fromNonMagicalWeapon"))
      FromNonMagicalWeapon(node \ "damageType" map (DamageType fromString _.text))
    else DamageType fromString node.text

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

  def speedFromXml(speeds : Node) : Seq[Speed] = {

      def extract(name : String, builder : DnDLength => Speed) : Option[Speed] =
        (speeds \ name ).toNodeOption  map { n =>
          builder( lengthFromXml(n) )}

      Seq("speed", "burrow", "climb", "fly", "swim") map (extract(_, Regular.apply)) flatten
//      Seq(extract("speed", Regular.apply),
//        extract("burrow", Regular.apply),
//        extract("climb", Regular.apply),
//        extract("fly", Regular.apply),
//        extract("swim", Regular.apply)).flatten
  }



}
