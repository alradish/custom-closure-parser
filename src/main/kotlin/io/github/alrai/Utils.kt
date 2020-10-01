package io.github.alrai

import org.mozilla.javascript.ast.AstNode

fun unreachable(): Nothing = error("Unreachable code")
fun unknownAstNode(node: AstNode): Nothing =
    error("Unknown ast node: ${node::class.simpleName}::${node.toSource()}")