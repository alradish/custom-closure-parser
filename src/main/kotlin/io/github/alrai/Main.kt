package io.github.alrai

import io.github.alrai.visitors.DebugVisitor

fun main() {
    val code0 = """
        function foo(a) {
          var b = 42;
          function bar(c) {
            return a + b + c;
          }
          return bar(24);
        }
    """.trimIndent()
    val code1 = """
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
    val result = ClosureTransformer().transform(code0)
    println(result)
}