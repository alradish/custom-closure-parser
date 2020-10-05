package io.github.alrai

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.fail
import org.mozilla.javascript.CompilerEnvirons
import org.mozilla.javascript.Parser
import org.mozilla.javascript.ast.AstRoot

abstract class AbstractParserTest {
    private var environment = CompilerEnvirons()
    private var parser = Parser(environment)

    protected fun parse(code: String): AstRoot =
        parser.parse(code, null, 0) ?: fail("Parser return null")

    @BeforeEach
    private fun setUp() {
        environment = CompilerEnvirons()
        parser = Parser(environment)
    }
}