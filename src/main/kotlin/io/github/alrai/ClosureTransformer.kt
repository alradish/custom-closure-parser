package io.github.alrai

import io.github.alrai.visitors.CallFinderVisitor
import io.github.alrai.visitors.SymbolTable
import io.github.alrai.visitors.UsedSymbolsVisitor
import org.mozilla.javascript.CompilerEnvirons
import org.mozilla.javascript.Parser
import org.mozilla.javascript.ast.FunctionNode
import org.mozilla.javascript.ast.Name

class ClosureTransformer {
    fun transform(code: String): String {
        val environment = CompilerEnvirons()
        val parser = Parser(environment)
        val root = parser.parse(code, null, 0)

//        DebugVisitor().let { root.visit(it) }

        val usedSymbols = UsedSymbolsVisitor()
            .runOn(root)
            .usedSymbol

        val notLocalSymbols = findNotLocal(usedSymbols)

        val functionCalls = CallFinderVisitor(notLocalSymbols.keys.toList())
            .runOn(root)
            .calls

        notLocalSymbols.forEach { (func, symbols) ->
            func.params = symbols.map { Name(0, it) } + func.params
            func.parent.removeChild(func)
        }


        functionCalls.forEach { (func, calls) ->
            calls.forEach { call ->
                val oldArguments = call.arguments
                val newArguments = notLocalSymbols.getValue(func).map { Name(0, it) }
                call.arguments = newArguments + oldArguments
            }
        }

        notLocalSymbols.forEach { (func, _) ->
            root.addChildToFront(func)
        }
        return root.toSource()
    }

    private fun findNotLocal(usedSymbols: SymbolTable): Map<FunctionNode, List<String>> {
        return usedSymbols
            .filterKeys { it is FunctionNode }
            .map { (scope, names) ->
                val functionNode = scope as FunctionNode
                val notLocalNames = names.map { it.identifier } - scope.symbols.map { it.name }
                functionNode to notLocalNames
            }.filter {
                it.second.isNotEmpty()
            }.toMap()
    }

}