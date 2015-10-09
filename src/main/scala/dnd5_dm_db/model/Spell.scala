package dnd5_dm_db
package model

import dnd5_dm_db.lang.Lang

import Spell.Concentration
case class Spell
( name : Local,
  level : Int,
  school : MagicSchool,
  castingTime : DnDTime,
  range : DnDLength,
  sAreaOfEffect: Option[AreaOfEffect],
  components : List[Components],
  durations : Seq[(DnDTime, Concentration)],
  description : Local,
  highLevelDescription : Option[Local],
  source : Option[Source])

object Spell extends ToHtml[Spell]{

  type Concentration = Boolean

  def optionHL (shl: Option[String])(implicit lang : Lang): String =
  shl match {
    case Some(str) =>
      s"<div><b>${lang.higherLevels}</b> : $str</div>"
    case None => ""
  }


  def toHtml(s : Spell)(implicit lang : Lang)  : String =
    toHtmlWithClass(s, "bloc")

  def toHtmlWithClass(s : Spell, clazz : String)(implicit lang : Lang)  : String = {

    def optionConcentrationText(t : DnDTime, c : Concentration) : String = {
      if (c) lang.concentration + ", " + lang.time(t)
      else lang.time(t)
    }

    val duration0 = s.durations map (t => optionConcentrationText(t._1, t._2))
    //    val duration0 = s.durations map  optionConcentrationText.tupled
    val duration = if(duration0.length == 1) duration0.head
    else duration0 mkString ("", s" ${lang.or} ", s" (${lang.seeBelow})")


  s"""<div class="$clazz">
      |     <div class="name">${s.name.value}</div>
      |     <div class="toHide">
      |     <div class="level">
      |       <em>${lang.level} ${s.level} ${lang.magicSchool(s.school)}</em>
      |     </div>
      |     <div><b>${lang.castingTime} : </b> ${lang.time(s.castingTime)}</div>
      |     <div><b>${lang.range}</b> : ${lang.length(s.range)}${lang.sAreaOfEffect(s.sAreaOfEffect)}</div>
      |     <div><b>${lang.components}</b> : ${s.components map lang.component mkString ", "}</div>
      |     <div><b>${lang.duration}</b> : $duration </div>
      |     <div class="description">${formatToHtml(s.description.value)}</div>
      |     ${optionHL(s.highLevelDescription map (_.value))}
      |     ${Source.toHtml(s.source)}
      |    </div>
      |</div> <!-- /bloc -->""".stripMargin
  }
}

