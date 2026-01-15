package org.openrewrite.java;

public enum TypeFormat {
    // Return the simple name of the Class (ex: List)
    SIMPLE,
    // Return the generics (ex: <String>)
    GENERICS,
    // Return the name of the class and its generic (ex: List<String>)
    CLASS_NAME_WITH_GENERICS,
    // Return the FQName but without the generic (ex: java.util.List)
    FULLY_QUALIFIED_NAME_WITHOUT_GENERICS,
    // Return the FQName with the generic (ex: java.util.List)
    FULLY_QUALIFIED_NAME_WITH_GENERICS
}
