package ru.maklas.expression;

import com.badlogic.gdx.utils.ObjectMap;

public class Test {

    private static final String expression = "-3 + max(3, 2) * pi * (sin(2 * 3x^e - 3 * max(2, 1)) + abs(-4)) * (3 / 2.2) - x";
    private static final String expression2 = "3";

    public static void main(String[] args) throws Exception {
        Expression expression = Compiler.compile(Test.expression2);
        double result = expression.evaluate(singletonMap("x", 6d));
        System.out.println(expression + " = " + result);

        Expression alwaysPassExp = Compiler.compile(Test.expression);
        alwaysPassExp.evaluate(singletonMap("x", 5d));
        System.out.println(alwaysPassExp);
        System.out.println(alwaysPassExp.equals(alwaysPassExp.cpy()));

    }

    private static <K, V> ObjectMap<K, V> singletonMap(K key, V value){
        ObjectMap<K, V> map = new ObjectMap<K, V>();
        map.put(key, value);
        return map;
    }

    //TODO list:
    //5. expression.cpy()       //Deep copy of whole tree
    //6. expression.getTokens() //Token Tree
    //7. expression.simplify()  //Solves all solvable parts.
    //8. expressionValidationException.getBadTokens(); //List of bad tokens. So that they could be highlighted
    //9. expression.derivative();
    // Memory usage optimization for singletone objects

}
