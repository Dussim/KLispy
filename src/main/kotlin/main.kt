fun main() {
    val list = L.of(1, 4, 5, 6, 2, 6).map(Int::toDouble compose ::Number)

    L.of(Plus, Minus, Multiply, Divide)
        .map(ParameterEvaluation<Expr>::eval bindRight list)
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
}
