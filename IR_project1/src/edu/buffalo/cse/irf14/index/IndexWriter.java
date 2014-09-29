package edu.buffalo.cse.irf14.index;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.buffalo.cse.irf14.analysis.Analyzer;
import edu.buffalo.cse.irf14.analysis.AnalyzerFactory;
import edu.buffalo.cse.irf14.analysis.Token;
import edu.buffalo.cse.irf14.analysis.TokenStream;
import edu.buffalo.cse.irf14.analysis.Tokenizer;
import edu.buffalo.cse.irf14.analysis.TokenizerException;
import edu.buffalo.cse.irf14.analysis.util.DictionaryMetadata;
import edu.buffalo.cse.irf14.analysis.util.TermMetadataForThisDoc;
import edu.buffalo.cse.irf14.document.Document;
import edu.buffalo.cse.irf14.document.FieldNames;

/**
 * @author nikhillo Class responsible for writing indexes to disk
 */
public class IndexWriter {

	/* Data structures for dictionaries and indexes */
	Map<Long, String> documentDictionary = new HashMap<Long, String>();
	Map<String, DictionaryMetadata> termDictionary = new HashMap<String, DictionaryMetadata>();
	Map<String, DictionaryMetadata> categoryDictionary = new HashMap<String, DictionaryMetadata>();
	Map<String, DictionaryMetadata> authorDictionary = new HashMap<String, DictionaryMetadata>();
	Map<String, DictionaryMetadata> placesDictionary = new HashMap<String, DictionaryMetadata>();

	Map<Character, Map<Long, Map<Long, TermMetadataForThisDoc>>> termIndex;
	Map<Character, Map<Long, Map<Long, TermMetadataForThisDoc>>> categoryIndex;
	Map<Character, Map<Long, Map<Long, TermMetadataForThisDoc>>> authorIndex;
	Map<Character, Map<Long, Map<Long, TermMetadataForThisDoc>>> placesIndex;

	/* File readers/writers */
	public static String termIndexFileNamePrefix = File.separator + "term_index_";
	public static String categoryIndexFileNamePrefix = File.separator + "category_index_";
	public static String authorIndexFileNamePrefix = File.separator + "author_index_";
	public static String placeIndexFileNamePrefix = File.separator + "place_index_";

	public static String termDictFileName = File.separator + "dictionary_terms.txt";
	public static String categDictFileName = File.separator + "dictionary_categories.txt";
	public static String authorDictFileName = File.separator + "dictionary_authors.txt";
	public static String placesDictFileName = File.separator + "dictionary_places.txt";

	public static String docuDictFileName = File.separator + "dictionaryOfDocs.txt";
	String indexDirectory;

	/* File writer for document dictionary */
	ObjectOutputStream docuDictionaryWriter = null;

	/* Booster scores and multipliers */
	final int BOOSTER_MULTIPLIER = 1;
	final int TITLE_BOOSTER = 3;
	final int AUTHOR_BOOSTER = 2;
	final int CONTENT_BOOSTER = 1;
	final int PLACES_BOOSTER = 2;
	final int CATEGORY_BOOSTER = 3;
	
	/* Miscellaneous declarations */
//	long startTime;
//	public float writeTime, analyzerTime;
	long docIdCounter, termIdCounter, authorIdCounter, categoryIdCounter, placeIdCounter;

