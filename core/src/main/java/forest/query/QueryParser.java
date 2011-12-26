package forest.query;


import java.util.Iterator;
import java.util.LinkedList;

import static forest.query.TokenType.*;
import static java.lang.String.format;

public class QueryParser {

    public static final String PROP_PREFIX = "prop";
    public static final String TIME_PREFIX = "time";

	private Lexer lexer;
    private final LinkedList<Token> lookAheads = new LinkedList<Token>();

    public QueryParser(String q) {
        lexer = new Lexer(q);
	}
    
    public Query query() {
        return propertyEqualsQuery();
    }

    private boolean lookAhead(TokenType... tokens) {
        loadAhead(tokens.length);
        if (lookAheads.size() < tokens.length)
            return false;
        Iterator<Token> la = lookAheads.iterator();
        for (TokenType token : tokens) {
            if (token != la.next().getType())
                return false;
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

    private Query propertyEqualsQuery() {
        if (lookAhead(NAME)) {
            Token token = nextToken();
            if (PROP_PREFIX.equals(token.getStringValue())) {
                consume(COLON);
                String property = consume(NAME).getStringValue();
                consume(EQUAL);
                Integer value = consume(INTEGER).getIntValue();
                return new PropertyEqualsQuery(property, value);
            }
        }
		return null;
	}

    private void loadAhead(int numTokens) {
        while (lookAheads.size() < numTokens) {
            lookAheads.add(lexer.nextToken());
        }
    }

}