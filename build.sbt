enablePlugins(ScalaJSPlugin)

lazy val root = (project in file(".")).settings(
  name := "openhackfrontend",
  organization := "info.ditrapani",
  version := "0.0.1",
  scalaVersion := "2.12.6"
)

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding",
  "UTF-8",
  "-unchecked",
  "-Xlint",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Ywarn-unused",
  "-Ywarn-value-discard",
  "-Xfuture"
)

// This is an application with a main method
scalaJSUseMainModuleInitializer := true

libraryDependencies ++= Seq(
  "be.doeraene" %%% "scalajs-jquery" % "0.9.3",
  "fr.hmil" %%% "roshttp" % "2.1.0",
  "com.lihaoyi" %%% "scalatags" % "0.6.7",
  "org.scala-js" %%% "scalajs-dom" % "0.9.5",
  // test
  "org.mockito" % "mockito-core" % "2.18.3" % "test",
  "org.scalatest" %% "scalatest" % "3.0.5" % "test"
)

wartremoverWarnings ++= Warts.allBut(
  Wart.Equals,
  Wart.NonUnitStatements,
  Wart.ToString
)

skip in packageJSDependencies := false
jsDependencies += "org.webjars" % "jquery" % "3.2.1" / "3.2.1/jquery.js"
jsEnv := new org.scalajs.jsenv.jsdomnodejs.JSDOMNodeJSEnv()
scalafmtVersion in ThisBuild := "1.5.1"
