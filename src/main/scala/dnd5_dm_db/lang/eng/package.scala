package dnd5_dm_db.lang

package object eng {
  def plural(i : Int) =
    if(i>1) "s"
    else ""

}
