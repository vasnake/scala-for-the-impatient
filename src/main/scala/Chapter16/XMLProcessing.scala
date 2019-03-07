package Chapter16

object XMLProcessing {
// topics:
    // XML literals
    // XML nodes
    // element attributes
    // embedded expressions
    // expressions in attributes
    // uncommon node types
    // XPath-like expressions
    // pattern matching
    // modifying elements and attributes
    // transforming XML
    // loading and saving
    // namespaces

    // built-in support for XML in scala
    // https://www.scala-lang.org/files/archive/api/current/scala-xml/scala/xml/index.html

    //NodeSeq type '<like>this</like>
    // scala code inside xml literals
    // node.child: yilds child nodes
    // node attributes yields metadata object
    // '\' and '\\' carry out xpath-like matches
    // match node with xml literals in 'case' clauses
    // RuleTransformer, RewriteRule to transform subnodes
    // java xml methods for loading/saving
    // ConstructingParser preserves comments and CDATA

    // XML literals
    def XMLLiterals = {
        ???
    }

    // XML nodes
    def XMLNodes = {
        ???
    }

    // element attributes
    def elementAttributes = {
        ???
    }

    // embedded expressions
    def embeddedExpressions = {
        ???
    }

    // expressions in attributes
    def expressionsInAttributes = {
        ???
    }

    // uncommon node types
    def uncommonNodeTypes = {
        ???
    }

    // XPath-like expressions
    def XPathLikeExpressions = {
        ???
    }

    // pattern matching
    def patternMatching = {
        ???
    }

    // modifying elements and attributes
    def modifyingElementsAndAttributes = {
        ???
    }

    // transforming XML
    def transformingXML = {
        ???
    }

    // loading and saving
    def loadingAndSaving = {
        ???
    }

    // namespaces
    def namespaces = {
        ???
    }

}

object XMLProcessing_Exercises {

    // 1. What is <fred/>(0)? <fred/>(0)(0)? Why?
    def ex1 = {
        ???
    }

    // 2. What is the result of
    //Click here to view code image
    //<ul>
    //<li>Opening
    // bracket: [</li>
    //<li>Closing
    // bracket: ]</li>
    //<li>Opening
    // brace: {</li>
    //<li>Closing
    // brace: }</li>
    //</ul>
    //How do you fix it?
    def ex2 = {
        ???
    }

    // 3. Contrast
    //Click here to view code image
    //<li>Fred</li> match { case <li>{Text(t)}</li> => t }
    //and
    //Click here to view code image
    //<li>{"Fred"}</li> match { case <li>{Text(t)}</li> => t }
    //Why do they act differently?
    def ex3 = {
        ???
    }

    // 4. Read an XHTML file and print all img elements that don’t have an alt attribute.
    def ex4 = {
        ???
    }

    // 5. Print the names of all images in an XHTML file. That is, print all src attribute values inside
    //img elements.
    def ex5 = {
        ???
    }

    // 6. Read an XHTML file and print a table of all hyperlinks in the file, together with their URLs.
    //That is, print the child text and the href attribute of each a element.
    def ex6 = {
        ???
    }

    // 7. Write a function that has a parameter of type Map[String, String] and returns a dl
    //element with a dt for each key and dd for each value. For example,
    //Click here to view code image
    //Map("A" -> "1", "B" -> "2")
    //should yield <dl><dt>A</dt><dd>1</dd><dt>B</dt><dd>2</dd></dl>.
    def ex7 = {
        ???
    }

    // 8. Write a function that takes a dl element and turns it into a Map[String, String]. This
    //function should be the inverse of the function in the preceding exercise, provided all dt
    //children are distinct.
    def ex8 = {
        ???
    }

    // 9. Transform an XHTML document by adding an alt="TODO" attribute to all img elements
    //without an alt attribute, preserving everything else.
    def ex9 = {
        ???
    }

    // 10. Write a function that reads an XHTML document, carries out the transformation of the
    //preceding exercise, and saves the result. Be sure to preserve the DTD and any CDATA sections.
    def ex10 = {
        ???
    }

}
