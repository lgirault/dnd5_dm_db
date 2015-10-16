package dnd5_dm_db.html_gen

import dnd5_dm_db._
import dnd5_dm_db.lang._
import dnd5_dm_db.model.{Spell, DnDTime}


object SpellHtmlGen extends ToHtml[Spell]{

  type Concentration = Boolean

  def optionHL (shl: Option[String])(implicit lang : Lang): String =
    shl match {
      case Some(str) =>
        s"<div><b>${lang.higherLevels}</b> : $str</div>"
      case None => ""
    }

  def toHtml(id : String, s : Spell)(implicit lang : Lang)  : String =
    toHtmlWithClass(Some(id), s, "bloc")

  def toHtmlWithClass(sid : Option[String], s : Spell, clazz : String)(implicit lang : Lang)  : String = {

    def optionConcentrationText(t : DnDTime, c : Concentration) : String = {
      if (c) lang.concentration + ", " + lang.time(t)
      else lang.time(t)
    }

    val duration0 = s.durations map (t => optionConcentrationText(t._1, t._2))
    //    val duration0 = s.durations map  optionConcentrationText.tupled
    val duration = if(duration0.length == 1) duration0.head
    else duration0 mkString ("", s" ${lang.or} ", s" (${lang.seeBelow})")

    s"""<div class="$clazz">
       |   <div class="dragbar"><div></div></div>
       |    ${sid map (Templates.tradDiv("spell", _, s.name, lang)) getOrElse ""}
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
       |     ${Templates.sourceToHtml(s.source)}
       |    </div>
       |</div> <!-- /bloc -->""".stripMargin
  }
}
