package edu.buffalo.cse.irf14.analysis;


public class CapitalizationFilter extends TokenFilter {

	TokenStream tStream=null;
	public CapitalizationFilter(TokenStream stream) {
		super(stream);
		this.tStream=stream;
		// TODO Auto-generated constructor stub
	}
	TokenStream tokenStream=new TokenStream();
	public void captilizationFilter(TokenStream tStream) throws FilterException
	{
		try{
			String filteredToken=null;
			boolean allCapsFlag,adjacentLetterCaps,adjacentwordCaps,firstWordCaps;
			allCapsFlag=false;
			adjacentLetterCaps=false;
			firstWordCaps=false;
			adjacentwordCaps=false;
			int count1=0;
			String token1=null;
			if(tStream != null && tStream.hasNext())
			{
				tStream.next();
				Token tokens=tStream.getCurrent();
				if(tokens!=null)
				{
					String token=tokens.getTermText();
					if(token!=null)
					{
						//Logic for checking all caps letters
						if(token.equals(token.toUpperCase()))
						{
							filteredToken=token;
							allCapsFlag=true;
						}
						else
						{
							allCapsFlag=false;
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
								if(tokens1!=null)
								{
									token1=tokens1.getTermText();
									tStream.reduceIndex();
									if(token1!=null && !token1.equals("")&& token!=null && !token.equals(""))
									{
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
							}
						}
						Token token2 = new Token();
						if(!allCapsFlag && !firstWordCaps && !adjacentLetterCaps && !adjacentwordCaps && filteredToken!=null)
						{
							token2.setTermText(filteredToken.toLowerCase());
							tokenStream.addTokenToStream(token2);
						}
						else if(filteredToken!=null)
						{
							token2.setTermText(filteredToken);
							tokenStream.addTokenToStream(token2);
						}
						//System.out.println("Token--->>"+filteredToken);
					}
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new FilterException("Exception in Capitilization Filter");
		}
	}	

	@Override
	public boolean increment() throws TokenizerException {
		try{
			captilizationFilter(tStream);
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
		// TODO Auto-generated method stub
		return tokenStream;
	}

	@Override
	public void processThroughFilters() {
		// TODO Auto-generated method stub
	}

}
