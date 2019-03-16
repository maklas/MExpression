package ru.maklas.expression;

import com.badlogic.gdx.utils.Array;

import java.util.regex.Matcher;

import static ru.maklas.expression.ExpressionUtils.*;

public class Compiler {

    /**
     * Compiles Expression out of the string
     * @param expression String representation of Expression
     * @return a valid expression.
     * @throws ExpressionEvaluationException if passed expression is invalid
     */
    public static Expression compile(String expression) throws ExpressionEvaluationException {
        //1. Tokenize
        Array<Token> tokens = tokenize(expression);
        //2. parse
        return parseExpression(tokens);
    }

    /** Tokenizes Expression in the most basic tokens **/
    public static Array<Token> tokenize(String expression) throws ExpressionEvaluationException {
        checkNoInvalidSymbols(expression);

        Array<Token> tokens = new Array<Token>(true, 32);
        Matcher matcher = tokenizingPattern.matcher(expression);

        while (matcher.find()){
            String tokenText = matcher.group();
            Token token;

            if (wordPattern.matcher(tokenText).matches()) {
                if (functionNames.contains(tokenText, false)) {
                    token = new Token(Token.Type.functionName, tokenText, matcher);
                } else if (constantNames.contains(tokenText, false)) {
                    token = new Token(Token.Type.constant, tokenText, matcher);
                } else {
                    token = new Token(Token.Type.var, tokenText, matcher);
                }
            } else if (positiveNumberPattern.matcher(tokenText).matches()) token = new Token(Token.Type.number, tokenText, matcher);
            else if (tokenText.equals("-")) token = new Token(Token.Type.minus, tokenText, matcher);
            else if (tokenText.equals("(")) token = new Token(Token.Type.openPar, tokenText, matcher);
            else if (tokenText.equals(")")) token = new Token(Token.Type.closePar, tokenText, matcher);
            else if (tokenText.equals(",")) token = new Token(Token.Type.comma, tokenText, matcher);
            else if (tokenText.equals("+")) token = new Token(Token.Type.plus, tokenText, matcher);
            else if (tokenText.equals("/")) token = new Token(Token.Type.divide, tokenText, matcher);
            else if (tokenText.equals("*")) token = new Token(Token.Type.multiply, tokenText, matcher);
            else if (tokenText.equals("^")) token = new Token(Token.Type.pow, tokenText, matcher);
            else throw new ExpressionEvaluationException("Invalid token: '" + tokenText + "' at position: " + matcher.start());

            tokens.add(token);
        }


        //further logical validation
        validateParenthesis(tokens);
        validateFunctionParams(tokens);
        validateTokens(tokens);
        return tokens;
    }

