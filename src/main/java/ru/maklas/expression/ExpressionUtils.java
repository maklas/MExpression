package ru.maklas.expression;

import com.badlogic.gdx.utils.Array;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExpressionUtils {

    public static final Array<String> constantNames = Array.with("pi", "e");
    public static final Pattern tokenizingPattern = Pattern.compile("[a-zA-Z][\\w]*|\\d+(\\.\\d+)?|\\.\\d+|\\(|\\)|,|\\+|-|/|\\*|\\^");
    public static final Pattern wordPattern = Pattern.compile("[a-zA-Z][\\w]*"); // сотоит из чисел и букв, но начинается обязательно с буквы
    public static final Pattern positiveNumberPattern = Pattern.compile("\\d+(\\.\\d+)?");

    public static double getConstantValue(String name){
        if (name.equalsIgnoreCase("pi")) return Math.PI;
        if (name.equalsIgnoreCase("e")) return Math.E;
        return 0;
    }

    ///////////////////////////////////////////////////////////////////////////
    // FUNCTIONS
    ///////////////////////////////////////////////////////////////////////////
    
    
    public static final Array<String> functionNames = Array.with("pow", "sin", "cos", "mod", "log", "rnd", "ln", "abs", "max", "min", "tan", "floor", "sqrt");

    public static int functionParameterSize(String functionName) {
        if ("pow".equals(functionName) || "max".equals(functionName) || "min".equals(functionName) || "mod".equals(functionName) || "log".equals(functionName) || "rnd".equals(functionName)) {
            return 2;
        }
        return 1;
    }

    public static double evaluateFunction(String function, Array<Double> parameters) throws ExpressionEvaluationException {
        if        ("sin".equals(function)) {
            return Math.sin(parameters.get(0));
        } else if ("pow".equals(function)) {
            return safePow(parameters.get(0), parameters.get(1));
        } else if ("cos".equals(function)) {
            return Math.cos(parameters.get(0));
        } else if ("abs".equals(function)) {
            return Math.abs(parameters.get(0));
        } else if ("max".equals(function)) {
            return Math.max(parameters.get(0), parameters.get(1));
        } else if ("min".equals(function)) {
            return Math.min(parameters.get(0), parameters.get(1));
        } else if ("tan".equals(function)) {
            return Math.tan(parameters.get(0));
        } else if ("sqrt".equals(function)) {
            return Math.sqrt(parameters.get(0));
        } else if ("mod".equals(function)) {
            return parameters.get(0) % parameters.get(1);
        } else if ("floor".equals(function)) {
            return Math.floor(parameters.get(0));
        } else if ("log".equals(function)) {
            return Math.log(parameters.get(0)) / Math.log(parameters.get(1));
        } else if ("ln".equals(function)) {
            return Math.log(parameters.get(0));
        } else if ("rnd".equals(function)) {
            Double min = parameters.get(0);
            Double max = parameters.get(1);
            return Math.random() * (max - min) + min;
        }
        throw new ExpressionEvaluationException("Function " + function + " is not supported");
    }


    public static double evaluate(Sign sign, double a, double b) throws ExpressionEvaluationException {
        switch (sign){
            case MINUS: return a - b;
            case PLUS: return a + b;
            case DIV: return a / b;
            case MUL: return a * b;
            case POW: return ExpressionUtils.safePow(a, b);
        }
        throw new ExpressionEvaluationException("Unexpected sign command: " + sign);
    }

    public static double safePow(double a, double b){
        if (a < 0) return -Math.pow(-a, b);
        return Math.pow(a, b);
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
    public static void validateParenthesis(Array<Token> tokens) throws ExpressionEvaluationException {
        int open = 0;
        for (Token token : tokens) {
            if (token.getType() == Token.Type.openPar){
                open++;
            } else if (token.getType() == Token.Type.closePar){
                open--;
                if (open < 0) throw new ExpressionEvaluationException("Closing parenthesis at position: " + token.getStart() + " doesn't have corresponding opening parenthesis");
            }
        }
        if (open > 0) {
            throw new ExpressionEvaluationException("Not even amount of opening and closing parentheses");
        }
    }

    /** Goes through each function and makes sure that it has proper params**/
    public static void validateFunctionParams(Array<Token> tokens) throws ExpressionEvaluationException{
        for (int i = 0; i < tokens.size; i++) {
            Token token = tokens.get(i);
            if (token.getType() == Token.Type.functionName){
                validateFunctionParams(tokens, token, i, functionParameterSize(token.getContent()));
            }
        }
    }

    /** Проверяет что количество параметров для функции совпадает с нужным **/
    private static void validateFunctionParams(Array<Token> tokens, Token token, int tokenId, int paramsSize) throws ExpressionEvaluationException {
        if (tokens.size < tokenId + 2){
            throw new ExpressionEvaluationException("Function " + token.getContent() + " needs to have " + paramsSize + " parameters");
        }
        if (tokens.get(tokenId + 1).getType() != Token.Type.openPar){
            throw new ExpressionEvaluationException("There must be '(' after " + token.getContent() + " function call");
        }
        if (paramsSize == 0){
            if (tokens.get(tokenId + 2).getType() != Token.Type.closePar){
                throw new ExpressionEvaluationException("A method '" + token.getContent() + "' must have 0 parameters");
            }
            return;
        }

        int openParenthesis = 1;
        int currentTokenId = tokenId + 2;
        Array<Array<Token>> params = new Array<Array<Token>>();
        Array<Token> currentParam = new Array<Token>();
        params.add(currentParam);

        while (openParenthesis != 0 && currentTokenId < tokens.size){
            Token t = tokens.get(currentTokenId);
            if (openParenthesis > 1){
                currentParam.add(t);
                if (t.getType() == Token.Type.openPar){
                    openParenthesis++;
                } else if (t.getType() == Token.Type.closePar){
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
                        currentParam = new Array<Token>();
                        params.add(currentParam);
                        break;
                }
            }
            currentTokenId++;
        }

        if (openParenthesis != 0) throw new ExpressionEvaluationException("Failed to parse method " + token.getContent());
        if (params.size != paramsSize) throw new ExpressionEvaluationException("Method " + token.getContent() + " should have " + paramsSize + " params, but it has " + params.size);
    }



    /**
     * Logical validation. 2 signs in a row are fobidden, as well as 2 numbers
     */
    public static void validateTokens(Array<Token> tokens) throws ExpressionEvaluationException {
        for (int i = 0; i < tokens.size - 1; i++) {
            Token tokenA = tokens.get(i);
            Token tokenB = tokens.get(i + 1);
            if (tokenA.type.isSign() && tokenB.type.isSign() && !tokenB.content.equals("-") && tokenA.content.equals("-")){
                throw new ExpressionEvaluationException("To signs in a row at: " + tokenA.start);
            } else if (tokenA.type == Token.Type.number && tokenB.type == Token.Type.number){
                throw new ExpressionEvaluationException("Sign is missing between " + tokenA.end + " and " + tokenB.start);
            }
        }

        if (tokens.size > 0 && tokens.peek().type.isSign()){
            throw new ExpressionEvaluationException("Last token of Expression can't be a sign");
        }

    }
}
