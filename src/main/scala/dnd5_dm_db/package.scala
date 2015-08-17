import scala.xml.{NodeSeq, Node}

package object dnd5_dm_db {

  type Name = String
  type LangId = String


  def error(msg : String) = sys.error(msg)

  def singleOptionAttribute(node : Node, attr : String) : Option[String] =
    node attribute attr match {
      case Some(Seq(att)) => Some(att.text)
      case Some(Nil) | None => None
      case _ => error(s"one or zero attribute $attr expected in node " + node.toString())
    }

  def optionBooleanAttribute(node : Node, attr : String) : Boolean =
    singleOptionAttribute(node, attr) match {
      case Some(b) => b.toBoolean
      case None => false
    }

  def singleAttribute(node : Node, attr : String) : String =
    node attribute attr match {
      case Some(Seq(att)) => att.text
      case Some(atts) if atts.nonEmpty => error(s"only one $attr expected")
      case _ => error(s"attribute $attr expected in node " + node.toString())
    }

  implicit class NodeSeqOps(val ns : NodeSeq) extends AnyVal {
    def toNode : Node = {
      ns.theSeq match {
        case Seq(n) => n
        case _ => error("Should have only one node :" + ns)
      }
    }
    def singleText : String = toNode.text

    def int : Int = singleText.toInt

    def toNodeOption : Option[Node] = {
      ns.theSeq match {
        case Seq(n) => Some(n)
        case Nil => None
        case _ => error("Should have only one or zero node :" + ns.mkString(","))
      }
    }

    def textOption : Option[String] = toNodeOption map (_.text)

  }

  implicit def nodeSeqToString( ns : NodeSeq) : String =
    NodeSeqOps(ns).singleText

  implicit def nodeSeqToInt( ns : NodeSeq) : Int =
    NodeSeqOps(ns).int


  implicit def nodeSeqToStringOption( ns : NodeSeq) : Option[String] =
    NodeSeqOps(ns).textOption
}
