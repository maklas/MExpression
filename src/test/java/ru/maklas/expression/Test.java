package ru.maklas.expression;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class Test {

    private static final String expression = "-3+max(3, 2) * pi * ((((max((sin(2 * 3x^e - 3 * max(2, 1)) + abs(-4)), 1))))) * (3 / 2.2) - x";
    private static final String expression2 = "3 + 2";
    private static final Array<String> simplifiable = Array.with("2 + 2", "3 - 5", "3.333 * 3", "5/2", "4 ^ 2", "2 + 3 * 4", "max(1, 2)", "pow(3, 3)", "abs(-3)", "floor(2.4)", "sqrt(2)", "2^(3 - max(5, 1))");

    public static void main(String[] args) throws Exception {
        int count = 0;
        for (String s : simplifiable) {
            Expression e = Compiler.compile(s);
            Expression simpleE = e.cpy();
            boolean simplified = simpleE.simplify();
            System.out.println("-----" + ++count + "-----");
            System.out.println("Original:      " + e);
            System.out.println("Simplified:    " + simpleE);
            System.out.println("Did something: " + simplified);
            System.out.println("Equal:         " + e.equals(simpleE));
            System.out.println();
        }

    }

    private static <K, V> ObjectMap<K, V> singletonMap(K key, V value){
        ObjectMap<K, V> map = new ObjectMap<K, V>();
        map.put(key, value);
        return map;
    }

    //TODO list:
    //7. expression.simplify()  //Solves all solvable parts.
    //8. expressionValidationException.getBadTokens(); //List of bad tokens. So that they could be highlighted
    //9. expression.derivative();
    // Memory usage optimization for singleton objects

}
