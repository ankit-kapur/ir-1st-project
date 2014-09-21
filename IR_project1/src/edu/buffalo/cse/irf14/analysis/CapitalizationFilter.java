package edu.buffalo.cse.irf14.analysis;


public class CapitalizationFilter extends TokenFilter {

	TokenStream tStream=null;
	public CapitalizationFilter(TokenStream stream) {
		super(stream);
		this.tStream=stream;
		// TODO Auto-generated constructor stub
	}
	TokenStream tokenStream=new TokenStream();
	public TokenStream captilizationFilter(TokenStream tStream)
	{
		String filteredToken=null;
		boolean allCapsFlag,adjacentLetterCaps,adjacentwordCaps,firstWordCaps;
		while(tStream.hasNext())
		{
			allCapsFlag=false;
			adjacentLetterCaps=false;
			firstWordCaps=false;
			adjacentwordCaps=false;
			int count=0,count1=0;
			String token1=null;
			tStream.next();
			Token tokens=tStream.getCurrent();
			String token=tokens.getTermText();

			//Logic for checking all caps letters
			for(char c : token.toCharArray()){
				if(c==Character.toUpperCase(c)){
					count++;
				}
			}
			if(count!=token.length())
			{
				allCapsFlag=false;
			}
			if(count==token.length())
			{
				filteredToken=token;
				allCapsFlag=true;
			}
			//Logic for checking adjacent caps letters in a single word
			if(!allCapsFlag)
			{
				adjacentLetterCaps=false;
				for(char c : token.toCharArray()){
					if(c==Character.toUpperCase(c)){
						count1++;
					}
					if(count1==2)
					{
						adjacentLetterCaps=true;
						break;
					}
				}
				if(adjacentLetterCaps)
				{
					filteredToken=token;
				}
			}

			//Logic for checking if its the first word and is it capitalized
			if(!allCapsFlag && !adjacentLetterCaps)
			{
				char fc=token.charAt(0);
				firstWordCaps=false;
				if(fc==Character.toUpperCase(fc) && tStream.first())
				{
					filteredToken=token.toLowerCase();
					firstWordCaps=true;
				}
				else
				{
					filteredToken=token;
				}
			}
			if(!allCapsFlag && !adjacentLetterCaps)
			{
				if(tStream.hasNext())
				{
					adjacentwordCaps=false;
					tStream.next();
					Token tokens1=tStream.getCurrent();
					token1=tokens1.getTermText();
					tStream.previous();
					char fc1=token1.charAt(0);
					char fc=token.charAt(0);
					if(fc1==Character.toUpperCase(fc1) && fc==Character.toUpperCase(fc))
					{
						filteredToken=token+" "+token1;
						adjacentwordCaps=true;
						tStream.next();
					}	

				}

			}
			Token token2 = new Token();
			if(!allCapsFlag && !firstWordCaps && !adjacentLetterCaps && !adjacentwordCaps)
				token2.setTermText(filteredToken.toLowerCase());
			else
				token2.setTermText(filteredToken);
			tokenStream.addTokenToStream(token2);
		}
		return tokenStream;
	}	

	@Override
	public boolean increment() throws TokenizerException {
		captilizationFilter(tStream);
		if(tStream.hasNext())
			return true;
		else
			return false;
	}
	@Override
	public TokenStream getStream() {
		// TODO Auto-generated method stub
		return tokenStream;
	}

}
