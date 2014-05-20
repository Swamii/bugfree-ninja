name := """map"""

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  javaCore,  // The core Java API
  javaJdbc,  // Java database API
  javaJpa.exclude("org.hibernate.javax.persistence", "hibernate-jpa-2.0-api"),   // Java JPA plugin
  "org.hibernate" % "hibernate-entitymanager" % "4.3.0.Final",
  //filters,   // A set of built-in filters
  "org.hibernate.javax.persistence" % "hibernate-jpa-2.1-api" % "1.0.0.Draft-16",
  "org.postgresql" % "postgresql" % "9.3-1101-jdbc41",
  "org.apache.httpcomponents" % "httpclient" % "4.3.3",
  "commons-codec" % "commons-codec" % "1.9",
  "joda-time" % "joda-time" % "2.3",
  "org.jadira.usertype" % "usertype.core" % "3.1.0.GA",
  "org.jsoup" % "jsoup" % "1.7.3"
)

play.Project.playJavaSettings
