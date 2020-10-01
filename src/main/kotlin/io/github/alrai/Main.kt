package io.github.alrai

import org.mozilla.javascript.CompilerEnvirons
import org.mozilla.javascript.IRFactory
import org.mozilla.javascript.Parser
import org.mozilla.javascript.Token
import org.mozilla.javascript.optimizer.Codegen


fun main() {
    val environment = CompilerEnvirons()
    Token.printTrees
    val parser = Parser(environment)
    val root = parser.parse(
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

        """.trimIndent(), null, 1
    )

    println(root.toSource())
    val irf = IRFactory(environment)
    val tree = irf.transformTree(root)
    println(tree.toSource())

    val codegen = Codegen()
    codegen.setMainMethodClass("someFunction")
    codegen.compileToClassFile(
        environment, "Main", tree, tree.encodedSource,
        false
    )
}