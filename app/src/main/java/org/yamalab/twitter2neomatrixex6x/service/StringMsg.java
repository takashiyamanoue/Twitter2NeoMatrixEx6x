package org.yamalab.twitter2neomatrixex6x.service;

public class StringMsg{
	private String val;
	private String cmd;
	public StringMsg(String c, String v){
		this.cmd=c; this.val=v;
	}
	public String getCommand(){
		return cmd;
	}
	public String getValue(){
		return val;
	}
}
