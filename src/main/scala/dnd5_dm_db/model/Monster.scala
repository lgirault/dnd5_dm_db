package dnd5_dm_db
package model

object Monster extends IndexType

case class Monster
( name : Local,
  size : Size,
  typ : MonsterType,
  typeTags: Seq[TypeTag],
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
  traits : Seq[Trait],
  spellCasting: Option[SpellCasting],
  actions : Seq[Action],
  reactions : Seq[Action],
  description : Option[Local],
  source : Seq[Source])


case class Trait(name : Local, description : Local) {
  def withMonsterName(monster : Local) : Trait =
    copy(description = description.replaceAllLiterally("{Monster}", monster))
}

case class SpellCasting
( casterLevel : Int,
  clazz : DnDClass,
  ability : Ability,
  saveDifficultyClass : Int,
  attackBonus : Int,
  spells : Seq[SpellList]
  )

case class SpellList
( level : Int,
  slots : Option[Int],
  spells : Seq[Spell])

