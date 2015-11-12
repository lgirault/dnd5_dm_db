package dnd5_dm_db
package model


case class Spell
( name : Local,
  level : Int,
  school : MagicSchool,
  ritual : Boolean,
  castingTime : DnDTime,
  range : DnDLength,
  sAreaOfEffect: Option[AreaOfEffect],
  components : List[Components],
  durations : Seq[DnDTime],
  description : Local,
  highLevelDescription : Option[Local],
  source : Seq[Source])

object Spell extends IndexType {
  val id = Constant.spells
}

