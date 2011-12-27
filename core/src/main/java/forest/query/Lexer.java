package forest.query;

import static forest.query.StringReader.isEOF;
import static forest.query.TokenType.AND;
import static forest.query.TokenType.ARROW;
import static forest.query.TokenType.COLON;
import static forest.query.TokenType.DASH;
import static forest.query.TokenType.DATE;
import static forest.query.TokenType.DOUBLE;
import static forest.query.TokenType.EOF;
import static forest.query.TokenType.EQUAL;
import static forest.query.TokenType.INTEGER;
import static forest.query.TokenType.NAME;
import static forest.query.TokenType.NOT_EQUAL;
import static forest.query.TokenType.OR;
import static forest.query.TokenType.STRING;
import static forest.query.TokenType.TIME;
import static forest.query.TokenType.WHITE_SPACE;
import static java.lang.Character.isDigit;
import static java.lang.Character.isJavaIdentifierStart;
import static java.lang.Character.isWhitespace;
import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;
import static java.lang.String.format;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public class Lexer {

	private static final DateTimeFormatter DATE_PARSER = DateTimeFormat.forPattern("d/M/yy");
	private static final DateTimeFormatter TIME_PARSER = ISODateTimeFormat.localTimeParser();
	private final StringReader text;

	public Lexer(String text) {
		this.text = new StringReader(text);
	}

	public Token nextToken() {
		char c = text.advance();
		if (isWhitespace(c)) return whiteSpace();
		if (isEOF(c)) return eof();
		if (isNameStartChar(c)) return name();
		if (isDigit(c)) return numberDateOrTime();
		if (isStringQuote(c)) return string();
		switch (c) {
		case '=': return equals();
		case '!': return notEqual();
		case ':': return colon();
        case '-': return dashOrArrow();
		case '&': return and();
		case '|': return or();
		default: throw new LexerException("Unexpected character: " + c);
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

	private Token numberDateOrTime() {
		char c = text.current();
		StringBuilder number = new StringBuilder();
		number.append(c);
		while (!isEOF(c = text.lookAhead()) && (isDigit(c) || c == '.' || c == '/' || c == ':')) {
			number.append(text.advance());
		}
		String n = number.toString();
		if (n.contains("/")) {
			return new Token(DATE, DATE_PARSER.parseDateTime(n).toLocalDate());
		} else if (n.contains(":")) {
			return new Token(TIME, TIME_PARSER.parseDateTime(n).toLocalTime());
		} else if (n.contains(".")) {
			return new Token(DOUBLE, parseDouble(n));
		} else {
			return new Token(INTEGER, parseInt(n));
		}
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

    private Token dashOrArrow() {
    	if (text.lookAhead() == '>') {
    		text.advance();
    		return new Token(ARROW);
    	} else {
    		return new Token(DASH);
    	}
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

    /* --- Other helpers --- */

    private void expect(char c) throws LexerException {
        if (text.advance() != c) {
            throw new LexerException(format("Expected %s, but found %s", c, text.current()));
        }
    }
    
    @Override
    public String toString() {
    	return new StringBuilder("[Lexer: ").append(text.toString()).append("]").toString();
    }

}
