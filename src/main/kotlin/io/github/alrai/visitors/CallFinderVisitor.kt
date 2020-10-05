package io.github.alrai.visitors

import org.mozilla.javascript.ast.*

class CallFinderVisitor(functions: List<FunctionNode>) : NodeVisitor {
    val calls: Map<FunctionNode, List<FunctionCall>>
        get() = _calls

    private val _calls = mutableMapOf<FunctionNode, MutableList<FunctionCall>>()
    private val functionByName = functions.map { it.functionName.identifier to it }.toMap()

    override fun visit(node: AstNode): Boolean {
        if (node is FunctionCall) {
            val name = (node.target as Name).identifier
            if (functionByName.contains(name)) {
                _calls.getOrPut(functionByName.getValue(name)) {
                    mutableListOf()
                }.add(node)
            }
        }
        return true
    }
}