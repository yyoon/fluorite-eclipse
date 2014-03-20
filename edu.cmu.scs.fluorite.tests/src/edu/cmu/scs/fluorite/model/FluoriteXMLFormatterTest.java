package edu.cmu.scs.fluorite.model;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.util.Calendar;

import org.junit.Before;
import org.junit.Test;

public class FluoriteXMLFormatterTest {
	
	private long mTimestamp;
	private FluoriteXMLFormatter mFormatter;
	private String mHead;
	
	@Before
	public void setUp() {
		mTimestamp = Calendar.getInstance().getTime().getTime();
		mFormatter = new FluoriteXMLFormatter(mTimestamp);
		mHead = mFormatter.getHead(null);
	}
	
	@Test
	public void logHeadMustContainTimestamp() {
		assertThat(mHead, containsString(String.format("startTimestamp=\"%1s\"", mTimestamp)));
	}

	@Test
	public void logHeadMustContainVersion() {
		assertThat(mHead, containsString("logVersion="));
	}
	
	@Test
	public void logHeadMustContainOSName() {
		assertThat(mHead, containsString("osName="));
	}
	
	@Test
	public void logHeadMustContainOSVersion() {
		assertThat(mHead, containsString("osVersion="));
	}
	
	@Test
	public void logHeadMustContainLineSeparator() {
		assertThat(mHead, containsString("lineSeparator="));
	}

}
