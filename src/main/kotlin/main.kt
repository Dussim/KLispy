import com.github.h0tk3y.betterParse.grammar.Grammar
import com.github.h0tk3y.betterParse.grammar.parseToEnd
import com.github.h0tk3y.betterParse.parser.ParseException
import env.EnvImpl
import env.addBuiltIns
import expr.ErrorExpr
import expr.Expr
import expr.SExpr
import parser.KLispyParser
import java.io.File
import java.util.Scanner

fun main() {
    val parser = KLispyParser()
    val stdFile = File("./src/main/resources/std.lisp")
    val env = EnvImpl()
    env.addBuiltIns()
    val stdExpr = stdFile.parse(parser)
    stdExpr.eval(env)
    val s = Scanner(System.`in`)
    while (true) {
        print("klispy> ")
        val line = s.nextLine()
        if (line == "exit") break
        if (line.startsWith("parse")) {
            line.substringAfter("parse").toExpr(parser).also { println("parsed -> $it") }
        } else {
            val expr = line.toExpr(parser).eval(env)
            println(expr)
        }
    }
    s.close()
}

private fun String.toExpr(parser: Grammar<SExpr>): Expr {
    return try {
        parser.parseToEnd(this)
    } catch (e: ParseException) {
        ErrorExpr(e.message.orEmpty())
    }
}

private fun File.parse(parser: Grammar<SExpr>): Expr {
    return readText().toExpr(parser)
}
