package io.github.alrai

import org.mozilla.javascript.ast.AstNode
import org.mozilla.javascript.ast.AstRoot
import org.mozilla.javascript.ast.Name
import org.mozilla.javascript.ast.Scope


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