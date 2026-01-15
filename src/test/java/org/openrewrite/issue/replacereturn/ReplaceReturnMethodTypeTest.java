package org.openrewrite.issue.replacereturn;

import org.junit.jupiter.api.Test;
import org.openrewrite.ExecutionContext;
import org.openrewrite.java.JavaVisitor;
import org.openrewrite.java.tree.J;
import org.openrewrite.java.tree.TypeTree;
import org.openrewrite.test.RewriteTest;

import static java.util.Collections.emptyList;
import static org.openrewrite.java.Assertions.java;
import static org.openrewrite.test.RewriteTest.toRecipe;

public class ReplaceReturnMethodTypeTest implements RewriteTest {

    @Test
    void replaceReturnType() {
        rewriteRun(
            spec -> spec.recipe(toRecipe(() -> new JavaVisitor<>() {
                @Override
                public J visitMethodDeclaration(J.MethodDeclaration m, ExecutionContext ctx) {
                    if (!m.isAbstract()) {
                        return m;
                    }

                    m = m.withModifiers(emptyList()).withReturnTypeExpression(TypeTree.build("StringBuilder"));
                    return m;
                }
            })).expectedCyclesThatMakeChanges(1).cycles(1),
            java(
                """
                    abstract class Test {
                        abstract String test();
                    }
                    """,
                """
                    abstract class Test {
                        StringBuilder test();
                    }
                    """
            )
        );
    }
}
