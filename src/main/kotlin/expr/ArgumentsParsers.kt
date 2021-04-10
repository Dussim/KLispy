package expr

import asserts.assertLength
import asserts.assertOneExpr
import asserts.assertType
import datastructures.Cons
import datastructures.Either
import datastructures.L
import datastructures.flatMap
import datastructures.index
import datastructures.map
import datastructures.mapLeft
import datastructures.reverse
import datastructures.size
import datastructures.take
import datastructures.takeAfter
import datastructures.takeUntil
import datastructures.zip

fun prepareVarargs(symbols: L<SymbolExpr.Unbound>, arguments: L<Expr>): Either<ErrorExpr, L<Pair<SymbolExpr.Unbound, Expr>>> {
    return arguments.assertLength(symbols.size - 1, true)
        .mapLeft { ErrorExpr("Arguments list ${SExpr(arguments)} of size ${arguments.size} should have length at least as symbols list ${SExpr(symbols)} of size ${symbols.size - 1}") }
        .flatMap {
            val start = symbols.takeUntil { it.symbol == ":" }
            val startArgs = arguments.take(start.size)
            val xs = symbols.takeAfter { it.symbol == ":" }
            val xsArgs = arguments.index(start.size)
            val startExprs = start.zip(startArgs, ::Pair)

            xs.assertOneExpr()
                .flatMap { it.assertType<SymbolExpr.Unbound>() }
                .map { Pair(it, QExpr(xsArgs)) }
                .map { xsExpr ->
                    Cons(xsExpr, startExprs.reverse()).reverse()
                }
        }
}

fun prepareNormalArgs(symbols: L<SymbolExpr.Unbound>, arguments: L<Expr>): Either<ErrorExpr, L<Pair<SymbolExpr.Unbound, Expr>>> {
    return arguments.assertLength(symbols.size)
        .mapLeft { ErrorExpr("Arguments list ${SExpr(arguments)} of size ${arguments.size} should have same length as symbols list ${SExpr(symbols)} of size ${symbols.size - 1}") }
        .map { symbols.zip(it, ::Pair) }
}
