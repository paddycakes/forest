package forest.storage;

public class Queries {

	public static Query eq(String variable, Object value) {
		return new EqualsQuery(variable, value);
	}

}
