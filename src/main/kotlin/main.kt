import datastructures.L
import datastructures.forEach
import datastructures.map
import datastructures.of
import env.EnvImpl
import utils.bindRight
import utils.compose
import utils.evalPrint

fun main() {
    val list = L.of(1, 4, 5, 6, 2, 6).map(Int::toDouble compose ::Number)

    L.of(Plus, Minus, Multiply, Divide)
        .map(ParameterEvaluation<Expr>::eval bindRight list bindRight env.EmptyEnv)
        .forEach(Expr::evalPrint)

    val qExpr = QExpr(Plus, Minus, Multiply, Divide)

    L.of(Head, Tail).map {
        SExpr(it, qExpr)
    }.forEach(Expr::evalPrint)

    SExpr(
        Join,
        qExpr,
        qExpr
    ).evalPrint("( join $qExpr $qExpr ) ==> ")

    SExpr(
        Tail,
        QExpr(Plus, Number(2.0), Number(5.0))
    ).evalPrint()

    SExpr(
        ListF,
        Plus,
        Minus,
        Multiply
    ).evalPrint().also {
        SExpr(Eval, it).evalPrint()
    }

    val env = EnvImpl()

    SExpr(
        Def, QExpr(RuntimeSymbol("x"), RuntimeSymbol("y")),
        QExpr(Plus),
        L.of(1, 2, 3, 4, 5).map(Int::toDouble compose ::Number).let(::QExpr)
    ).evalPrint(env = env)

    env[RuntimeSymbol("x")]?.evalPrint()
    env[RuntimeSymbol("y")]?.evalPrint()

    val qExpr2 = SExpr(
        Join,
        RuntimeSymbol("x"),
        RuntimeSymbol("y")
    ).evalPrint(env = env)

    SExpr(Eval, qExpr2).evalPrint()
}
