import datastructures.Cons
import datastructures.L
import datastructures.None
import datastructures.iterator
import datastructures.of
import env.Env
import env.EnvImpl
import env.addBuiltIns
import iterators.Iter
import java.io.File
import java.util.Scanner

fun main() {
    val stdLib = File("./src/main/resources/std.lisp")
    val env = EnvImpl()
    env.addBuiltIns()
    env.loadFromFile(stdLib)
    val s = Scanner(System.`in`)
    while (true) {
        print("klispy> ")
        val line = s.nextLine()
        if (line == "exit") break
        if (line.startsWith("parse")) {
            line.substringAfter("parse").toExpr().also {
                println("parsed -> $it")
            }
        } else {
            println(line.toExpr().eval(env))
        }
    }
    s.close()
}

fun String.toExpr(): SExpr {
    val l = L.of(*split("\\s".toRegex()).filter(String::isNotBlank).toTypedArray())
    val i = l.iterator()
    return SExpr(parse(i))
}

private fun parse(i: Iter<String>): L<Expr> {
    if (!i.hasNext()) return None
    val s = i.next()
    val expr = when (s) {
        "{" -> QExpr(parse(i))
        "(" -> SExpr(parse(i))
        else -> s.toDoubleOrNull()?.let(::Number) ?: Symbol.Unbound(s)
    }
    return when (s) {
        ")", "}" -> None
        else -> Cons(expr, parse(i))
    }
}

private fun Env.loadFromFile(file: File) {
    file.reader().readText().toExpr().eval(this)
}
