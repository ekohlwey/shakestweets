Shakestweets Generator
======================

This utility produces "fake" Twitter data by processing the Shakespeare's First Folio
corpus, which is available from Project Gutenberg.

The resulting Tweet corpus is useful for experimenting with data mining techniques
prior to the availability of real Twitter data. The techniques that will be valid
against both sets are going to be very conceptually simple, so this sort of data
is more for noobs. 

Building
--------

Building this utility is simple. From your console, type

```bash
mvn package
mvn assembly:single
```


Usage
-----

Once the jar is generated, you can run it with

```bash
java -jar target/shakes-twitter-generator-0.0.1-SNAPSHOT-jar-with-dependencies.jar > shakestweets
```

This will output the tweets into a file called shakestweets.
