package dnd5_dm_db
package lang

import dnd5_dm_db.model._


object Fr
  extends Lang
  with fr.AlignmentText
  with fr.UnitsText
  with fr.SpellText
  with fr.MonsterText
  with fr.SkillAndLanguageText{

  val id: String = "fr"

  val or : String = "ou"

  val seeBelow : String = "voir ci-dessous"

  val monsters : String = "Monstres"

  val range: String = "Portée"

  val level: String = "niveau"


  val armorClass : String = "Classe d'armure"



  val hp : String = "PV"
  val speed : String = "Vitesse"

  val speedLength : Speed => String = {
    s =>
      val kind = s match {
      case Regular(_) => ""
      case Burrow(_) => "creuser : "
      case Climb(_) => "escalade : "
      case Fly(_) => "vol : "
      case Swim(_) => "nage : "
    }
    kind + length(s.speed)
  }

  val ability_short : Ability => String = {
    case Strength => "FOR"
    case Dexterity => "DEX"
    case Constitution => "CON"
    case Intelligence => "INT"
    case Wisdom => "SAG"
    case Charisma => "CHA"
  }

  val ability_long : Ability => String = {
    case Strength => "la Force"
    case Dexterity => "la Dextérité"
    case Constitution => "la Constitution"
    case Intelligence => "l'Intelligence"
    case Wisdom => "la Sagesse"
    case Charisma => "le Charisme"
  }

  val savingThrows : String = "Jets de sauvegarde"

  val challengeRanking : String = "Facteur de puissance"
  val xp : String = "PX"

  val actionName : Action => String = {
    case aa : AttackAction =>
      aa.kind match {
        case _ : MeleeAttack => "Attaque d'arme de corps à corps"
        case _ : RangedAttack | _ : RangedSpecial => "Attaque d'arme à distance"
        case _ : MeleeOrRange => "Attaque d'arme de corps à corps ou à distance"
      }

    case _ : MultiAttack => "Attaque multiple"
    case sa : SpecialAction => sa.name.value(this)
    case wa : WeaponAction => actionName(wa.toAttackAction(this))
  }
  val toHit : String = "au toucher"
  val target : Int => String = "cible" + fr.plural(_)

  val senses : String = "Sens"

  val sens : Sens => String = {
    case PassivePerception(v) => s"Perception passive $v"
    case BlindSight(r) => s"Perception aveugle ${length(r)}"
    case DarkVision(r) => s"Visibilité dans l'obscurité ${length(r)}"
    case Tremorsense(r) => s"Détection des vibrations ${length(r)}"
    case TrueSight(r) => s"Vision véritable ${length(r)}"
  }

  val damageType : DamageType => String = {
    case FromNonMagicalWeapon(dt) => (dt map damageType mkString ", ") + " d'une arme non magique."
    case Acid => "acides"
    case Bludgeoning => "contondants"
    case Cold => "de froid"
    case Fire => "de feu"
    case Force => "de force"
    case Lightning => "de foudre"
    case Necrotic => "nécrotiques"
    case Piercing => "perforants"
    case Poison => "empoisonnés"
    case Psychic => "psychiques"
    case Radiant => "rayonnants"
    case Slashing => "tranchants"
    case Thunder => "de tonnerre"
  }


  val versatile : Die => String =
    d =>
      s", ou ${d.average} ($d) dégâts tranchants si utilisé à deux mains."
//, or 6 (1d10 + 1) slashing damage if used with two hands.

  val reach: String = "allonge"

  val hit : String = "Touche"

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
  val unknown : String = "inconnue"

  val atWill : String = "à volonté"

  val spellLvl : Int => String = {
    case 0 => "Sorts mineurs"
    case x => s"Niveau $x"
  }
  val slots : Option[Int] => String = {
    case None => atWill
    case Some(x) => s"$x emplacement${fr.plural(x)}"
  }

  val spells : String = "Sorts"
  val spellCastingText : (String, SpellCasting) => String = {
    case (name, sc) =>
      s"Un $name est un lanceur de sorts de niveau ${sc.casterLevel}. Sa caractéristique pour lancer des sorts est ${ability_long(sc.ability)} (sauvegarde DD ${sc.saveDifficultyClass}, ${Die.bonus_str(sc.attackBonus)} au jet d\'attaque). Un $name a les sorts de ${clazz(sc.clazz)} suivants préparés:"
  }

  val clazz : DnDClass => String = {
    case Barbarian => "Barbare"
    case Bard => "Barde"
    case Cleric => "Clerc"
    case Druid => "Druide"
    case Fighter => "Guerrier"
    case Monk => "Moine"
    case Paladin => "Paladin"
    case Ranger => "Ranger"
    case Rogue => "Roublard"
    case Sorcerer => "Ensorceleur"
    case Warlock => "Sorcier"
    case Wizard => "Magicien"
  }


  val clearScreen : String = "Tout effacer"
  val createScreen : String = "Nouvel Écran"
  val deleteScreen : String = "Supprimer"

  val damageVulnerabilites : String = "Vulnérable aux dégâts"
  val damageImmunities : String = "Immunisé aux dégâts"
  val conditionImmunities : String = "Immunisé aux conditions"
  val resistance : String = "Résistances"

  val conditions : Condition => String = {
    case Prone => "À terre"
    case Grappled => "Agrippé"
    case Deafened => "Assourdi"
    case Blinded => "Aveuglé"
    case Charmed => "Charmé"
    case Frightened => "Effrayé"
    case Poisoned => "Empoisonné"
    case Restrained => "Entravé"
    case Stunned => "Étourdi"
    case Incapacitated => "Incapable d'agir"
    case Unconscious => "Inconscient"
    case Invisible => "Invisible"
    case Paralyzed => "Paralysé"
    case Petrified => "Pétrifié"
    case Exhaustion => "Épuisement"
  }

}