
import sbt._, Keys._

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
      //"-Xfatal-warnings",
      "-Yno-adapted-args",
      "-Ywarn-dead-code",        // N.B. doesn't work well with the ??? hole
      "-Ywarn-numeric-widen",
      "-Ywarn-value-discard",
      "-Xfuture",
      "-Ywarn-unused-import"  // 2.11 only
    )


  lazy val root = (project in file(".")
      settings Seq[Setting[_]] (
        organization := "",
        name := "dnd5-dm-db",
        version := "no-version",
        scalaVersion := "2.11.7",
        sbtVersion := "0.13.8",

        resolvers += Resolver.url("typesafe-ivy-repo",
          url("http://typesafe.artifactoryonline.com/typesafe/releases"))(Resolver.ivyStylePatterns),

        libraryDependencies ++= Seq(
            "org.scalaz" %% "scalaz-core" % "7.2.0-M2",
            "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.4",
            "org.scala-lang.modules" %% "scala-xml" % "1.0.4",
            "org.scala-sbt" %% "io" % "0.13.8"
          ),
        scalacOptions ++= myScalacOptions


      )
    )
}
