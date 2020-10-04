package io.github.alrai

import org.mozilla.javascript.ast.*

fun <T: NodeVisitor> T.runOn(node: AstNode): T {
    node.visit(this)
    return this
}

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

// FIXME remove before release
fun Map<Scope, List<Name>>.toPrettyString(): String {
    return toList()
        .joinToString(separator = "\n----------------------------------\n") { pair ->
            val (key, value) = pair
            val keyString = if (key is AstRoot) "root" else key.toSource()
            val valueString = value.joinToString { it.toSource() }

            "$keyString to $valueString\n${key.symbolTable}"
        }
}

fun unreachable(): Nothing = error("Unreachable code")
fun unknownAstNode(node: AstNode): Nothing =
    error("Unknown ast node: ${node::class.simpleName}::${node.toSource()}")