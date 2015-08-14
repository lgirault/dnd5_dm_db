package dnd5_dm_db

import scala.xml.Node

trait FromXmlToHtml[A] {
  def fromXml(n : Node)(implicit lang : Lang) : A
  def toHtml(s : A)(implicit lang : Lang)  : String
}
