package ru.maklas.expression;

import com.badlogic.gdx.utils.Array;

import java.util.regex.Matcher;

import static ru.maklas.expression.ExpressionUtils.*;

public class Compiler {

    public static Expression compile(String expression) throws ExpressionEvaluationException {
        checkNoInvalidSymbols(expression); //Validate string
        Array<BasicToken> tokens = tokenize(expression); //Tokenize
        // further validation based on tokens position and logic
        validateParenthesisEven(tokens);
        validateFunctionParams(tokens);
        validateTokens(tokens); //Валидация для всех других случаев. Нельзя два плюса/минуса между цифрами и т.д
        //Creating ExpressionTree, consisting of basic expression.
        //E.g: "3 + 2" - basic expression.
        //But "3 + 2 * 5" is not. It consist of 2 expressions (2 * 5) and (3 + ex1).
        //Also "((3) + 2) * 5" is not. It consist of 3 expressions (3), (ex1 + 2) and (ex2 * 5).
        //Every multiplication, division, function call or parenthesis are individual expressions.
        return makeTokenTree(tokens);
    }

    //разбиваем на Более функциональные токены. Причём возможно добавление знаков. Функции начинают сворачиваться в свои токены.
    private static Expression makeTokenTree(Array<BasicToken> tokens) throws ExpressionEvaluationException {

        Array<Expression> list = new Array<Expression>(true, 16);
        for (int i = 0; i < tokens.size; i++) {
            BasicToken token = tokens.get(i);
            switch (token.getType()){
                case functionName: {
                    if (tokens.get(i + 2).getType() == BasicToken.Type.closePar){ //Исли за открывающейся скобкой сразу идёт закрывающаяся, значит параметров нет.
                        list.add(new FunctionExpression(token, new Array<Expression>()));
                    } else {

                        Array<Expression> params = new Array<Expression>();
                        Array<BasicToken> currentParam = new Array<BasicToken>();
                        int openPar = 1;
                        int j = 1;
                        while (openPar >= 1){
                            j++;
                            BasicToken t = tokens.get(i + j);
                            if (t.getType() == BasicToken.Type.openPar) {
                                openPar++;
                                currentParam.add(t);
                            } else if (t.getType() == BasicToken.Type.closePar) {
                                openPar--;
                                if (openPar == 0){
                                    params.add(makeTokenTree(currentParam));
                                } else {
                                    currentParam.add(t);
                                }
                            } else if (openPar == 1 && t.getType() == BasicToken.Type.comma){
                                params.add(makeTokenTree(currentParam));
                                currentParam = new Array<BasicToken>();
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
                    list.add(new VariableExpression(token.getContent()));
                    break;
                case constant:
                    list.add(new ConstantExpression(getConstantValue(token.getContent()), token.getContent()));
                    break;
                case number:
                    list.add(new ValueExpression(Double.parseDouble(token.getContent()), ValueExpression.Source.NUMBER));
                    break;
                case openPar: {
                    int par = 1;
                    int j = 0;
                    Array<BasicToken> expressionTokens = new Array<BasicToken>();
                    while (par >= 1) {
                        j++;
                        BasicToken t = tokens.get(i + j);
                        if (t.getType() == BasicToken.Type.openPar) {
                            par++;
                            expressionTokens.add(t);
                        } else if (t.getType() == BasicToken.Type.closePar) {
                            par--;
                            if (par != 0) expressionTokens.add(t);
                        } else {
                            expressionTokens.add(t);
                        }
                    }
                    i += j;
                    list.add(makeTokenTree(expressionTokens));
                }
                break;
                case closePar:
                    //In theory should never be called
                    throw new ExpressionEvaluationException("Should never encountered Closing parenthesis");
                case comma:
                    throw new ExpressionEvaluationException("Should never encountered comma");
                case plus:
                    list.add(new SignExpression(SignExpression.Sign.PLUS));
                    break;
                case minus:
                    list.add(new SignExpression(SignExpression.Sign.MINUS));
                    break;
                case multiply:
                    list.add(new SignExpression(SignExpression.Sign.MUL));
                    break;
                case divide:
                    list.add(new SignExpression(SignExpression.Sign.DIV));
                    break;
                case pow:
                    list.add(new SignExpression(SignExpression.Sign.POW));
                    break;
            }
        }

        if (list.size == 0) return new NullExpression();
        if (list.size == 1) return list.first();
        if (list.get(0) instanceof SignExpression && ((SignExpression) list.get(0)).getSign() == SignExpression.Sign.MINUS && list.get(1) instanceof ValueExpression){
            ((ValueExpression) list.get(1)).negate();
            list.removeIndex(0);
        }
        if (list.size == 1) return list.first();
        return new CummulativeResolveExpression(list);
    }

    /** Tokenizes Expression in the most basic tokens. Does basic checks of invalid symbols**/
    public static Array<BasicToken> tokenize(String expression) throws ExpressionEvaluationException {
        Array<BasicToken> tokens = new Array<BasicToken>(true, 32);
        Matcher matcher = basicTokenizingPattern.matcher(expression);


        while (matcher.find()){
            String tokenText = matcher.group();
            BasicToken token;

            if (wordPattern.matcher(tokenText).matches()) {
                if (functionNames.contains(tokenText, false)) {
                    token = new BasicToken(BasicToken.Type.functionName, tokenText, matcher);
                } else if (constantNames.contains(tokenText, false)) {
                    token = new BasicToken(BasicToken.Type.constant, tokenText, matcher);
                } else {
                    token = new BasicToken(BasicToken.Type.var, tokenText, matcher);
                }
            } else if (positiveNnumberPattern.matcher(tokenText).matches()) token = new BasicToken(BasicToken.Type.number, tokenText, matcher);
            else if (tokenText.equals("-")) token = new BasicToken(BasicToken.Type.minus, tokenText, matcher);
            else if (tokenText.equals("(")) token = new BasicToken(BasicToken.Type.openPar, tokenText, matcher);
            else if (tokenText.equals(")")) token = new BasicToken(BasicToken.Type.closePar, tokenText, matcher);
            else if (tokenText.equals(",")) token = new BasicToken(BasicToken.Type.comma, tokenText, matcher);
            else if (tokenText.equals("+")) token = new BasicToken(BasicToken.Type.plus, tokenText, matcher);
            else if (tokenText.equals("/")) token = new BasicToken(BasicToken.Type.divide, tokenText, matcher);
            else if (tokenText.equals("*")) token = new BasicToken(BasicToken.Type.multiply, tokenText, matcher);
            else if (tokenText.equals("^")) token = new BasicToken(BasicToken.Type.pow, tokenText, matcher);
            else throw new ExpressionEvaluationException("Invalid token: '" + tokenText + "' at position: " + matcher.start());

            tokens.add(token);
        }
        return tokens;
    }

}
