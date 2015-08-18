package dnd5_dm_db

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
}

object Fr extends Lang {

  val id: String = "fr"

  val monsters : String = "Monstres"


  val range: String = "Portée"

  val feetToMeter : Int => String ={
    import java.text.DecimalFormat
    val cmPerFoot = 30
    val formatter = new DecimalFormat("#.##")

    { i => formatter.format((i * cmPerFoot).toDouble / 100) }
  }


  val length : DnDLength => String = {
      case Feet(i) => feetToMeter(i) +" m"
      case Contact => "contact"
  }

  val rangeLength : ((DnDLength, DnDLength)) => String = {
    case (Feet(reg), Feet(extra)) => feetToMeter(reg) +"/" +feetToMeter(extra) +"m"
    case _ => error("rangeLength expect a pair of feet length")
  }

  val level: String = "niveau"

  val castingTime: String = "Temps d'incantation"

  val components : String = "Composantes"

  val component : Components => String = {
    case Verbose => "V"
    case Somatic => "G"
    case Material(txt) => s"M ($txt)"
  }

  val duration: String = "Durée"

  def plural(i : Int) =
    if(i>1) "s"
    else ""

  val time : DnDTime => String = {
    case UpTo(t) => "jusqu'à " + time(t)
    case Minute(i) => i + " minute" + plural(i)
    case Hour(i) => i +" minute" + plural(i)
    case RegularAction(i) => i + " action" + plural(i)
    case BonusAction(i) => i + " action" + plural(i) + " bonus"
    case Instant => "instantané"
  }

  val higherLevels = "Aux niveaux supérieurs"


  val size : Size => String = {
    case Tiny => "très petite"
    case Small => "petite"
    case Medium => "moyenne"
    case Large => "grande"
    case Huge => "très grande"
    case Gargantuan => "gigantesque"
  }


  val monsterType : MonsterType => String = {
    case Aberration => "Aberration"
    case Beast => "Bête"
    case CelestialType => "Célese"
    case Construct => "Construction"
    case Dragon => "Dragon"
    case Elemental => "Elémentaire"
    case Fey => "Fée"
    case Fiend => "Démon"
    case GiantType => "Géant"
    case Humanoid => "Humanoïde"
    case Monstrosity => "Monstruosité"
    case Ooze => "Gelée"
    case Plant => "Plante"
    case Undead => "Mort-Vivant"
    case AnyRace(t) => monsterType(t) + " (race quelconque)"
    case TaggedType(t, r) => s"${monsterType(t)} ($r)"
  }
  val monsterTypeAndSize : (Size, MonsterType) => String = {
    case (s, mt) => monsterType(mt)+ s" de taille ${size(s)}"
  }
  val moral : Moral => String = {
    case Good => "bon"
    case Neuter => "neutre"
    case Evil => "mauvais"
  }
  val order : Order => String = {
    case Lawful => "loyal"
    case Neuter => "neutre"
    case Chaotic => "chaotique"
  }

  def moralOrOrderToString : Either[Order, Moral] => String = {
    case Left(o) => order(o)
    case Right(m) => moral(m)
  }
  def alignmentRestrictionToStr : AlignmentRestriction => String ={
    case Restrict(om) => moralOrOrderToString(om)
    case RestrictNot(Left(a)) => alignment(a)
    case RestrictNot(Right(om)) => moralOrOrderToString(om)
  }

  val alignment : Alignment => String = {
    case Unaligned => "non aligné"
    case AnyAlignment(None) => "alignement quelconque"
    case AnyAlignment(Some(res : Restrict)) => s"alignement ${alignmentRestrictionToStr(res)} quelconque"
    case AnyAlignment(Some(res : RestrictNot)) => s"alignement quelconque non ${alignmentRestrictionToStr(res)}"
    case Aligned(Neuter, Neuter) => "neutre"
    case Aligned(o, m) => order(o) +" " + moral(m)
  }
  val armorClass : String = "Classe d'armure"

  val skills : String = "Compétences"
  val skill : Skill => String = {
    case Athletics => "Athlétisme"
    case Acrobatics => "Acrobaties"
    case SleightOfHand => "Escamotage"
    case Stealth => "Discrétion"
    case Arcana => "Arcanes"
    case History => "Hitoire"
    case Investigation => "Investigation"
    case Nature => "Nature"
    case Religion => "Religion"
    case AnimalHandling => "Dressage"
    case Insight => "Intuition"
    case Medicine => "Médecine"
    case Perception => "Perception"
    case Survival => "Survie"
    case Deception => "Tromperie"
    case Intimidation => "Intimidation"
    case Performance => "Représentation"
    case Persuasion => "Persuasion"
  }

  val magicSchool : MagicSchool => String = {
    case Abjuration => "abjuration"
    case Conjuration => "invocation"
    case Divination => "divination"
    case Enchantment => "enchantement"
    case Evocation => "évocation"
    case Illusion => "illusion"
    case Necromancy => "nécromancie"
    case Transmutation => "transmutation"
  }

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
  val languages : String = "Langues"

  val language : Language => String = {
    case AnyLanguage(default) => "une langue au choix"+
      default.map(d => s" (généralement le ${language(d)})").getOrElse("")
    case Common => "Commun"
    case Dwarvish => "Nain"
    case Elvish => "Elfe"
    case GiantLang => "Géant"
    case Gnomish => "Gnome"
    case Goblin => "Gobelin"
    case Halfling => "Hobbit"
    case Orc => "Orc"

    case Abyssal => "Abyssal"
    case CelestialLang => "Célestial"
    case Draconic => "Draconique"
    case DeepSpeech => "Langue des profondeurs"
    case Infernal => "Infernal"
    case Primordial => "Primordial"
    case Sylvan => "Sylvain"
    case Undercommon => "Commun des profondeurs"
  }

  val challengeRanking : String = "Facteur de puissance"
  val xp : String = "PX"

  val actionName : Action => String = {
    case aa : AttackAction =>
      aa.kind match {
        case _ : Melee => "Attaque d'arme de corps à corps"
        case _ : Ranged => "Attaque d'arme à distance"
        case _ : MeleeOrRange => "Attaque d'arme de corps à corps ou à distance"
      }

    case _ : MultiAttack => "Attaque multiple"
    case sa : SpecialAction => sa.name
  }
  val toHit : String = "au toucher"
  val target : Int => String = "cible" + plural(_)

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
    case Radiant => "radiants(rayonnants ?)"
    case Slashing => "tranchants"
    case Thunder => "de tonnerre"
  }
  val reach: String = "allonge"
  val damages: String = "Dégâts"

  val source : String = "Source"
  val unknown : String = "inconnue"

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

  val atWill : String = "à volonté"

  val spellLvl : Int => String = {
    case 0 => "Sorts mineurs"
    case x => s"Niveau $x"
  }
  val slots : Option[Int] => String = {
    case None => atWill
    case Some(x) => s"$x emplacement${plural(x)}"
  }
  val clear : String = "Tout effacer"
}