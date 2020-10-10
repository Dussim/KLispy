fun Expr.evalPrint(prefix: String = "") = this.also {
    println("$prefix${eval()}")
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