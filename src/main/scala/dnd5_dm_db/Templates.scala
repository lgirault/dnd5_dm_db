package dnd5_dm_db

object Templates {

  def html_header(title : String, headTags : List[String]) : String =
    "<!DOCTYPE html>\n<html>\n<head>\n" +
      """<meta charset="UTF-8">""" +
      headTags.mkString("\n","\n", "\n") +
      s"<title>$title</title>\n</head>\n\n<body>\n"

  val html_footer : String =
    "</body>\n\n</html>"

  type SpellKeyName = (String, Spell)
  def index(spells : Seq[SpellKeyName]) : String =
    html_header("DnD5 - DM DataBase",
      List("""<link href="css/style.css" rel="stylesheet" type="text/css" />""",
           """<script type="text/javascript" src="js/main.js"></script>""")) +
      """<div id="left_frame" >""" +
        spells.map{
          case ((k , s)) =>
            s"""<div><a class="spell" href="$k">${s.name}</a></div>"""
        }.mkString("\n") +
      """</div><div id="right_frame"></div>""" +
      html_footer

}
