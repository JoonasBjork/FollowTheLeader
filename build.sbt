name := "OS2 Project"

version := "0.1"

scalaVersion := "2.13.5"

// Add dependency on ScalaFX library
libraryDependencies += "org.scalafx" %% "scalafx" % "14-R19"

libraryDependencies += "org.scala-lang.modules" %% "scala-swing" % "3.0.0"

libraryDependencies += "org.scalafx" %% "scalafx-extras" % "0.3.6"

libraryDependencies += "org.scalatest" %% "scalatest-flatspec" % "3.2.5" % "test"

libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.5"

scapegoatVersion in ThisBuild := "1.4.8"

// Determine OS version of JavaFX binaries
lazy val osName = System.getProperty("os.name") match {
case n if n.startsWith("Linux") => "linux"
case n if n.startsWith("Mac") => "mac"
case n if n.startsWith("Windows") => "win"
case _ => throw new Exception("Unknown platform!")
}

// Add JavaFX dependencies, as these are required by ScalaFx
lazy val javaFXModules = Seq("base", "controls", "fxml", "graphics", "media", "swing", "web")
libraryDependencies ++= javaFXModules.map( m=>
"org.openjfx" % s"javafx-$m" % "14.0.1" classifier osName
)
