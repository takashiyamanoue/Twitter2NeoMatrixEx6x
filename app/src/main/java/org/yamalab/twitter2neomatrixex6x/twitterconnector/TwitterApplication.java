package org.yamalab.twitter2neomatrixex6x.twitterconnector;

public interface TwitterApplication {
	public String getOutput();
	public boolean parseCommand(String cmd, String v);
}
