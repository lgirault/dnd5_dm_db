package dnd5_dm_db.lang

import dnd5_dm_db._

abstract class Lang {
  val id : String

  val monsters : String

  val level : String

  val castingTime : String

  val range : String

  val length : DnDLength => String

  val rangeLength : ((DnDLength, DnDLength)) => String

  val components : String

  val component : Components => String

  val duration : String

  val time : DnDTime => String

  val higherLevels : String

  val size : Size => String

  val monsterType : MonsterType => String
  val monsterTypeAndSize : (Size, MonsterType) => String

  val alignment : Alignment => String

  val armorClass : String

  val skills : String
  val skill : Skill => String

  val magicSchool : MagicSchool => String

  val hp : String
  val speed : String

  val speedLength : Speed => String

  val ability_short : Ability => String
  val ability_long : Ability => String

  val savingThrows : String

  val senses : String
  val sens : Sens => String

  val languages : String
  val language : Language => String

  val challengeRanking : String
  val xp : String

  val actionName : Action => String
  val toHit : String
  val reach : String
  val target : Int => String
  val hit : String
  val hits : Hit => String
  val damages : String

  val damageType : DamageType => String


  val source : String
  val unknown : String
  val spells : String
  val spellCastingText : (String, SpellCasting) => String

  val clazz : DnDClass => String

  val atWill : String
  val slots : Option[Int] => String
  val spellLvl : Int => String

  val clear : String

  val damageImmunities : String
  val conditionImmunities : String
  val resistance : String
  val conditions : Condition => String
}
