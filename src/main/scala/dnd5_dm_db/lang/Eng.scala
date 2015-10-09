package dnd5_dm_db
package lang

import dnd5_dm_db.model._


object Eng
  extends Lang
  with eng.AlignmentText
  with eng.UnitsText
  with eng.SpellText
  with eng.MonsterText
  with eng.SkillAndLanguageText{

  val id: String = "eng"

  val or : String = "or"

  val seeBelow : String = "see below"

  val monsters : String = "Monsters"

  val range: String = "Range"

  val level: String = "level"


  val armorClass : String = "Armor Class"



  val hp : String = "HP"
  val speed : String = "Speed"

  val speedLength : Speed => String = {
    s =>
      val kind = s match {
      case Regular(_) => ""
      case Burrow(_) => "burrow : "
      case Climb(_) => "climb : "
      case Fly(_) => "fly : "
      case Swim(_) => "swim : "
    }
    kind + length(s.speed)
  }

  val ability_short : Ability => String = {
    case Strength => "STR"
    case Dexterity => "DEX"
    case Constitution => "CON"
    case Intelligence => "INT"
    case Wisdom => "WIS"
    case Charisma => "CHA"
  }

  val ability_long : Ability => String = {
    case Strength => "Strength"
    case Dexterity => "Dexterity"
    case Constitution => "Constitution"
    case Intelligence => "Intelligence"
    case Wisdom => "Wisdom"
    case Charisma => "Charisma"
  }

  val savingThrows : String = "Saving Throws"

  val challengeRanking : String = "Challenge Ranking"
  val xp : String = "XP"

  val actionName : Action => String = {
    case aa : AttackAction =>
      aa.kind match {
        case _ : MeleeAttack => "Melee Attack"
        case _ : RangedAttack | _ : RangedSpecial => "Ranged Attack"
        case _ : MeleeOrRange => "Melee or Ranged Attack"
      }

    case _ : MultiAttack => "Multiattack"
    case sa : SpecialAction => sa.name.value(this)
  }
  val toHit : String = "to hit"
  val target : Int => String = "target" + fr.plural(_)

  val senses : String = "Senses"

  val sens : Sens => String = {
    case PassivePerception(v) => s"Passive perception $v"
    case BlindSight(r) => s"blindsight ${length(r)}"
    case DarkVision(r) => s"darkvision ${length(r)}"
    case Tremorsense(r) => s"tremorsense ${length(r)}"
    case TrueSight(r) => s"truesight ${length(r)}"
  }

  val damageType : DamageType => String = {
    case FromNonMagicalWeapon(dt) => (dt map damageType mkString ", ") + " from a non-magical weapon."
    case Acid => "acid"
    case Bludgeoning => "bludgeoning"
    case Cold => "cold"
    case Fire => "fire"
    case Force => "force"
    case Lightning => "lightning"
    case Necrotic => "necrotic"
    case Piercing => "piercing"
    case Poison => "poins"
    case Psychic => "psychic"
    case Radiant => "radiant"
    case Slashing => "slashing"
    case Thunder => "thunder"
  }


  val versatile : Die => String =
    d =>
      s", or ${d.average} ($d) damage if used with two hands."

  val reach: String = "reach"

  val hit : String = "hit"

  val damages: String = "dégâts"

  val hits : Hit => String = {
    case Damage(die, types, sdesc) =>
//      val types =
//        types0 map damageType mkString (" ",", ", "")

      val descStr = sdesc map (desc => formatToHtml(desc.value(this))) getOrElse ""
      s"${die.average} ($die) $damages ${damageType(types)}$descStr"

    case SpecialHit(desc) => desc.value(this)
    }



  val source : String = "Source"
  val unknown : String = "unknown"

  val atWill : String = "at will"

  val spellLvl : Int => String = {
    case 0 => "Cantrips"
    case x => s"Level $x"
  }
  val slots : Option[Int] => String = {
    case None => atWill
    case Some(x) => s"$x slot${fr.plural(x)}"
  }

  val spells : String = "Spells"
  val intAdj : Int => String = {
    case 1 => "1st"
    case 2 => "2nd"
    case 3 => "3rd"
    case x => x +"th"
  }
  val spellCastingText : (String, SpellCasting) => String = {
    case (name, sc) =>
      s"The $name is a ${intAdj(sc.casterLevel)}-level spellcaster. Its spellcasting ability is ${ability_long(sc.ability)} (spell save DC ${sc.saveDifficultyClass}, ${Die.bonus_str(sc.attackBonus)} to hit with spell attacks). The $name has the following ${clazz(sc.clazz)} spells prepared:"
  }

  val clazz : DnDClass => String = {
    case Barbarian => "Barbarian"
    case Bard => "Bard"
    case Cleric => "Cleric"
    case Druid => "Druid"
    case Fighter => "Fighter"
    case Monk => "Monk"
    case Paladin => "Paladin"
    case Ranger => "Ranger"
    case Rogue => "Rogue"
    case Sorcerer => "Sorcerer"
    case Warlock => "Warlock"
    case Wizard => "Wizard"
  }


  val clear : String = "Clear"

  val damageVulnerabilites : String = "Damage vulnerabilites"
  val damageImmunities : String = "Damage immunities"
  val conditionImmunities : String = "Condition immunities"
  val resistance : String = "Resistance"

  val conditions : Condition => String = {
    case Prone => "Prone"
    case Grappled => "Grappled"
    case Deafened => "Deafened"
    case Blinded => "Blinded"
    case Charmed => "Charmed"
    case Frightened => "Frightened"
    case Poisoned => "Poisoned"
    case Restrained => "Restrained"
    case Stunned => "Stunned"
    case Incapacitated => "Incapacitated"
    case Unconscious => "Unconscious"
    case Invisible => "Invisible"
    case Paralyzed => "Paralyzed"
    case Petrified => "Petrified"
    case Exhaustion => "Exhaustion"
  }

}