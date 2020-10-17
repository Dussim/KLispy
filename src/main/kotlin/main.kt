import datastructures.Cons
import datastructures.L
import datastructures.None
import datastructures.iterator
import datastructures.of
import env.EnvImpl
import iterators.Iter
import java.util.Scanner

fun main() {
    val env = EnvImpl()
    val s = Scanner(System.`in`)
    do {
        print("klispy> ")
        val line = s.nextLine()
        if (line.startsWith("parse")) {
            line.substringAfter("parse").toExpr().also {
                println("parsed -> $it")
            }
        } else {
            println(line.toExpr().eval(env))
        }
    } while (line != "exit")
    s.close()
}

fun String.toExpr(): SExpr {
    val l = L.of(*split(" ").filter(String::isNotBlank).toTypedArray())
    val i = l.iterator()
    return SExpr(parse(i))
}

private fun parse(i: Iter<String>): L<Expr> {
    if (!i.hasNext()) return None
    val s = i.next()
    val expr = when (s) {
        "+" -> Plus
        "-" -> Minus
        "/" -> Divide
        "*" -> Multiply
        "head" -> Head
        "tail" -> Tail
        "join" -> Join
        "eval" -> Eval
        "list" -> ListF
        "def" -> Def
        "\\" -> LambdaF
        "{" -> QExpr(parse(i))
        "(" -> SExpr(parse(i))
        else -> s.toDoubleOrNull()?.let(::Number) ?: RuntimeSymbol(s)
    }
    return when (s) {
        ")", "}" -> None
        else -> Cons(expr, parse(i))
    }
}
