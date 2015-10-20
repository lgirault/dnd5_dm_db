package dnd5_dm_db

import java.io.File

import dnd5_dm_db.html_gen.{Templates, MonsterHtmlGen, SpellHtmlGen}
import dnd5_dm_db.lang.{Fr, Lang}
import dnd5_dm_db.model._
import dnd5_dm_db.xml_parse._
import sbt.{PathFinder, IO}
import Templates._

object GenAll {


  implicit def stringToFile(path : String) : File = new File(path)

  implicit val monsterGen = MonsterHtmlGen
  implicit val spellGen = SpellHtmlGen


  import Settings.{genAllOutDir => out}
  val resources = new File(Settings.resourcesDir)

  def genPages[A]
  ( keySeq : Seq[(Name, A)], typ : String)
  ( implicit builder : ToHtml[A], lang : Lang) =
  keySeq.foreach {
    case (n, s) =>
      IO.write(new File(s"$out/$typ/${lang.id}/$n.html"), builder.toHtml(n, s))
  }

  def main(args : Array[String]): Unit = {


    val spellOutDir : File = out + spells
    val monsterOutDir : File = out + monsters

    val spellFilesFinder : PathFinder = PathFinder(resources ) / spells ** "*.xml"
    val monsterFilesFinder : PathFinder = PathFinder(resources ) / monsters ** "*.xml"
    val traitsFileFinder : PathFinder = PathFinder(resources ) / traits ** "*.xml"
    val weaponsFileFinder : PathFinder = PathFinder(resources ) / weapons ** "*.xml"

    val traitSeq = ParseSeq(traitsFileFinder)(TraitXmlParser)
    val weaponsSeq = ParseSeq(weaponsFileFinder)(WeaponXmlParser)
    val spellSeq = ParseSeq(spellFilesFinder)(SpellXmlParser)

    val spellsMap : Map[String, Spell] = spellSeq.toMap
    val traitsMap : Map[String, Trait] = traitSeq.toMap
    val weaponsMap : Map[String, Weapon] = weaponsSeq.toMap

    IO.createDirectories(List(spellOutDir, monsterOutDir))
    IO.copyDirectory(resources + "css/", out + "css/")
    IO.copyDirectory(resources + "js/", out + "js/")
    IO.copyDirectory(resources + "images/", out + "images/")




    lang.locales.foreach { l =>
      val dir: File = out + l.id
      dir.mkdir()
      val menu : File = dir + "/menu.html"
      val index : File = dir + "/index.html"
      index.createNewFile()
      implicit val language = l
      IO.write(index, genIndex(l))

      val monsterSeq =
        ParseSeq(monsterFilesFinder)(MonsterXmlParser.fromXml(spellsMap, traitsMap, weaponsMap))

      IO.write(menu, genMenu(Templates.spells, spellSeq))
      IO.write(menu, genMenu(Templates.monsters, monsterSeq), append = true)

      genPages(spellSeq, spells)
      genPages(monsterSeq, monsters)

    }

  }
}