	/**
	 * Default constructor
	 * 
	 * @param indexDir
	 *             : The root directory to be sued for indexing
	 */
	public IndexWriter(String indexDir) {
		this.indexDirectory = indexDir;
		// listOfWriters = new HashMap<Character, ObjectOutputStream>();
		termIndex = new HashMap<Character, Map<Long, Map<Long, TermMetadataForThisDoc>>>();
		categoryIndex = new HashMap<Character, Map<Long, Map<Long, TermMetadataForThisDoc>>>();
		authorIndex = new HashMap<Character, Map<Long, Map<Long, TermMetadataForThisDoc>>>();
		placesIndex = new HashMap<Character, Map<Long, Map<Long, TermMetadataForThisDoc>>>();

		termIdCounter = 0;
		docIdCounter = 0;

//		writeTime = 0.0f;
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
		AnalyzerFactory analyzerFactory = new AnalyzerFactory();

		try {
			String category = (doc.getField(FieldNames.CATEGORY) != null && doc.getField(FieldNames.CATEGORY).length > 0) ? doc.getField(FieldNames.CATEGORY)[0] : "";
			String documentId = category + doc.getField(FieldNames.FILEID)[0];
			documentDictionary.put(docIdCounter, documentId);

//			startTime = new Date().getTime();
			Tokenizer tokenizer = new Tokenizer();
			String fieldText = null;

			/* Building the TERM index */
			List<FieldNames> fieldNameList = new ArrayList<FieldNames>();
			fieldNameList.add(FieldNames.TITLE);
			fieldNameList.add(FieldNames.CONTENT);

			for (FieldNames fieldName : fieldNameList) {
				if (doc.getField(fieldName) != null) {

					fieldText = doc.getField(fieldName)[0];
					TokenStream tokenstream = tokenizer.consume(fieldText);

					Analyzer analyzer = analyzerFactory.getAnalyzerForField(fieldName, tokenstream);
					analyzer.processThroughFilters();
					tokenstream = analyzer.getStream();

					/* Transfer tokenstream into the dictionary */
					if (fieldName.equals(FieldNames.TITLE) || fieldName.equals(FieldNames.NEWSDATE) || fieldName.equals(FieldNames.CONTENT)) {
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
								termId = termDictionary.get(token.getTermText()).getTermId();
								/*
								 * Increase overall term frequency in
								 * term-dictionary
								 */
								termDictionary.get(token.getTermText()).setFrequency(termDictionary.get(token.getTermText()).getFrequency() + 1);

							} else {
								DictionaryMetadata dictionaryMetadata = new DictionaryMetadata(termIdCounter++, 1);
								termDictionary.put(token.getTermText(), dictionaryMetadata);
							}

							/*
							 * Set booster-score and frequency (relevant
							 * to this doc)
							 */
							int boosterScore = BOOSTER_MULTIPLIER * (fieldName.equals(FieldNames.TITLE) ? TITLE_BOOSTER : (fieldName.equals(FieldNames.CONTENT) ? CONTENT_BOOSTER : 1));

							/* Put in the corresponding alphabet-index */
							Map<Long, Map<Long, TermMetadataForThisDoc>> termIndexForThisAlphabet;
							char firstChar = token.getTermText().toLowerCase().charAt(0);
							if (termIndex.containsKey(firstChar)) {
								termIndexForThisAlphabet = termIndex.get(firstChar);
							} else {
								termIndexForThisAlphabet = new HashMap<Long, Map<Long, TermMetadataForThisDoc>>();

								if (firstChar >= 'a' && firstChar <= 'z') {
									termIndex.put(firstChar, termIndexForThisAlphabet);
								} else {
									termIndex.put('_', termIndexForThisAlphabet);
								}
							}

							/* Put in the term index */
							Map<Long, TermMetadataForThisDoc> termIndexForThisDoc;
							if (termIndexForThisAlphabet.containsKey(termId)) {
								termIndexForThisDoc = termIndexForThisAlphabet.get(termId);
							} else {
								termIndexForThisDoc = new HashMap<Long, TermMetadataForThisDoc>();
								termIndexForThisAlphabet.put(termId, termIndexForThisDoc);
							}

							/* For the doc */
							TermMetadataForThisDoc termMetadataForThisDoc = null;
							if (termIndexForThisDoc.containsKey(docIdCounter)) {
								termMetadataForThisDoc = termIndexForThisDoc.get(docIdCounter);
								termMetadataForThisDoc.setTermFrequency(termMetadataForThisDoc.getTermFrequency() + 1);
								termMetadataForThisDoc.setBoosterScore(termMetadataForThisDoc.getBoosterScore() + boosterScore);

							} else {
								termMetadataForThisDoc = new TermMetadataForThisDoc(1, boosterScore, token.getTermText().charAt(0));
								termIndexForThisDoc.put(docIdCounter, termMetadataForThisDoc);
							}
						}
					}
				}
			}

