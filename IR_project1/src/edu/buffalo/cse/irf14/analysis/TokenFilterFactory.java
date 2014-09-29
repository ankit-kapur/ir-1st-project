/**
 * 
 */
package edu.buffalo.cse.irf14.analysis;

/**
 * Factory class for instantiating a given TokenFilter
 * 
 * @author nikhillo
 *
 */
public class TokenFilterFactory {

	private static TokenFilterFactory tokenFilterFactory;

	/**
	 * Static method to return an instance of the factory class. Usually
	 * factory classes are defined as singletons, i.e. only one instance of the
	 * class exists at any instance. This is usually achieved by defining a
	 * private static instance that is initialized by the "private"
	 * constructor. On the method being called, you return the static instance.
	 * This allows you to reuse expensive objects that you may create during
	 * instantiation
	 * 
	 * @return An instance of the factory
	 */
	public static TokenFilterFactory getInstance() {
		// TODO : YOU MUST IMPLEMENT THIS METHOD
		if (tokenFilterFactory == null) {
			tokenFilterFactory = new TokenFilterFactory();
		}
		return tokenFilterFactory;
	}

	/**
	 * Returns a fully constructed {@link TokenFilter} instance for a given
	 * {@link TokenFilterType} type
	 * 
	 * @param type
	 *             : The {@link TokenFilterType} for which the
	 *             {@link TokenFilter} is requested
	 * @param stream
	 *             : The TokenStream instance to be wrapped
	 * @return The built {@link TokenFilter} instance
	 * @throws TokenizerException 
	 */
	public TokenFilter getFilterByType(TokenFilterType type, TokenStream stream) throws TokenizerException {
		// TODO : YOU MUST IMPLEMENT THIS METHOD
		try{
			if (type == TokenFilterType.ACCENT)
				return new AccentFilter(stream);
			else if (type == TokenFilterType.NUMERIC)
				return new NumberFilter(stream);
			else if (type == TokenFilterType.STOPWORD)
				return new StopWordFilter(stream);
			else if (type == TokenFilterType.STEMMER)
				return new StemmerFilter(stream);
			else if (type == TokenFilterType.SPECIALCHARS)
				return new SpecialCharFilter(stream);
			else if (type == TokenFilterType.CAPITALIZATION)
				return new CapitalizationFilter(stream); // To be changed
			else if (type == TokenFilterType.DATE)
				return new DateFilter(stream);
			else if (type == TokenFilterType.SYMBOL)
				return new SymbolFilter(stream);
			else
				return null;
		}
		catch(Exception e )
		{
			e.printStackTrace();
			throw new TokenizerException("Error in TokenFilter Factory");
		}
	}
}