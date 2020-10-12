package utils

import BuiltInSymbol
import ErrorExpr
import Expr
import datastructures.L

fun ErrorExpr.Companion.emptyParamListForSymbol(builtInSymbol: BuiltInSymbol) = ErrorExpr("Symbol[ ${builtInSymbol.symbol} ] passed datastructures.empty parameter list")
fun ErrorExpr.Companion.paramsNotNumbers(builtInSymbol: BuiltInSymbol, l: L<Expr>) = ErrorExpr("Symbol[ ${builtInSymbol.symbol} ] expects datastructures.all values to be number in $l")
