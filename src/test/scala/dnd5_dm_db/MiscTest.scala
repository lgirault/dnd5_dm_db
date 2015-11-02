package dnd5_dm_db

import java.io.File

import dnd5_dm_db.model.{Weapon, Trait, Spell}
import dnd5_dm_db.xml_parse._
import sbt.PathFinder
import Constant._

/**
 * Created by lorilan on 10/29/15.
 */
class MiscTest extends AcceptanceSpec {

  val resources = new File(Settings.resourcesDir)
  println(Settings.resourcesDir)
  val spellFilesFinder : PathFinder = PathFinder(resources ) / spells ** "*.xml"
  val monsterFilesFinder : PathFinder = PathFinder(resources ) / monsters ** "*.xml"
  val traitsFileFinder : PathFinder = PathFinder(resources ) / traits ** "*.xml"
  val weaponsFileFinder : PathFinder = PathFinder(resources ) / weapons ** "*.xml"


  feature("misc tests"){

    scenario("default XP per CR") {
      val traitSeq = ParseSeq(traitsFileFinder)(TraitXmlParser)
      val weaponsSeq = ParseSeq(weaponsFileFinder)(WeaponXmlParser)
      val spellSeq = ParseSeq(spellFilesFinder)(SpellXmlParser)

      val spellsMap: Map[String, Spell] = spellSeq.toMap
      val traitsMap: Map[String, Trait] = traitSeq.toMap
      val weaponsMap: Map[String, Weapon] = weaponsSeq.toMap

      val monsterSeq =
        ParseSeq(monsterFilesFinder)(MonsterXmlParser.fromXml(spellsMap, traitsMap, weaponsMap))
      println(monsterSeq.length)

      assert(monsterSeq.forall {_ => true})
    }
  }
}
