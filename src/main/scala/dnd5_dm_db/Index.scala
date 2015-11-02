package dnd5_dm_db

import dnd5_dm_db.html_gen.Templates
import dnd5_dm_db.html_gen.Templates.NameExtractor
import dnd5_dm_db.lang.Lang
import dnd5_dm_db.model._
import dnd5_dm_db.xml_parse.{SpellXmlParser, MonsterXmlParser, MonsterNameXmlParser, NameXmlParser}
import xml_parse.Utils._

import scala.xml.{XML, Node}

/**
 * Created by lorilan on 10/30/15.
 */
trait IndexType {
  val id : String
  lazy val htmlClass = id+"Index"
}

object IndexType{
  def fromString(str : String) : IndexType = str.toLowerCase match {
    case Constant.monsters => Monster
    case Constant.spells => Spell
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

trait IndexCriteria[T]{
  val parser : FromXml[T]
  val ordering : Ordering[T]
  val formatter : Formatter[T]
}

object MagicSchoolIndex extends IndexCriteria[MagicSchool] {
  override val parser: FromXml[MagicSchool] = SpellXmlParser.magicSchool
  override val formatter: Formatter[MagicSchool] = new Formatter[MagicSchool]{
    def apply(t : MagicSchool)(implicit lang : Lang) : String =
      lang magicSchool t
  }
  override val ordering: Ordering[MagicSchool] = new Ordering[MagicSchool] {
    override def compare(x: MagicSchool, y: MagicSchool): Int =
      Ordering.String.compare(x.toString, y.toString)
  }
}

object SpellLevelIndex extends IndexCriteria[Int] {
  override val parser: FromXml[Int] = SpellXmlParser.level
  override val formatter: Formatter[Int] = new Formatter[Int]{
    def apply(t : Int)(implicit lang : Lang) : String =
      s"${lang.level} $t"
  }
  override val ordering: Ordering[Int] = Ordering.Int
}

object ChallengeRankingIndex extends IndexCriteria[ChallengeRanking] {
  override val parser: FromXml[ChallengeRanking] =
    MonsterXmlParser.challengeRanking
  override val formatter: Formatter[ChallengeRanking] =
    new Formatter[ChallengeRanking]{
    def apply(t : ChallengeRanking)(implicit lang : Lang) : String =
      s" $t (${t.xp}  ${lang.xp})"
  }
  override val ordering: Ordering[ChallengeRanking] =
    new Ordering[ChallengeRanking]{
    override def compare(x: ChallengeRanking, y: ChallengeRanking): Int =
      Ordering.Double.compare(x.value, y.value)
  }
}
object IndexToHtml{

  def spellIndexToHtmlOrderedByMagicSchool(dir : String) =
    new IndexToHtml[MagicSchool](dir, Spell, NameXmlParser,
      ("<div>", "</div><div>","</div>"), Before, MagicSchoolIndex)

  def spellIndexToHtmlOrderedByLevel(dir : String) =
    new IndexToHtml[Int](dir, Spell, NameXmlParser,
      ("<div>", "</div><div>","</div>"), Before, SpellLevelIndex)

  def monsterIndexToHtml(dir : String) =
    new IndexToHtml[ChallengeRanking](dir,
        Monster,
        MonsterNameXmlParser,
        ("""<div class="itemList">""", ", ","</div>"), Before,
      ChallengeRankingIndex)
}

sealed abstract class Place {
  def apply(a : => Unit, b : =>Unit) : Unit
}
object Before extends Place {
  def apply(a : => Unit, b : =>Unit) : Unit ={
    a;b
  }
}
object After extends Place{
  def apply(a : => Unit, b : =>Unit) : Unit ={
    b;a
  }
}
object Absent extends Place{
  def apply(a : => Unit, b : =>Unit) : Unit ={
    b
  }
}


class IndexToHtml[T]
( val dir : String,
  val indexType : IndexType,
  val nameParser : FromXml[Local],
  val sameOrderSeparator : (String, String, String),
  val orderPlace : Place,
  val criteria : IndexCriteria[T])
  extends ToHtml[Index] {

    type IndexEntry = (ItemId, (Local, T))
    def getCriteria( e : IndexEntry) : T = e._2._2
    def getName(e : IndexEntry) : Local = getName0(e._2)
    implicit val getName0 : NameExtractor[(Local, T)]= e => e._1

    def filter(ids : Seq[ItemId]) : (List[ItemId], List[IndexEntry]) =
      ids.foldLeft((List[ItemId](), List[IndexEntry]())){
        case ((fails, successes), id) =>
          try {
            val n = XML.loadFile(s"$dir/$id.xml")
            (fails, (id, (nameParser.fromXml(n), criteria.parser.fromXml(n))) :: successes)
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

      val ss : Seq[(T, List[IndexEntry])] = successes groupBy getCriteria toSeq

      ss.sortBy(_._1)(criteria.ordering) foreach {
        case ((o, l0)) =>

          val l = l0.sortBy(getName(_).value)
          sb.append("""<div class="orderClass">""")
          orderPlace(sb.append(s"""<div class="orderCriteria">${criteria.formatter(o)}</div>"""),
            sb.append(l.map( Templates.menuItem(indexType.id, _)) mkString (bs,ms,es)))
          sb.append("""</div>""")
      }
      
      s"""<div id="indexBloc" class="${indexType.htmlClass}">
        |   <h1>${i.name.value}</h1>
        |   <div class="index_successes">
        |   ${sb.toString()}
        |   </div>
        |   <div class="index_error">
        |       ${fails mkString ","}
        |   </div>
        |</div>""".stripMargin
    }


}