    /**
     * Parses tokens into an Expression. Expects valid set of tokens!!!
     * @return Valid Expression
     * @throws ExpressionEvaluationException if tokenized expression is not logically valid.
     */
    public static Expression parseExpression(Array<Token> tokens) throws ExpressionEvaluationException {

        Array<Expression> list = new Array<Expression>(true, 16);
        for (int i = 0; i < tokens.size; i++) {
            Token token = tokens.get(i);
            switch (token.getType()){
                case functionName: {
                    if (tokens.get(i + 2).getType() == Token.Type.closePar){ //Исли за открывающейся скобкой сразу идёт закрывающаяся, значит параметров нет.
                        list.add(new FunctionExpression(token, new Array<Expression>()));
                    } else {

                        Array<Expression> params = new Array<Expression>();
                        Array<Token> currentParam = new Array<Token>();
                        int openPar = 1;
                        int j = 1;
                        while (openPar >= 1){
                            j++;
                            Token t = tokens.get(i + j);
                            if (t.getType() == Token.Type.openPar) {
                                openPar++;
                                currentParam.add(t);
                            } else if (t.getType() == Token.Type.closePar) {
                                openPar--;
                                if (openPar == 0){
                                    params.add(parseExpression(currentParam));
                                } else {
                                    currentParam.add(t);
                                }
                            } else if (openPar == 1 && t.getType() == Token.Type.comma){
                                params.add(parseExpression(currentParam));
                                currentParam = new Array<Token>();
                            } else {
                                currentParam.add(t);
                            }
                        }
                        list.add(new FunctionExpression(token, params));
                        i += j;
                    }
                }
                break;
                case var:
                    list.add(new VariableExpression(token));
                    break;
                case constant:
                case number:
                    list.add(ValueExpression.forToken(token));
                    break;
                case openPar: {
                    int par = 1;
                    int j = 0;
                    Array<Token> expressionTokens = new Array<Token>();
                    while (par >= 1) {
                        j++;
                        Token t = tokens.get(i + j);
                        if (t.getType() == Token.Type.openPar) {
                            par++;
                            expressionTokens.add(t);
                        } else if (t.getType() == Token.Type.closePar) {
                            par--;
                            if (par != 0) expressionTokens.add(t);
                        } else {
                            expressionTokens.add(t);
                        }
                    }
                    i += j;
                    list.add(parseExpression(expressionTokens));
                }
                break;
                case closePar:
                    //In theory should never be called
                    throw new ExpressionEvaluationException("Should never encountered Closing parenthesis");
                case comma:
                    throw new ExpressionEvaluationException("Should never encountered comma");
                case plus:
                    list.add(new SignExpression(token, Sign.PLUS));
                    break;
                case minus:
                    list.add(new SignExpression(token, Sign.MINUS));
                    break;
                case multiply:
                    list.add(new SignExpression(token, Sign.MUL));
                    break;
                case divide:
                    list.add(new SignExpression(token, Sign.DIV));
                    break;
                case pow:
                    list.add(new SignExpression(token, Sign.POW));
                    if (i + 2 < tokens.size) {
                        Token next = tokens.get(i + 1);
                        if (next.type == Token.Type.number) {
                            Token next2 = tokens.get(i + 2);
                            if (next2.type == Token.Type.var) {
                                list.add(new ComplexExpression(Array.with(ValueExpression.forToken(next), new SignExpression(Token.nullToken, Sign.MUL), new VariableExpression(next2))));
                                i += 2;
                            } else if (next2.type == Token.Type.constant) {
                                list.add(new ComplexExpression(Array.with(ValueExpression.forToken(next), new SignExpression(Token.nullToken, Sign.MUL), ValueExpression.forToken(next2))));
                                i += 2;
                            }
                        }
                    }
                    break;
            }
        }

        if (list.size == 0) return NullExpression.getInstance();
        if (list.size == 1) return list.first();
        if (list.get(0) instanceof SignExpression && ((SignExpression) list.get(0)).getSign() == Sign.MINUS) {
            if (list.get(1) instanceof ValueExpression){
                ((ValueExpression) list.get(1)).negate();
                list.removeIndex(0);
            } else if (list.get(1) instanceof VariableExpression){
                ((VariableExpression) list.get(1)).negate();
                list.removeIndex(0);
            }
        }
        if (list.size == 1) return list.first();


        boolean foundProblems;
        do {
            foundProblems = false;
            int problemIndex = 0; //Index of minus.

            for (int i = 1; i < list.size - 1; i++) {
                Expression prev = list.get(i - 1);
                Expression curr = list.get(i);
                Expression next = list.get(i + 1);
                if (curr instanceof SignExpression && prev instanceof SignExpression && next instanceof ValueExpression && ((SignExpression) prev).getSign() != Sign.MINUS){
                    problemIndex = i;
                    foundProblems = true;
                    break;
                }
            }
            if (foundProblems){
                list.removeIndex(problemIndex);
                ((ValueExpression) list.get(problemIndex)).negate();
            }

        } while (foundProblems);

        return new ComplexExpression(list);
    }

}
