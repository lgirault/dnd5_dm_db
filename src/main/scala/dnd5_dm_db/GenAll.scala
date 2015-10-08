package dnd5_dm_db

import java.io.File

import dnd5_dm_db.lang.{Fr, Lang}
import dnd5_dm_db.model._
import dnd5_dm_db.xml_parse._
import sbt.{PathFinder, IO}

object GenAll {

  val root = "/home/lorilan/projects/dnd5_dm_db/"
  val resources = root + "src/main/resources/"
  val out = root + "out/"

  val monsters = "monsters"
  val spells = "spells"
  val traits = "traits"
  val weapons = "weapons"

  implicit def stringToFile(path : String) : File = new File(path)

  implicit val language = Fr


  def nlSeqToMap[A](nls : NLSeq[A]) : Map[String, A] =
    nls map { case (k, p, s) => (k, s) } toMap

  def genPages[A]
  ( keySeq : Seq[(Name, LangId, A)], typ : String)
  ( implicit builder : ToHtml[A], lang : Lang) =
  keySeq.foreach {
    case (n, l, s) =>
      IO.write(new File(s"$out/$typ/$l/$n.html"), builder.toHtml(s))
  }

  def main(args : Array[String]): Unit = {


    val spellOutDir : File = out + spells
    val monsterOutDir : File = out + monsters

    val spellFilesFinder : PathFinder = PathFinder(resources ) / spells ** "*.xml"
    val monsterFilesFinder : PathFinder = PathFinder(resources ) / monsters ** "*.xml"
    val traitsFileFinder : PathFinder = PathFinder(resources ) / traits ** "*.xml"
    val weaponsFileFinder : PathFinder = PathFinder(resources ) / weapons ** "*.xml"

    val frTraitSeq = ParseSeq(traitsFileFinder)(TraitXmlParser, Fr)
    val frWeaponsSeq = ParseSeq(weaponsFileFinder)(WeaponXmlParser, Fr)
    val frSpellSeq = ParseSeq(spellFilesFinder)(SpellXmlParser, Fr)

    val spellsMap : Map[String, Spell] = nlSeqToMap(frSpellSeq)
    val traitsMap : Map[String, Trait] = nlSeqToMap(frTraitSeq)
    val weaponsMap : Map[String, Weapon] = nlSeqToMap(frWeaponsSeq)

    val frMonsterSeq =
      ParseSeq(monsterFilesFinder)(MonsterXmlParser.fromXml(spellsMap, traitsMap, weaponsMap), Fr)

    IO.createDirectories(List(spellOutDir, monsterOutDir))
    IO.copyDirectory(resources + "css/", out + "css/")
    IO.copyDirectory(resources + "js/", out + "js/")
    IO.copyDirectory(resources + "images/", out + "images/")
    val index : File = out + "index.html"

    index.createNewFile()

    genPages(frSpellSeq, spells)(Spell, Fr)


    genPages(frMonsterSeq, monsters)(MonsterHtmlGen.toHtml(spellsMap), Fr)

    import Templates._
    IO.write(index, genIndex(frSpellSeq, frMonsterSeq))
  }
}
