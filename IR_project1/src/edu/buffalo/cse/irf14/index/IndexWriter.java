

/**
 * 
 */
package edu.buffalo.cse.irf14.index;

import java.util.ArrayList;
import java.util.List;

import edu.buffalo.cse.irf14.analysis.Analyzer;
import edu.buffalo.cse.irf14.analysis.AnalyzerFactory;
import edu.buffalo.cse.irf14.analysis.TokenStream;
import edu.buffalo.cse.irf14.analysis.Tokenizer;
import edu.buffalo.cse.irf14.analysis.TokenizerException;
import edu.buffalo.cse.irf14.document.Document;
import edu.buffalo.cse.irf14.document.FieldNames;

/**
 * @author nikhillo Class responsible for writing indexes to disk
 */
public class IndexWriter {
	/**
	 * Default constructor
	 * 
	 * @param indexDir
	 *             : The root directory to be sued for indexing
	 */
	public IndexWriter(String indexDir) {
		// TODO : YOU MUST IMPLEMENT THIS
	}

	/**
	 * Method to add the given Document to the index This method should take
	 * care of reading the filed values, passing them through corresponding
	 * analyzers and then indexing the results for each indexable field within
	 * the document.
	 * 
	 * @param d
	 *             : The Document to be added
	 * @throws IndexerException
	 *              : In case any error occurs
	 */
	public void addDocument(Document d) throws IndexerException {
		// TODO : YOU MUST IMPLEMENT THIS
		AnalyzerFactory analyzerFactory = new AnalyzerFactory();

		try {
			Tokenizer tokenizer = new Tokenizer();
			String fieldText = null;
			List<FieldNames> fieldNameList = new ArrayList<FieldNames>();
			fieldNameList.add(FieldNames.TITLE);
			fieldNameList.add(FieldNames.AUTHOR);
			
			for (FieldNames fieldName : fieldNameList) {
				if (d.getField(fieldName) != null) {
					fieldText = d.getField(fieldName)[0];
					TokenStream tokenstream = tokenizer.consume(fieldText);

					Analyzer analyzer = analyzerFactory.getAnalyzerForField(fieldName, tokenstream);
					analyzer.processThroughFilters();
					tokenstream = analyzer.getStream();
				}
				
				/* TODO: Write to index file */
			}
		} catch (TokenizerException e) {
			System.out.println("Exception caught");
			e.printStackTrace();
		}
	}

	/**
	 * Method that indicates that all open resources must be closed and cleaned
	 * and that the entire indexing operation has been completed.
	 * 
	 * @throws IndexerException
	 *              : In case any error occurs
	 */
	public void close() throws IndexerException {
		// TODO
	}
}

