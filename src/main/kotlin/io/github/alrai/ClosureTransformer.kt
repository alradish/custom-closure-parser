package io.github.alrai

import io.github.alrai.visitors.CallFinderVisitor
import io.github.alrai.visitors.UsedSymbolsVisitor
import org.mozilla.javascript.CompilerEnvirons
import org.mozilla.javascript.Parser
import org.mozilla.javascript.ast.FunctionNode
import org.mozilla.javascript.ast.Name
import org.mozilla.javascript.ast.Scope

fun transform(code: String): String {
    val environment = CompilerEnvirons()
    val parser = Parser(environment)

    val root = try {
        parser.parse(code, null, 0)
    } catch (e: org.mozilla.javascript.EvaluatorException) {
        error("Parser error")
    }

    val usedSymbols = UsedSymbolsVisitor()
        .runOn(root)
        .usedSymbol

    val functionsWithNotLocalSymbol = findNotLocal(usedSymbols)

    val functionCalls = CallFinderVisitor(functionsWithNotLocalSymbol.keys.toList())
        .runOn(root)
        .calls

    // Add notLocal to param list
    functionsWithNotLocalSymbol.forEach { (func, symbols) ->
        func.params = symbols.map { Name(0, it) } + func.params
    }


    // Update argument list for every call
    functionCalls.forEach { (func, calls) ->
        calls.forEach { call ->
            val oldArguments = call.arguments
            val newArguments = functionsWithNotLocalSymbol.getValue(func).map { Name(0, it) }
            call.arguments = newArguments + oldArguments
        }
    }

    // Put functions to top
    functionsWithNotLocalSymbol.forEach { (func, _) ->
        func.parent.removeChild(func)
        root.addChildToFront(func)
    }
    return root.toSource()
}

private fun findNotLocal(usedSymbols: Map<Scope, List<Name>>): Map<FunctionNode, List<String>> {
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