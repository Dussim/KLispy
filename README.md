### What is this?
This is made for fun lisp like interpreter in Kotlin.
It is heavily based on great work of Daniel Holden and his book http://www.buildyourownlisp.com/

### If this is based on Build Your Own Lisp why it is not in C? 
Because I am an android developer, and I work mostly with Kotlin, sometimes with Java and don't know C. 
I thought It will be a lot of fun to use kotlin for this project, and it was :).

### Libraries used
- [better-parse](https://github.com/h0tk3y/better-parse) from _h0tk3y_ Sergey Igushkin used for parsing.
- I tried to implement basic classes I would need to have like lists in a functional way. There are some kotlin std lib classes and methods used but not much.

### Things that don't work yet
- equality between types other than number, tbh I forgot about this
- there are some problems with my implementation of joinToString on my list class...
- std lib is _a bit incomplete_
- there is no way to read from file from inside of interpreter
- there is no way to type multiline expr inside of interpreter
- there are no Strings 
- error reporting for some builtin functions is far from perfect


#### Misc
I really recommend building your own lisp, this was so satisfying that I will probably do it in every language I would like to learn.
