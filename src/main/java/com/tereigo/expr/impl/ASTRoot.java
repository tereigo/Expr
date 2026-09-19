package com.tereigo.expr.impl;

/*
  It's a result of the expression compilation
  And this is the entry point for evaluation
  This class is needed just to record the original source expression to be able to dump it in errors
 */
record ASTRoot(String source, Expr expr) {
}
