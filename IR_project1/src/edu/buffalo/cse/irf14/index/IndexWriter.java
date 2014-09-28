package edu.buffalo.cse.irf14.index;

import java.io.BufferedWriter;
import java.io.File;
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

	/* File readers/writers */
	String termIndexFileNamePrefix = File.separator + "termIndex_";
	String termDictFileName = File.separator + "dictionaryOfTerms.txt";
	String docuDictFileName = File.separator + "dictionaryOfDocs.txt";
	String indexDirectory;

	long startTime;
	public float writeTime, analyzerTime;
	long termIdCounter, docIdCounter;

	public static Map<String, DictionaryMetadata> termDictionary = new HashMap<String, DictionaryMetadata>();
	public static Map<Long, String> documentDictionary = new HashMap<Long, String>();

	public static Map<Character, Map<Long, Map<Long, TermMetadataForThisDoc>>> termIndex;

	Map<Character, BufferedWriter> listOfWriters = null;
	BufferedWriter termDictionaryWriter = null;
	BufferedWriter docuDictionaryWriter = null;

	/**
	 * Default constructor
	 * 
	 * @param indexDir
	 *             : The root directory to be sued for indexing
	 */
	public IndexWriter(String indexDir) {
		this.indexDirectory = indexDir;
		listOfWriters = new HashMap<Character, BufferedWriter>();
		termIndex = new HashMap<Character, Map<Long, Map<Long, TermMetadataForThisDoc>>>();

		termIdCounter = 0;
		docIdCounter = 0;

		try {
			/* For dictionaries */
			File termDictFile = new File(indexDirectory + termDictFileName);
			File docuDictFile = new File(indexDirectory + docuDictFileName);
			if (termDictFile.exists())
				termDictFile.delete();
			termDictFile.createNewFile();
			if (docuDictFile.exists())
				docuDictFile.delete();
			docuDictFile.createNewFile();
			termDictionaryWriter = new BufferedWriter(new FileWriter(termDictFile, true));
			docuDictionaryWriter = new BufferedWriter(new FileWriter(docuDictFile, true));

			/*
			 * For indexes: Create the files
			 */
			File termIndexFile;
			for (char i = 'a'; i <= 'z'; i++) {
				termIndexFile = new File(indexDirectory + termIndexFileNamePrefix + i + ".txt");
				if (termIndexFile.exists()) {
					termIndexFile.delete();
				}
				BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(termIndexFile, true));
				listOfWriters.put(i, bufferedWriter);
			}

			/* Create the last file for miscellaneous characters */
			termIndexFile = new File(indexDirectory + termIndexFileNamePrefix + "_" + ".txt");
			if (termIndexFile.exists()) {
				termIndexFile.delete();
			}
			termIndexFile.createNewFile();
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(termIndexFile, true));
			listOfWriters.put('_', bufferedWriter);

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
			fieldNameList.add(FieldNames.CONTENT);

			// Map<Long, TermMetadataForThisDoc> termsInThisDocument = new
			// HashMap<Long, TermMetadataForThisDoc>();

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
								termId = termDictionary.get(token.getTermText()).getTermId();
							} else {
								DictionaryMetadata dictionaryMetadata = new DictionaryMetadata(termIdCounter++, 1);
								termDictionary.put(token.getTermText(), dictionaryMetadata);
							}

							/*
							 * Set booster-score and frequency (relevant
							 * to this doc)
							 */
							int boosterScore = BOOSTER_MULTIPLIER * (fieldName.equals(FieldNames.TITLE) ? TITLE_BOOSTER : (fieldName.equals(FieldNames.AUTHOR) ? AUTHOR_BOOSTER : (fieldName.equals(FieldNames.CONTENT) ? CONTENT_BOOSTER : 1)));

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

							/* The doc */
							TermMetadataForThisDoc termMetadataForThisDoc = null;
							if (termIndexForThisDoc.containsKey(docIdCounter)) {
								termMetadataForThisDoc = termIndexForThisDoc.get(docIdCounter);
								termMetadataForThisDoc.setTermFrequency(termMetadataForThisDoc.getTermFrequency() + 1);
								termMetadataForThisDoc.setBoosterScore(termMetadataForThisDoc.getBoosterScore() + boosterScore);

								/*
								 * Increase overall term frequency in
								 * term-dictionary
								 */
								termDictionary.get(token.getTermText()).setFrequency(termDictionary.get(token.getTermText()).getFrequency() + 1);
							} else {
								termMetadataForThisDoc = new TermMetadataForThisDoc(1, boosterScore, token.getTermText().charAt(0));
								termIndexForThisDoc.put(docIdCounter, termMetadataForThisDoc);
							}
						}
					}
				}
			}

			analyzerTime += (new Date().getTime() - startTime) / 1000.0;

			/* Write using RandomFileAccess */
			// writeToTermIndexWithRAF(termsInThisDocument, docIdCounter);

			docIdCounter++;

		} catch (TokenizerException e) {
			System.out.println("Exception caught");
			e.printStackTrace();
		}
		// catch (IOException e) {
		// e.printStackTrace();
		// throw new
		// IndexerException("IndexerException occured while writing to indexer files");
		// }
	}

	/* Write the term-dictionary to disk */
	private void writeTermDictionary() throws IOException {
		if (termDictionary != null) {
			for (String term : termDictionary.keySet()) {
				termDictionaryWriter.write(term + " = " + termDictionary.get(term).getTermId() + ";" + termDictionary.get(term).getFrequency() + "\n");
				
			}
		}
	}
	
	/* Write the document-dictionary to disk */
	private void writeDocumentDictionary() throws IOException {
		if (documentDictionary != null) {
			for (Long docId : documentDictionary.keySet()) {
				docuDictionaryWriter.write(docId + " = " + documentDictionary.get(docId) + "\n");
			}
		}
	}

	private void writeToTermIndexWithBuff() throws IOException {
		if (termIndex != null) {
			BufferedWriter writer = null;
			Iterator<Character> alphabets = termIndex.keySet().iterator();
			while (alphabets.hasNext()) {
				char character = alphabets.next();
				writer = listOfWriters.get(character);

				Map<Long, Map<Long, TermMetadataForThisDoc>> termMap = termIndex.get(character);
				StringBuilder strForThisAlphabet = new StringBuilder("");
				if (termMap != null) {
					Iterator<Long> termList = termMap.keySet().iterator();
					while (termList.hasNext()) {
						long termId = termList.next();
						Map<Long, TermMetadataForThisDoc> docIdMap = termMap.get(termId);
						strForThisAlphabet.append(termId + " = ");

						if (docIdMap != null) {

							Iterator<Long> docList = docIdMap.keySet().iterator();
							if (docList.hasNext()) {
								long docId = docList.next();
								TermMetadataForThisDoc metadata = docIdMap.get(docId);
								strForThisAlphabet.append(docId + ";" + metadata.getTermFrequency() + ";" + metadata.getBoosterScore());
							}
							while (docList.hasNext()) {
								long docId = docList.next();
								TermMetadataForThisDoc metadata = docIdMap.get(docId);
								strForThisAlphabet.append(" --> " + docId + ";" + metadata.getTermFrequency() + ";" + metadata.getBoosterScore());
							}
						}
						strForThisAlphabet.append("\n");
					}
				}
				writer.write(strForThisAlphabet.toString());
			}
		}
	}

	// private void writeToTermIndexWithRAF(Map<Long, TermMetadataForThisDoc>
	// termsInThisDoc, long documentId) throws IndexerException, IOException {
	// Iterator<Long> keySetIterator = termsInThisDoc.keySet().iterator();
	//
	// /* Move through each term in this doc */
	// while (keySetIterator.hasNext()) {
	// Long keyTermId = keySetIterator.next();
	// String keyTerm = termsInThisDoc.get(keyTermId).getTermText();
	// RandomAccessFile fileRW = null;
	//
	// if (keyTerm != null && keyTerm.length() > 0) {
	// char firstChar = keyTerm.toLowerCase().charAt(0);
	// if (firstChar >= 'a' && firstChar <= 'z') {
	// fileRW = listOfFileRWs.get(firstChar);
	// } else {
	// fileRW = listOfFileRWs.get('_');
	// }
	//
	// boolean found = false;
	// String fileLine = null;
	// long previousLinePos = 0;
	//
	// /* Find the keyTerm in the file */
	// while ((fileLine = fileRW.readLine()) != null) {
	// if (fileLine.matches("^(\\d+) .*$")) {
	// String firstTermInLine = fileLine.substring(0, fileLine.indexOf(' '));
	// if (firstTermInLine.trim().length() > 0 &&
	// firstTermInLine.equals(String.valueOf(keyTermId))) {
	// /*
	// * Term already exists in the index file. We
	// * need to transfer the metadata from the
	// * local index to the file
	// */
	// String modifiedLine = "\n" + fileLine + " --> " + documentId + ";" +
	// termsInThisDoc.get(keyTermId).getTermFrequency() + ";" +
	// termsInThisDoc.get(keyTermId).getBoosterScore() + "\n";
	//
	// fileRW.seek(previousLinePos);
	// String blankString = "";
	// for (int i = 0; i < fileLine.length(); i++)
	// blankString += " ";
	// fileRW.writeBytes(blankString);
	// fileRW.seek(fileRW.length());
	// fileRW.writeBytes(modifiedLine);
	// // fileRW.seek(previousLinePos + fileLine.length() + 2);
	//
	// fileRW.seek(0);
	// found = true;
	// break;
	// }
	// }
	// previousLinePos = fileRW.getFilePointer();
	// }
	//
	// /* Term wasn't found in the index file. We need to append it */
	// if (!found) {
	// String writerString = keyTermId + " " + documentId + ";" +
	// termsInThisDoc.get(keyTermId).getTermFrequency() + ";" +
	// termsInThisDoc.get(keyTermId).getBoosterScore() + "\n";
	// fileRW.seek(fileRW.length());
	// fileRW.writeBytes(writerString);
	// fileRW.seek(0);
	// }
	// }
	//
	// }
	// }

	/**
	 * Method that indicates that all open resources must be closed and cleaned
	 * and that the entire indexing operation has been completed.
	 * 
	 * @throws IndexerException
	 *              : In case any error occurs
	 */
	public void close() throws IndexerException {

		/* Write to term index */
		try {
			startTime = new Date().getTime();
			writeTermDictionary();
			writeDocumentDictionary();
			writeToTermIndexWithBuff();
			writeTime += (new Date().getTime() - startTime) / 1000.0;
		} catch (IOException e) {
			e.printStackTrace();
			throw new IndexerException("IndexerException occured while writing to indexer files");
		}

		System.out.println("\nTime for filtering ==> " + analyzerTime);
		System.out.println("Time for writing ==> " + writeTime);

		try {
			for (char c = 'a'; c <= 'z'; c++) {
				if (listOfWriters.get(c) != null) {
					listOfWriters.get(c).close();
					;
				}
			}
			if (listOfWriters.get('_') != null) {
				listOfWriters.get('_').close();
			}
			
			if (termDictionaryWriter != null) {
				termDictionaryWriter.close();
			}
			if (docuDictionaryWriter != null) {
				docuDictionaryWriter.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new IndexerException("IndexerException occured while writing to indexer files");
		}
	}


	public class TermMetadataForThisDoc {
		int termFrequency;
		int boosterScore;
		char firstLetter;

		public TermMetadataForThisDoc(int termFrequency, int boosterScore, char firstLetter) {
			super();
			this.termFrequency = termFrequency;
			this.boosterScore = boosterScore;
			this.firstLetter = firstLetter;
		}

		public TermMetadataForThisDoc() {
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

		public char getTermText() {
			return firstLetter;
		}

		public void setTermText(char firstLetter) {
			this.firstLetter = firstLetter;
		}
	}

	public class DictionaryMetadata {
		long termId;
		int frequency;

		public DictionaryMetadata() {

		}

		public DictionaryMetadata(long termId, int frequency) {
			super();
			this.termId = termId;
			this.frequency = frequency;
		}

		public long getTermId() {
			return termId;
		}

		public void setTermId(long termId) {
			this.termId = termId;
		}

		public int getFrequency() {
			return frequency;
		}

		public void setFrequency(int frequency) {
			this.frequency = frequency;
		}
	}
}
