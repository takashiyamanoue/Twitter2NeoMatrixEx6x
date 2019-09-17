package org.yamalab.twitter2neomatrixex6x.service;

public class FontTransmitter {
	/*
	 Android                         Arduino
	            <--------------       'y'  (yes, can send)
	            <--------------       'n'  (no, can't send)
	  
	  (if yes)
	  'f'     --------------->
	  font-position
	  font[32]
	    	            
	 
	 */
	AdkService service;
	boolean sendEnabled;
	FontAccessor fontAccessor;
	String currentTweet;
	String nextTweet;
	int fontPosition;
	char[] charArray;
	int times;
	private static String TAG="FontTransmitter";
	public FontTransmitter(AdkService s){
		service=s;
		times=-1;
	}
	public void setSendEnable(String f){
		if(!(isCharArrayReady())) return;
		if(f.equals("r")){
			if(startsWithColorCommand()){
			}
			else {
				byte[] cf = getCurrentFont();
				if (cf == null) return;
				service.outputFontToDevice(cf);
			}
		}
	}
    public boolean putString(String x){
    	if(x==null) return false;
    	if(x.equals("")) return true;
    	if(nextTweet!=null) return false;
    	if(currentTweet==null){
    		currentTweet=x;
    	}
    	else{
	        nextTweet=x;
    	}
    	return true;
    }
    private byte[] getCurrentFont(){
        if(charArray==null) return null;
        byte[] fa=null;
        try {
			fa = fontAccessor.getFontJIS(charArray[fontPosition]);
		}
		catch(Exception e){
        	fa=fontAccessor.getAsciiFont('*');
		}
		if(fa==null){
//			service.showLog(TAG+"-getCurrentFont, fa== null.");
			return null;
		}
//		printFontToLog(fa);
		fontPosition++;
        if(fontPosition>=currentTweet.length()){
        	fontPosition=0;
        	if(nextTweet!=null){
        		currentTweet=nextTweet;
        		nextTweet=null;
        		try {
					charArray = fontAccessor.getJISString(currentTweet);
				}
				catch(Exception e){
        			charArray="font error!....".toCharArray();
				}
        	}
        }
        return fa;
    }
	/*
	Color command in the message:
	   (red)
	   (blue)
	   (green)
	   (yellow)
	   (magenta)
	   (cyan)
	   (white)
	   (rainbow)
	   (rgb:<r>,<g>,<b>)...<r>,<g>,<b>...number:0-255
	 */
	private boolean isCharArrayReady(){
		if(fontAccessor==null){
			fontAccessor=service.getFontAccessor();
		    if(fontAccessor==null) {
				service.showLog(TAG + "-getCurrentFont fontAccessor== null.");
				return false;
			}
		}
		if(charArray==null) {
			if (currentTweet != null) {
				charArray = fontAccessor.getJISString(currentTweet + "  ");
				fontPosition = 0;
			}
			if (charArray == null) {
				service.showLog(TAG + "-getCurrentFont, charArray==null.");
				return false;
			}
		}
        return true;
	}
	private boolean isStartsWith(String x){
//		if(!(isCharArrayReady())) return false;
		int len=x.length();
		for(int i=0;i<len;i++){
			char cx=x.charAt(i);
			if(charArray[fontPosition+i]!=cx){
				return false;
			}
		}
		return true;
	}
	public boolean startsWithColorCommand(){
		byte[] colorCommand=new byte[5];
		if(isStartsWith("(red)")){
			fontPosition=fontPosition+("(red)".length());
			colorCommand[0]='c';
			colorCommand[1]='d';
			colorCommand[2]=(byte)255;
			colorCommand[3]=(byte)0;
			colorCommand[4]=(byte)0;
			service.outputCommandToDevice(colorCommand);
			return true;
		}
		else if(isStartsWith("(green)")){
			fontPosition=fontPosition+("(green)".length());
			colorCommand[0]='c';
			colorCommand[1]='d';
			colorCommand[2]=(byte)0;
			colorCommand[3]=(byte)255;
			colorCommand[4]=(byte)0;
			service.outputCommandToDevice(colorCommand);
			return true;
		}
		else if(isStartsWith("(blue)")){
			fontPosition=fontPosition+("(blue)".length());
			colorCommand[0]='c';
			colorCommand[1]='d';
			colorCommand[2]=(byte)0;
			colorCommand[3]=(byte)0;
			colorCommand[4]=(byte)255;
			service.outputCommandToDevice(colorCommand);
			return true;
		}
		else if(isStartsWith("(yellow)")){
			fontPosition=fontPosition+("(yellow)".length());
			colorCommand[0]='c';
			colorCommand[1]='d';
			colorCommand[2]=(byte)255;
			colorCommand[3]=(byte)255;
			colorCommand[4]=(byte)0;
			service.outputCommandToDevice(colorCommand);
			return true;
		}
		else if(isStartsWith("(magenta)")){
			fontPosition=fontPosition+("(magenta)".length());
			colorCommand[0]='c';
			colorCommand[1]='d';
			colorCommand[2]=(byte)255;
			colorCommand[3]=(byte)0;
			colorCommand[4]=(byte)255;
			service.outputCommandToDevice(colorCommand);
			return true;
		}
		else if(isStartsWith("(cyan)")){
			fontPosition=fontPosition+("(cyan)".length());
			colorCommand[0]='c';
			colorCommand[1]='d';
			colorCommand[2]=(byte)0;
			colorCommand[3]=(byte)255;
			colorCommand[4]=(byte)255;
			service.outputCommandToDevice(colorCommand);
			return true;
		}
		else if(isStartsWith("(white)")){
			fontPosition=fontPosition+("(white)".length());
			colorCommand[0]='c';
			colorCommand[1]='d';
			colorCommand[2]=(byte)200;
			colorCommand[3]=(byte)200;
			colorCommand[4]=(byte)200;
			service.outputCommandToDevice(colorCommand);
			return true;
		}
		else if(isStartsWith("(pink)")){
			fontPosition=fontPosition+("(pink)".length());
			colorCommand[0]='c';
			colorCommand[1]='d';
			colorCommand[2]=(byte)200;
			colorCommand[3]=(byte)50;
			colorCommand[4]=(byte)50;
			service.outputCommandToDevice(colorCommand);
			return true;
		}
		else if(isStartsWith("(orange)")){
			fontPosition=fontPosition+("(orange)".length());
			colorCommand[0]='c';
			colorCommand[1]='d';
			colorCommand[2]=(byte)233;
			colorCommand[3]=(byte)163;
			colorCommand[4]=(byte)0;
			service.outputCommandToDevice(colorCommand);
			return true;
		}
		else if(isStartsWith("(brown)")){
			fontPosition=fontPosition+("(brown)".length());
			colorCommand[0]='c';
			colorCommand[1]='d';
			colorCommand[2]=(byte)100;
			colorCommand[3]=(byte)77;
			colorCommand[4]=(byte)55;
			service.outputCommandToDevice(colorCommand);
			return true;
		}
		else if(isStartsWith("(purple)")){
			fontPosition=fontPosition+("(purple)".length());
			colorCommand[0]='c';
			colorCommand[1]='d';
			colorCommand[2]=(byte)128;
			colorCommand[3]=(byte)0;
			colorCommand[4]=(byte)128;
			service.outputCommandToDevice(colorCommand);
			return true;
		}
		else if(isStartsWith("(rainbow)")){
			fontPosition=fontPosition+("(rainbow)".length());
			colorCommand[0]='c';
			colorCommand[1]='r';
			colorCommand[2]=(byte)255;
			colorCommand[3]=(byte)255;
			colorCommand[4]=(byte)255;
			service.outputCommandToDevice(colorCommand);
			return true;
		}

		else if(isStartsWith("(rgb:")){
			int[] nw=new int[1];
			int cr,cg,cb;
			int preFontPosition=fontPosition;
			fontPosition=fontPosition+("(rgb:".length());
			if(!isNumX(nw)) { fontPosition=preFontPosition; return false;}
			cr=nw[0];
			if(charArray[fontPosition]!=',') { fontPosition=preFontPosition; return false;}
			fontPosition++;
			if(!isNumX(nw)) {fontPosition=preFontPosition; return false;}
			cg=nw[0];
			if(charArray[fontPosition]!=',') {fontPosition=preFontPosition; return false;}
			fontPosition++;
			if(!isNumX(nw)) {fontPosition=preFontPosition; return false;}
			if(charArray[fontPosition]!=')') {fontPosition=preFontPosition; return false;}
			fontPosition++;
			cb=nw[0];
			colorCommand[0]='c';
			colorCommand[1]='d';
			colorCommand[2]=(byte)cr;
			colorCommand[3]=(byte)cg;
			colorCommand[4]=(byte)cb;
			service.outputCommandToDevice(colorCommand);
			return true;
		}
		return false;
	}
	private boolean isNumX(int n[]){
		int w=0;
		if(!isNumber(charArray[fontPosition])) return false;
		w=(int)(charArray[fontPosition]);
		fontPosition++;
		while(isNumber(charArray[fontPosition])){
			w=w*10+(int)(charArray[fontPosition]);
			fontPosition++;
		}
		n[0]=w;
		return true;
	}
    private boolean isNumber(char x){
		if(x<'0') return false;
		if('9'<x) return false;
		return true;
	}
}
