package io.github.alrai.visitors

import io.github.alrai.ancestorWithType
import org.mozilla.javascript.ast.*

class UsedSymbolsVisitor() : NodeVisitor {
    val usedSymbols: MutableMap<Scope, MutableList<Name>> = mutableMapOf()
    override fun visit(node: AstNode): Boolean {
        if (node is Name) {
            if(shouldSkip(node)) return true

            val key = node.ancestorWithType<Scope>()!!
            if (usedSymbols[key] == null) {
                usedSymbols[key] = mutableListOf(node)
            } else {
                usedSymbols[key]!!.add(node)
            }
        }
        return true
    }

    private fun shouldSkip(node: Name): Boolean {
        // Skip Name in function creation and params
        val parent = node.parent
        if (parent is FunctionNode) {
            if (node == parent.functionName) return true
            if (parent.params.contains(node)) return true
        }
        if(parent is VariableInitializer) {
            if (node == parent.target) return true
        }
        return false
    }
}