package io.github.alrai.visitors

import io.github.alrai.ancestorWithType
import org.mozilla.javascript.ast.*

class UsedSymbolsVisitor : NodeVisitor {
    val usedSymbol: Map<Scope, List<Name>>
        get() = _usedSymbol.map { (scope, values) ->
            scope to values.distinctBy { it.identifier }
        }.toMap()
    val changedSymbol: Map<Scope, List<Name>>
        get() = _changedSymbol

    private val _usedSymbol: MutableMap<Scope, MutableList<Name>> = mutableMapOf()
    private val _changedSymbol: MutableMap<Scope, MutableList<Name>> = mutableMapOf()
    override fun visit(node: AstNode): Boolean {
        if (node is Name) {
            if(shouldSkip(node)) return true

            val key = node.ancestorWithType<Scope>()!!
            when {
                node.isAssigned() -> _changedSymbol.getOrPut(key) {
                    mutableListOf()
                }
                else ->  _usedSymbol.getOrPut(key) {
                    mutableListOf()
                }
            }.add(node)
        }
        return true
    }

    private fun Name.isAssigned(): Boolean {
        return parent is Assignment && (parent as Assignment).left == this
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