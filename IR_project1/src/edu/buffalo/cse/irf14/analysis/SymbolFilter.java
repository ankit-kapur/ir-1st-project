package edu.buffalo.cse.irf14.analysis;

import java.util.HashMap;

public class SymbolFilter extends TokenFilter {

	TokenStream tStream = null;

	public SymbolFilter(TokenStream stream) {
		super(stream);
		this.tStream = stream;
		// TODO Auto-generated constructor stub
	}

	TokenStream tokenStream = new TokenStream();
	private static final HashMap<String, String> contractionsMap = new HashMap<String, String>();
	static {
		contractionsMap.put("isn't", "is not");
		contractionsMap.put("don't", "do not");
		contractionsMap.put("won't", "will not");
		contractionsMap.put("shan't", "shall not");
		contractionsMap.put("I'm", "I am");
		contractionsMap.put("we're", "we are");
		contractionsMap.put("they're", "they are");
		contractionsMap.put("I've", "I have");
		contractionsMap.put("Should've", "Should have");
		contractionsMap.put("They'd", "They would");
		contractionsMap.put("She'll", "She will");
		contractionsMap.put("'em", "them");
	}

	public void symbolFilter(TokenStream tStream) throws FilterException {
		String filteredToken = null, hardString1 = null, hardString2 = null;
		int digitCount = 0, alphacount = 0;
		boolean apostropheFlag = false, digitFlag = false, alphaFlag = false, hardFlag = false;
		try {
			tStream.next();
			apostropheFlag = false;
			alphaFlag = false;
			digitFlag = false;
			Token token = tStream.getCurrent();
			if (token != null) {
				String tokenString = token.getTermText();
				if (tokenString != null) {

					if (tokenString.contains("'s")) {
						filteredToken = tokenString.replaceAll("'s", "");
						apostropheFlag = true;
					} else if (contractionsMap.containsKey(tokenString)) {
						filteredToken = contractionsMap.get(tokenString);
						if (tokenString.contains("'em")) {
							String[] hardString = tokenString.split(" ");
							hardString1 = hardString[0];
							hardString2 = contractionsMap.get("'em");
						}

					} else if (tokenString.contains("'") && !apostropheFlag) {
						filteredToken = tokenString.replaceAll("'", "");
					} else if (tokenString.endsWith(".")) {
						filteredToken = tokenString.substring(0, tokenString.length() - 1);
					} else if (tokenString.endsWith("?") || tokenString.endsWith("!")) {
						StringBuilder sb = new StringBuilder();
						for (char c : tokenString.toCharArray()) {
							if (Character.isDigit(c) || Character.isAlphabetic(c) || c == '.') {
								sb.append(c);
							}
						}
						filteredToken = sb.toString();
					} else if (tokenString.contains("-")) {
						for (char c : tokenString.toCharArray()) {
							if (Character.isDigit(c))
								digitCount++;
							if (Character.isAlphabetic(c))
								alphacount++;
						}
						if (digitCount == 0) {
							filteredToken = tokenString.replaceAll("[\\s--]", "");
							digitFlag = true;
						}
						if (alphacount == tokenString.length() - 1) {
							filteredToken = tokenString.replaceAll("[\\s--]", " ");
							alphaFlag = true;
						}
						if (!alphaFlag && !digitFlag) {
							filteredToken = tokenString;
						}
					}

					else {
						filteredToken = tokenString;
					}

					if (filteredToken != null && !filteredToken.equals("") && !filteredToken.equals(" ") && !hardFlag) {
						Token token2 = new Token();
						if (filteredToken != null)
							token2.setTermText(filteredToken);
						tokenStream.addTokenToStream(token2);
					}
					if (hardFlag) {
						Token token2 = new Token();
						Token token3 = new Token();
						if (hardString1 != null)
							token2.setTermText(hardString1);
						if (hardString2 != null)
							token3.setTermText(hardString2);
						tokenStream.addTokenToStream(token2);
						tokenStream.addTokenToStream(token3);
					}
				}
			} else {
				/* WHY NULL?????? */
				System.out.println("NULL TOKEN");
			}

		} catch (Exception e) {
			e.printStackTrace();
			// throw new FilterException("Exception in Symbol Filter");
		}
	}

	@Override
	public boolean increment() throws TokenizerException {
		try {
			symbolFilter(tStream);
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
		// TODO Auto-generated method stub
		return tokenStream;
	}

	@Override
	public void processThroughFilters() {
		// TODO Auto-generated method stub

	}

}
