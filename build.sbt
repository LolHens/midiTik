name := "midiTik"

version := "0.0.0"

scalaVersion := "2.12.3"

externalResolvers := Seq(
  Resolver.defaultLocal,
  "artifactory-maven" at "http://lolhens.no-ip.org/artifactory/maven-public/",
  Resolver.url("artifactory-ivy", url("http://lolhens.no-ip.org/artifactory/ivy-public/"))(Resolver.ivyStylePatterns)
)
