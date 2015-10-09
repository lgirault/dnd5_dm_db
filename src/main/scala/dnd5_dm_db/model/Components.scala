package dnd5_dm_db
package model

sealed abstract class Components
case object Verbose extends Components
case object Somatic extends Components
case class Material(cpts : Local) extends Components

