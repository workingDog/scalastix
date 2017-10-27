Change Log
==========

### changes in 0.6-SNAPSHOT  

* restructuring of directories
* remove examples and stixfiles to another repo
* updated scala, sbt, plugins and dependencies  

### changes in 0.5

* remove description from all Observables
* renamed x_custom to custom for all Observables
* added custom Format
* removed most theReads and theWrites and use Format instead

### changes in 0.4  

* added the missing hashes to ExternalReference
* removed toString from ExternalReference and GranularMarking

### changes in 0.3  

* bring back "org.threeten" % "threetenbp" instead of java.time, to cater for ZonedDateTime in scala and scala.js
* made cross compile for jvm and js
* add support for custom properties for SDO, SRO and StixObj, see also CustomProps

### changes in 0.2

* fixed a bug/typo in WindowPESectionType.`type`
* removed dependency on "org.threeten" % "threetenbp" % "1.3.4" (using java instead)
* remove all circe dependencies 
* add Play json dependency
* replace all circe json code with Play Json code

 

