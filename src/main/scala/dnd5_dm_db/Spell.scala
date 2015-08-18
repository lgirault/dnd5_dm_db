package dnd5_dm_db

import scala.xml.Node

case class Spell
( name : String,
  level : Int,
  school : MagicSchool,
  castingTime : DnDTime,
  range : DnDLength,
  components : List[Components],
  duration : DnDTime,
  concentration : Boolean,
  description : String,
  highLevelDescription : Option[String],
  source : Option[String])

object Spell extends FromXmlToHtml[Spell]{

  def fromXml(spell: Node)(implicit lang : Lang) : Spell = {
    Spell(spell \ "name" \ lang.id ,
      spell \ "level" ,
      MagicSchool.fromString(spell \ "type"),
      DnDTime fromXml (spell \ "ctime").toNode,
      DnDLength fromXml (spell \ "range").toNode,
      Components.fromXml((spell \ "components").toNode, lang),
      DnDTime fromXml (spell \ "duration").toNode,
      optionBooleanAttribute((spell \ "duration").toNode, "concentration"),
      spell \ "description" \ lang.id  ,
      spell \ "higher-level-description" \ lang.id,
      spell \ "source"
    )
  }

  def optionHL (shl: Option[String])(implicit lang : Lang): String =
  shl match {
    case Some(str) =>
      s"<div><b>${lang.higherLevels}</b> : $str</div>"
    case None => ""
  }


  def toHtml(s : Spell)(implicit lang : Lang)  : String =
    toHtmlWithClass(s, "bloc")

  def toHtmlWithClass(s : Spell, clazz : String)(implicit lang : Lang)  : String = {
  s"""<div class="$clazz">
      |     <div class="name">${s.name}</div>
      |     <div class="toHide">
      |     <div class="level">
      |       <em>${lang.level} ${s.level} ${lang.magicSchool(s.school)}</em>
      |     </div>
      |     <div><b>${lang.castingTime} : </b> ${lang.time(s.castingTime)}</div>
      |     <div><b>${lang.range}</b> : ${lang.length(s.range)}</div>
      |     <div><b>${lang.components}</b> : ${s.components map lang.component mkString ", "}</div>
      |     <div><b>${lang.duration}</b> : ${lang.time(s.duration)} </div>
      |     <div class="description">${s.description.replaceAllLiterally("[","<").replaceAllLiterally("]",">")}</div>
      |     ${optionHL(s.highLevelDescription)}
      |    <div><em>${lang.source} : ${s.source.getOrElse(lang.unknown)}</em></div>
      |    </div>
      |</div> <!-- /bloc -->""".stripMargin
  }
}

