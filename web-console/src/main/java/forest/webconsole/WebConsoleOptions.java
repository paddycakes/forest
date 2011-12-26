package forest.webconsole;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.Parser;
import org.apache.commons.cli.PosixParser;

public class WebConsoleOptions {

	private static final String[] HELP = new String[] { "h", "help" };
	private static final String[] PORT = new String[] { "p", "server.port" };
	private static final String[] STORE_FILE = new String[] { "f", "store.file" };
	private static final String[] STORE_TYPE = new String[] { "t", "store.type" };
	
	private final CommandLine commandLine;
	private Options options;

	public WebConsoleOptions(String[] args) throws ParseException {
		options = new Options();
		options.addOption(HELP[0], HELP[1], false, "print this help message");
		options.addOption(PORT[0], PORT[1], true, "server port number");
		options.addOption(STORE_FILE[0], STORE_FILE[1], true, "forest event store file");
		options.addOption(STORE_TYPE[0], STORE_TYPE[1], true, "forest event store file type");
		Parser parser = new PosixParser();
		commandLine = parser.parse(options, args);
	}

	public int getPort() {
		String port = commandLine.getOptionValue(PORT[0]);
		return port != null ? Integer.valueOf(port) : 8080;
	}

	public boolean printHelp() {
		if (commandLine.hasOption(HELP[0])) {
			HelpFormatter helpFormatter = new HelpFormatter();
			helpFormatter.printHelp("forest-web-console [options...]", options);
			return true;
		} else {
			return false;
		}
	}

}
