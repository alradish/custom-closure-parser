package io.github.alrai

import io.github.alrai.visitors.UsedSymbolsVisitor
import org.junit.jupiter.api.*
import org.mozilla.javascript.CompilerEnvirons
import org.mozilla.javascript.Parser
import org.mozilla.javascript.ast.AstRoot
import org.mozilla.javascript.ast.FunctionNode
import org.mozilla.javascript.ast.Name
import org.mozilla.javascript.ast.Scope

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
        containSymbols(
            usedSymbols(root),
            mapOf(null to listOf("a"))
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
        containSymbols(
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


    // FIXME order independent
    private fun containSymbols(table: SymbolTable, symbols: Map<String?, List<String>>) {
        Assertions.assertEquals(symbols.size, table.size)
        (symbols.toList() zip table.toList()).forEach { (s, t) ->
            if (s.first == null)
                assert(t.first is AstRoot)
            else {
                assert(t.first is FunctionNode)
                assert((t.first as FunctionNode).functionName.identifier == s.first)
            }
            assert(t.second.map { it.identifier }.containsAll(s.second))
        }
    }

    @BeforeEach
    private fun setUp() {
        environment = CompilerEnvirons()
        parser = Parser(environment)
    }


}