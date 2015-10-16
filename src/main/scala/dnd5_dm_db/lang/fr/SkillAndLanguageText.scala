package dnd5_dm_db.lang.fr

import dnd5_dm_db.lang.Lang
import dnd5_dm_db.model._


trait SkillAndLanguageText {
  self : Lang =>
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

  val languages : String = "Langues"

  val language : Language => String = {
    case AnyLanguage(x, default) => s"$x langue au choix"+
      default.map(d => s" (généralement le ${language(d)})").getOrElse("")
    case LanguageSpecial(str) => str.value(self)
    case UnderstandOnly(l) =>
      s"comprends le ${language(l)} mais ne le parle pas"
    case Common => "Commun"
    case Dwarvish => "Nain"
    case Elvish => "Elfe"
    case GiantLang => "Géant"
    case Gnomish => "Gnome"
    case Goblin => "Gobelin"
    case Halfling => "Halfelin"
    case Orc => "Orc"

    case Abyssal => "Abyssal"
    case CelestialLang => "Célestial"
    case Draconic => "Draconique"
    case DeepSpeech => "Langue des profondeurs"
    case Infernal => "Infernal"
    case Primordial => "Primordial"
    case Sylvan => "Sylvain"
    case Undercommon => "Commun des profondeurs"
    case Troglodyte => "Troglodyte"
  }

}