			/* Building the AUTHOR index */
			FieldNames fieldName = FieldNames.AUTHOR;
			if (doc.getField(fieldName) != null) {
				fieldText = doc.getField(fieldName)[0];
				TokenStream tokenstream = tokenizer.consume(fieldText);

				Analyzer analyzer = analyzerFactory.getAnalyzerForField(fieldName, tokenstream);
				analyzer.processThroughFilters();
				tokenstream = analyzer.getStream();

				/* Transfer tokenstream into the dictionary */
				tokenstream.reset();
				while (tokenstream.hasNext()) {
					Token token = tokenstream.next();
					Long termId = authorIdCounter;

					/*
					 * Check if the dictionary already contains the term.
					 * If yes, get the ID. If not, add the term to the dictionary
					 */
					if (authorDictionary.containsKey(token.getTermText())) {
						termId = authorDictionary.get(token.getTermText()).getTermId();
						/*
						 * Increase overall term frequency in
						 * term-dictionary
						 */
						authorDictionary.get(token.getTermText()).setFrequency(authorDictionary.get(token.getTermText()).getFrequency() + 1);

					} else {
						DictionaryMetadata dictionaryMetadata = new DictionaryMetadata(authorIdCounter++, 1);
						authorDictionary.put(token.getTermText(), dictionaryMetadata);
					}

					/*
					 * Set booster-score and frequency (relevant to this
					 * doc)
					 */
					int boosterScore = BOOSTER_MULTIPLIER * AUTHOR_BOOSTER;

					/* Put in the corresponding alphabet-index */
					Map<Long, Map<Long, TermMetadataForThisDoc>> termIndexForThisAlphabet;
					char firstChar = token.getTermText().toLowerCase().charAt(0);
					if (authorIndex.containsKey(firstChar)) {
						termIndexForThisAlphabet = authorIndex.get(firstChar);
					} else {
						termIndexForThisAlphabet = new HashMap<Long, Map<Long, TermMetadataForThisDoc>>();

						if (firstChar >= 'a' && firstChar <= 'z') {
							authorIndex.put(firstChar, termIndexForThisAlphabet);
						} else {
							authorIndex.put('_', termIndexForThisAlphabet);
						}
					}

					/* Put in the term index */
					Map<Long, TermMetadataForThisDoc> termIndexForThisDoc;
					if (termIndexForThisAlphabet.containsKey(termId)) {
						termIndexForThisDoc = termIndexForThisAlphabet.get(termId);
					} else {
						termIndexForThisDoc = new HashMap<Long, TermMetadataForThisDoc>();
						termIndexForThisAlphabet.put(termId, termIndexForThisDoc);
					}

					/* For the doc */
					TermMetadataForThisDoc termMetadataForThisDoc = null;
					if (termIndexForThisDoc.containsKey(docIdCounter)) {
						termMetadataForThisDoc = termIndexForThisDoc.get(docIdCounter);
						termMetadataForThisDoc.setTermFrequency(termMetadataForThisDoc.getTermFrequency() + 1);
						termMetadataForThisDoc.setBoosterScore(termMetadataForThisDoc.getBoosterScore() + boosterScore);

					} else {
						termMetadataForThisDoc = new TermMetadataForThisDoc(1, boosterScore, token.getTermText().charAt(0));
						termIndexForThisDoc.put(docIdCounter, termMetadataForThisDoc);
					}
				}
			}
			
