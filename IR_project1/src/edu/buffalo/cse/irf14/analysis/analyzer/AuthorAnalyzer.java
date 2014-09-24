package edu.buffalo.cse.irf14.analysis.analyzer;

import java.util.ArrayList;
import java.util.List;

import edu.buffalo.cse.irf14.analysis.Analyzer;
import edu.buffalo.cse.irf14.analysis.TokenFilter;
import edu.buffalo.cse.irf14.analysis.TokenFilterFactory;
import edu.buffalo.cse.irf14.analysis.TokenFilterType;
import edu.buffalo.cse.irf14.analysis.TokenStream;
import edu.buffalo.cse.irf14.analysis.TokenizerException;

public class AuthorAnalyzer implements Analyzer {

	TokenStream tokenStream;

	public AuthorAnalyzer(TokenStream stream) {
		this.tokenStream = stream;
	}

	@Override
	public boolean increment() throws TokenizerException {
		// No need to implement
		return false;
	}

	@Override
	public TokenStream getStream() {
		return tokenStream;
	}

	public void processThroughFilters() {
		TokenFilterFactory factory = TokenFilterFactory.getInstance();
		List<TokenFilterType> filterTypeList = new ArrayList<TokenFilterType>();

		/* Order of filters */
		filterTypeList.add(TokenFilterType.DATE);
		filterTypeList.add(TokenFilterType.STEMMER);
		
		for (TokenFilterType filterType : filterTypeList) {
			try {
				TokenFilter filter = factory.getFilterByType(filterType, tokenStream);
				while (filter.increment());
				tokenStream = filter.getStream();
			} catch (TokenizerException e) {
				System.err.println("An error occured during filter processing");
			}
		}
	}
}
