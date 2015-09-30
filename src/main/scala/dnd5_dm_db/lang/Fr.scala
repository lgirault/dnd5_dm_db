package dnd5_dm_db.lang

import dnd5_dm_db._


object Fr
  extends Lang
  with fr.AlignmentText
  with fr.UnitsText
  with fr.SpellText
  with fr.MonsterText
  with fr.SkillAndLanguageText{

  val id: String = "fr"

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
        case _ : Melee => "Attaque d'arme de corps à corps"
        case _ : Ranged | _ : RangedSpecial => "Attaque d'arme à distance"
        case _ : MeleeOrRange => "Attaque d'arme de corps à corps ou à distance"
      }

    case _ : MultiAttack => "Attaque multiple"
    case sa : SpecialAction => sa.name
  }
  val toHit : String = "au toucher"
  val target : Int => String = "cible" + fr.plural(_)

  val senses : String = "Sens"

  val sens : Sens => String = {
    case PassivePerception(v) => s"Percepion passive $v"
    case BlindSight(r) => s"Perception aveugle ${length(r)}"
    case DarkVision(r) => s"Visibilité dans l'obscurité ${length(r)}"
    case Tremorsense(r) => s"Détection des vibrations ${length(r)}"
    case TrueSight(r) => s"Vision véritable ${length(r)}"
  }

  val damageType : DamageType => String = {
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
  val reach: String = "allonge"

  val hit : String = "Touche"

  val damages: String = "dégâts"

  val hits : Hit => String = {
    case Damage(avg, die, types0, sdesc) =>
      val types =
        types0 map damageType mkString (" ",", ", "")

      val descStr = sdesc map (desc => formatToHtml(desc)) getOrElse "."
      s"$avg ($die) $damages $types$descStr"

    case SpecialHit(desc) => desc
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


  val clear : String = "Tout effacer"

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
  }

}