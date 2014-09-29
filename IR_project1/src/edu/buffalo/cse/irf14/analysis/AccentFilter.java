package edu.buffalo.cse.irf14.analysis;
import java.util.HashMap;
/**
 * @author Harsh
 * @parameter {@link TokenStream}
 * @return {@link TokenStream}
 * @description This class contains the logic for removing accents according to test cases.
 */
public class AccentFilter extends TokenFilter {

public static long accentTime=0;
	TokenStream tStream=null;
	boolean accentFlag=false;
	public AccentFilter(TokenStream stream) {
		super(stream);
		this.tStream=stream;
	}



	TokenStream tokenStream=new TokenStream();
	private static final HashMap<String,String> accentMap =  new HashMap<String,String>() ;
	static{
		accentMap.put("À", "A");
		accentMap.put("Á", "A");
		accentMap.put("Â", "A");
		accentMap.put("Ã", "A");
		accentMap.put("Ä", "A");
		accentMap.put("Å", "A");
		accentMap.put("Æ", "AE");
		accentMap.put("Ç", "C");
		accentMap.put("È", "E");
		accentMap.put("É", "E");
		accentMap.put("Ê", "E");
		accentMap.put("Ë", "E");
		accentMap.put("Ì", "I");
		accentMap.put("Í", "I");
		accentMap.put("Î", "I");
		accentMap.put("Ï", "I");
		accentMap.put("Ĳ", "IJ");
		accentMap.put("Ð", "D");
		accentMap.put("Ñ", "N");
		accentMap.put("Ò", "O");
		accentMap.put("Ó", "O");
		accentMap.put("Ô", "O");
		accentMap.put("Õ", "O");
		accentMap.put("Ö", "O");
		accentMap.put("Ø", "O");
		accentMap.put("Œ", "OE");
		accentMap.put("Þ","TH");
		accentMap.put("Ù", "U");
		accentMap.put("Ú", "U");
		accentMap.put("Û", "U");
		accentMap.put("Ü", "U");
		accentMap.put("Ý", "Y");
		accentMap.put("Ÿ", "Y");
		accentMap.put("à", "a");
		accentMap.put("á", "a");
		accentMap.put("â", "a");
		accentMap.put("ã", "a");
		accentMap.put("ä", "a");
		accentMap.put("å", "a");
		accentMap.put("æ", "ae");
		accentMap.put("ç", "c");
		accentMap.put("è", "e");
		accentMap.put("é", "e");
		accentMap.put("ê", "e");
		accentMap.put("ë", "e");
		accentMap.put("ì", "i");
		accentMap.put("í", "i");
		accentMap.put("î", "i");
		accentMap.put("ï", "i");
		accentMap.put("ĳ", "ij");
		accentMap.put("ð", "d");
		accentMap.put("ñ", "n");
		accentMap.put("ò", "o");
		accentMap.put("ó", "o");
		accentMap.put("ô", "o");
		accentMap.put("õ", "o");
		accentMap.put("ö", "o");
		accentMap.put("ø", "o");
		accentMap.put("œ", "oe");
		accentMap.put("ß", "ss");
		accentMap.put("þ", "th");
		accentMap.put("ù", "u");
		accentMap.put("ú", "u");
		accentMap.put("û", "u");
		accentMap.put("ü", "u");
		accentMap.put("ý", "y");
		accentMap.put("ÿ", "y");
		accentMap.put("ﬀ", "ff");
		accentMap.put("ﬁ", "fi");
		accentMap.put("ﬂ", "fl");
		accentMap.put("ﬃ", "ffi");
		accentMap.put("ﬄ", "ffl");
		accentMap.put("ﬅ", "ft");
		accentMap.put("ﬆ", "st");

	}
	public void accentFilter(TokenStream tStream) throws FilterException
	{
		try{
			String finalString=null;
			if(tStream.hasNext())
			{
				tStream.next();
				accentFlag=false;
				int count=0;
				Token tokens=tStream.getCurrent();
				if(tokens!=null)
				{
					String token=tokens.getTermText();
					if(token!=null)
					{
						String[] filter=token.split("");
						String value=null;
						for(String s:filter)
						{
							if(accentMap.containsKey(s))
							{
								value=accentMap.get(s);
								count++;
								accentFlag=true;
								if(count<2)
									finalString=token.replace(s,value);
								if(count>=2)
									finalString=finalString.replace(s,value);
							}
						}
						Token token2 = new Token();
						if(accentFlag && finalString!=null && !finalString.equals(""))
						{
							token2.setTermText(finalString);
							tokenStream.addTokenToStream(token2);
						}
						else if(!accentFlag && token!=null && !token.equals(""))
						{
							token2.setTermText(token);
							tokenStream.addTokenToStream(token2);
						}
					}
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new FilterException("Exception in Accent Filter");
		}		
	}

	@Override
	public boolean increment() throws TokenizerException {
//		long startTimeaccent = new Date().getTime();
		try{ 
			accentFilter(tStream);
			if(tStream != null && tStream.hasNext())
				return true;
			else
			{
//				long endTimeAccent = new Date().getTime();
//				accentTime=(endTimeAccent-startTimeaccent)/1000;
				return false;
				
			
			}
		}
		catch(FilterException e)
		{
			e.printStackTrace();
		}
		return false;
	}
	@Override
	public TokenStream getStream() {
		return tokenStream;
	}

	@Override
	public void processThroughFilters() {
		// TODO Auto-generated method stub

	}

}
