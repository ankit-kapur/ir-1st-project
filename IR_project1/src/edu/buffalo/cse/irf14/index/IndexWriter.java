/**
 * 
 */
package edu.buffalo.cse.irf14.index;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.buffalo.cse.irf14.analysis.Analyzer;
import edu.buffalo.cse.irf14.analysis.AnalyzerFactory;
import edu.buffalo.cse.irf14.analysis.Token;
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

	String indexDirectory;
	public Map<Integer, String> termDictionary = new HashMap<Integer, String>();
	public Map<Integer, String> documentDictionary = new HashMap<Integer, String>();
	// Map<Integer, String> termIndex = new HashMap<Integer, String>();
	int termIdCounter, docIdCounter;

	String termDictionaryLocation = "\\termDictionary.txt";
	String docuDictionaryLocation = "\\docuDictionary.txt";
	
	/* File writers */
	BufferedWriter termDictWriter = null;
	BufferedWriter docuDictWriter = null;

	public IndexWriter(String indexDir) {
		// TODO : YOU MUST IMPLEMENT THIS
		this.indexDirectory = indexDir;

		String termDictionaryLocation = "\\termDictionary.txt";		
		termIdCounter = 0;
		
		String docuDictionaryLocation = "\\docuDictionary.txt";
		docIdCounter = 0;

		try {
			termDictWriter = new BufferedWriter(new FileWriter(new File(indexDirectory + termDictionaryLocation)));
			docuDictWriter = new BufferedWriter(new FileWriter(new File(indexDirectory + docuDictionaryLocation)));
		} catch (IOException e) {
			System.err.println("IndexerException occured while writing to indexer files");
			e.printStackTrace();
		}
	}

	/**
	 * Method to add the given Document to the index This method should take
	 * care of reading the filed values, passing them through corresponding
	 * analyzers and then indexing the results for each indexable field within
	 * the document.
	 * 
	 * @param doc
	 *             : The Document to be added
	 * @throws IndexerException
	 *              : In case any error occurs
	 */
	public void addDocument(Document doc) throws IndexerException {
		// TODO : YOU MUST IMPLEMENT THIS
		AnalyzerFactory analyzerFactory = new AnalyzerFactory();

		
		try {

			Tokenizer tokenizer = new Tokenizer();
			String fieldText = null, documentId = null;
			List<FieldNames> fieldNameList = new ArrayList<FieldNames>();
			fieldNameList.add(FieldNames.TITLE);
			fieldNameList.add(FieldNames.AUTHOR);
			// Map<Integer, String> termIndex = new HashMap<Integer,
			// String>();

			for (FieldNames fieldName : fieldNameList) {
				if (doc.getField(fieldName) != null) {

					documentId = doc.getField(FieldNames.CATEGORY)[0] + doc.getField(FieldNames.FILEID)[0];
					documentDictionary.put(docIdCounter, documentId);

					fieldText = doc.getField(fieldName)[0];
					TokenStream tokenstream = tokenizer.consume(fieldText);

					Analyzer analyzer = analyzerFactory.getAnalyzerForField(fieldName, tokenstream);
					analyzer.processThroughFilters();
					tokenstream = analyzer.getStream();
					/* Transfer tokenstream into the dictionary */
					if (fieldName.equals(FieldNames.AUTHOR) || fieldName.equals(FieldNames.TITLE) || fieldName.equals(FieldNames.NEWSDATE)) {
						tokenstream.reset();
						while (tokenstream.hasNext()) {
							Token token = tokenstream.next();
							if (!termDictionary.containsValue(token.getTermText())) {
								termDictionary.put(termIdCounter++, token.getTermText());
							}
						}
					}
				}
			}

			/* Write to indexer files */
			for (int i = 0; i < termDictionary.size(); i++) {
				termDictWriter.write(i + " = " + termDictionary.get(i) + "\n");
			}
			docuDictWriter.write(docIdCounter++ + " = " + documentId + "\n");
			
			
		} catch (TokenizerException e) {
			System.out.println("Exception caught");
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new IndexerException("IndexerException occured while writing to indexer files");
		} catch (IOException e) {
			e.printStackTrace();
			throw new IndexerException("IndexerException occured while writing to indexer files");
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
		if (termDictWriter != null) {
			try {
				termDictWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
				throw new IndexerException("IndexerException occured while writing to indexer files");
			}
		}
	}
}
