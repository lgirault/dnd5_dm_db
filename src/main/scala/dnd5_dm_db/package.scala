package object dnd5_dm_db {

  type ItemId = String
  type LangId = String

  def error(msg : String) = sys.error(msg)

  def formatToHtml(str : String) : String =
    str.replaceAllLiterally("[","<").replaceAllLiterally("]",">")

}
