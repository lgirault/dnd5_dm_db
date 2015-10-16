package dnd5_dm_db
package model

object Language {
  def fromString(str : String) : Language = str.toLowerCase match {
    case "common" => Common
    case "dwarvish" => Dwarvish
    case "elvish" => Elvish
    case "giant" => GiantLang
    case "gnomish" => Gnomish
    case "goblin" => Goblin
    case "halfling" => Halfling
    case "orc" => Orc

    case "abyssal" => Abyssal
    case "celestial" => CelestialLang
    case "draconic" => Draconic
    case "deepSpeech" => DeepSpeech
    case "infernal" => Infernal
    case "primordial" => Primordial
    case "sylvan" => Sylvan
    case "undercommon" => Undercommon

    case "troglodyte" => Troglodyte

    case _ => error(s"unknown language $str")
  }
}
sealed abstract class Language


case object Common extends Language
case object Dwarvish extends Language
case object Elvish extends Language
case object GiantLang extends Language
case object Gnomish extends Language
case object Goblin extends Language
case object Halfling extends Language
case object Orc extends Language

case object Abyssal extends Language
case object CelestialLang extends Language
case object Draconic extends Language
case object DeepSpeech extends Language
case object Infernal extends Language
case object Primordial extends Language
case object Sylvan extends Language
case object Undercommon extends Language

case object Troglodyte extends Language
//Todo handle case of any X language
//exemple with spy and any 2 language
case class AnyLanguage(num : Int, default : Option[Language]) extends Language
case class UnderstandOnly(lang : Language) extends Language
case class LanguageSpecial(name : Local) extends Language
