package io.github.alrai

import io.github.alrai.visitors.UsedSymbolsVisitor
import org.mozilla.javascript.CompilerEnvirons
import org.mozilla.javascript.Parser

class ClosureTransformer {
    fun transform(code: String): String {
        val environment = CompilerEnvirons()
        val parser = Parser(environment)
        val root = parser.parse(code, null, 0)
        val usedSymbols = UsedSymbolsVisitor().let {
            root.visit(it)
            it.usedSymbols
        }

        // TODO analyze usedSymbols and find closure

        // TODO create new FunctionNode with params

        // TODO add FunctionNode to top level

        // TODO add to FunctionCall params

        val res = root
        return res.toSource()
    }
}