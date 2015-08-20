package dnd5_dm_db

import dnd5_dm_db.lang.Lang

import scala.xml.Node


case class Source(src : String, page : Option[Int]){
  override def toString =
    src + (page map (p => s" ( p.$p )") getOrElse "")
}
object Source {
  def fromXml(node : Node): Source = {
    Source(node.text, singleOptionAttribute(node, "page") map (_.toInt))
  }
  def toHtml(ssource : Option[Source]) ( implicit lang : Lang) : String = {
    s"""<div class="source">${lang.source} : ${ssource.getOrElse(lang.unknown)}</div>"""
  }
}

