package io.github.alrai

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ClosureTransformerTest : AbstractParserTest() {
    private var transformer = ClosureTransformer()

    @Test
    fun noClosure1() {
        assertEquals(
            """
                var a = 1;
                var b = a + 2;
            """.trimIndent(),
            """
                var a = 1;
                var b = a + 2;
            """.trimIndent()
        )
    }

    @Test
    fun noClosure2() {
        assertEquals(
            """
                var a = 2;
                function foo() {
                    return 2;
                }
                var b = a;
            """.trimIndent(),
            """
                var a = 2;
                function foo() {
                    return 2;
                }
                var b = a;
            """.trimIndent()
        )
    }


    @Test
    fun simpleClosure1() {
        assertEquals(
            """
                var a = 2;
                function foo() {
                    return a + 20;
                }
                var b = foo();
            """.trimIndent(),
            """
                function foo(a) {
                    return a + 20;
                }
                var a = 2;
                var b = foo(a);
            """.trimIndent()
        )
    }

    @Test
    fun simpleClosure2() {
        assertEquals(
            """
                function foo(a) {
                    var b = 42;
                    function bar(c) {
                        return a + b + c;
                    }
                    return bar(24);
                }
            """.trimIndent(),
            """
                function bar(a, b, c) {
                    return a + b + c;
                }
                function foo(a) {
                    var b = 42;
                    return bar(a, b, 24);
                }
            """.trimIndent()
        )
    }

    @Test
    fun nested1() {
        assertEquals(
            """
                var a = 2;
                function foo(x) {
                    var y = 42 + a;
                    function bar(z) {
                        return x + y + z;
                    }
                    return bar(24);
                }
                var c = foo(a)
            """.trimIndent(),
            """
                function bar(x, y, z) {
                    return x + y + z;
                }
                function foo(a, x) {
                    var y = 42 + a;
                    return bar(x, y, 24);
                }
                var a = 2;
                var c = foo(a, a)
            """.trimIndent()
        )

    }

    @Test
    fun nested2() {
        assertEquals(
            """
                var a = 2;
                var b = a + 43;
                function foo() {
                    var fooa = 0;
                    function bar(x) {
                        var bara = a;
                        function foobar() {
                            return x + 1 ;
                        }
                        var barb = fooa - 2;
                    }
                    var foob = 2 - fooa + bar(fooa);
                }  
                var c = foo()
            """.trimIndent(),
            """
                function foobar(x) {
                    return x + 1
                }
                function bar(a, fooa, x) {
                    var bara = a;
                    var barb = fooa - 2;
                }
                var a = 2;
                var b = a + 43;
                function foo() {
                    var fooa = 0;
                    var foob = 2 - fooa + bar(a, fooa, fooa);
                }  
                var c = foo()
            """.trimIndent()
        )
    }

    private fun assertEquals(code1: String, code2: String) {
        Assertions.assertEquals(
            transformer.transform(code1).trim(),
            parse(code2).toSource().trim()
        )
    }
}