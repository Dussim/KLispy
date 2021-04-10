package expr

import asserts.allOfType
import asserts.assertCons
import datastructures.Cons
import datastructures.Right
import datastructures.flatMap
import datastructures.isOneElement
import datastructures.map
import datastructures.merge
import datastructures.reduce

fun mathOp(symbol: String, op: (Cons<NumberExpr>) -> Expr): SymbolExpr.Builtin {
    return SymbolExpr.Builtin(symbol) { _, l ->
        Right(l)
            .flatMap { it.allOfType<NumberExpr>() }
            .flatMap { it.assertCons() }
            .map(op)
            .merge()
    }
}

val plus = mathOp("+") { cons -> cons.reduce(NumberExpr::plus) }
val multiply = mathOp("*") { cons -> cons.reduce(NumberExpr::times) }
val divide = mathOp("/") { cons -> cons.reduce(NumberExpr::div) }
val minus = mathOp("-") { cons -> if (cons.isOneElement()) NumberExpr(-cons.head.value) else cons.reduce(NumberExpr::minus) }
