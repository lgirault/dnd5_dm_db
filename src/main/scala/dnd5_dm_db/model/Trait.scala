package dnd5_dm_db
package model

import dnd5_dm_db.lang.Lang

case class Trait(name : Local, description : Local) {
  def withMonsterName(monster : Local) : Trait =
    copy(description = description.replaceAllLiterally("{Monster}", monster))
}
object Trait{
  def toHtml(t : Trait)(implicit lang: Lang): String =
    s"<div><b>${t.name.value}:</b> ${t.description.value}</div>"
}