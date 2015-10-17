import scala.xml.{NodeSeq, Node}

package object dnd5_dm_db {

  val root = "/home/lorilan/projects/dnd5_dm_db/"
  val resources = root + "src/main/resources/"
  val out = root + "out/"

  type Name = String
  type LangId = String

  type NLSeq[A] = Seq[(Name, LangId, A)]

  def error(msg : String) = sys.error(msg)

  def formatToHtml(str : String) : String =
    str.replaceAllLiterally("[","<").replaceAllLiterally("]",">")

}
