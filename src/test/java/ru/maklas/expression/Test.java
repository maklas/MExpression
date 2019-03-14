package ru.maklas.expression;

import com.badlogic.gdx.utils.ObjectMap;

public class Test {

    private static final String expression = "-3 + max(3, 2) * pi * (sin(2 * 3x^e - 3 * max(2, 1)) + abs(-4)) * 3 / 2.2 - x";

    public static void main(String[] args) throws Exception {
        Expression expression = Compiler.compile(Test.expression);
        ObjectMap<String, Double> parameters = new ObjectMap<String, Double>();
        parameters.put("x", 5d);
        double result = expression.evaluate(parameters);
        System.out.println(result);
    }

    //TODO list:
    //1. expression.validate() //Validates the expression before solving it. Marking as valid onwards
    //2. expression.isValid()
    //3. expression.variables()
    //4. expression.equals(exp2)
    //5. expression.toString()  //Human readable representation
    //6. expression.cpy()       //Deep copy of whole tree
    //7. expression.getTokens() //Token Tree
    //8. expression.simplify()  //Solves all solvable parts.
    //9. expressionValidationException.getBadTokens(); //List of bad tokens. So that they could be highlighted

}
