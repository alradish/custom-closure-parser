package io.github.alrai

import io.github.alrai.visitors.UsedSymbolsVisitor
import org.junit.jupiter.api.*
import org.mozilla.javascript.CompilerEnvirons
import org.mozilla.javascript.Parser
import org.mozilla.javascript.ast.*

private typealias SymbolTable = Map<Scope, List<Name>>

class UsedSymbolsVisitorTest {
    private var environment = CompilerEnvirons()
    private var parser = Parser(environment)

    @Test
    fun simpleTest() {
        val root = parse(
            """
            var a = 0;
        """.trimIndent()
        )
        containsSymbols(
            usedSymbols(root),
            mapOf()
        )
    }

    @Test
    fun single() {
        val root = parse("var a = b + 1;")
        containsSymbols(
            usedSymbols(root),
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
            usedSymbols(root),
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
            usedSymbols(root),
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
              };
              return bar(24);
            };

        """.trimIndent()
        )
        containsSymbols(
            usedSymbols(root),
            mapOf(
                null to listOf("x"),
                "someFunction" to listOf("y"),
                "bar" to listOf("a", "b", "c"),
                "foo" to listOf("bar")
            )
        )
    }


    private fun parse(code: String): AstRoot =
        parser.parse(code, null, 0) ?: fail("Parser return null")


    private fun usedSymbols(root: AstRoot): SymbolTable {
        val visitor = UsedSymbolsVisitor()
        root.visit(visitor)
        return visitor.usedSymbols
    }

    private fun containsSymbols(table: SymbolTable, symbols: Map<String?, List<String>>) {
        Assertions.assertEquals(symbols.size, table.size)
        for ((scope, names) in table) {
            val list = when (scope) {
                is AstRoot -> symbols[null]
                is FunctionNode -> symbols[scope.functionName.identifier]
                is GeneratorExpression, is ArrayComprehension, is Loop, is LetNode -> TODO()
                else -> unreachable()
            } ?: error("Can't find symbols for ${scope.toSource()}")
            Assertions.assertEquals(list.size, names.size)
            assert(list.containsAll(names.map { it.identifier }))
        }
    }

    @BeforeEach
    private fun setUp() {
        environment = CompilerEnvirons()
        parser = Parser(environment)
    }


}