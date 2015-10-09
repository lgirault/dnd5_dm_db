import scala.xml.{NodeSeq, Node}

package object dnd5_dm_db {

  type Name = String
  type LangId = String

  type NLSeq[A] = Seq[(Name, LangId, A)]

  def error(msg : String) = sys.error(msg)

  def formatToHtml(str : String) : String =
    str.replaceAllLiterally("[","<").replaceAllLiterally("]",">")

}
