## STIX 2.1 protocol in Scala

[1] "Structured Threat Information Expression (STIX™) is a language and serialization format used to exchange cyber threat intelligence (CTI). STIX enables organizations to share CTI with one another in a consistent and machine readable manner, allowing security communities to better understand what computer-based attacks they are most likely to see and to anticipate and/or respond to those attacks faster and more effectively. STIX is designed to improve many different capabilities, such as collaborative threat analysis, automated threat exchange, automated detection and response, and more."

This library **ScalaStix** is an API for serializing and de-serializing STIX 2.1 JSON content.
It includes all SDO, SRO, OpenVocab, Markings and supporting data types

## References
 
1) [STIX 2.1 Specification](https://docs.google.com/document/d/1yvqWaPPnPW-2NiVCLqzRszcx91ffMowfT5MmE9Nsy_w/edit)
   

## Dependencies

 [circe library](https://github.com/circe/circe)
 
 
 
### Conventions

The "type" parameter of the classes, is set to a constant with the required type name.

All SDO classes have their required parameters listed first, followed by the optionals. 

All SDO and SRO constructors start with their "id" parameter, if omitted, a random id with the correct type is generated.
 This is followed by the "created" and "modified" parameters, if omitted a timestamp now is generated for those.
 
 
## Usage

 
## Status

A place holder, work in progress, not yet functional.
