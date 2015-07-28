package dnd5_dm_db

import java.io.File

import sbt.{PathFinder, IO}

import scala.xml.XML

object Front {

  val root = "/home/lorilan/projects/dnd5-dm-db/"
  val resources = root + "src/main/resources/"
  val out = root + "out/"

  val monsters = "monsters"
  val spells = "spells"

  implicit def stringToFile(path : String) : File = new File(path)

  val magicSchools = Map(
    "abjuration" -> "abjuration",
    "conjuration" -> "invocation",
    "divination" -> "divination",
    "enchantment" -> "enchantement",
    "evocation" -> "évocation",
    "illusion" -> "illusion",
    "necromancy" -> "nécromancie",
    "transmutation" -> "transmutation"
  )

  val skills = Map(
    "Athletics" -> "Athlétisme",
    "Acrobatics" -> "Acrobaties",
    "Sleight of Hand" -> "Escamotage",
    "Stealth" -> "Discrétion",
    "Arcana" -> "Arcanes",
    "History" -> "Hitoire",
    "Investigation" -> "Investigation",
    "Nature" -> "Nature",
    "Religion" -> "Religion",
    "Animal Handling" -> "Dressage",
    "Insight" -> "Intuition",
    "Medicine" -> "Médecine",
    "Perception" -> "Perception",
    "Survival" -> "Survie",
    "Deception" -> "Tromperie",
    "Intimidation" -> "Intimidation",
    "Performance" -> "Représentation",
    "Persuasion" -> "Persuasion"
  )

  implicit val language = Fr

  def main(args : Array[String]): Unit = {


    val spellOutDir : File = out + spells
    val monsterOutDir : File = out + monsters

    val spellFilesFinder : PathFinder = PathFinder(resources ) / spells ** "*.xml"
    val monsterFilesFinder : PathFinder = PathFinder(resources ) / monsters ** "*.xml"


    val spellSeq = spellFilesFinder.getPaths map { path =>

      val f = XML.loadFile(path)
      try {
        (s"$spells/${path.getName.stripSuffix("xml")}html",
          Spell.fromXml(f))
      }catch {
        case e : Exception =>
          println(path)
          throw e
      }
    }

    IO.createDirectories(List(spellOutDir, monsterOutDir))
    IO.copyDirectory(resources + "css/", out + "css/")
    IO.copyDirectory(resources + "js/", out + "js/")
    IO.copyDirectory(resources + "images/", out + "images/")
    val index : File = out + "index.html"

    index.createNewFile()

    spellSeq.foreach {
      case (k, s) =>
        IO.write(new File(out + k), Spell.toHtml(s))
    }
    IO.write(index, Templates.index(spellSeq))
  }
}
