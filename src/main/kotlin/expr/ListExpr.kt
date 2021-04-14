package expr

import datastructures.L
import datastructures.all
import datastructures.size
import datastructures.zip

sealed interface ListExpr : Expr {
    val content: L<Expr>
    val type: ListExprType

    override fun eq(o: Expr): Boolean {
        return if (type == (o as? ListExpr)?.type) {
            if (content.size == o.content.size) {
                content.zip(o.content) { a, b -> a == b }.all()
            } else false
        } else false
    }

    enum class ListExprType {
        SEXPR, QEXPR
    }
}
