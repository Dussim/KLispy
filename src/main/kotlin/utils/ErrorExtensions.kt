package utils

import ErrorExpr
import Expr
import Symbol
import datastructures.L

fun ErrorExpr.Companion.emptyParamListForSymbol(symbol: Symbol) = ErrorExpr("Symbol[ $symbol ] passed empty parameter list")
fun ErrorExpr.Companion.paramsNotNumbers(symbol: Symbol, l: L<Expr>) = ErrorExpr("Symbol[ $symbol ] expects all values to be number in $l")
