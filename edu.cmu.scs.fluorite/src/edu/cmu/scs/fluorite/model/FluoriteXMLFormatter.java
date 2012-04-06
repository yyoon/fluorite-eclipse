package edu.cmu.scs.fluorite.model;

import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import edu.cmu.scs.fluorite.commands.ICommand;
import edu.cmu.scs.fluorite.util.Utilities;

public class FluoriteXMLFormatter extends Formatter {

	public FluoriteXMLFormatter(long startTimestamp) {
		mStartTimestamp = startTimestamp;
	}

	private long mStartTimestamp;
	private static final String LOG_VERSION = "0.2.1";

	@Override
	public String format(LogRecord rec) {
		Object[] params = rec.getParameters();
		if (params.length != 1 || !(params[0] instanceof ICommand)) {
			return null;
		}

		ICommand command = (ICommand) params[0];
		return command.persist();
	}

	@Override
	public String getHead(Handler h) {
		return "<Events startTimestamp=\"" + Long.toString(mStartTimestamp)
				+ "\" logVersion=\"" + LOG_VERSION + "\">" + Utilities.NewLine;
	}

	@Override
	public String getTail(Handler h) {
		return "</Events>" + Utilities.NewLine;
	}

}
