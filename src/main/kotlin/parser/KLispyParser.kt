package parser

import Expr
import Number
import QExpr
import SExpr
import Symbol
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

class KLispyParser : Grammar<SExpr>() {
    private val `{` by literalToken("{")
    private val `}` by literalToken("}")
    private val `(` by literalToken("(")
    private val `)` by literalToken(")")
    private val ws by regexToken("\\s+", ignore = true)
    private val number by regexToken("-?\\d+")
    private val symbol by regexToken("[^(){}\\s]+")

    private val numberParser: Parser<Number> = number use { Number(text.toDouble()) }
    private val symbolParser: Parser<Symbol.Unbound> = symbol use { Symbol.Unbound(text) }

    private val exprParser: Parser<List<Expr>> = zeroOrMore(parser(::sExpr) or parser(::qExpr) or symbolParser or numberParser)

    private val qExpr: Parser<QExpr> by (-`{` and exprParser and -`}`).map { QExpr(*it.toTypedArray()) }
    private val sExpr: Parser<SExpr> by (-`(` and exprParser and -`)`).map { SExpr(*it.toTypedArray()) }

    override val rootParser: Parser<SExpr> = exprParser.map { SExpr(*it.toTypedArray()) }
}
