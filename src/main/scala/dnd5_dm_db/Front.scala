package dnd5_dm_db

import java.io.File

import dnd5_dm_db.lang.{Fr, Lang}
import sbt.{PathFinder, IO}

import scala.xml.{Node, XML}

object Front {

  val root = "/home/lorilan/projects/dnd5_dm_db/"
  val resources = root + "src/main/resources/"
  val out = root + "out/"

  val monsters = "monsters"
  val spells = "spells"
  val traits = "traits"

  implicit def stringToFile(path : String) : File = new File(path)

  implicit val language = Fr



  def parseSeq[A]
  ( fileFinder : PathFinder)
  (implicit builder : FromXmlToHtml[A], lang : Lang)
  : NLSeq[A] =
    fileFinder.getPaths map { path =>
      try {
        val f = XML.loadFile(path)
        val name = path.getName.stripSuffix(".xml")
        (name, lang.id, builder.fromXml(f))
      } catch {
        case e : Exception =>
          println(path)
          throw e
      }
    }

  def nlSeqToMap[A](nls : NLSeq[A]) : Map[String, A] =
    nls map { case (k, p, s) => (k, s) } toMap

  def genPages[A]
  ( keySeq : Seq[(Name, LangId, A)], typ : String)
  ( implicit builder : FromXmlToHtml[A], lang : Lang) =
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

    val frTraitSeq = parseSeq(traitsFileFinder)(Trait, Fr)

    val frSpellSeq = parseSeq(spellFilesFinder)(Spell, Fr)

    val m : Map[String, Spell] = nlSeqToMap(frSpellSeq)
    val traitsMap : Map[String, Trait] = nlSeqToMap(frTraitSeq)


    val frMonsterSeq =
      parseSeq(monsterFilesFinder)(Monster.fromXmlToHml(m, traitsMap), Fr)

    IO.createDirectories(List(spellOutDir, monsterOutDir))
    IO.copyDirectory(resources + "css/", out + "css/")
    IO.copyDirectory(resources + "js/", out + "js/")
    IO.copyDirectory(resources + "images/", out + "images/")
    val index : File = out + "index.html"

    index.createNewFile()

    genPages(frSpellSeq, spells)(Spell, Fr)


    genPages(frMonsterSeq, monsters)(Monster.fromXmlToHml(m, traitsMap), Fr)

    IO.write(index, Templates.index(frSpellSeq, frMonsterSeq))
  }
}
