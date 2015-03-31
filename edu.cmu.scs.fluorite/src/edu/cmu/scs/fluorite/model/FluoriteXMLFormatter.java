package edu.cmu.scs.fluorite.model;

import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Monitor;

import edu.cmu.scs.fluorite.commands.ICommand;
import edu.cmu.scs.fluorite.util.Utilities;

public class FluoriteXMLFormatter extends Formatter {

	public FluoriteXMLFormatter(long startTimestamp) {
		mStartTimestamp = startTimestamp;
		mLogVersion = getLogVersion();
	}

	private long mStartTimestamp;
	private String mLogVersion;
	
	// If this value is returned, there's something wrong with the version recognizing logic. 
	private static final String LOG_VERSION = "0.5.0.unknown";
	
	/**
	 * Programatically determine the Fluorite version number and use it for log version.
	 * @return Log version (which is the same as Fluorite version)
	 */
	private static String getLogVersion() {
		String versionStr = LOG_VERSION;

		try {
			versionStr = Platform.getBundle("edu.cmu.scs.fluorite").getVersion().toString();
		} catch (Exception e) {
			// Do nothing but just print out the stack trace.
			// There might be some null pointer exception or such.
			e.printStackTrace();
		}

		return versionStr;
	}

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
		String lineSeparator = Utilities.NewLine;
		lineSeparator = lineSeparator.replace("\r", "\\r");
		lineSeparator = lineSeparator.replace("\n", "\\n");
		
		final Display display = Display.getDefault();
		MonitorBoundsExtractor mbe = new MonitorBoundsExtractor(display);
		display.syncExec(mbe);
		
		Monitor[] monitors = mbe.getMonitors();
		Rectangle[] bounds = mbe.getBounds();
		int numMonitors = monitors != null ? monitors.length : 0;
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < numMonitors; ++i) {
			if (i > 0) { builder.append(", "); }
			Rectangle r = bounds[i];
			builder.append(String.format("[%d, %d, %d, %d]", r.x, r.y, r.width, r.height));
		}
		
		return String.format(
				"<Events"
				+ " startTimestamp=\"%1$s\""
				+ " logVersion=\"%2$s\""
				+ " osName=\"%3$s\""
				+ " osVersion=\"%4$s\""
				+ " lineSeparator=\"%5$s\""
				+ " numMonitors=\"%6$d\""
				+ " monitorBounds=\"%7$s\""
				+ ">%n",
				mStartTimestamp,
				mLogVersion,
				System.getProperty("os.name"),
				System.getProperty("os.version"),
				lineSeparator,
				numMonitors,
				builder.toString());
	}

	@Override
	public String getTail(Handler h) {
		return "</Events>" + Utilities.NewLine;
	}
	
	private static class MonitorBoundsExtractor implements Runnable {

		private Display display;
		private Monitor[] monitors;
		private Rectangle[] bounds;
		
		public MonitorBoundsExtractor(Display display) {
			this.display = display;
			this.monitors = null;
			this.bounds = null;
		}
		
		public Monitor[] getMonitors() {
			return this.monitors;
		}
		
		public Rectangle[] getBounds() {
			return this.bounds;
		}
		
		@Override
		public void run() {
			this.monitors = this.display.getMonitors();
			if (this.monitors != null) {
				this.bounds = new Rectangle[this.monitors.length];
				for (int i = 0; i < this.bounds.length; ++i) {
					this.bounds[i] = this.monitors[i].getBounds();
				}
			}
		}
		
	}

}
