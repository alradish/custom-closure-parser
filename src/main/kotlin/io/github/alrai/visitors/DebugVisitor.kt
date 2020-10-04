package io.github.alrai.visitors

import org.mozilla.javascript.ast.AstNode
import org.mozilla.javascript.ast.NodeVisitor

class DebugVisitor : NodeVisitor {
    override fun visit(node: AstNode): Boolean {
//        println("${node.toSource()}\nin\n${node.ancestorWithType<Scope>()?.toSource()}")
//        println("in ${node.toSource()}")
        println("${node.toSource()} is ${node.let { it::class.simpleName }}")
//        if(node is ScriptNode) {
//            println(node.symbols)
//        }
        println("----------------------------------------")
        return true
    }
}