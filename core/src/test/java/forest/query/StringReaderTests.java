package forest.query;

import static forest.query.StringReader.EOT;
import static forest.query.StringReader.isEOF;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;


public class StringReaderTests {
	
	@Test
	public void advance() {
		StringReader reader = new StringReader("Go!");
		assertEquals('G', reader.advance());
		assertEquals('o', reader.advance());
		assertEquals('!', reader.advance());
		assertEquals(EOT, reader.advance());
		// It will keep returning EOFs:
		assertEquals(EOT, reader.advance());
		assertEquals(EOT, reader.advance());
	}
	
	@Test
	public void current( ){
		StringReader reader = new StringReader("Go!");
		reader.advance();
		reader.advance();
		assertEquals('o', reader.current());
	}
	
	@Test
	public void lookAhead() {
		StringReader reader = new StringReader("Go!");
		assertEquals('G', reader.lookAhead());
		reader.advance();
		assertEquals('o', reader.lookAhead());
	}
	
	@Test
	public void isEof() {
		StringReader reader = new StringReader("G");
		reader.advance();
		assertTrue(isEOF(reader.advance()));
	}
	
	@Test
	public void toStringShowsCurrentPosition() {
		StringReader reader = new StringReader("Go!");
		reader.advance();
		reader.advance();
		assertEquals("\"G[o]!\"EOF", reader.toString());		
	}
	
	@Test
	public void toStringAtStartPosition() {
		StringReader reader = new StringReader("Go!");
		assertEquals("[]\"Go!\"EOF", reader.toString());		
	}
	
	@Test
	public void toStringAtFirstPosition() {
		StringReader reader = new StringReader("Go!");
		reader.advance();
		assertEquals("\"[G]o!\"EOF", reader.toString());		
	}
	
	@Test
	public void toStringForEmptyString() {
		StringReader reader = new StringReader("");
		assertEquals("\"\"[EOF]", reader.toString());		
	}

}
