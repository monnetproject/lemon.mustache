Lemon Mustache grammar generation
=================================

This is a framework for generating grammars from _lemon_ lexica. The program consists of the following components:

 * `main`: The converter
 * `web`: The web interface to the converter

The converter requires the creation of two external files:

### SPARQL Extractor

SPARQL files consist of a file containing queries of the form

    id <<<
        SELECT ?x WHERE {
           ?x a owl:Class .
        }
    >>>

The form of the query is standard [SPARQL](http://www.w3.org/TR/rdf-sparql-query/)

### Mustache Generation

Generation is performed using [Mustache](http://mustache.github.com/) templates

The matches to a given query can be extracted using `{{#id}}` and the query variables are 
then available as normal variables.

    {{#id}}
       {{x}} is an OWL class
    {{/#id}}

Example grammars can be seen at [here](web/src/main/resources/)

