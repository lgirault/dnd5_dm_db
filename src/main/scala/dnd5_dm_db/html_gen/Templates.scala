package dnd5_dm_db
package html_gen
import model._

import dnd5_dm_db.lang.Lang

trait Named {
  val name : String
}

object Templates {

  val monsters = "monsters"
  val spells = "spells"
  val traits = "traits"
  val weapons = "weapons"

  type NameExtractor[A] = A => Local

  def html_header(title : String, headTags : List[String]) : String =
    "<!DOCTYPE html>\n<html>\n<head>\n" +
      """<meta charset="UTF-8">""" +
      headTags.mkString("\n","\n", "\n") +
      s"<title>$title</title>\n</head>\n\n<body>\n"

  val html_footer : String =
    "</body>\n\n</html>"


  implicit val spellNameExtractor : NameExtractor[Spell] =
    s => s.name

  def tradDiv(t : String, id : String, name : Local, l : Lang) = {
    val links = lang.otherLocales(l) map {l => s"""<a href="?$t=${l.id}/$id">${name.value(l)}</a>""" }
    val linksStr = links mkString "<br/>"

    s"""<div class="trad">$linksStr</div>"""
  }

  implicit val monsterNameExtractor : NameExtractor[Monster] =
    m => m.name


  def genMenu[A](typ : String, kseq : Seq[(Name, A)])
                (implicit lang : Lang, extract : NameExtractor[A]) : String =
    s"""<div id="${typ}_index" class="menu">""" +
      kseq.sortBy(t => extract(t._2).value).map{case (n, elt) =>
        s"""<div><a class="menuLink" href="?$typ=${lang.id}/$n">${extract(elt).value}</a></div>"""
      }.mkString("\n") +
    "</div>"

  def menuLocaleLink(lg : Lang) : String =
      s"""<a class="menuLang" href="?menu=${lg.id}">${lg.id}</a>"""


  def genIndex[A,B](lg : Lang): String =
    html_header("DnD5 - DM DataBase",
      List("""<link type="text/css" href="/css/style.css" rel="stylesheet" />""",
        """<script type="text/javascript" src="/js/prelude.js"></script>""",
        """<script type="text/javascript" src="/js/utils.js"></script>""",
        """<script type="text/javascript" src="/js/main.js"></script>"""))+
     s"""<div id="left_frame" class="frame" >
         |<div>
         |   <button id="toggle_button"> ${lg.monsters} / ${lg.spells} </button>
         |   ${lang.locales map menuLocaleLink mkString "|" }
         |</div>""".stripMargin +
    s"""</div><div id="right_frame" class="frame" >
      |   <div id="monsters_screen"><h1>${lg.monsters}<button class="clearScreen">${lg.clear}</button></h1></div>
      |   <div id="spells_screen"><h1>${lg.spells}<button class="clearScreen">${lg.clear}</button></h1></div>
      </div>""".stripMargin +
      html_footer

  def sourceToHtml(sources : Seq[Source]) ( implicit lang : Lang) : String = {
      val sourceStr =
        if(sources.isEmpty) lang.unknown
        else sources mkString ", "

    s"""<div class="source">${lang.source}(s) : $sourceStr</div>"""
  }

}
