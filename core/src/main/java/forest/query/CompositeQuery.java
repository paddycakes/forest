package forest.query;

import static java.util.Arrays.asList;

import java.util.Collections;
import java.util.List;

public abstract class CompositeQuery implements Query {

	protected final List<Query> underlying;

	public CompositeQuery(Query... underlying) {
		this.underlying = Collections.unmodifiableList(asList(underlying));
	}

	public List<Query> getUnderlying() {
		return underlying;
	}

}