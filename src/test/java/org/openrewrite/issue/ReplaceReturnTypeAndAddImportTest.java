package org.openrewrite.issue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openrewrite.ExecutionContext;
import org.openrewrite.Tree;
import org.openrewrite.java.*;
import org.openrewrite.java.tree.*;
import org.openrewrite.java.tree.TypeUtils;
import org.openrewrite.marker.Markers;
import org.openrewrite.test.RewriteTest;

import static java.util.Collections.singletonList;
import static org.openrewrite.java.Assertions.java;
import static org.openrewrite.java.TypeUtils.*;
import static org.openrewrite.test.RewriteTest.toRecipe;

class ReplaceReturnTypeAndAddImportTest implements RewriteTest {

    @Test
    @DisplayName("Replace the return type of a method and import the FQN.")
    void replaceReturnTypeAndAddImportUsingTest() {
        String newReturnType = "java.util.List<String>";
        String methodPattern = "Foo bar(..)";
        rewriteRun(
            spec -> spec.recipe(toRecipe(() -> new JavaIsoVisitor<>() {
                private final MethodMatcher methodMatcher = new MethodMatcher(methodPattern, false);
                @Override
                public J.MethodDeclaration visitMethodDeclaration(J.MethodDeclaration method, ExecutionContext ctx) {
                    J.MethodDeclaration m = super.visitMethodDeclaration(method, ctx);

                    if (methodMatcher.matches(m.getMethodType())) {
                        m = m.withReturnTypeExpression(
                            TypeTree.build(extractTypeName(newReturnType, TypeFormat.CLASS_NAME_WITH_GENERICS)));
                        doAfterVisit(new AddImport<>(extractTypeName(newReturnType, TypeFormat.FULLY_QUALIFIED_NAME_WITHOUT_GENERICS),
                            null, false));
                        return autoFormat(m, ctx);
                    }
                    return m;
                }
            })).expectedCyclesThatMakeChanges(1).cycles(1),
            java(
                """
                  class Foo {
                    Object bar() {
                        return null;
                    }
                  }
                  """, """
                import java.util.List;

                class Foo {
                    List<String> bar() {
                        return null;
                    }
                }
              """
            )
        );
    }

    @Test
    @DisplayName("Replace the return type of a method and import the FQN.")
    void replaceReturnTypeAndAddImportUsingParameterizedTypeTest() {
        String newReturnType = "java.util.List<String>";
        String methodPattern = "Foo bar(..)";
        rewriteRun(
            spec -> spec.recipe(toRecipe(() -> new JavaIsoVisitor<>() {
                final MethodMatcher methodMatcher = new MethodMatcher(methodPattern, false);

                @Override
                public J.MethodDeclaration visitMethodDeclaration(J.MethodDeclaration method, ExecutionContext ctx) {
                    J.MethodDeclaration m = super.visitMethodDeclaration(method, ctx);

                    if (methodMatcher.matches(m.getMethodType()) &&
                        !TypeUtils.isAssignableTo(newReturnType, m.getReturnTypeExpression().getType())) {
                        maybeAddImport(extractTypeName(newReturnType,TypeFormat.FULLY_QUALIFIED_NAME_WITHOUT_GENERICS));
                        m = m.withReturnTypeExpression(getParameterizedType());
                        return autoFormat(m, ctx);
                    }
                    return m;
                }

                /*
                   Method definition and import:

                   import java.util.list
                   List<String> bar() {}

                 */
                private J.ParameterizedType getParameterizedType() {
                    TypeTree typeTree = createTypeTree(extractTypeName(newReturnType,TypeFormat.FULLY_QUALIFIED_NAME_WITHOUT_GENERICS));
                    return new J.ParameterizedType(
                        Tree.randomId(),
                        Space.SINGLE_SPACE,
                        Markers.EMPTY,
                        // The NameTree clazz should be: "java.util.List" from "java.util.List<String>"
                        typeTree,
                        // The JContainer<org.openrewrite.java.tree.Expression> typeParameters should be "java.lang.String" from the generics of "java.util.List<String>"
                        JContainer.build(singletonList(JRightPadded.build(
                            TypeTree.build(convertGenericToFQNameClass(extractTypeName(newReturnType,TypeFormat.GENERICS)))))),
                        // The JavaType type should be: "java.util.List" from "java.util.List<String>"
                        JavaType.buildType(typeTree.getType().toString())
                    );
                }
            })).expectedCyclesThatMakeChanges(1).cycles(1),
            java(
                """
                  class Foo {
                    Object bar() {
                        return null;
                    }
                  }
                  """, """
                import java.util.List;

                class Foo {
                    List<java.lang.String> bar() {
                        return null;
                    }
                }
              """
            )
        );
    }
}