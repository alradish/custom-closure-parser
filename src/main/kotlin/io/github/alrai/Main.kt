package io.github.alrai

import java.io.File

fun readFile(name: String): String {
    val inputFile = File(name)
    if (!inputFile.exists()) {
        error("File $name does not exist")
    }
    if (!inputFile.canRead()) {
        error("Can't read file $name")
    }

    return inputFile.readText()
}

fun writeInFile(name: String, text: String) {
    val outputFile = File(name)
    if (outputFile.createNewFile()) {
        outputFile.writeText(text)
        return
    } else {
        error("File with name $name already exist")
    }
}

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        println("Usage: custom-closure-parser INPUT_FILE [OUTPUT_FILE]")
        return
    }

    val inputFileName = args.first()
    val outputFileName = if (args.size == 1) {
        "output.js"
    } else {
        args[1]
    }

    try {
        val code =
            readFile(inputFileName)

        val res =
            ClosureTransformer().transform(code)

        writeInFile(outputFileName, res)
    } catch (e: IllegalStateException) {
        when (e.message) {
            "Parser error" -> println("Can't parse code from file. Please check the file for syntax errors")
            else -> println(e.message)
        }
    }
}