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

	public TokenStream specialCharFilter(TokenStream tStream)
	{
		boolean matcherFlag;
		int digitCount=0;
		String filteredToken=null;
		while(tStream.hasNext())
		{
			matcherFlag=false;
			tStream.next();
			Token tokens=tStream.getCurrent();
			String token=tokens.getTermText();
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
			if(matcherFlag)
				token2.setTermText(filteredToken);
			else
				token2.setTermText(token);
			tokenStream.addTokenToStream(token2);
		}
		return tokenStream;		
	}

	@Override
	public boolean increment() throws TokenizerException {
		specialCharFilter(tStream);
		if(tStream.hasNext())
			return true;
		else
			return false;
	}

	@Override
	public TokenStream getStream() {
		return tokenStream;
	}


}
