package dnd5_dm_db

import scala.xml.Node


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

  def fromXml(languageList : Node) : Seq[Language] = {

    val ls = (languageList \ "language") map {n =>
      val l = fromString(n.text)
      singleOptionAttribute(languageList, "doNotSpeak") match {
        case Some("true") => UnderstandOnly(l)
        case _ => l
      }
    }
    if(optionBooleanAttribute(languageList, "anyLanguage")){
      AnyLanguage(singleOptionAttribute(languageList, "default") map fromString) +: ls
    }
    else ls
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

case class AnyLanguage(default : Option[Language]) extends Language
case class UnderstandOnly(lang : Language) extends Language
//case class CustomLanguage(name : String) extends Language
