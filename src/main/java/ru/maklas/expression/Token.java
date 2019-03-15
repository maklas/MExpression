package ru.maklas.expression;

import java.util.regex.Matcher;

/** A token, representing one of the basic types acceptable for expression like: words, parenthesis, commas etc.
 *  Not complex ones like function calls.
 */
public class Token {

    public enum Type {
        functionName,   //Only name of a function
        var,            //Any other string that is not a function
        constant,       //A constant. Like 'pi' or 'e'.
        number,         //A number. Always positive. Negativity is shown by having a minus token before number token
        openPar,        // ( Parenthesis
        closePar,       // )
        comma,          // ,
        plus,           // +
        minus,          // -
        multiply,       // *
        divide,         // /
        pow,            // ^
        nill             //Null token. Doesn't exist in original text, but could be created during simplification or out of no text
        ;

        public boolean isSign(){
            return this == plus || this == minus || this == multiply || this == divide || this == pow;
        }

        public boolean isParenthesis(){
            return this == openPar || this == closePar;
        }
    }

    public static final Token nullToken = new Token(Type.nill, "", 0, 0);

    /** type of token **/
    Type type;
    /** Value of a token **/
    String content;
    /** Start position of a token from original string **/
    int start;
    /** End position of a token from original string**/
    int end;

    public Token(Type type, String content, int start, int end) {
        this.type = type;
        this.content = content;
        this.start = start;
        this.end = end;
    }

    public Token(Type type, String content, Matcher matcher) {
        this.type = type;
        this.content = content;
        this.start = matcher.start();
        this.end = matcher.end();
    }

    public Token(Type type, String content, int start) {
        this.type = type;
        this.content = content;
        this.start = start;
        this.end = content.length();
    }

    public Type getType() {
        return type;
    }

    public String getContent() {
        return content;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    @Override
    public String toString() {
        return content;
    }
}
