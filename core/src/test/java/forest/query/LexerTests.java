package forest.query;

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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.junit.After;
import org.junit.Test;


public class LexerTests {
	
	private Lexer lexer;

	@After
	public void assertEOF() {
		assertNextTokenMatches(EOF);
	}

	@Test
	public void singleEqualsWithWhiteSpace() {
		lexer = new Lexer(" = \t ");
		assertWhiteSpace();
		assertNextTokenMatches(EQUAL);
		assertWhiteSpace();
	}
	
	@Test
	public void singleNameWithWhiteSpace() {
		lexer = new Lexer("\t Emily ");
		assertWhiteSpace();
		assertNextTokenMatches(NAME, "Emily");
		assertWhiteSpace();
	}

	@Test
	public void string() {
		lexer = new Lexer("'one' \"two\" '\\\'three\\\'' \"\\\"four\\\"\"");
		assertNextTokenMatches(STRING, "one");
		assertWhiteSpace();
		assertNextTokenMatches(STRING, "two");
		assertWhiteSpace();
		assertNextTokenMatches(STRING, "\'three\'");
		assertWhiteSpace();
		assertNextTokenMatches(STRING, "\"four\"");
	}
	
	@Test
	public void anInteger() {
		lexer = new Lexer("\t 56 ");
		assertWhiteSpace();
		assertNextTokenMatches(INTEGER, 56);
		assertWhiteSpace();
	}
	
	@Test
	public void aDouble() {
		lexer = new Lexer("\t 56.245 ");
		assertWhiteSpace();
		assertNextTokenMatches(DOUBLE, 56.245);
		assertWhiteSpace();
	}
	
	@Test
	public void date() {
		lexer = new Lexer("25/12/2011");
		assertNextTokenMatches(DATE, new LocalDate(2011, 12, 25));
	}
	
	@Test
	public void time() {
		lexer = new Lexer("14:23:08.482");
		assertNextTokenMatches(TIME, new LocalTime(14, 23, 8, 482));
	}
	
	@Test
	public void dateTime() {
		lexer = new Lexer("2/1/2012-8:07");
		assertNextTokenMatches(DATE, new LocalDate(2012, 1, 2));
		assertNextTokenMatches(DASH);
		assertNextTokenMatches(TIME, new LocalTime(8, 7, 0, 0));
	}
	
	@Test
	public void and() {
		lexer = new Lexer("and");
		assertNextTokenMatches(AND);
	}
	
	@Test
	public void andWithAmpersand() {
		lexer = new Lexer("&");
		assertNextTokenMatches(AND);
	}
	
	@Test
	public void or() {
		lexer = new Lexer("or");
		assertNextTokenMatches(OR);
	}
	
	@Test
	public void orWithBar() {
		lexer = new Lexer("|");
		assertNextTokenMatches(OR);
	}
	
	@Test
	public void colon() {
		lexer = new Lexer(":");
		assertNextTokenMatches(COLON);		
	}
	
	@Test
	public void dash() {
		lexer = new Lexer("-");
		assertNextTokenMatches(DASH);		
	}

    @Test
    public void arrow() {
        lexer = new Lexer("->");
        assertNextTokenMatches(ARROW);
    }
	
	@Test
	public void notEqual() {
		lexer = new Lexer("!=");
		assertNextTokenMatches(NOT_EQUAL);		
	}
	
	@Test
	public void simpleQuery() {
		lexer = new Lexer("param:widgetId= 42");
		assertNextTokenMatches(NAME, "param");
		assertNextTokenMatches(COLON);
		assertNextTokenMatches(NAME, "widgetId");
		assertNextTokenMatches(EQUAL);
		assertWhiteSpace();
		assertNextTokenMatches(INTEGER, 42);
	}
	
	@Test
	public void andQuery() {
		lexer = new Lexer("prefix:widgetId=3.4532 and thingy !=  'blah'");
		assertNextTokenMatches(NAME, "prefix");
		assertNextTokenMatches(COLON);
		assertNextTokenMatches(NAME, "widgetId");
		assertNextTokenMatches(EQUAL);
		assertNextTokenMatches(DOUBLE, 3.4532);
		assertWhiteSpace();
		assertNextTokenMatches(AND);
		assertWhiteSpace();
		assertNextTokenMatches(NAME, "thingy");
		assertWhiteSpace();
		assertNextTokenMatches(NOT_EQUAL);
		assertWhiteSpace();
		assertNextTokenMatches(STRING, "blah");
	}
	
	@Test
	public void toStringShowsReaderPosition() {
		lexer = new Lexer("param:widgetId=12");
		lexer.nextToken();
		lexer.nextToken();
		
		assertEquals("[Lexer: \"param[:]widgetId=12\"EOF]", lexer.toString());
		lexer.nextToken();
		lexer.nextToken();
		lexer.nextToken();
	}
	
	
	/* Private */
	
	private void assertWhiteSpace() {
		assertNextTokenMatches(WHITE_SPACE);
	}
	
	private void assertNextTokenMatches(TokenType expectedType) {
		Token actual = lexer.nextToken();
		assertEquals(expectedType, actual.getType());
		assertNull(actual.getValue());
	}
	
	private void assertNextTokenMatches(TokenType expectedType, Object expectedValue) {
		Token actual = lexer.nextToken();
		assertEquals(expectedType, actual.getType());
		assertEquals(expectedValue, actual.getValue());
	}

}
