/**
 * 
 */
package edu.buffalo.cse.irf14.analysis;

import java.util.StringTokenizer;

/**
 * @author nikhillo 
 * 
 * 	Class that converts a given string into a  {@link TokenStream} instance
 * 
 *         This class is responsible for converting strings into TokenStream
 *         objects. A Tokenizer is instantiated without any arguments or a given
 *         delimiter. The former merely implies space-delimited tokenization.
 * 
 */
public class Tokenizer {
	
	private String delimiter;
	
	/**
	 * Default constructor. 
	 * Assumes tokens are whitespace delimited
	 */
	public Tokenizer() {
		// TODO : YOU MUST IMPLEMENT THIS METHOD
		
		this.delimiter = " ";
	}

	/**
	 * Overloaded constructor. Creates the tokenizer with the given
	 * delimiter
	 * 
	 * @param delim
	 *                  : The delimiter to be used
	 */
	public Tokenizer(String delim) {
		// TODO : YOU MUST IMPLEMENT THIS METHOD
		
		this.delimiter = delim;
	}

	/**
	 * Method to convert the given string into a TokenStream instance.
	 * This must only break it into tokens and initialize the stream. No
	 * other processing must be performed. Also the number of tokens would
	 * be determined by the string and the delimiter. So if the string
	 * were "hello world" with a whitespace delimited tokenizer, you would
	 * get two tokens in the stream. But for the same text used with lets
	 * say "~" as a delimiter would return just one token in the stream.
	 * 
	 * @param str
	 *                  : The string to be consumed
	 * @return : The converted TokenStream as defined above
	 * @throws TokenizerException
	 *                   : In case any exception occurs during
	 *                   tokenization
	 */
	public TokenStream consume(String str) throws TokenizerException {
		// YOU MUST IMPLEMENT THIS METHOD
		if (str == null || str.trim().equals("")) {
			throw new TokenizerException("Null or empty string passed for tokenizer consumption.");
		}
		TokenStream tokenStream = new TokenStream();
		StringTokenizer stringTokenizer = new StringTokenizer(str, delimiter);
		if (stringTokenizer != null) {
			while (stringTokenizer.hasMoreTokens()) {
				String nextTokenString = stringTokenizer.nextToken();
				Token newToken = new Token();
				newToken.setTermText(nextTokenString);
				newToken.setTermBuffer(nextTokenString.toCharArray());
				tokenStream.addTokenToStream(newToken);
			}
		}
		
		return tokenStream;
	}
}
