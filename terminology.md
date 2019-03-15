# Some words and phrases worth remembering

Programming in Scala
: book

Scala for the Impatient
: book

Parser Combinators

Delimited Continuations

Dynamic Invocation

Implicit Classes

Package Object

Companion Object

Scaladoc
: https://www.scala-lang.org/api/current/

Self Type
: indication that a trait requires another type, `this: Type => ...`, useful in cake pattern

Type Projection
: nested class objects access, e.g. `Network#Member`

Class Parameters

Object-private

Uniform Access Principle

Sbt Logging
: https://www.scala-sbt.org/release/docs/Howto-Logging.html

Chained Package

Import Selector Syntax

Early Definitions

Value Classes

Anonymous Subclasses

Structural Type
: duck typing equivalent, specification of type members; implemented using reflection

Universal Traits

Extractor

Lift method

Diamond Inheritance Problem

Default Methods

Linearization of the Class

Implicit Conversion

Case Class

Strongly Typed Language

Eta-expansion
: converts an expression of method type to an equivalent expression of function type
https://alvinalexander.com/scala/fp-book/how-to-use-scala-methods-like-functions

Closure

Java 8 Lambda Expressions

Java SAM Interface
: Single Abstract Method Interface, functional interface; pass Scala function literal to Java code

Control Abstractions
: call by name notation for `() => Unit`

Uniform Creation Principle

Uniform Return Type Principle

Streams

Lazy Views

Fork-Join Pool (execution context)

Partial Functions

Destructuring

View Bounds vs Context Bounds (types)
: implicit conversions vs implicit values

Existential Types
: formalism added for compatibility with Java wildcards, `typeExpr[T] forSome { type T ... }`

Singleton Type
: this.type, useful for method chaining or fluent interface

Compound Type
: aka intersection type, `T1 with T2 with T3 ...`

Cake Pattern
: self types implementing dependency injection
