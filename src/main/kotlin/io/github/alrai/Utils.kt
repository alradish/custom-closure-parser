package io.github.alrai

import org.mozilla.javascript.ast.*

fun <T: NodeVisitor> T.runOn(node: AstNode): T {
    node.visit(this)
    return this
}

inline fun <reified T : AstNode> AstNode.ancestorWithType(): T? {
    var p = parent
    while (p != null) {
        if (p is T) return p
        p = p.parent
    }
    return null
}

fun unreachable(): Nothing = error("Unreachable code")