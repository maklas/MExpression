package ru.maklas.expression;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

import java.io.PrintStream;

public class Test {

    private static final String expression = "-3 + max(3, 2) * pi * (max((sin(2 * 3x^2e - 3 * max(2, 1)) + abs(-4)), 1)) * (3 / 2.2) - x";
    private static final String expression2 = "x^2(pi)";
    private static final String expression3 = "x^(2x)";
    private static final String expression4 = "x^2x == x^(2x) != x^2 * x";
    private static final String expression5 = "x^2(x) == (x^2)(x)";
    private static final Array<String> simplifiable = Array.with("-3", "2 + 2", "3 - 5", "3.333 * 3", "5/2", "4 ^ 2", "2 + 3 * 4", "max(1, 2 + 2)", "pow(3, 3)", "abs(-3)", "floor(2.4)", "sqrt(2)", "2^3 + (3 - max(5, 1))", "rnd(100, 200)");

    public static void main(String[] args) throws Exception {
        ObjectMap<String, Double> params = singletonMap("x", 5d);
        Expression exp = Compiler.compile(expression);
        System.out.println(exp + " = " + exp.evaluate(params));
    }


    private static void testSimplify() throws Exception {

        int count = 0;
        for (String s : simplifiable) {
            Expression e = Compiler.compile(s);
            Expression simplified = e.simplify();

            PrintStream writer = (((e instanceof ComplexExpression) && ((ComplexExpression) e).canBeSimplified()) || (e instanceof FunctionExpression && ((FunctionExpression) e).canBeSimplified())) ? System.err : System.out;

            writer.println("-----" + ++count + "-----");
            writer.println("Original:      " + e + " = " + e.evaluate(singletonMap("x", 5d)));
            writer.println("Simplified:    " + simplified + " = " + simplified.evaluate(singletonMap("x", 5d)));
            writer.println("Equal:         " + e.equals(simplified));
            writer.println();
            Thread.sleep(100);
        }
    }

    private static <K, V> ObjectMap<K, V> singletonMap(K key, V value){
        ObjectMap<K, V> map = new ObjectMap<K, V>();
        map.put(key, value);
        return map;
    }

    //TODO list:
    //1. Пофиксить проблему     abs(-3)    ===>     abs((-3))
    //7. expression.simplify()  //Solves all solvable parts.
    //8. expressionValidationException.getBadTokens(); //List of bad tokens. So that they could be highlighted
    //9. expression.derivative();
    // Memory usage optimization for singleton objects

}
