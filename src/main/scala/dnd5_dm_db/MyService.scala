package dnd5_dm_db

import java.io.{FileNotFoundException, File}

import akka.actor.{Actor, Props}
import dnd5_dm_db.html_gen.{Templates, SpellHtmlGen, MonsterHtmlGen}
import dnd5_dm_db.lang.{Fr, langFromString}
import dnd5_dm_db.model._
import dnd5_dm_db.xml_parse._
import sbt.PathFinder
import spray.http.MediaTypes._
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
  def receive = runRoute( myRoute ~ getMonster ~ getSpell ~ getFileRoute )

}


// this trait defines our service behavior independently from the service actor
trait MyService extends HttpService {

  val root : String
  val pathFinder : PathFinder = PathFinder(new File(root))
  import GenAll.{monsters, resources, spells, traits, weapons}

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


  def makeXmlRoute[A](prefix : String, fromXml: FromXml[A], toHtml : ToHtml[A]) : Route =
    pathPrefix(prefix / Segment){
      langSegment =>
        path(Segment){
          idSegment =>
              get {
                respondWithMediaType(`text/html`) {
                  complete {
                    try {
                      val lang = langFromString(langSegment)
                      val id = idSegment.replaceAllLiterally(".html", ".xml")
                      val node = XML.loadFile(s"$resources/$prefix/$id")
                      toHtml.toHtml(id, fromXml.fromXml(node))(lang)

                    } catch {
                      case e : FileNotFoundException =>
                        e.getMessage
                      case _ : NoSuchElementException =>
                        "unknown lang"
                    }
                  }
                }
              }
        }
    }

  def fetchSpell : Retriever[Spell] = {
    id =>
      val node = XML.loadFile(s"$resources/$spells/$id.xml")
      SpellXmlParser.fromXml(node)
  }

  def fetchTrait : Retriever[Trait] = {
    id =>
      val node = XML.loadFile(s"$resources/$traits/$id.xml")
      TraitXmlParser.fromXml(node)
  }

  def fetchWeapon : Retriever[Weapon] = {
    id =>
      val node = XML.loadFile(s"$resources/$weapons/$id.xml")
      WeaponXmlParser.fromXml(node)
  }

  val getMonster : Route = makeXmlRoute(monsters, MonsterXmlParser.fromXml(fetchSpell, fetchTrait, fetchWeapon), MonsterHtmlGen)


  val getSpell : Route =
    makeXmlRoute(spells, SpellXmlParser, SpellHtmlGen)


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