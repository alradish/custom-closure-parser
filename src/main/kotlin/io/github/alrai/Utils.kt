package io.github.alrai

import org.mozilla.javascript.ast.AstNode


fun AstNode.haveAncestor(node: AstNode): Boolean {
    return this.parent?.let {
        if (it == node) true else it.haveAncestor(node)
    } ?: false
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
fun unknownAstNode(node: AstNode): Nothing =
    error("Unknown ast node: ${node::class.simpleName}::${node.toSource()}")