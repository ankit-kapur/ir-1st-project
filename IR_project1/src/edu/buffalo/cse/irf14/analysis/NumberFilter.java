package edu.buffalo.cse.irf14.analysis;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * @author Harsh
 * @parameter {@link TokenStream}
 * @return {@link TokenStream}
 * @description This class contains the logic for removing numbers according to
 *              test cases.
 */
public class NumberFilter extends TokenFilter {
	TokenStream tStream = null;

	public NumberFilter(TokenStream stream) {
		super(stream);
		this.tStream = stream;
	}

	TokenStream tokenStream = new TokenStream();

	public void numberFilter(TokenStream tStream) throws FilterException {
		try {
			String filteredTokenString = null;
			boolean matcherFlag;
			matcherFlag = false;
			int count = 0;
			tStream.next();
			Token tokens = tStream.getCurrent();
			String tokenString = tokens.getTermText();
			Pattern pattern = Pattern.compile("[a-zA-Z]");
			Matcher matcher = pattern.matcher(tokenString);
			if (!matcher.find()) {
				matcherFlag = true;
				StringBuilder sb = new StringBuilder();
				for (char c : tokenString.toCharArray()) {
					if (!Character.isDigit(c) && c != ',' && c != '.') {
						sb.append(c);
						count++;
					}

				}
				filteredTokenString = sb.toString();
			}
			Token token2 = new Token();
			if (matcherFlag && count > 0 && filteredTokenString != null) {
				token2.setTermText(filteredTokenString);
				tokenStream.addTokenToStream(token2);
			}
			else if (!matcherFlag && count == 0 && tokenString != null) {
				token2.setTermText(tokenString);
				tokenStream.addTokenToStream(token2);
			}

		} catch (Exception e) {
			throw new FilterException("Exception in Number Filter");
		}
	}

	@Override
	public boolean increment() throws TokenizerException {
		try {
			numberFilter(tStream);
			if (tStream.hasNext())
				return true;
			else
				return false;
		} catch (FilterException e) {
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
