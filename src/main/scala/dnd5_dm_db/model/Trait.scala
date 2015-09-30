package dnd5_dm_db.model

import dnd5_dm_db.lang.Lang

import scala.xml.Node

case class Trait(name : String, description : String) {
  def withMonsterName(monster : String) : Trait =
    copy(description = description
      .replaceAllLiterally("{Monster}", monster)
      .replaceAllLiterally("{monster}", monster.toLowerCase))
}
object Trait extends FromXmlToHtml[Trait]{

  def fromXml(node : Node)(implicit lang : Lang) : Trait =
    Trait (node \ lang.id \ "name",
      node \ lang.id \ "description" )
  def toHtml(t : Trait)(implicit lang: Lang): String =
    s"<div><b>${t.name}:</b> ${t.description}</div>"


}