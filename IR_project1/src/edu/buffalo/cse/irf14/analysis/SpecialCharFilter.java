package edu.buffalo.cse.irf14.analysis;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpecialCharFilter extends TokenFilter {

	TokenStream tStream=null;


	public SpecialCharFilter(TokenStream stream) {
		super(stream);
		this.tStream=stream;
	}
	TokenStream tokenStream=new TokenStream();

	public void specialCharFilter(TokenStream tStream) throws FilterException
	{
		try
		{
			boolean matcherFlag;
			int digitCount=0;
			String filteredToken=null;
			matcherFlag=false;
			if(tStream.hasNext())
			{
				tStream.next();
				Token tokens=tStream.getCurrent();
				if(tokens!=null)
				{
					String token=tokens.getTermText();
					if(token!=null)
					{
						Pattern pattern = Pattern.compile("[^a-zA-Z]");
						Matcher matcher=pattern.matcher(token);
						if(matcher.find())
						{
							matcherFlag=true;
							filteredToken=token.replaceAll("[~!@=#$%^&*()_+\\;\',/{}|:\"<>?\\\\/]", "");

							if(filteredToken.contains("-"))
							{
								for(char c : filteredToken.toCharArray()){
									if(Character.isDigit(c))
										digitCount++;
								}
								if(digitCount==0)
								{
									filteredToken=filteredToken.replaceAll("[~!@=#$%^&--*()_+\\;\',/{}|:\"<>?]", "");
								}
							}
						}
						Token token2 = new Token();
						if(matcherFlag && filteredToken!=null && !filteredToken.equals(""))
						{
							token2.setTermText(filteredToken);
							tokenStream.addTokenToStream(token2);
						}
						else if(!matcherFlag && token!=null && !token.equals(""))
						{
							token2.setTermText(token);
							tokenStream.addTokenToStream(token2);
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			throw new FilterException("Exception in Special Character Filter");
		}		
	}

	@Override
	public boolean increment() throws TokenizerException {
		try{
			specialCharFilter(tStream);
			if(tStream != null && tStream.hasNext())
				return true;
			else
				return false;
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
