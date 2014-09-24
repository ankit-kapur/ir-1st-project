package edu.buffalo.cse.irf14.analysis;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
/**
 * @author Harsh
 * @parameter {@link TokenStream}
 * @return {@link TokenStream}
 * @description This class contains the logic for removing numbers according to test cases.
 */
public class NumberFilter extends TokenFilter{
	TokenStream tStream=null;
	public NumberFilter(TokenStream stream) {
		super(stream);
		this.tStream=stream;
	}
	TokenStream tokenStream=new TokenStream();
	public TokenStream numberFilter(TokenStream tStream) throws FilterException
	{
		try{
			String filteredToken=null;
			boolean matcherFlag;
			while(tStream.hasNext())
			{
				matcherFlag=false;
				int count=0;
				tStream.next();
				Token tokens=tStream.getCurrent();
				String token=tokens.getTermText();
				Pattern pattern = Pattern.compile("[a-zA-Z]");
				Matcher matcher=pattern.matcher(token);
				if(!matcher.find())
				{
					matcherFlag=true;
					StringBuilder sb = new StringBuilder();
					for(char c : token.toCharArray()){
						if(!Character.isDigit(c) && c!=',' && c!='.'){
							sb.append(c);
							count++;
						}

					}
					filteredToken=sb.toString();
				}
				Token token2 = new Token();
				if(matcherFlag && count>0)
					token2.setTermText(filteredToken);
				else if(!matcherFlag &&count==0)
					token2.setTermText(token);
				tokenStream.addTokenToStream(token2);
			}
		}
		catch(Exception e)
		{
			throw new FilterException("Exception in Number Filter");
		}
		return tokenStream;		
	}

	@Override
	public boolean increment() throws TokenizerException {
		try{
			numberFilter(tStream);
			if(tStream.hasNext())
				return true;
			else
				return false;
		}
		catch (FilterException e)
		{
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public TokenStream getStream() {
		return tokenStream;
	}

}
