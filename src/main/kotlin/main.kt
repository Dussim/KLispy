import com.github.h0tk3y.betterParse.grammar.Grammar
import com.github.h0tk3y.betterParse.grammar.parseToEnd
import env.EnvImpl
import env.addBuiltIns
import parser.KLispyParser
import java.io.File
import java.util.Scanner

fun main() {
    val parser = KLispyParser()
    val stdFile = File("./src/main/resources/std.lisp")
    val env = EnvImpl()
    env.addBuiltIns()
    stdFile.parse(parser).eval(env)
    val s = Scanner(System.`in`)
    while (true) {
        print("klispy> ")
        val line = s.nextLine()
        if (line == "exit") break
        if (line.startsWith("parse")) {
            line.substringAfter("parse").toExpr(parser).also { println("parsed -> $it") }
        } else {
            println(line.toExpr(parser).eval(env))
        }
    }
    s.close()
}

private fun String.toExpr(parser: Grammar<SExpr>): SExpr {
    return parser.parseToEnd(this)
}

private fun File.parse(parser: Grammar<SExpr>): SExpr {
    return readText().toExpr(parser)
}
