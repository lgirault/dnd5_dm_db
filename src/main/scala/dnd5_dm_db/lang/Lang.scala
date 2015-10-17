package dnd5_dm_db.lang

import dnd5_dm_db.model._


abstract class Lang
  extends SpellText {
  val id : String

  val or : String

  val seeBelow : String

  val monsters : String

  val level : String

  val range : String

  val length : DnDLength => String

  val rangeLength : ((DnDLength, DnDLength)) => String


  val time : DnDTime => String


  val size : Size => String

  val monsterType : MonsterType => String
  val monsterRace : Race => String

  val monsterTypeAndSize : (Size, MonsterType, Seq[TypeTag]) => String


  val alignment : Alignment => String

  val armorClass : String

  val skills : String
  val skill : Skill => String


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

  val versatile : Die => String

  val source : String
  val unknown : String
  val spells : String
  val spellCastingText : (String, SpellCasting) => String

  val clazz : DnDClass => String

  val atWill : String
  val slots : Option[Int] => String
  val spellLvl : Int => String

  val clear : String

  val damageVulnerabilites : String
  val damageImmunities : String
  val conditionImmunities : String
  val resistance : String
  val conditions : Condition => String
}
