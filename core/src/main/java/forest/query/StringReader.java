package forest.query;

public class StringReader {
	
	private static final char EOT = '\u0004';
	
	private final String text;
	private int position = -1;

	public StringReader(String text) {
		this.text = text;
	}
	
	public char advance() {
		if (position < text.length()) position++;
		return current();
	}

	public char current() {
		return getCharAt(position);
	}

	public char lookAhead() {
		return getCharAt(position + 1);
	}

	private char getCharAt(int position) {
		return position < text.length() ? text.charAt(position) : EOT;
	}
	
	public static boolean isEOF(char c) {
		return c == EOT;
	}
	
}
