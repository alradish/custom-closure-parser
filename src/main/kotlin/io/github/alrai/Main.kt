package io.github.alrai

import jdk.nashorn.internal.ir.FunctionNode
import jdk.nashorn.internal.ir.Statement
import jdk.nashorn.internal.parser.Parser
import jdk.nashorn.internal.runtime.Context
import jdk.nashorn.internal.runtime.ErrorManager
import jdk.nashorn.internal.runtime.Source
import jdk.nashorn.internal.runtime.options.Options


fun main(args: Array<String>) {
    val options = Options("nashorn")
    options.set("anon.functions", true)
    options.set("parse.only", true)
    options.set("scripting", true)

    val errors = ErrorManager()
    val context = Context(options, errors, Thread.currentThread().contextClassLoader)
    val source = Source.sourceFor(
        "test", "var a = 10; var b = a + 1;" +
                "function someFunction() { return b + 1; }  "
    )
    val parser = Parser(context.env, source, errors)
    val functionNode: FunctionNode = parser.parse()
    val block = functionNode.body
    val statements: List<Statement> = block.statements
}