package forest.query;

import static forest.query.Operator.GREATER_THAN_EQUAL;
import static forest.query.Operator.LESS_THAN_EQUAL;
import static forest.query.TokenType.AND;
import static forest.query.TokenType.COLON;
import static forest.query.TokenType.DASH;
import static forest.query.TokenType.DATE;
import static forest.query.TokenType.DOUBLE;
import static forest.query.TokenType.EOF;
import static forest.query.TokenType.EQUAL;
import static forest.query.TokenType.INTEGER;
import static forest.query.TokenType.NAME;
import static forest.query.TokenType.OR;
import static forest.query.TokenType.STRING;
import static forest.query.TokenType.TIME;
import static forest.query.TokenType.WHITE_SPACE;
import static java.lang.String.format;

import java.util.Iterator;
import java.util.LinkedList;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

public class QueryParser {

	public static final String PROP_PREFIX = "prop";
	public static final String FROM_PREFIX = "from";
	public static final String TO_PREFIX = "to";

	private Lexer lexer;
	private final LinkedList<Token> lookAheads = new LinkedList<Token>();

	public QueryParser(String q) {
		lexer = new Lexer(q);
	}

	public Query query() {
		Query expression = expression();
		if (lookAhead(AND)) {
			consume(AND);
			AndQuery query = new AndQuery(expression, expression());
			consume(EOF);
			return query;
		} else if (lookAhead(OR)) {
			consume(OR);
			OrQuery query = new OrQuery(expression, expression());
			consume(EOF);
			return query;			
		}
		if (expression != null) {
			consume(EOF);
			return expression;
		}
		throw new ParseException("Unable to parse: " + lexer.toString());
	}

	private Query expression() {
		Token token = nextToken();
		if (PROP_PREFIX.equals(token.getStringValue())) {
			consume(COLON);
			String property = consume(NAME).getStringValue();
			consume(EQUAL);
			// TODO: Implement <, <=, >=, > property query.
			return new ParameterQuery(property, value(), Operator.EQUAL);
		}
		Operator operator;
		if (FROM_PREFIX.equals(token.getStringValue())) {
			operator = GREATER_THAN_EQUAL;
		} else if (TO_PREFIX.equals(token.getStringValue())) {
			operator = LESS_THAN_EQUAL;
		} else {
			throw new ParseException(format("Expected %s token with value %s or %s, but found %s", NAME,
					FROM_PREFIX, TO_PREFIX, token));
		}
		consume(EQUAL);
		return new TimeQuery(dateTimeValue(), operator);
	}

	private Object value() {
		consumeOptionalWhiteSpace();
		Token valueToken = nextToken();
		switch (valueToken.getType()) {
		case INTEGER:
			return valueToken.getIntValue();
		case DOUBLE:
			return valueToken.getDoubleValue();
		case STRING:
			return valueToken.getStringValue();
		default:
			throw new ParseException(format("Expected %s, %s or %s token, but got %s.", INTEGER, DOUBLE, STRING,
					valueToken));
		}
	}

	private DateTime dateTimeValue() {
		LocalDate date = consume(DATE).getLocalDateValue();
		LocalTime time = consume(DASH, TIME).getLocalTimeValue();
		return date.toDateTime(time);
	}

	private boolean lookAhead(TokenType... tokens) {
		loadAhead(tokens.length);
		if (lookAheads.size() < tokens.length) return false;
		Iterator<Token> la = lookAheads.iterator();
		for (TokenType token : tokens) {
			if (token != la.next().getType()) return false;
		}
		return true;
	}

	private Token nextToken() {
		loadAhead(1);
		return lookAheads.remove();
	}

	private Token consume(TokenType... types) {
		Token lastToken = null;
		for (TokenType type : types) {
			consumeOptionalWhiteSpace();
			lastToken = nextToken();
			if (lastToken.getType() != type) {
				throw new ParseException(format("Expected %s, but found %s", type, lastToken));
			}
		}
		return lastToken;
	}

	private void consumeOptionalWhiteSpace() {
		while (lookAhead(WHITE_SPACE)) {
			nextToken();
		}
	}

	private void loadAhead(int numTokens) {
		while (lookAheads.size() < numTokens) {
			Token nextToken = lexer.nextToken();
			if (nextToken.getType() != WHITE_SPACE) {
				lookAheads.add(nextToken);
			}
		}
	}

}