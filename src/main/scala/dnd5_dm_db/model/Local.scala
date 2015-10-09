package dnd5_dm_db
package model

import dnd5_dm_db.lang.Lang

case class Local(map : Map[Lang, String]) {

  override def toString : String = sys.error("use value for toString")

  def value(implicit lang : Lang) : String = map(lang)

  def replaceAllLiterally(literal : String, replacement : Local) : Local =
    Local(map.foldLeft(Map[Lang, String]()){
      case (m, (l, str)) =>
        val replacementStr = replacement.value(l)
        m + (l -> str.replaceAllLiterally(literal, replacementStr)
          .replaceAllLiterally(literal.toLowerCase, replacementStr.toLowerCase))
    })


}
