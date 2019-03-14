package ru.maklas.expression;

import com.badlogic.gdx.utils.Array;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExpressionUtils {

    public static final Array<String> constantNames = Array.with("pi", "e");
    public static final Pattern basicTokenizingPattern = Pattern.compile("[a-zA-Z][\\w]*|\\d+(\\.\\d+)?|\\.\\d+|\\(|\\)|,|\\+|-|/|\\*|\\^");
    public static final Pattern wordPattern = Pattern.compile("[a-zA-Z][\\w]*"); // сотоит из чисел и букв, но начинается обязательно с буквы
    public static final Pattern positiveNnumberPattern = Pattern.compile("\\d+(\\.\\d+)?");

    public static double getConstantValue(String name){
        if (name.equals("pi")) return Math.PI;
        if (name.equals("e")) return Math.E;
        return 0;
    }


    ///////////////////////////////////////////////////////////////////////////
    // FUNCTIONS
    ///////////////////////////////////////////////////////////////////////////
    
    
    public static final Array<String> functionNames = Array.with("sin", "cos", "abs", "max", "min", "tg", "mod", "div");

    public static int functionParameterSize(String functionName) {
        if ("max".equals(functionName) || "min".equals(functionName) || "mod".equals(functionName) || "div".equals(functionName)) {
            return 2;
        }
        return 1;
    }

    public static double evaluateFunction(String function, Array<Double> parameters) throws ExpressionEvaluationException {
        if ("sin".equals(function)) {
            return Math.sin(parameters.get(0));
        } else if ("cos".equals(function)) {
            return Math.cos(parameters.get(0));
        } else if ("abs".equals(function)) {
            return Math.abs(parameters.get(0));
        } else if ("max".equals(function)) {
            return Math.max(parameters.get(0), parameters.get(1));
        } else if ("min".equals(function)) {
            return Math.min(parameters.get(0), parameters.get(1));
        } else if ("tg".equals(function)) {
            return Math.tan(parameters.get(0));
        }
        throw new ExpressionEvaluationException("Function " + function + " is not supported");
    }
    

    ///////////////////////////////////////////////////////////////////////////
    // VALIDATION
    ///////////////////////////////////////////////////////////////////////////

    public static void checkNoInvalidSymbols(String expression) throws ExpressionEvaluationException {
        Matcher matcher = Pattern.compile("[^\\s\\w\\-+.*/^,()]").matcher(expression);
        StringBuilder sb = new StringBuilder();
        if (matcher.find()){
            sb.append("Invalid characters found in expression: ");
            sb.append("'").append(matcher.group()).append("'");
        }

        while (matcher.find()){
            sb.append(", '").append(matcher.group()).append("'");
        }

        if (sb.length() > 0){
            sb.append(";");
            throw new ExpressionEvaluationException(sb.toString());
        }
    }

    /** makes sure there are equal amount of opening and closing parenthesis **/
    public static void validateParenthesisEven(Array<BasicToken> tokens) throws ExpressionEvaluationException {
        int open = 0;
        for (BasicToken token : tokens) {
            if (token.getType() == BasicToken.Type.openPar){
                open++;
            } else if (token.getType() == BasicToken.Type.closePar){
                open--;
                if (open < 0) throw new ExpressionEvaluationException("Closing parenthesis at position: " + token.getStart() + " doesn't have corresponding opening parenthesis");
            }
        }
        if (open > 0) {
            throw new ExpressionEvaluationException("Not even amount of opening and closing parentheses");
        }
    }

    /** Goes through each function and makes sure that **/
    public static void validateFunctionParams(Array<BasicToken> tokens) throws ExpressionEvaluationException{
        for (int i = 0; i < tokens.size; i++) {
            BasicToken token = tokens.get(i);
            if (token.getType() == BasicToken.Type.functionName){
                validateFunctionParams(tokens, token, i, functionParameterSize(token.getContent()));
            }
        }
    }

    /** Проверяет что количество параметров для функции совпадает с нужным **/
    private static void validateFunctionParams(Array<BasicToken> tokens, BasicToken token, int tokenId, int paramsSize) throws ExpressionEvaluationException {
        if (tokens.size < tokenId + 2){
            throw new ExpressionEvaluationException("Function " + token.getContent() + " needs to have " + paramsSize + " parameters");
        }
        if (tokens.get(tokenId + 1).getType() != BasicToken.Type.openPar){
            throw new ExpressionEvaluationException("There must be '(' after " + token.getContent() + " function call");
        }
        if (paramsSize == 0){
            if (tokens.get(tokenId + 2).getType() != BasicToken.Type.closePar){
                throw new ExpressionEvaluationException("A method '" + token.getContent() + "' must have 0 parameters");
            }
            return;
        }

        int openParenthesis = 1;
        int currentTokenId = tokenId + 2;
        Array<Array<BasicToken>> params = new Array<Array<BasicToken>>();
        Array<BasicToken> currentParam = new Array<BasicToken>();
        params.add(currentParam);

        while (openParenthesis != 0 && currentTokenId < tokens.size){
            BasicToken t = tokens.get(currentTokenId);
            if (openParenthesis > 1){
                currentParam.add(t);
                if (t.getType() == BasicToken.Type.openPar){
                    openParenthesis++;
                } else if (t.getType() == BasicToken.Type.closePar){
                    openParenthesis--;
                }
            } else {
                switch (t.getType()) {
                    default: //Everything except opening and closing parentheses is added as parameter
                        currentParam.add(t);
                        break;
                    case openPar:
                        openParenthesis++;
                        break;
                    case closePar:
                        openParenthesis--;
                        break;
                    case comma:
                        currentParam = new Array<BasicToken>();
                        params.add(currentParam);
                        break;
                }
            }
            currentTokenId++;
        }

        if (openParenthesis != 0) throw new ExpressionEvaluationException("Failed to parse method " + token.getContent());
        if (params.size != paramsSize) throw new ExpressionEvaluationException("Method " + token.getContent() + " should have " + paramsSize + " params, but it has " + params.size);
    }



    /** Валидация логической части массива токенов. Такие как знаки идущие подряд без цифр, цифры идущие подряд без знаков и т.д.
     * Так же тут возможна автоматическое добавление токенов. Таким образом 3х может превратиться в 3*x
     */
    public static void validateTokens(Array<BasicToken> tokens) {
        //TODO
    }
}
