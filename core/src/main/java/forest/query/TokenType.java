package forest.query;

public enum TokenType {
	
	WHITE_SPACE,
	NAME,
	COLON,
    ARROW,
	EQUAL,
	NOT_EQUAL,
	AND,
	OR,
	INTEGER,
	DOUBLE,
	STRING,
	EOF;
	
	/*
	 * - Name spaced identifier
	 * - Simple identifier
	 * - Expression (just numbers and string at the moment?)
	 * - Statement
	 * 
	 * <compund_statement>    ::= <statement> AND <compund_statement> | <statement>
	 * <statement>            ::= <id> EQUAL <expression> | <id> NOT_EQUAL <expression>
	 * <id>                   ::= <namespaced_id> | <simple_id>
	 * <namespaced_id>        ::= <namespace> COLON <simple_id>
	 * <namespace>            ::= NAME
	 * <simple_id>            ::= NAME
	 * <expression>           ::= DOUBLE | INTEGER | STRING
	 * <optional_white_space> ::= " " <optional_white_space> | ""
	 * 
	 * 
	 * <composite_query> ::= <query> AND <composite_query> | <query>
	 * <query>           ::= <id> EQUAL <expression> |
	 */

}
