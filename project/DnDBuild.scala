
import java.io.FileWriter

import sbt._, Keys._
import com.typesafe.sbt.packager.archetypes.JavaAppPackaging

object DnDBuild extends Build {

  val myScalacOptions = Seq(
      "-deprecation",
      "-encoding", "UTF-8",       // yes, this is 2 args
      "-feature",
      "-language:existentials",
      "-language:higherKinds",
      "-language:implicitConversions",
      "-language:postfixOps",
      "-unchecked",
      "-Xlint",
      //"-Ylog-classpath",
      //"-Xfatal-warnings",
      "-Yno-adapted-args",
      "-Ywarn-dead-code",        // N.B. doesn't work well with the ??? hole
      "-Ywarn-numeric-widen",
      "-Ywarn-value-discard",
      "-Xfuture",
      "-Ywarn-unused-import"  // 2.11 only
    )

  val classPathFileName = settingKey[String]("Location of generated classpath script")
  val printClassPathFile = taskKey[File]("create a file containing the fullclass path")


  def varDecl(varName : String) = sys.env get "SHELL" match {
    case None => sys.error("env var SHELL does not exists")
      // let's assume we're working with windows
      //s"SET $varName=" //then cp separator is ; but even then it's not working toto ...
    case Some(name) if name endsWith "fish" =>
      s"set $varName "
    case _ =>
      varName+"="
  }


  val akkaV = "2.3.9" //"2.4-M3"
  val sprayV = "1.3.3"
  val scalaTestV = "2.2.1"

  import JavaAppPackaging.autoImport.batScriptExtraDefines

  lazy val root = (project in file(".")

      enablePlugins JavaAppPackaging

      settings Seq[Setting[_]] (
        organization := "",
        name := "dnd5-dm-db",
        version := "0.21-alph",
        scalaVersion := "2.11.7",
        sbtVersion := "0.13.9",

        mainClass in Compile := Some("dnd5_dm_db.Server"),

        resolvers += Resolver.url("typesafe-ivy-repo",
          url("https://repo.typesafe.com/typesafe/ivy-releases"))(Resolver.ivyStylePatterns),

        libraryDependencies ++= Seq(
            "org.scala-lang"          % "scala-reflect" % "2.11.7",

//            "org.scalaz"              %% "scalaz-core" % "7.2.0-M2",
            "org.scalatest"           %% "scalatest"                % scalaTestV,
            "org.scala-lang.modules"  %% "scala-parser-combinators" % "1.0.4",
            "org.scala-lang.modules"  %% "scala-xml" % "1.0.4",
            "org.scala-sbt"           %% "io" % "0.13.9",


            "io.spray"                %%  "spray-can"     % sprayV,
            "io.spray"                %%  "spray-routing" % sprayV,
            "io.spray"                %%  "spray-testkit" % sprayV  % "test",
            "com.typesafe.akka"       %%  "akka-actor"    % akkaV,
            "com.typesafe.akka"       %%  "akka-testkit"  % akkaV   % "test"
          ),
        scalacOptions ++= myScalacOptions,

        classPathFileName := "CLASSPATH.bat",
        printClassPathFile := {
          val f = baseDirectory.value / "target" / classPathFileName.value

          val writter = new FileWriter(f)
          val fcp = (fullClasspath in Compile).value.map(_.data.absolutePath)
          writter.write(fcp.mkString(varDecl("CLASSPATH"), ":", ""))
          writter.close()
          f
        },

        batScriptExtraDefines += """set _APT_ARGS=!_APT_ARGS! resourcesDir=%DND5_DM_DB_HOME%"""

      )
    )
}
