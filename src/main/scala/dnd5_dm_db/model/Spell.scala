package dnd5_dm_db
package model

import Spell.Concentration
case class Spell
( name : Local,
  level : Int,
  school : MagicSchool,
  castingTime : DnDTime,
  range : DnDLength,
  sAreaOfEffect: Option[AreaOfEffect],
  components : List[Components],
  durations : Seq[(DnDTime, Concentration)],
  description : Local,
  highLevelDescription : Option[Local],
  source : Seq[Source])

object Spell extends IndexType{
  type Concentration = Boolean
}