			/* Building the PLACES index */
			fieldName = FieldNames.PLACE;
			if (doc.getField(fieldName) != null) {
				fieldText = doc.getField(fieldName)[0];
				TokenStream tokenstream = tokenizer.consume(fieldText);
				
				Analyzer analyzer = analyzerFactory.getAnalyzerForField(fieldName, tokenstream);
				analyzer.processThroughFilters();
				tokenstream = analyzer.getStream();

				/* Transfer tokenstream into the dictionary */
				tokenstream.reset();
				while (tokenstream.hasNext()) {
					Token token = tokenstream.next();
					Long termId = placeIdCounter;

					/*
					 * Check if the dictionary already contains the term.
					 * If yes, get the ID. If not, add the term to the dictionary
					 */
					if (placesDictionary.containsKey(token.getTermText())) {
						termId = placesDictionary.get(token.getTermText()).getTermId();
						/*
						 * Increase overall term frequency in
						 * term-dictionary
						 */
						placesDictionary.get(token.getTermText()).setFrequency(placesDictionary.get(token.getTermText()).getFrequency() + 1);

					} else {
						DictionaryMetadata dictionaryMetadata = new DictionaryMetadata(placeIdCounter++, 1);
						placesDictionary.put(token.getTermText(), dictionaryMetadata);
					}

					/*
					 * Set booster-score and frequency (relevant to this
					 * doc)
					 */
					int boosterScore = BOOSTER_MULTIPLIER * PLACES_BOOSTER;

					/* Put in the corresponding alphabet-index */
					Map<Long, Map<Long, TermMetadataForThisDoc>> termIndexForThisAlphabet;
					char firstChar = token.getTermText().toLowerCase().charAt(0);
					if (placesIndex.containsKey(firstChar)) {
						termIndexForThisAlphabet = placesIndex.get(firstChar);
					} else {
						termIndexForThisAlphabet = new HashMap<Long, Map<Long, TermMetadataForThisDoc>>();

						if (firstChar >= 'a' && firstChar <= 'z') {
							placesIndex.put(firstChar, termIndexForThisAlphabet);
						} else {
							placesIndex.put('_', termIndexForThisAlphabet);
						}
					}

					/* Put in the term index */
					Map<Long, TermMetadataForThisDoc> termIndexForThisDoc;
					if (termIndexForThisAlphabet.containsKey(termId)) {
						termIndexForThisDoc = termIndexForThisAlphabet.get(termId);
					} else {
						termIndexForThisDoc = new HashMap<Long, TermMetadataForThisDoc>();
						termIndexForThisAlphabet.put(termId, termIndexForThisDoc);
					}

					/* For the doc */
					TermMetadataForThisDoc termMetadataForThisDoc = null;
					if (termIndexForThisDoc.containsKey(docIdCounter)) {
						termMetadataForThisDoc = termIndexForThisDoc.get(docIdCounter);
						termMetadataForThisDoc.setTermFrequency(termMetadataForThisDoc.getTermFrequency() + 1);
						termMetadataForThisDoc.setBoosterScore(termMetadataForThisDoc.getBoosterScore() + boosterScore);

					} else {
						termMetadataForThisDoc = new TermMetadataForThisDoc(1, boosterScore, token.getTermText().charAt(0));
						termIndexForThisDoc.put(docIdCounter, termMetadataForThisDoc);
					}
				}
			}

