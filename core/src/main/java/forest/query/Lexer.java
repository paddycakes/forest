package forest.query;

import static forest.query.StringReader.isEOF;

import java.lang.Character;

import static forest.query.TokenType.*;
import static java.lang.Character.isDigit;
import static java.lang.Character.isJavaIdentifierStart;
import static java.lang.Character.isWhitespace;
import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;
import static java.lang.String.format;

public class Lexer {

	private final StringReader text;

	public Lexer(String text) {
		this.text = new StringReader(text);
	}

	public Token nextToken() {
		char c = text.advance();
		if (isWhitespace(c)) return whiteSpace();
		if (isEOF(c)) return eof();
		if (isNameStartChar(c)) return name();
		if (isDigit(c)) return number();
		if (isStringQuote(c)) return string();
		switch (c) {
		case '=': return equals();
		case '!': return notEqual();
		case ':': return colon();
        case '-': return arrow();
		case '&': return and();
		case '|': return or();
		default: throw new LexerException("Unable to process char: " + c);
		}
	}
	
	/* --- Token builders --- */

	private Token whiteSpace() {
		while (isWhitespace(text.lookAhead())) text.advance();
		return new Token(WHITE_SPACE);
	}

	private Token eof() {
		return new Token(EOF);
	}

	private Token name() {
		char c = text.current();
		StringBuilder name = new StringBuilder();
		name.append(c);
		while (isNameChar(text.lookAhead())) {
			name.append(text.advance());
		}
		String n = name.toString();
		if (n.equals("and")) return and();
		else if (n.equals("or")) return or();
		else return new Token(NAME, n);
	}

	private Token number() {
		char c = text.current();
		StringBuilder number = new StringBuilder();
		number.append(c);
		while (isNumberChar(text.lookAhead())) {
			number.append(text.advance());
		}
		String n = number.toString();
		return n.contains(".") ?  new Token(DOUBLE, parseDouble(n)) : new Token(INTEGER, parseInt(n));
	}

	private Token string() {
		StringBuilder value = new StringBuilder();
		char c = text.advance();
		while (!isStringQuote(c)) {
			if (c == '\\') /* Escape ' and " */c = text.advance();
			value.append(text.current());
			c = text.advance();
		}		
		return new Token(STRING, value.toString());
	}

	private Token equals() {
		return new Token(EQUAL);
	}
	
	private Token notEqual() {
        expect('=');
		return new Token(NOT_EQUAL);
	}

	private Token colon() {
		return new Token(COLON);
	}

    private Token arrow() {
        expect('>');
        return new Token(ARROW);
    }

	private Token and() {
		return new Token(AND);
	}

	private Token or() {
		return new Token(OR);
	}
	
	/* --- Character identifiers --- */

	private boolean isNameStartChar(char c) {
		return isJavaIdentifierStart(c);
	}

	private boolean isNameChar(char c) {
		return !isEOF(c) && Character.isJavaIdentifierPart(c);
	}
	
	private boolean isStringQuote(char c) {
		return c == '\'' || c == '"';
	}

	private boolean isNumberChar(char c) {
		return !isEOF(c) && (isDigit(c) || c == '.');
	}

    /* --- Other helpers --- */

    private void expect(char c) throws LexerException {
        if (text.advance() != c) {
            throw new LexerException(format("Expected %s, but found %s", c, text.current()));
        }
    }

}
