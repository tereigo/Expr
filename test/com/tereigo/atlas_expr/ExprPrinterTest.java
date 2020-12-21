package com.tereigo.atlas_expr;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ExprPrinterTest extends ExprEvaluatorTestBase {

    @Test
    void testAstHierarchyPrinter() {
        AstHierarchyPrinter printer = new AstHierarchyPrinter();
        assertEquals("+\n" +
                "│\n" +
                "├── 1\n" +
                "│\n" +
                "├── 2", printer.print(ExprCompiler.compile("1 + 2")));

        assertEquals("+\n" +
                "│\n" +
                "├── A\n" +
                "│\n" +
                "├── BCD", printer.print(ExprCompiler.compile("\"A\" + \"BCD\"")));

        assertEquals("-\n" +
                "│\n" +
                "├── +\n" +
                "│   │\n" +
                "│   ├── 1\n" +
                "│   │\n" +
                "│   ├── 2\n" +
                "│\n" +
                "├── +\n" +
                "│   │\n" +
                "│   ├── 3\n" +
                "│   │\n" +
                "│   ├── 5", printer.print(ExprCompiler.compile("(1+2)-(3+5)")));

        assertEquals("or\n" +
                "│\n" +
                "├── true\n" +
                "│\n" +
                "├── and\n" +
                "│   │\n" +
                "│   ├── false\n" +
                "│   │\n" +
                "│   ├── ==\n" +
                "│   │   │\n" +
                "│   │   ├── 1\n" +
                "│   │   │\n" +
                "│   │   ├── 2", printer.print(ExprCompiler.compile("true or false and 1==2")));

        assertEquals("and\n" +
                "│\n" +
                "├── ==\n" +
                "│   │\n" +
                "│   ├── +\n" +
                "│   │   │\n" +
                "│   │   ├── 1\n" +
                "│   │   │\n" +
                "│   │   ├── 4\n" +
                "│   │\n" +
                "│   ├── 5\n" +
                "│\n" +
                "├── not\n" +
                "│   │\n" +
                "│   ├── or\n" +
                "│   │   │\n" +
                "│   │   ├── true\n" +
                "│   │   │\n" +
                "│   │   ├── >=\n" +
                "│   │   │   │\n" +
                "│   │   │   ├── -\n" +
                "│   │   │   │   │\n" +
                "│   │   │   │   ├── 5\n" +
                "│   │   │   │\n" +
                "│   │   │   ├── 6", printer.print(ExprCompiler.compile("1+4 == 5 and not(true or -5 >= 6)")));

        assertEquals("in\n" +
                "│\n" +
                "├── 1.0\n" +
                "│\n" +
                "├── [2.0, 3.0]", printer.print(ExprCompiler.compile("1.0 in [2.0, 3.0]")));

        assertEquals("in\n" +
                "│\n" +
                "├── 1\n" +
                "│\n" +
                "├── [2]", printer.print(ExprCompiler.compile("1 in [2]")));

        assertEquals("in\n" +
                "│\n" +
                "├── A\n" +
                "│\n" +
                "├── [A, B]", printer.print(ExprCompiler.compile("\"A\" in [\"A\", \"B\"]")));

        assertEquals("==\n" +
                "│\n" +
                "├── call func($id, 1)\n" +
                "│\n" +
                "├── $curTime", printer.print(ExprCompiler.compile("func($id, 1) == $curTime")));

        assertEquals("==\n" +
                "│\n" +
                "├── call func($id, call isEven(call rnd()))\n" +
                "│\n" +
                "├── $curTime", printer.print(ExprCompiler.compile("func($id, isEven(rnd())) == $curTime")));

        assertEquals("call contains(ABC, $id)", printer.print(ExprCompiler.compile("\"ABC\".contains($id)")));
    }

    @Test
    void testAstPolishPrinter() {
        AstPolishPrinter printer = new AstPolishPrinter();
        assertEquals("true", printer.print(ExprCompiler.compile("true")));
        assertEquals("(not true)", printer.print(ExprCompiler.compile("not true")));
        assertEquals("(not true)", printer.print(ExprCompiler.compile("not true")));
        assertEquals("1.0", printer.print(ExprCompiler.compile("1.0")));
        assertEquals("(- 1.0)", printer.print(ExprCompiler.compile("-1.0")));
        assertEquals("2", printer.print(ExprCompiler.compile("2")));
        assertEquals("(- 2)", printer.print(ExprCompiler.compile("-2")));
        assertEquals("true", printer.print(ExprCompiler.compile("true")));
        assertEquals("a", printer.print(ExprCompiler.compile("\"a\"")));
        assertEquals("(group (not (group true)))", printer.print(ExprCompiler.compile("(not(true))")));
        assertEquals("(+ 1.0 2)", printer.print(ExprCompiler.compile("1.0 + 2")));
        assertEquals("(% 3 2)", printer.print(ExprCompiler.compile("3 % 2")));
        assertEquals("(group (+ 1.0 (group 2)))", printer.print(ExprCompiler.compile("(1.0 + (2))")));
        assertEquals("(or (group (+ 1.0 (group 2))) (and true (group (== A B))))", printer.print(ExprCompiler.compile("(1.0 + (2)) or true and (\"A\" == \"B\")")));
        assertEquals("(in 1.0 [2.0, 3.0])", printer.print(ExprCompiler.compile("1.0 in [2.0, 3.0]")));
        assertEquals("(in 1 [2])", printer.print(ExprCompiler.compile("1 in [2]")));
        assertEquals("(in A [A, B])", printer.print(ExprCompiler.compile("\"A\" in [\"A\", \"B\"]")));
        assertEquals("(== call func($id, 1) $curTime)", printer.print(ExprCompiler.compile("func($id, 1) == $curTime")));
        assertEquals("(== call func($id, call isEven(call rnd())) $curTime)", printer.print(ExprCompiler.compile("func($id, isEven(rnd())) == $curTime")));
        assertEquals("call contains(ABC, $id)", printer.print(ExprCompiler.compile("\"ABC\".contains($id)")));
    }

    @Test
    void testAstPrinterTheSameExpression() {
        AstPolishPrinter printer = new AstPolishPrinter();
        ASTRoot expr = ExprCompiler.compile("(1.0+2.0)");
        assertEquals("(group (+ 1.0 2.0))", printer.print(expr));
        ExprEvaluator exprEvaluator = new ExprEvaluator(expr);
        assertEquals(3.0, exprEvaluator.evaluateDouble(), EPS);
        assertEquals("(group (+ 1.0 2.0))", printer.print(expr));
        assertEquals(3.0, exprEvaluator.evaluateDouble(), EPS);
        assertEquals("(group (+ 1.0 2.0))", printer.print(expr));
        assertEquals(3.0, exprEvaluator.evaluateDouble(), EPS);

        expr = ExprCompiler.compile("not true and (not (false or true)) or 1==2");
        exprEvaluator = new ExprEvaluator(expr);
        assertEquals("(or (and (not true) (group (not (group (or false true))))) (== 1 2))", printer.print(expr));
        assertFalse(exprEvaluator.evaluateBool());
        assertEquals("(or (and (not true) (group (not (group (or false true))))) (== 1 2))", printer.print(expr));
        assertFalse(exprEvaluator.evaluateBool());
        assertEquals("(or (and (not true) (group (not (group (or false true))))) (== 1 2))", printer.print(expr));
        assertFalse(exprEvaluator.evaluateBool());
    }
}