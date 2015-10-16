package dnd5_dm_db
package model

case class Monster
( name : Local,
  size : Size,
  typ : MonsterType,
  alignment: Alignment,
  armorClass : Int,
  armorDesc : Option[Local],
  hitPoint : Die,
  speed : Seq[Speed],
  abilities: Abilities,
  savingThrows : Seq[(Ability, Int)],
  skills : Seq[(Skill, Int)],
  vulnerabilities : Seq[DamageType],
  damageImmunities : Seq[DamageType],
  conditionImmunities : Seq[Condition],
  resistances : Seq[DamageType],
  sensList : Seq[Sens],
  languages : Seq[Language],
  challengeRanking : ChallengeRanking,
  xp : Int,
  traits : Seq[Trait],
  spellCasting: Option[SpellCasting],
  actions : Seq[Action],
  reactions : Seq[Action],
  description : Option[Local],
  source : Option[Source])



