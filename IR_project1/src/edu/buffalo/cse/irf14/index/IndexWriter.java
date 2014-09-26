/**
 * 
 */
package edu.buffalo.cse.irf14.index;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
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

	/* Booster scores and multipliers */
	final int BOOSTER_MULTIPLIER = 1;
	final int TITLE_BOOSTER = 3;
	final int AUTHOR_BOOSTER = 2;
	final int CONTENT_BOOSTER = 1;

	long startTime;
	public float writeTime, analyzerTime;
	String indexDirectory;

	public Map<String, Long> termDictionary = new HashMap<String, Long>();
	public Map<Long, String> documentDictionary = new HashMap<Long, String>();
	long termIdCounter, docIdCounter;

	/* File readers/writers */
	BufferedReader termIndexReader = null;
	BufferedWriter termIndexWriter = null;
	String termIndexLocation = File.separator + "termIndex.txt";

	/**
	 * Default constructor
	 * 
	 * @param indexDir
	 *             : The root directory to be sued for indexing
	 */
	public IndexWriter(String indexDir) {
		// TODO : YOU MUST IMPLEMENT THIS
		this.indexDirectory = indexDir;

		termIdCounter = 0;
		docIdCounter = 0;

		try {
			File termIndexFile = new File(indexDirectory + termIndexLocation);
			if (termIndexFile.exists()) {
				termIndexFile.delete();
			}
			termIndexFile.createNewFile();
			termIndexWriter = new BufferedWriter(new FileWriter(termIndexFile, true));
			termIndexReader = new BufferedReader(new FileReader(termIndexFile));
			// docuDictWriter = new BufferedWriter(new FileWriter(new
			// File(indexDirectory + docuDictionaryLocation)));
		} catch (IOException e) {
			System.err.println("IndexerException occured while writing to indexer files");
			e.printStackTrace();
		}

		writeTime = 0.0f;
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

			String documentId = doc.getField(FieldNames.CATEGORY)[0] + doc.getField(FieldNames.FILEID)[0];
			documentDictionary.put(docIdCounter, documentId);

			startTime = new Date().getTime();
			Tokenizer tokenizer = new Tokenizer();
			String fieldText = null;
			List<FieldNames> fieldNameList = new ArrayList<FieldNames>();
			fieldNameList.add(FieldNames.TITLE);
			fieldNameList.add(FieldNames.AUTHOR);

			Map<Long, TermMetadataForThisDoc> termsInThisDocument = new HashMap<Long, TermMetadataForThisDoc>();

			for (FieldNames fieldName : fieldNameList) {
				if (doc.getField(fieldName) != null) {

					fieldText = doc.getField(fieldName)[0];
					TokenStream tokenstream = tokenizer.consume(fieldText);

					Analyzer analyzer = analyzerFactory.getAnalyzerForField(fieldName, tokenstream);
					analyzer.processThroughFilters();
					tokenstream = analyzer.getStream();
					/* Transfer tokenstream into the dictionary */
					if (fieldName.equals(FieldNames.AUTHOR) || fieldName.equals(FieldNames.TITLE) || fieldName.equals(FieldNames.NEWSDATE) || fieldName.equals(FieldNames.CONTENT)) {
						tokenstream.reset();
						while (tokenstream.hasNext()) {
							Token token = tokenstream.next();
							Long termId = termIdCounter;

							/*
							 * Check if term dictionary already contains
							 * the term. If yes, get the ID. If not, add
							 * the term to the dictionary
							 */
							if (termDictionary.containsKey(token.getTermText())) {
								termId = termDictionary.get(token.getTermText());
							} else {
								termDictionary.put(token.getTermText(), termIdCounter++);
							}

							TermMetadataForThisDoc termMetadataForThisDoc;
							/*
							 * Set booster-score and frequency (relevant
							 * to this doc)
							 */
							int boosterScore = BOOSTER_MULTIPLIER * (fieldName.equals(FieldNames.TITLE) ? TITLE_BOOSTER : (fieldName.equals(FieldNames.AUTHOR) ? AUTHOR_BOOSTER : (fieldName.equals(FieldNames.CONTENT) ? CONTENT_BOOSTER : 1)));
							if (termsInThisDocument.containsKey(termId)) {

								termMetadataForThisDoc = termsInThisDocument.get(termId);
								termMetadataForThisDoc.setTermFrequency(termMetadataForThisDoc.getTermFrequency() + 1);
								termMetadataForThisDoc.setBoosterScore(termMetadataForThisDoc.getBoosterScore() + boosterScore);
							} else {
								termMetadataForThisDoc = new TermMetadataForThisDoc(1, boosterScore);
								termsInThisDocument.put(termId, termMetadataForThisDoc);
							}
						}
					}
				}
			}
			analyzerTime += (new Date().getTime() - startTime) / 1000.0;

			/* Write to term index */
			startTime = new Date().getTime();
			writeToTermIndex(termsInThisDocument, docIdCounter++);
			writeTime += (new Date().getTime() - startTime) / 1000.0;

		} catch (TokenizerException e) {
			System.out.println("Exception caught");
			e.printStackTrace();
		}
	}

	private void writeToTermIndex(Map<Long, TermMetadataForThisDoc> termsInThisDoc, long documentId) throws IndexerException {
		try {
			String line;
			if (termIndexReader != null) {
				while ((line = termIndexReader.readLine()) != null) {
					if (line.contains(" ")) {
						String firstTerm = line.substring(0, line.indexOf(' '));
						if (termsInThisDoc.containsKey(firstTerm)) {
							/*
							 * Term already exists in the index file. We
							 * need to transfer the metadata from the
							 * local index to the file
							 */
							
							termsInThisDoc.remove(firstTerm);
						}
					}
				}

				/* Write the remaining terms here from termsInThisDoc, that
				 * weren't in the index file */
				Iterator<Long> termIDIterator = termsInThisDoc.keySet().iterator();
				while (termIDIterator.hasNext()) {
					Long termId = termIDIterator.next();
					String writerString = termId + " " + documentId + ";" + termsInThisDoc.get(termId).getTermFrequency() + ";" + termsInThisDoc.get(termId).getBoosterScore() + "\n";
					termIndexWriter.write(writerString);
				}
			}

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

		System.out.println("\nTime for filtering ==> " + analyzerTime);
		System.out.println("Time for writing ==> " + writeTime);

		try {
			if (termIndexReader != null) {
				termIndexReader.close();
			}
			if (termIndexWriter != null) {
				termIndexWriter.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new IndexerException("IndexerException occured while writing to indexer files");
		}
	}

	class TermMetadataForThisDoc {
		int termFrequency;
		int boosterScore;

		public TermMetadataForThisDoc(int termFrequency, int boosterScore) {
			super();
			this.termFrequency = termFrequency;
			this.boosterScore = boosterScore;
		}

		public TermMetadataForThisDoc() {
			// TODO Auto-generated constructor stub
		}

		public int getTermFrequency() {
			return termFrequency;
		}

		public void setTermFrequency(int termFrequency) {
			this.termFrequency = termFrequency;
		}

		public int getBoosterScore() {
			return boosterScore;
		}

		public void setBoosterScore(int boosterScore) {
			this.boosterScore = boosterScore;
		}
	}
}
