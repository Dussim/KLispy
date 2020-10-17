package utils

import Expr
import env.EmptyEnv
import env.Env

fun Expr.evalPrint(prefix: String = "", env: Env = EmptyEnv) = eval(env).also {
    println("$prefix$it")
}

infix fun <T, R1, R2> ((T) -> R1).compose(o: (R1) -> R2): (T) -> R2 {
    return { a: T -> o(this(a)) }
}

infix fun <A, B, C> ((A, B) -> C).bindLeft(a: A): (B) -> C {
    return { this(a, it) }
}

infix fun <A, B, C> ((A, B) -> C).bindRight(b: B): (A) -> C {
    return { this(it, b) }
}

infix fun <A, B, C, D> ((A, B, C) -> D).bindLeft(a: A): (B, C) -> D {
    return { b, c -> this(a, b, c) }
}

infix fun <A, B, C, D> ((A, B, C) -> D).bindRight(c: C): (A, B) -> D {
    return { a, b -> this(a, b, c) }
}
