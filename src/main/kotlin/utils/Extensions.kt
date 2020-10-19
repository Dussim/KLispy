package utils

infix fun <A, B, C> ((A) -> B).compose(o: (B) -> C): (A) -> C {
    return { a: A -> o(this(a)) }
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
