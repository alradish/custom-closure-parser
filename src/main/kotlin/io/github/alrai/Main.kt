package io.github.alrai

import org.mozilla.javascript.CompilerEnvirons
import org.mozilla.javascript.Parser

fun main() {
    val environment = CompilerEnvirons()
    val parser = Parser(environment)
    val root = parser.parse(
        "var a = 10; var b = a + 1;" +
                "function someFunction() { return b + 1; }  ", null, 1
    )
    println(root.debugPrint())
}