(def {nil} {} )

(def {fun} (\{f b} {def(head f)(\ (tail f) b)}))

(fun { len l } {
    if (== l {}) {0} {+ 1 (len (tail l))}
})