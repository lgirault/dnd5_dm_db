package dnd5_dm_db
package model


case class Source(src : String, page : Option[Int]){
  override def toString =
    src + (page map (p => s" ( p.$p )") getOrElse "")
}


