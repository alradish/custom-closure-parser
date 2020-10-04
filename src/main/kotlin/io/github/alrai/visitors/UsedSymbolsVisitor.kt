package io.github.alrai.visitors

import io.github.alrai.ancestorWithType
import org.mozilla.javascript.ast.*

class UsedSymbolsVisitor() : NodeVisitor {
    val usedSymbols: MutableMap<AstNode, MutableList<Name>> = mutableMapOf()
    override fun visit(node: AstNode): Boolean {
        if(node is Name) {
            val parent = node.parent
            if (parent is FunctionNode && node == parent.functionName) {
                return false
            }
            // Delete this later, cos symbolsTable contains params symbols
            if(parent is FunctionNode && parent.params.contains(node)) {
                return false
            }



            val key = node.ancestorWithType<Scope>()!!
            if (usedSymbols[key] == null) {
                usedSymbols[key] = mutableListOf(node)
            } else {
                usedSymbols[key]!!.add(node)
            }
        }
        return true
    }
}