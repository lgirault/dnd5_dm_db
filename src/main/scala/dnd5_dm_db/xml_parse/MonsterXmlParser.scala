package dnd5_dm_db.xml_parse

import dnd5_dm_db.FromXml
import dnd5_dm_db.model._

import scala.xml.Node
import Utils._
import LocalXmlParser._

object MonsterXmlParser extends MiscXmlParsers {

  def fromXml
  ( spells : Retriever[Spell],
    traits : Retriever[Trait],
    weapons: Retriever[Weapon]) : FromXml[Monster] = new FromXml[Monster] {
    def fromXml(n : Node) : Monster =
      monsterFromXml(n, traits, weapons)

  }

  def monsterTypeFromXml(identity : Node) : MonsterType =
    MonsterType.fromString(identity \ "type",
      (identity \ "typeTags" \ "race").toNodeOption map (_.text))


  def monsterFromXml(monster : Node,
              traitsMap : Retriever[Trait],
              weaponsMap: Retriever[Weapon]) : Monster = {
    val identity : Node = monster \ "identity"
    val abilities = abilitiesFromXml(monster \ "abilities")
    val skillMisc : Node = monster \ "skillMisc"



    val damageVulnerabilities = skillMisc \ "vulnerabilities" \ "damageType"  map {
      n => DamageType.fromString(n.text)
    }

    val damageImmunities = skillMisc \ "damageImmunities" \ "damageType"  map damageFromXml
    val conditionImmunities = skillMisc \ "conditionImmunities" \ "conditionType"  map {
      n => Condition.fromString(n.text)
    }
    val damageResistances =  skillMisc \ "resistances" \ "damageType" map damageFromXml

    val traits = {
      val ts =  monster \ "traits" \ "trait" map TraitXmlParser.fromXml

      val sShared = (monster \ "traits" \ "shared").toNodeOption

      val sts = sShared map {
        shared =>
          val name = localFromXml(shared)
          shared \ "id" map (n => traitsMap(n.text).withMonsterName(name) )
      } getOrElse Seq()

      ts ++: sts
    }

    implicit val iwm = weaponsMap
    val spellCasting = (monster \ "spellcasting").toNodeOption
    val actions = monster \ "actions" \ "action" map ActionXmlParser.actionFromXml
    val reactions = monster \ "reactions" \ "action" map ActionXmlParser.actionFromXml

    val skills = (skillMisc \ "skills" \ "skill") map Skill.fromXml
    val perceptionModifier =
      skills find (_._1 == Perception) match {
        case Some((_, m)) => m
        case None => Ability.modifier(abilities.wisdom)
      }

    val passivePerception =
      (skillMisc \ "passivePerception").toNodeOption map (n => PassivePerception(n.text.toInt)) getOrElse PassivePerception(10 + perceptionModifier)

    val senses =
      (skillMisc \ "senses").toNodeOption map sensFromXml getOrElse Seq()

    val ac = (identity \ "ac").toNode
    val acDesc = {
      val loc = localFromXml(ac)
      if(loc.map.isEmpty) None
      else Some(loc)
    }

    Monster(
      localFromXml(identity \ "name"),
      Size.fromString(identity \ "size"),
      monsterTypeFromXml(identity),
      Alignment fromXml (identity\"alignment").toNode,
      singleAttribute(ac, "value").toInt,
      acDesc,
      Die fromXml (identity \ "hp").toNode,
      speedFromXml (identity \ "speeds"),
      abilities,
      (skillMisc \ "savingThrows" \ "save") map Ability.savingThrowFromXml,
      skills,
      damageVulnerabilities,
      damageImmunities,
      conditionImmunities,
      damageResistances,
      passivePerception +: senses,
      (skillMisc\"languages").toNodeOption map languageFromXml getOrElse Seq(), // language
      ChallengeRanking.fromString(skillMisc \ "cr"),
      (skillMisc \ "xp").text.toInt,
      traits,
      spellCasting map spellCastingFromXml,
      actions,
      reactions,
      (monster \ "description").toNodeOption map localFromXml,
      (monster \ "source" ).toNodeOption map Source.fromXml )
  }


  def abilitiesFromXml(node : Node) : Abilities =
    Abilities(singleAttribute(node, "str").toInt,
      singleAttribute(node, "dex").toInt,
      singleAttribute(node, "con").toInt,
      singleAttribute(node, "int").toInt,
      singleAttribute(node, "wis").toInt,
      singleAttribute(node, "cha").toInt)


  def spellCastingFromXml(node : Node) : SpellCasting = {
    SpellCasting(
      (node \ "casterLevel").text.toInt,
      DnDClass.fromString(node \ "class"),
      Ability.fromString(node \ "ability"),
      ( node \ "dc").text.toInt,
      (node \ "attackBonus").text.toInt,
      node \ "spells" \ "spellList" map spellListFromXml)
  }


  def spellListFromXml(spellList : Node) : SpellList =
    SpellList(
      singleAttribute( spellList, "level").toInt,
      singleOptionAttribute(spellList, "slots") map (_.toInt),
      (spellList \ "spell").theSeq map (_.text)
    )

  def languageFromXml(languageList : Node) : Seq[Language] = {

    val ls = (languageList \ "language") map {n =>
      if(optionBooleanAttribute(n, "special"))
        LanguageSpecial(localFromXml(n))
      else {
        val l = Language.fromString(n.text)
        singleOptionAttribute(languageList, "doNotSpeak") match {
          case Some("true") => UnderstandOnly(l)
          case _ => l
        }
      }
    }
    if(optionBooleanAttribute(languageList, "anyLanguage")){
      AnyLanguage(singleOptionAttribute(languageList, "default") map Language.fromString) +: ls
    }
    else ls
  }
}
