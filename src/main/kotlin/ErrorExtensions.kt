fun Error.Companion.emptyParamListForSymbol(builtInSymbol: BuiltInSymbol) = Error("Symbol[ ${builtInSymbol.symbol} ] passed empty parameter list")
fun Error.Companion.paramsNotNumbers(builtInSymbol: BuiltInSymbol, l: L<Expr>) = Error("Symbol[ ${builtInSymbol.symbol} ] expects all values to be number in $l")
