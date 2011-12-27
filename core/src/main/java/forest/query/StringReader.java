package forest.query;

public class StringReader {

	public static final char EOT = '\u0004';

	private final String text;
	private int position = -1;

	public StringReader(String text) {
		assert text != null;
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

	@Override
	public String toString() {
		if (text.length() == 0) return "\"\"[EOF]"; 
		StringBuilder s = new StringBuilder();
		switch (position) {
		case -1:
			return s.append("[]\"").append(text).append("\"EOF").toString();
		case 0:
			return s.append("\"[").append(text.charAt(0)).append("]").append(text.substring(1)).append("\"EOF").toString();
		default:
			return s.append("\"").append(text.substring(0, position)).append("[").append(text.charAt(position)).append("]")
				.append(text.substring(position + 1)).append("\"EOF").toString();
		}
	}

}
