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
			String filteredToken = null;
			boolean matcherFlag;
			matcherFlag = false;
			int count = 0;
			if (tStream.hasNext()) {
				tStream.next();
				Token tokens = tStream.getCurrent();
				if (tokens != null) {
					String token = tokens.getTermText();
					if (token != null) {
						Pattern pattern = Pattern.compile("[a-zA-Z-]");
						Matcher matcher = pattern.matcher(token);
						Pattern pattern1 = Pattern.compile("[0-9]{8}");
						Matcher matcher1 = pattern1.matcher(token);
						Pattern pattern2 = Pattern.compile("[0-9]{8}-[0-9]{8}");
						Matcher matcher2 = pattern2.matcher(token);
						Pattern pattern3 = Pattern.compile("[0-9]{2}:[0-9]{2}:[0-9]{2}");
						Matcher matcher3 = pattern3.matcher(token);
						if (!matcher.find() && !matcher1.find() && !matcher2.find() && !matcher3.find()) {
							matcherFlag = true;
							StringBuilder sb = new StringBuilder();
							for (char c : token.toCharArray()) {
								if (!Character.isDigit(c) && c != ',' && c != '.') {
									sb.append(c);
									count++;
								}

							}
							if (sb != null)
								filteredToken = sb.toString();
						}
						Token token2 = new Token();
						if (matcherFlag && count > 0 && filteredToken != null && !filteredToken.equals("")) {
							token2.setTermText(filteredToken);
							tokenStream.addTokenToStream(token2);
						} else if (!matcherFlag && count == 0 && token != null && !token.equals("")) {
							token2.setTermText(token);
							tokenStream.addTokenToStream(token2);
						}
					}
				}
			}
		} catch (Exception e) {
			throw new FilterException("Exception in Number Filter");
		}
	}

	@Override
	public boolean increment() throws TokenizerException {
		try {
			numberFilter(tStream);
			if (tStream != null && tStream.hasNext())
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
