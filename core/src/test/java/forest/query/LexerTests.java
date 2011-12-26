package forest.query;

import static forest.query.TokenType.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

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
