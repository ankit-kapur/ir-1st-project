/**
 * 
 */
package edu.buffalo.cse.irf14.analysis;

import edu.buffalo.cse.irf14.analysis.analyzer.AuthorAnalyzer;
import edu.buffalo.cse.irf14.analysis.analyzer.CategoryAnalyzer;
import edu.buffalo.cse.irf14.analysis.analyzer.DateAnalyzer;
import edu.buffalo.cse.irf14.analysis.analyzer.PlaceAnalyzer;
import edu.buffalo.cse.irf14.analysis.analyzer.TermAnalyzer;
import edu.buffalo.cse.irf14.analysis.analyzer.TitleAnalyzer;
import edu.buffalo.cse.irf14.document.FieldNames;

/**
 * @author nikhillo This factory class is responsible for instantiating
 *         "chained" {@link Analyzer} instances
 */
public class AnalyzerFactory {

	private static AnalyzerFactory factoryInstance;

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
	public static AnalyzerFactory getInstance() {
		// TODO: YOU NEED TO IMPLEMENT THIS METHOD
		if (factoryInstance == null) {
			factoryInstance = new AnalyzerFactory();
		}
		return factoryInstance;
	}

	/**
	 * Returns a fully constructed and chained {@link Analyzer} instance for a
	 * given {@link FieldNames} field Note again that the singleton factory
	 * instance allows you to reuse {@link TokenFilter} instances if need be
	 * 
	 * @param name
	 *             : The {@link FieldNames} for which the {@link Analyzer} is
	 *             requested
	 * @param TokenStream
	 *             : Stream for which the Analyzer is requested
	 * @return The built {@link Analyzer} instance for an indexable
	 *         {@link FieldNames} null otherwise
	 */
	public Analyzer getAnalyzerForField(FieldNames name, TokenStream stream) {
		try {
			if (name == FieldNames.TITLE)
				return new TitleAnalyzer(stream);
			else if (name == FieldNames.AUTHOR || name == FieldNames.AUTHORORG)
				return new AuthorAnalyzer(stream);
			else if (name == FieldNames.CONTENT)
				return new TermAnalyzer(stream);
			else if (name == FieldNames.NEWSDATE)
				return new DateAnalyzer(stream);
			else if (name == FieldNames.PLACE)
				return new PlaceAnalyzer(stream);
			else if (name == FieldNames.CATEGORY)
				return new CategoryAnalyzer(stream);
			else
				return null;
		} catch (Exception e) {
			System.err.println("An exception occured while getting analyzers for field: " + name);
			e.printStackTrace();
		}
		return null;
	}
}
