package forest.query;

import static forest.query.TokenType.EQUAL;
import static forest.query.TokenType.NAME;
import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class TokenTests {
	
	@Test
	public void toStringWithoutText() {
		assertEquals("<Token:EQUAL>", new Token(EQUAL).toString());
	}
	
	@Test
	public void toStringWithText() {
		assertEquals("<Token:NAME 'param'>", new Token(NAME, "param").toString());
	}

}
