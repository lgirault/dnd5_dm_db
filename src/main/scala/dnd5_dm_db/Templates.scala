package dnd5_dm_db

trait Named {
  val name : String
}

object Templates {

  def html_header(title : String, headTags : List[String]) : String =
    "<!DOCTYPE html>\n<html>\n<head>\n" +
      """<meta charset="UTF-8">""" +
      headTags.mkString("\n","\n", "\n") +
      s"<title>$title</title>\n</head>\n\n<body>\n"

  val html_footer : String =
    "</body>\n\n</html>"


  implicit def spellToNamed(s : Spell) : Named =  new Named {
      val name = s.name
  }


  implicit def monsterToNamed( m : Monster) : Named =  new Named {
    val name = m.name
  }


  def keyNameDivs[A](c : String, kseq : KeySeq[(RelativePath, A)])
                (implicit convert : A => Named) : String =
    s"""<div id="${c}_index" >""" +
      kseq.map{case ((_, (p, n))) =>
        s"""<div><a class="menuLink" href="$p">${n.name}</a></div>"""
      }.mkString("\n") +
    "</div>"


  def index
   ( spells : KeySeq[(RelativePath, Spell)],
     monsters : KeySeq[(RelativePath,Monster)] )
   (implicit lang : Lang ): String =
    html_header("DnD5 - DM DataBase",
      List("""<link href="css/style.css" rel="stylesheet" type="text/css" />""",
        """<script type="text/javascript" src="js/main.js"></script>"""))+
     s"""<div id="left_frame" class="frame" >
         |<div>
         |   <button id="toggle_button"> ${lang.monsters} / ${lang.spells} </button>
         |</div>""".stripMargin +
        keyNameDivs("spells", spells) +
        keyNameDivs("monsters", monsters) +
    s"""</div><div class="frame" >
      |   <div id="monsters_screen"><h1>${lang.monsters}</h1></div>
      |   <div id="spells_screen"><h1>${lang.spells}</h1></div>
      </div>""".stripMargin +
      html_footer

}
