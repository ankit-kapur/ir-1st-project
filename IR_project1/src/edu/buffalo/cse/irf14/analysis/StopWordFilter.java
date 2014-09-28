package edu.buffalo.cse.irf14.analysis;

import java.util.HashSet;
/**
 * @author Harsh
 * @parameter {@link TokenStream}
 * @return {@link TokenStream}
 * @description This class contains the logic for removing StopWords according to test cases.
 */

public class StopWordFilter extends TokenFilter{
	TokenStream tStream=null;
	public StopWordFilter(TokenStream stream) {
		super(stream);
		this.tStream=stream;
		// TODO Auto-generated constructor stub
	}
	private static final HashSet<String> stopWordMap =  new HashSet<String>() ;
	TokenStream tokenStream=new TokenStream();
	static
	{
		stopWordMap.add("a");
		stopWordMap.add("able");
		stopWordMap.add("about");
		stopWordMap.add("across");
		stopWordMap.add("after");
		stopWordMap.add("all");
		stopWordMap.add("almost");
		stopWordMap.add("also");
		stopWordMap.add("am");
		stopWordMap.add("among");
		stopWordMap.add("an");
		stopWordMap.add("and");
		stopWordMap.add("any");
		stopWordMap.add("are");
		stopWordMap.add("as");
		stopWordMap.add("at");
		stopWordMap.add("be");
		stopWordMap.add("because");
		stopWordMap.add("been");
		stopWordMap.add("but");
		stopWordMap.add("by");
		stopWordMap.add("can");
		stopWordMap.add("cannot");
		stopWordMap.add("could");
		stopWordMap.add("dear");
		stopWordMap.add("did");
		stopWordMap.add("do");
		stopWordMap.add("does");
		stopWordMap.add("either");
		stopWordMap.add("else");
		stopWordMap.add("ever");
		stopWordMap.add("every");
		stopWordMap.add("for");
		stopWordMap.add("from");
		stopWordMap.add("get");
		stopWordMap.add("got");
		stopWordMap.add("had");
		stopWordMap.add("has");
		stopWordMap.add("have");
		stopWordMap.add("he");
		stopWordMap.add("her");
		stopWordMap.add("hers");
		stopWordMap.add("him");
		stopWordMap.add("his");
		stopWordMap.add("how");
		stopWordMap.add("however");
		stopWordMap.add("i");
		stopWordMap.add("if");
		stopWordMap.add("in");
		stopWordMap.add("into");
		stopWordMap.add("is");
		stopWordMap.add("it");
		stopWordMap.add("its");
		stopWordMap.add("just");
		stopWordMap.add("least");
		stopWordMap.add("let");
		stopWordMap.add("like");
		stopWordMap.add("likely");
		stopWordMap.add("may");
		stopWordMap.add("me");
		stopWordMap.add("might");
		stopWordMap.add("most");
		stopWordMap.add("must");
		stopWordMap.add("my");
		stopWordMap.add("neither");
		stopWordMap.add("no");
		stopWordMap.add("nor");
		stopWordMap.add("not");
		stopWordMap.add("of");
		stopWordMap.add("off");
		stopWordMap.add("often");
		stopWordMap.add("on");
		stopWordMap.add("only");
		stopWordMap.add("or");
		stopWordMap.add("other");
		stopWordMap.add("our");
		stopWordMap.add("own");
		stopWordMap.add("rather");
		stopWordMap.add("said");
		stopWordMap.add("say");
		stopWordMap.add("says");
		stopWordMap.add("she");
		stopWordMap.add("should");
		stopWordMap.add("since");
		stopWordMap.add("so");
		stopWordMap.add("some");
		stopWordMap.add("than");
		stopWordMap.add("that");
		stopWordMap.add("the");
		stopWordMap.add("their");
		stopWordMap.add("them");
		stopWordMap.add("then");
		stopWordMap.add("there");
		stopWordMap.add("these");
		stopWordMap.add("they");
		stopWordMap.add("this");
		stopWordMap.add("tis");
		stopWordMap.add("to");
		stopWordMap.add("too");
		stopWordMap.add("twas");
		stopWordMap.add("us");
		stopWordMap.add("wants");
		stopWordMap.add("was");
		stopWordMap.add("we");
		stopWordMap.add("were");
		stopWordMap.add("what");
		stopWordMap.add("when");
		stopWordMap.add("where");
		stopWordMap.add("which");
		stopWordMap.add("while");
		stopWordMap.add("who");
		stopWordMap.add("whom");
		stopWordMap.add("why");
		stopWordMap.add("will");
		stopWordMap.add("with");
		stopWordMap.add("would");
		stopWordMap.add("yet");
		stopWordMap.add("you");
		stopWordMap.add("your");
	}
	public void stopWordFilter(TokenStream tStream) throws FilterException
	{
		try
		{
			if(tStream.hasNext())
			{
				tStream.next();
				Token tokens=tStream.getCurrent();
				if(tokens!=null)
				{
					String token=tokens.getTermText();
					if(token!=null)
					{
						if(!stopWordMap.contains(token))
						{
							Token token2 = new Token();
							if(token!=null && !token.equals(""))
							{
								token2.setTermText(token);
								tokenStream.addTokenToStream(token2);
							}
						}
					}
				}
			}
		}
		catch(Exception e)
		{
			throw new FilterException("Exception in Stop Word Filter");
		}

	}
	@Override
	public TokenStream getStream() {
		// TODO Auto-generated method stub
		return tokenStream;
	}	

	@Override
	public boolean increment() throws TokenizerException {
		try{
			stopWordFilter(tStream);
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
	public void processThroughFilters() {
		// TODO Auto-generated method stub

	}
}