			/* Building the CATEGORY index */
			fieldName = FieldNames.CATEGORY;
			if (doc.getField(fieldName) != null) {
				fieldText = doc.getField(fieldName)[0];
				TokenStream tokenstream = tokenizer.consume(fieldText);

				Analyzer analyzer = analyzerFactory.getAnalyzerForField(fieldName, tokenstream);
				analyzer.processThroughFilters();
				tokenstream = analyzer.getStream();

				/* Transfer tokenstream into the dictionary */
				tokenstream.reset();
				while (tokenstream.hasNext()) {
					Token token = tokenstream.next();
					Long termId = categoryIdCounter;

					/*
					 * Check if the dictionary already contains the term.
					 * If yes, get the ID. If not, add the term to the dictionary
					 */
					if (categoryDictionary.containsKey(token.getTermText())) {
						termId = categoryDictionary.get(token.getTermText()).getTermId();
						/*
						 * Increase overall term frequency in
						 * term-dictionary
						 */
						categoryDictionary.get(token.getTermText()).setFrequency(categoryDictionary.get(token.getTermText()).getFrequency() + 1);

					} else {
						DictionaryMetadata dictionaryMetadata = new DictionaryMetadata(categoryIdCounter++, 1);
						categoryDictionary.put(token.getTermText(), dictionaryMetadata);
					}

					/*
					 * Set booster-score and frequency (relevant to this
					 * doc)
					 */
					int boosterScore = BOOSTER_MULTIPLIER * CATEGORY_BOOSTER;

					/* Put in the corresponding alphabet-index */
					Map<Long, Map<Long, TermMetadataForThisDoc>> termIndexForThisAlphabet;
					char firstChar = token.getTermText().toLowerCase().charAt(0);
					if (categoryIndex.containsKey(firstChar)) {
						termIndexForThisAlphabet = categoryIndex.get(firstChar);
					} else {
						termIndexForThisAlphabet = new HashMap<Long, Map<Long, TermMetadataForThisDoc>>();

						if (firstChar >= 'a' && firstChar <= 'z') {
							categoryIndex.put(firstChar, termIndexForThisAlphabet);
						} else {
							categoryIndex.put('_', termIndexForThisAlphabet);
						}
					}

					/* Put in the term index */
					Map<Long, TermMetadataForThisDoc> termIndexForThisDoc;
					if (termIndexForThisAlphabet.containsKey(termId)) {
						termIndexForThisDoc = termIndexForThisAlphabet.get(termId);
					} else {
						termIndexForThisDoc = new HashMap<Long, TermMetadataForThisDoc>();
						termIndexForThisAlphabet.put(termId, termIndexForThisDoc);
					}

					/* For the doc */
					TermMetadataForThisDoc termMetadataForThisDoc = null;
					if (termIndexForThisDoc.containsKey(docIdCounter)) {
						termMetadataForThisDoc = termIndexForThisDoc.get(docIdCounter);
						termMetadataForThisDoc.setTermFrequency(termMetadataForThisDoc.getTermFrequency() + 1);
						termMetadataForThisDoc.setBoosterScore(termMetadataForThisDoc.getBoosterScore() + boosterScore);

					} else {
						termMetadataForThisDoc = new TermMetadataForThisDoc(1, boosterScore, token.getTermText().charAt(0));
						termIndexForThisDoc.put(docIdCounter, termMetadataForThisDoc);
					}
				}
			}
			
//			analyzerTime += (new Date().getTime() - startTime) / 1000.0;
			docIdCounter++;

		} catch (TokenizerException e) {
			System.out.println("Exception caught");
			e.printStackTrace();
		}
	}

	/* Write the document-dictionary to disk */
	private void writeDocumentDictionary() throws IOException {
		if (documentDictionary != null) {
			File docuDictFile = new File(indexDirectory + docuDictFileName);
			if (docuDictFile.exists())
				docuDictFile.delete();
			docuDictFile.createNewFile();
			docuDictionaryWriter = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(docuDictFile, true)));
			docuDictionaryWriter.writeObject(documentDictionary);
		}
	}

	/* Write the term-dictionary to disk */
	private void writeDictionaryToFile(String dictFileName, Map<String, DictionaryMetadata> dictionary) throws IOException {
		if (dictionary != null) {
			File termDictFile = new File(indexDirectory + dictFileName);
			if (termDictFile.exists())
				termDictFile.delete();
			termDictFile.createNewFile();

			ObjectOutputStream termDictionaryWriter = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(termDictFile, true)));
			if (termDictionaryWriter != null) {
				/* Write the dictionary */
				termDictionaryWriter.writeObject(dictionary);

				/* Close the dictionary writer */
				termDictionaryWriter.close();
			}
		}
	}

	private void writeIndexToFile(String indexFileNamePrefix, Map<Character, Map<Long, Map<Long, TermMetadataForThisDoc>>> index) throws IOException {
		/* For indexes: Create the files */
		File termIndexFile;
		for (char i = 'a'; i <= 'z'; i++) {
			termIndexFile = new File(indexDirectory + indexFileNamePrefix + i + ".txt");
			if (termIndexFile.exists()) {
				termIndexFile.delete();
			}
			termIndexFile.createNewFile();
			ObjectOutputStream writer = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(termIndexFile, true)));
			if (writer != null) {
				writer.writeObject(index.get(i));
				writer.close();
			}
		}

		/* Create the last file for miscellaneous characters */
		termIndexFile = new File(indexDirectory + indexFileNamePrefix + "_" + ".txt");
		if (termIndexFile.exists()) {
			termIndexFile.delete();
		}
		termIndexFile.createNewFile();
		ObjectOutputStream writer = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(termIndexFile, true)));
		if (writer != null) {
			writer.writeObject(index.get('_'));
			writer.close();
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

		try {
//			startTime = new Date().getTime();

			/* Write the document dictionaries to files */
			writeDocumentDictionary();

			/* Write the term dictionaries */
			writeDictionaryToFile(termDictFileName, termDictionary);
			writeDictionaryToFile(categDictFileName, categoryDictionary);
			writeDictionaryToFile(authorDictFileName, authorDictionary);
			writeDictionaryToFile(placesDictFileName, placesDictionary);

			/* Write the indexes to files */
			writeIndexToFile(termIndexFileNamePrefix, termIndex);
			writeIndexToFile(categoryIndexFileNamePrefix, categoryIndex);
			writeIndexToFile(authorIndexFileNamePrefix, authorIndex);
			writeIndexToFile(placeIndexFileNamePrefix, placesIndex);

//			writeTime += (new Date().getTime() - startTime) / 1000.0;
		} catch (IOException e) {
			e.printStackTrace();
			throw new IndexerException("IndexerException occured while writing to indexer files");
		}

//		System.out.println("\nTime for filtering ==> " + analyzerTime);
//		System.out.println("Time for writing ==> " + writeTime);

		try {
			if (docuDictionaryWriter != null) {
				docuDictionaryWriter.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new IndexerException("IndexerException occured while writing to indexer files");
		}
	}
}
