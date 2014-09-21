package edu.buffalo.cse.irf14.analysis;

import java.text.Normalizer;
/**
 * @author Harsh
 * @parameter {@link TokenStream}
 * @return {@link TokenStream}
 * @description This class contains the logic for removing accents according to test cases.
 */
public class AccentFilter extends TokenFilter {


	TokenStream tStream=null;
	public AccentFilter(TokenStream stream) {
		super(stream);
		this.tStream=stream;
	}



	TokenStream tokenStream=new TokenStream();
	public TokenStream accentFilter(TokenStream tStream)
	{

		while(tStream.hasNext())
		{
			tStream.next();
			Token tokens=tStream.getCurrent();
			String token=tokens.getTermText();
			String filteredToken = Normalizer.normalize(token, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
			Token token2 = new Token();
			token2.setTermText(filteredToken);
			tokenStream.addTokenToStream(token2);
		}
		return tokenStream;		
	}

	@Override
	public boolean increment() throws TokenizerException {
		accentFilter(tStream);
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
