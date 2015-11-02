package dnd5_dm_db

import dnd5_dm_db.lang.Lang
import dnd5_dm_db.model._
import dnd5_dm_db.xml_parse.{SpellXmlParser, MonsterXmlParser, MonsterNameXmlParser, NameXmlParser}
import xml_parse.Utils._

import scala.xml.{XML, Node}

/**
 * Created by lorilan on 10/30/15.
 */
trait IndexType

object IndexType{
  def fromString(str : String) : IndexType = str.toLowerCase match {
    case "monsters" => Monster
    case "spells" => Spell
    case _ => error("unexpected index type")
  }

}

object IndexXmlParser {

  def fromXml(node : Node) : Index = {

    val (n, it) = withoutEntries.fromXml(node)
    new Index(n, it,
      node \ "entry" map (singleAttribute(_, "id")))

  }

  val withoutEntries = new FromXml[(Local, IndexType)] {
    override def fromXml(node : Node): (Local, IndexType) =
      (NameXmlParser.fromXml(node),IndexType.fromString(singleAttribute(node, "entry-type")))
  }


}

class Index
( val name : Local,
  val typ : IndexType,
  val entries : Seq[String])

trait Formatter[T]{
  def apply(t : T)(implicit lang : Lang) : String
}


object IndexToHtml{

  val magicSchoolOrdering = new Ordering[MagicSchool] {
    override def compare(x: MagicSchool, y: MagicSchool): Int =
      Ordering.String.compare(x.toString, y.toString)
  }
  val magicSchoolFormatter = new Formatter[MagicSchool]{
   def apply(t : MagicSchool)(implicit lang : Lang) : String =
    lang magicSchool t
  }

  val challengeRankingOrdering = new Ordering[ChallengeRanking]{
    override def compare(x: ChallengeRanking, y: ChallengeRanking): Int =
      Ordering.Double.compare(x.value, y.value)
  }
  val challengeRankingFormatter = new Formatter[ChallengeRanking]{
    def apply(t : ChallengeRanking)(implicit lang : Lang) : String =
      s" $t (${t.xp}  ${lang.xp})"
  }


  def spellIndexToHtml(dir : String) =
    new IndexToHtml[MagicSchool](dir, "spellIndex", NameXmlParser,
    SpellXmlParser.magicSchool,magicSchoolOrdering,
      ("<div>", "</div><div>","</div>"), magicSchoolFormatter )

  def monsterIndexToHtml(dir : String) =
    new IndexToHtml[ChallengeRanking](dir, "monsterIndex", MonsterNameXmlParser,
      MonsterXmlParser.challengeRanking,challengeRankingOrdering,
      ("<div>", ", ","</div>"), challengeRankingFormatter)
}


class IndexToHtml[T]
( val dir : String,
  val htmlClass : String,
  val nameParser : FromXml[Local],
  val ordParser : FromXml[T],
  val ordering : Ordering[T],
  val sameOrderSeparator : (String,String, String),
  val formatter : Formatter[T])
  extends ToHtml[Index] {

    def filter(ids : Seq[String]) : (List[String], List[(Local, T)]) =
      ids.foldLeft((List[String](), List[(Local, T)]())){
        case ((fails, successes), id) =>
          try {
            val n = XML.loadFile(s"$dir/$id.xml")
            (fails, (nameParser.fromXml(n), ordParser.fromXml(n)):: successes)
          } catch {
            case e : Exception =>
              //println(id + "fails : "+ e.getMessage)
              (id :: fails, successes)
          }
      }

    private val (bs, ms, es) = sameOrderSeparator

    def toHtml(id : String, i : Index)(implicit lang : Lang)  : String = {
      
      val (fails, successes) = filter(i.entries)

      val sb  = new StringBuilder

      val ss : Seq[(T, List[(Local, T)])] = successes groupBy (_._2) toSeq

      ss.sortBy(_._1)(ordering) map {
        case ((o, l)) =>
          sb.append(l.map(_._1.value) mkString (bs,ms,es))
          sb.append(s"""<div class="order">${formatter(o)}</div>""")
      }
      
      s"""<div id="indexBloc" class="$htmlClass">
        |   <div class="index_successes">
        |   ${sb.toString()}
        |   </div>
        |   <div class="index_error">
        |       ${fails mkString ","}
        |   </div>
        |</div>""".stripMargin
    }


}
