package org.openrewrite.java;

import org.openrewrite.java.tree.*;
import org.openrewrite.marker.Markers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static org.openrewrite.Tree.randomId;
import static org.openrewrite.java.tree.TypeUtils.findQualifiedJavaLangTypeName;

public class TypeUtils {

    private static final Map<String, String> CORE_JDK_CLASSES = Map.ofEntries(
        Map.entry("String", "java.lang.String"),
        Map.entry("Integer", "java.lang.Integer"),
        Map.entry("Boolean", "java.lang.Boolean"),
        Map.entry("Object", "java.lang.Object"),

        Map.entry("List", "java.util.List"),
        Map.entry("ArrayList", "java.util.ArrayList"),
        Map.entry("Set", "java.util.Set"),
        Map.entry("Map", "java.util.Map"),
        Map.entry("Collection", "java.util.Collection"),
        Map.entry("Optional", "java.util.Optional"),
        Map.entry("Stream", "java.util.stream.Stream"),

        Map.entry("LocalDate", "java.time.LocalDate"),
        Map.entry("LocalDateTime", "java.time.LocalDateTime"),
        Map.entry("Duration", "java.time.Duration"),

        Map.entry("File", "java.io.File"),
        Map.entry("Path", "java.nio.file.Path")
    );

    /**
     * Extracts from the return type expressed as FQN, the class name (e.g., List), import name (e.g., java.util.List)
     * or simply the Class name with the generic (e.g., List<String></String>)
     *
     *
     * @param fqn The fully qualified name (e.g., "java.util.List<String>").
     * @return The type name, including generic parameters or just the class name or import name.
     */
    public static String extractTypeName(String fqn, TypeFormat format) {
        if (fqn == null || fqn.isEmpty()) {
            return "";
        }

        if (format == TypeFormat.FULLY_QUALIFIED_NAME_WITH_GENERICS) {
            return fqn;
        }

        // 1. Handle Generics: Find the position of '<' (start of generic parameters)
        int genericIndex = fqn.indexOf('<');
        String baseName = fqn;
        String generics = "";

        if (genericIndex != -1) {
            generics = fqn.substring(genericIndex); // Captures <String>, <T>, etc.
            baseName = fqn.substring(0, genericIndex); // Captures only "java.util.List"
        }

        // 2. Find the position of the last dot (package separator)
        int lastDotIndex = baseName.lastIndexOf('.');

        String simpleName;

        if (lastDotIndex != -1) {
            // Extract the name after the last dot
            simpleName = baseName.substring(lastDotIndex + 1);
        } else {
            // No package separator found (already a simple name)
            simpleName = baseName;
        }

        // 3. Return the result based on requirements
        return switch (format) {
            case SIMPLE -> simpleName; // Return "List" from java.util.List<String>
            case GENERICS ->  generics.replaceAll("[<>]", ""); // Return the Class of the generics "String" from java.util.List<String>
            case CLASS_NAME_WITH_GENERICS -> simpleName + generics; // Return "List" + "<String>"
            case FULLY_QUALIFIED_NAME_WITHOUT_GENERICS -> baseName; // Return java.util.List;
            case FULLY_QUALIFIED_NAME_WITH_GENERICS -> fqn;
        };
    }

    public static TypeTree createTypeTree(String typeName) {
        int arrayIndex = typeName.lastIndexOf('[');
        if (arrayIndex != -1) {
            TypeTree elementType = createTypeTree(typeName.substring(0, arrayIndex));
            return new J.ArrayType(
                randomId(),
                Space.EMPTY,
                Markers.EMPTY,
                elementType,
                null,
                JLeftPadded.build(Space.EMPTY),
                new JavaType.Array(null, elementType.getType(), null)
            );
        }
        int genericsIndex = typeName.indexOf('<');
        if (genericsIndex != -1) {
            TypeTree rawType = createTypeTree(typeName.substring(0, genericsIndex));
            List<JRightPadded<Expression>> typeParameters = new ArrayList<>();
            for (String typeParam : typeName.substring(genericsIndex + 1, typeName.lastIndexOf('>')).split(",")) {
                typeParameters.add(JRightPadded.build((Expression) createTypeTree(typeParam.trim())));
            }
            return new J.ParameterizedType(
                randomId(),
                Space.EMPTY,
                Markers.EMPTY,
                rawType,
                JContainer.build(Space.EMPTY, typeParameters, Markers.EMPTY),
                new JavaType.Parameterized(null, (JavaType.FullyQualified) rawType.getType(), null)
            );
        }
        JavaType.Primitive type = JavaType.Primitive.fromKeyword(typeName);
        if (type != null) {
            return new J.Primitive(
                randomId(),
                Space.EMPTY,
                Markers.EMPTY,
                type
            );
        }
        if ("?".equals(typeName)) {
            return new J.Wildcard(
                randomId(),
                Space.EMPTY,
                Markers.EMPTY,
                null,
                null
            );
        }
        if (typeName.startsWith("?") && typeName.contains("extends")) {
            return new J.Wildcard(
                randomId(),
                Space.EMPTY,
                Markers.EMPTY,
                new JLeftPadded<>(Space.SINGLE_SPACE, J.Wildcard.Bound.Extends, Markers.EMPTY),
                createTypeTree(typeName.substring(typeName.indexOf("extends") + "extends".length() + 1).trim()).withPrefix(Space.SINGLE_SPACE)
            );
        }
        if (typeName.indexOf('.') == -1) {
            String javaLangType = findQualifiedJavaLangTypeName(typeName);
            if (javaLangType != null) {
                return new J.Identifier(
                    randomId(),
                    Space.EMPTY,
                    Markers.EMPTY,
                    emptyList(),
                    typeName,
                    JavaType.buildType(javaLangType),
                    null
                );
            }
        }
        TypeTree typeTree = TypeTree.build(typeName);
        // somehow the type attribution is incomplete, but `ChangeType` relies on this
        if (typeTree instanceof J.FieldAccess) {
            typeTree = ((J.FieldAccess) typeTree).withName(((J.FieldAccess) typeTree).getName().withType(typeTree.getType()));
        } else if (typeTree.getType() == null) {
            typeTree = ((J.Identifier) typeTree).withType(JavaType.ShallowClass.build(typeName));
        }
        return typeTree;
    }

    public static String convertGenericToFQNameClass(String generics) {
        String className = generics.replaceAll("[<>]", "");
        return getFQNameFromSimpleName(className).get();
    }

    /**
     * Try to resolve the simple class name to its JDK FQN.
     *
     * @param simpleName The simple class name (ex: "List").
     * @return The FQName if found (ex: "java.util.List"), otherwise Optional.empty().
     */
    public static Optional<String> getFQNameFromSimpleName(String simpleName) {
        if (simpleName == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(CORE_JDK_CLASSES.get(simpleName));
    }

}
