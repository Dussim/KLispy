package parser

import com.github.h0tk3y.betterParse.combinators.and
import com.github.h0tk3y.betterParse.combinators.map
import com.github.h0tk3y.betterParse.combinators.or
import com.github.h0tk3y.betterParse.combinators.unaryMinus
import com.github.h0tk3y.betterParse.combinators.use
import com.github.h0tk3y.betterParse.combinators.zeroOrMore
import com.github.h0tk3y.betterParse.grammar.Grammar
import com.github.h0tk3y.betterParse.grammar.parser
import com.github.h0tk3y.betterParse.lexer.literalToken
import com.github.h0tk3y.betterParse.lexer.regexToken
import com.github.h0tk3y.betterParse.parser.Parser
import expr.Expr
import expr.NumberExpr
import expr.QExpr
import expr.SExpr
import expr.StringExpr
import expr.SymbolExpr

class KLispyParser : Grammar<SExpr>() {
    private val `{` by literalToken("{")
    private val `}` by literalToken("}")
    private val `(` by literalToken("(")
    private val `)` by literalToken(")")
    private val ws by regexToken("\\s+", ignore = true)

    //    private val quotation by literalToken("\"")
    private val string by regexToken("'[a-zA-Z\\s\\d]*'")
    private val number by regexToken("[+-]?([0-9]*[.])?[0-9]+")
    private val symbol by regexToken("[^\"(){}\\s]+")

    private val numberExprParser: Parser<NumberExpr> = number use { NumberExpr(text.toDouble()) }
    private val symbolExprParser: Parser<SymbolExpr.Unbound> = symbol use { SymbolExpr.Unbound(text) }
    private val stringParser: Parser<StringExpr> = string map { token -> StringExpr(token.text.replace("'", "")) }

    private val exprParser: Parser<List<Expr>> = zeroOrMore(stringParser or parser(::sExpr) or parser(::qExpr) or numberExprParser or symbolExprParser)

    private val qExpr: Parser<QExpr> by (-`{` and exprParser and -`}`).map { QExpr(*it.toTypedArray()) }
    private val sExpr: Parser<SExpr> by (-`(` and exprParser and -`)`).map { SExpr(*it.toTypedArray()) }

    override val rootParser: Parser<SExpr> = exprParser.map { SExpr(*it.toTypedArray()) }
}
