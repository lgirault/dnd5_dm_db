package dnd5_dm_db.lang.eng

import dnd5_dm_db.lang.Lang
import dnd5_dm_db.model._


trait SkillAndLanguageText {
  self : Lang =>
  val skills : String = "Skills"
  val skill : Skill => String = {
    case Athletics => "Athletics"
    case Acrobatics => "Acrobatics"
    case SleightOfHand => "Sleight of hand"
    case Stealth => "Stealth"
    case Arcana => "Arcana"
    case History => "History"
    case Investigation => "Investigation"
    case Nature => "Nature"
    case Religion => "Religion"
    case AnimalHandling => "Animal handling"
    case Insight => "Insight"
    case Medicine => "Medicine"
    case Perception => "Perception"
    case Survival => "Survival"
    case Deception => "Deception"
    case Intimidation => "Intimidation"
    case Performance => "Performance"
    case Persuasion => "Persuasion"
  }

  val languages : String = "Languages"

  val language : Language => String = {
    case AnyLanguage(x, default) => s"any $x language"+
      default.map(d => s" (usually ${language(d)})").getOrElse("")
    case LanguageSpecial(str) => str.value(self)
    case UnderstandOnly(l) =>
      s"understands ${language(l)} but can't speak"
    case Common => "Common"
    case Dwarvish => "Dwarvish"
    case Elvish => "Elvish"
    case GiantLang => "Giant"
    case Gnomish => "Gnomish"
    case Goblin => "Goblin"
    case Halfling => "Halfling"
    case Orc => "Orc"

    case Abyssal => "Abyssal"
    case CelestialLang => "Celestial"
    case Draconic => "Draconic"
    case DeepSpeech => "Deep speech"
    case Infernal => "Infernal"
    case Primordial => "Primordial"
    case Sylvan => "Sylvan"
    case Undercommon => "Undercommon"
    case Troglodyte => "Troglodyte"
  }

}
