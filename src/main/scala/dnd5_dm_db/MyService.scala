package dnd5_dm_db

import java.io.File

import akka.actor.{Actor, Props}
import dnd5_dm_db.lang.{Fr, Lang, langFromString}
import dnd5_dm_db.model.{Spell, Templates}
import dnd5_dm_db.xml_parse._
import sbt.PathFinder
import spray.http.MediaTypes._
import spray.httpx.marshalling.ToResponseMarshallable
import spray.routing._

import scala.xml.XML

object MyServiceActor {
  def props( root : String) =
    Props(new MyServiceActor( root))
}

// we don't implement our route structure directly in the service actor because
// we want to be able to test it independently, without having to spin up an actor
class MyServiceActor(val root : String) extends Actor with MyService {

  // the HttpService trait defines only one abstract member, which
  // connects the services environment to the enclosing actor or test
  def actorRefFactory = context

  // this actor only runs our route, but you could add
  // other things here, like request stream processing
  // or timeout handling
  def receive = runRoute(myRoute ~ getSpell ~ getFileRoute)

}


// this trait defines our service behavior independently from the service actor
trait MyService extends HttpService {


  val root : String
  val pathFinder : PathFinder = PathFinder(new File(root))
  import GenAll.{monsters, resources, spells}

  val myRoute : Route =
    path("") {
      get {
        respondWithMediaType(`text/html`) { // XML is marshalled to `text/xml` by default, so we simply override here
          complete {
          requestUri
            implicit val lang = Fr
            val monsterFilesFinder : PathFinder = pathFinder / monsters ** "*.xml"
            val spellFilesFinder : PathFinder = pathFinder / spells ** "*.xml"

            val frMonsterSeq = ParseSeq(monsterFilesFinder)(MonsterNameXmlParser, Fr)
            val frSpellSeq = ParseSeq(spellFilesFinder)(NameXmlParser, Fr)
            Templates.genIndex(frSpellSeq, frMonsterSeq)
          }
        }
      }
    }


  def makeXmlRoute(prefix : String)
                  ( makeResponse : (Lang, String) => ToResponseMarshallable) : Route =
    pathPrefix(prefix / Segment){
      langSegment =>
        pathPrefix(Segment){
          idSegment =>
            pathEnd {
              get {
                respondWithMediaType(`text/html`) {
                  complete {
                    try {
                      val lang = langFromString(langSegment)
                      makeResponse(lang, idSegment.replaceAllLiterally(".html", ".xml"))
                    } catch {
                      case _ : NoSuchElementException =>
                        "unknown lang"
                    }
                  }
                }
              }
            }
        }
    }

  val getSpell : Route =
    makeXmlRoute(spells){
      (l, id) =>
        try {
          val node = XML.loadFile(s"$resources/$spells/$id")
          val s = SpellXmlParser.fromXml(node)
          Spell.toHtml(s)(l)
        } catch {
          case e: Exception => s"$id unknown spell"
        }
    }


  def getFileRouteGen(name : String) : Route =
    pathPrefix(name){
      compressResponse() {
        getFromResourceDirectory(name)
      }
    }

  val getFileRoute : Route =
    getFileRouteGen("images") ~
    getFileRouteGen("css") ~
    getFileRouteGen("js")



}