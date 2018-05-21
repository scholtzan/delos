# Detecting Log Statements in Source Code

> Note: This project is work in progress and currently not stable.

DeLoS is a Scala Compiler Plugin that extracts log statements from provided source code. 
The tool is intended for research purposes in the area of analysing and improving log statements.

## Features

- [x] Basic detection of log statements in Scala code
- [x] Provide support for testing inspectors 
- [ ] Simplify usage so that `build.sbt` does not need to be changed manually
- [ ] Improve output of results (support custom location, formatting, ...)
- [ ] Support more log libraries
    - [x] [SLF4J](https://www.slf4j.org/)/[scala-logging](https://github.com/lightbend/scala-logging)
    - [ ] [Journal](https://github.com/Verizon/journal)
    - [ ] Java logging libraries
- [ ] Provide statistics about log statement characteristics
    * Uniqueness
    * Average number of variables
    * Average number of strings
    * ...


## Usage

To use the plugin, the following needs to be added to `build.sbt`:

`addCompilerPlugin("net.scholtzan" %% "delos" % "0.0.1")`

## Supporting more log libraries

Currently only log statements of [SLF4J](https://www.slf4j.org/) are detected. 
To add support for more libraries a new `Inspector` class can be added to the `log` package. 
This class needs to extend `LogStatementInspector` and override the `inspect` method which traverses the abstract syntax tree (AST) of the source code files. 
Using Scalas pattern matching the AST is traversed and log statements are detected (see [Scala Reflection](https://docs.scala-lang.org/overviews/reflection/symbols-trees-types.html)). 
`SLF4JInspector` provides an exemplary implementation of a log statement inspector.
 
To test the compiler plugin, for each inspector a `InspectorSpec` can be added to `test`. 
These specs extend the trait `LogInspectorSpec` which takes care of running the compiler plugin against provided code and checks if the results are as expected.
`SLF4JInspectorSpec` provides some examples for such tests.




