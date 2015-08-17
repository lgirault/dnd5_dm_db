package dnd5_dm_db

import java.io.File

import sbt.{PathFinder, IO}

import scala.xml.{Node, XML}

object Front {

  val root = "/home/lorilan/projects/dnd5-dm-db/"
  val resources = root + "src/main/resources/"
  val out = root + "out/"

  val monsters = "monsters"
  val spells = "spells"

  implicit def stringToFile(path : String) : File = new File(path)

  implicit val language = Fr



  def parseSeq[A]
  ( fileFinder : PathFinder)
  (implicit builder : FromXmlToHtml[A], lang : Lang)
  : Seq[(Name, LangId, A)] =
    fileFinder.getPaths map { path =>
      val f = XML.loadFile(path)
      try {
        val name = path.getName.stripSuffix(".xml")
        (name, lang.id, builder.fromXml(f))
      } catch {
        case e : Exception =>
          println(path)
          throw e
      }
    }




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



    val frSpellSeq = parseSeq(spellFilesFinder)(Spell, Fr)

    val m : Map[String, Spell] = frSpellSeq map {
      case (k, p, s) => (k, s)
    } toMap


    val frMonsterSeq = parseSeq(monsterFilesFinder)(Monster.fromXmlToHml(m), Fr)

    IO.createDirectories(List(spellOutDir, monsterOutDir))
    IO.copyDirectory(resources + "css/", out + "css/")
    IO.copyDirectory(resources + "js/", out + "js/")
    IO.copyDirectory(resources + "images/", out + "images/")
    val index : File = out + "index.html"

    index.createNewFile()

    genPages(frSpellSeq, spells)(Spell, Fr)


    genPages(frMonsterSeq, monsters)(Monster.fromXmlToHml(m), Fr)

    IO.write(index, Templates.index(frSpellSeq, frMonsterSeq))
  }
}
