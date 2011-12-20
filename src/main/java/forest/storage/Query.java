package forest.storage;

import forest.event.Event;

public interface Query {

	boolean matches(Event event);

}
