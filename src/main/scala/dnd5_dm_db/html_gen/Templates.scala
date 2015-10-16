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


  def keyNameDivs[A](c : String, kseq : Seq[(Name, LangId, A)], typ : String)
                (implicit lang : Lang, extract : NameExtractor[A]) : String =
    s"""<div id="${c}_index" >""" +
      kseq.sortBy(t => extract(t._3).value).map{case (n, l, elt) =>
        s"""<div><a class="menuLink" href="?$typ=$l/$n">${extract(elt).value}</a></div>"""
      }.mkString("\n") +
    "</div>"


  def genIndex[A,B]
   ( spells : Seq[(Name, LangId, A)],
     monsters : Seq[(Name, LangId, B)] )
   (implicit lang : Lang, extractA : NameExtractor[A], extractB : NameExtractor[B]): String =
    html_header("DnD5 - DM DataBase",
      List("""<link href="css/style.css" rel="stylesheet" type="text/css" />""",
        """<script type="text/javascript" src="js/main.js"></script>"""))+
     s"""<div id="left_frame" class="frame" >
         |<div>
         |   <button id="toggle_button"> ${lang.monsters} / ${lang.spells} </button>
         |</div>""".stripMargin +
        keyNameDivs("spells", spells, "spells") +
        keyNameDivs("monsters", monsters, "monsters") +
    s"""</div><div id="right_frame" class="frame" >
      |   <div id="monsters_screen"><h1>${lang.monsters}<button class="clearScreen">${lang.clear}</button></h1></div>
      |   <div id="spells_screen"><h1>${lang.spells}<button class="clearScreen">${lang.clear}</button></h1></div>
      </div>""".stripMargin +
      html_footer

  def sourceToHtml(ssource : Option[Source]) ( implicit lang : Lang) : String =
    s"""<div class="source">${lang.source} : ${ssource.getOrElse(lang.unknown)}</div>"""

}
