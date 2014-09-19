/**
 * 
 */
package edu.buffalo.cse.irf14.index;

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
	 *                  : The root directory to be sued for indexing
	 */
	public IndexWriter(String indexDir) {
		// TODO : YOU MUST IMPLEMENT THIS
	}

	/**
	 * Method to add the given Document to the index This method should
	 * take care of reading the filed values, passing them through
	 * corresponding analyzers and then indexing the results for each
	 * indexable field within the document.
	 * 
	 * @param d
	 *                  : The Document to be added
	 * @throws IndexerException
	 *                   : In case any error occurs
	 */
	public void addDocument(Document d) throws IndexerException {
		// TODO : YOU MUST IMPLEMENT THIS

		try {
			Tokenizer tokenizer = new Tokenizer();
			String title = null, author = null, authorOrg = null, place = null, newsdate = null, content = null;
			if (d.getField(FieldNames.TITLE)[0] != null) {
				title = d.getField(FieldNames.TITLE)[0];
				tokenizer.consume(title);
			}
			if (d.getField(FieldNames.AUTHOR)[0] != null) {
				author = d.getField(FieldNames.AUTHOR)[0];
				tokenizer.consume(author);
			}
			if (d.getField(FieldNames.AUTHORORG)[0] != null) {
				authorOrg = d.getField(FieldNames.AUTHORORG)[0];
				tokenizer.consume(authorOrg);
			}
			if (d.getField(FieldNames.PLACE)[0] != null) {
				place = d.getField(FieldNames.PLACE)[0];
				tokenizer.consume(place);
			}
			if (d.getField(FieldNames.NEWSDATE)[0] != null) {
				newsdate = d.getField(FieldNames.NEWSDATE)[0];
				tokenizer.consume(newsdate);
			}
			if (d.getField(FieldNames.CONTENT)[0] != null) {
				content = d.getField(FieldNames.CONTENT)[0];
				tokenizer.consume(content);
			}
			if (d.getField(FieldNames.CONTENT)[0] != null) {
				content = d.getField(FieldNames.CONTENT)[0];
				tokenizer.consume(content);
			}
		} catch (TokenizerException e) {
			System.out.println("Exception caught");
			e.printStackTrace();
		}
	}

	/**
	 * Method that indicates that all open resources must be closed and
	 * cleaned and that the entire indexing operation has been completed.
	 * 
	 * @throws IndexerException
	 *                   : In case any error occurs
	 */
	public void close() throws IndexerException {
		// TODO
	}
}
