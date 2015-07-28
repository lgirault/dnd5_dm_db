package dnd5_dm_db

import scala.xml.Node

case class Spell
( name : String,
  level : Int,
  typ : String,
  castingTime : DnDTime,
  range : DnDLength,
  components : List[Components],
  duration : DnDTime,
  concentration : Boolean,
  description : String,
  highLevelDescription : Option[String])

object Spell {

  def fromXml(spell: Node)(implicit lang : Lang) : Spell = {
    Spell(spell \ "name" \ lang.id ,
      (spell \ "level").singleText.toInt ,
      spell \ "type",
      DnDTime fromXml (spell \ "ctime").toNode,
      DnDLength fromXml (spell \ "range").toNode,
      Components fromXml (spell \ "components").toNode,
      DnDTime fromXml (spell \ "duration").toNode,
      singleOptionAttribute((spell \ "duration").toNode, "concentration") match {
        case Some(attr) => attr.toBoolean
        case None => false
      },
      spell \ "description" \ lang.id  ,
      spell \ "higher-level-description" \ lang.id
    )
  }

  def optionHL (shl: Option[String])(implicit lang : Lang): String =
  shl match {
    case Some(str) =>
      s"<div><b>${lang.higherLevels}</b> : $str</div>"
    case None => ""
  }
  def toHtml(s : Spell)(implicit lang : Lang)  : String = {
    s"""<div class="bloc">
      |     <div class="nom">${s.name}</div>
      |     <div class="niveau"><em>${lang.level} ${s.level} ${s.typ}</em></div>
      |     <div><b>${lang.castingTime} : </b> ${lang.time(s.castingTime)}</div>
      |     <div><b>${lang.range}</b> : ${s.components map lang.component mkString ", "}</div>
      |     <div><b>${lang.components}</b> : </div>
      |     <div><b>${lang.duration}</b> : ${lang.time(s.duration)} </div>
      |     <div class="description">${s.description}</div>
      |     ${optionHL(s.highLevelDescription)}
      |</div> <!-- /bloc -->""".stripMargin
  }
}

