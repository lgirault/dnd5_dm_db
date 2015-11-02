package dnd5_dm_db
package html_gen
import model._

import dnd5_dm_db.lang.Lang

trait Named {
  val name : String
}

object Templates {


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


  def menuItem[A]
    (typ : String, item :(ItemId, A))
    (implicit lang : Lang, extract : NameExtractor[A]) : String = item match {
      case (n, elt) =>
        s"""<a class="itemLink" href="?$typ=${lang.id}/$n">${extract(elt).value}</a>"""
    }


  def genMenu[A](typ : String, menuId : String, kseq : Seq[(ItemId, A)])
                (implicit lang : Lang, extract : NameExtractor[A]) : String =
    s"""<div id="$menuId" class="menu">""" +
      kseq.sortBy(t => extract(t._2).value).map(menuItem(typ, _)).mkString("<div>", "</div>\n<div>", "</div>") +
    "</div>"

  def menuLocaleLink(lg : Lang) : String =
      s"""<a class="menuLang" href="?${lg.id}">${lg.id}</a>"""

  def cssInclude(href : String) : String =
    s"""<link type="text/css" href="$href" rel="stylesheet" />"""

  def jsInclude(src : String) : String =
    s"""<script type="text/javascript" src="$src"></script>"""

  def genIndex[A,B](lg : Lang): String =
    html_header("DnD5 - DM DataBase",
      List(cssInclude("/css/style.css"), cssInclude("/css/roundTabs.css"),
        jsInclude("/js/prelude.js"), jsInclude("/js/string_score.js"),
        jsInclude("/js/utils.js"), jsInclude("/js/main.js")))+
      s"""<div id="left_frame" class="frame" >
         |  <div>
         |   <a class="menu_chooser" href="?${Constant.monsters}_menu">${lg.monsters}</a>
         |   <a class="menu_chooser" href="?${Constant.monsters}_indexes">(${lg.indexes})</a>
         |   <a class="menu_chooser" href="?${Constant.spells}_menu">${lg.spells}</a>
         |   <a class="menu_chooser" href="?${Constant.spells}_indexes">(${lg.indexes})</a>
   ${lang.locales map menuLocaleLink mkString "|" }
         |   <input id="search" type="text"/>
         |  </div>
         |  <div id="index_wrapper"></div>
         |</div>
         |<div id="right_frame" class="frame" >
         |  <div>
         |    <button id="createScreen">${lg.createScreen}</button>
         |    <button id="clearScreen">${lg.clearScreen}</button>
         |    <button id="deleteScreen">${lg.deleteScreen}</button>
         |    <button id="renameScreen">${lg.renameScreen}</button>
         |  </div>
         |  <div id="pannelList"></div>
         |</div>""".stripMargin +
      html_footer

  def sourceToHtml(sources : Seq[Source]) ( implicit lang : Lang) : String = {
      val sourceStr =
        if(sources.isEmpty) lang.unknown
        else sources mkString ", "

    s"""<div class="source">${lang.source}(s) : $sourceStr</div>"""
  }

}
