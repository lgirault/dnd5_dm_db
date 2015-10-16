package dnd5_dm_db

import dnd5_dm_db.lang.Lang

import scala.xml.Node

trait FromXml[A] {
  def fromXml(n : Node) : A
}

trait ToHtml[A]{
  def toHtml(id : String, s : A)(implicit lang : Lang)  : String
}
