package io.github.alrai

fun main() {
    val code =
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
    println(ClosureTransformer().transform(code))
}