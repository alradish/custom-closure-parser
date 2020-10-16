package io.github.alrai

import io.github.alrai.visitors.UsedSymbolsVisitor
import org.junit.jupiter.api.*
import org.mozilla.javascript.CompilerEnvirons
import org.mozilla.javascript.Parser
import org.mozilla.javascript.ast.*

private typealias SymbolTable = Map<Scope, List<Name>>

class UsedSymbolsVisitorTest : AbstractParserTest() {
    @Test
    fun useInInit() {
        val root = parse(
            """
            var a = 2;
            var b = a;
            """.trimIndent()
        )
        containsSymbols(
            root,
            mapOf(
                null to listOf("a")
            )
        )
    }

    @Test
    fun assignment() {
        val root = parse(
            """
            var a = 2;
            a = 3;
        """.trimIndent()
        )
        containsSymbols(
            root,
            changed = mapOf(
                null to listOf("a")
            )
        )
    }

    @Test
    fun usageAndAssignment() {
        val root = parse(
            """
            var a = 2;
            function foo() {
                var b = a;
                b = b + b;
                return b;
            }
            a = 3;
        """.trimIndent()
        )
        containsSymbols(
            root,
            mapOf(
                "foo" to listOf("a", "b")
            ),
            mapOf(
                null to listOf("a"),
                "foo" to listOf("b")
            )
        )
    }

    @Test
    fun simpleTest() {
        val root = parse(
            """
            var a = 0;
            """.trimIndent()
        )
        containsSymbols(
            root,
            mapOf()
        )
    }

    @Test
    fun single() {
        val root = parse("var a = b + 1;")
        containsSymbols(
            root,
            mapOf(
                null to listOf("b")
            )
        )
    }

    @Test
    fun twoUsages() {
        val root = parse(
            """
            var a = b + b;
        """.trimIndent()
        )
        containsSymbols(
            root,
            mapOf(
                null to listOf("b")
            )
        )
    }


    @Test
    fun inParams() {
        val root = parse(
            """
            var a = 2
            function foo(x) {
                return a
            }
            var b = foo(a)
            """.trimIndent()
        )
        containsSymbols(
            root,
            mapOf(
                null to listOf("a", "foo"),
                "foo" to listOf("a")
            )
        )
    }

    @Test
    fun nested() {
        val root = parse(
            """
            var a = 2;
            var b = a + 43;
            function foo() {
                var fooa = 0;
                function bar(x) {
                    var bara = a
                    function foobar() {
                        return x + 1 
                    }
                    var barb = fooa - 2
                }
                var foob = 2 - fooa;
            }
            """.trimIndent()
        )
        containsSymbols(
            root,
            mapOf(
                null to listOf("a"),
                "foo" to listOf("fooa"),
                "bar" to listOf("a", "fooa"),
                "foobar" to listOf("x")
            )
        )
    }

    @Test
    fun test() {
        val root = parse(
            """
            var x = 10; var y = x + 1;
            function someFunction() { return y + 1; }
            function foo(a) {
              var b = 42;
              function bar(c) {
                return a + b + c;
              }
              return bar(24);
            }
        """.trimIndent()
        )
        containsSymbols(
            root,
            mapOf(
                null to listOf("x"),
                "someFunction" to listOf("y"),
                "bar" to listOf("a", "b", "c"),
                "foo" to listOf("bar")
            )
        )
    }

    private fun usedSymbols(root: AstRoot): SymbolTable {
        val visitor = UsedSymbolsVisitor()
        root.visit(visitor)
        return visitor.usedSymbol
    }

    private fun containsSymbols(
        root: AstRoot,
        symbols: Map<String?, List<String>> = emptyMap(),
        changed: Map<String?, List<String>> = emptyMap()
    ) {
        val (used, assigned) = UsedSymbolsVisitor()
            .runOn(root)
            .run { usedSymbol to changedSymbol }
        Assertions.assertEquals(symbols.size, used.size)
        Assertions.assertEquals(changed.size, assigned.size)
        check(symbols, used)
        check(changed, assigned)
    }

    private fun check(expected: Map<String?, List<String>>, actual: Map<Scope, List<Name>>) {
        for ((scope, names) in actual) {
            val list = when (scope) {
                is AstRoot -> expected[null]
                is FunctionNode -> expected[scope.functionName.identifier]
                is GeneratorExpression, is ArrayComprehension, is Loop, is LetNode -> TODO()
                else -> unreachable()
            } ?: error("Can't find symbols for ${scope.toSource()}")
            Assertions.assertEquals(list.size, names.size)
            assert(list.containsAll(names.map { it.identifier }))
        }
    }

}