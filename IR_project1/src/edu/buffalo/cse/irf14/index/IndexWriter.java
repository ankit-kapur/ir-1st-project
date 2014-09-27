/**
 * 
 */
package edu.buffalo.cse.irf14.index;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
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
	Map<Character, RandomAccessFile> listOfFileRWs = null;
	String termIndexFileNamePrefix = File.separator + "termIndex_";
	String indexDirectory;

	long startTime;
	public float writeTime, analyzerTime;
	long termIdCounter, docIdCounter;

	public static Map<String, Long> termDictionary = new HashMap<String, Long>();
	public static Map<Long, String> documentDictionary = new HashMap<Long, String>();
	
	
	

	/**
	 * Default constructor
	 * 
	 * @param indexDir
	 *             : The root directory to be sued for indexing
	 */
	public IndexWriter(String indexDir) {
		// TODO : YOU MUST IMPLEMENT THIS
		this.indexDirectory = indexDir;
		listOfFileRWs = new HashMap<Character, RandomAccessFile>();

		termIdCounter = 0;
		docIdCounter = 0;

		try {
			/* Create the files */
			File termIndexFile;
			for (char i = 'a'; i <= 'z'; i++) {
				termIndexFile = new File(indexDirectory + termIndexFileNamePrefix + i + ".txt");
				if (termIndexFile.exists()) {
					termIndexFile.delete();
				}
				RandomAccessFile randomAccessFile = new RandomAccessFile(termIndexFile, "rw");
				listOfFileRWs.put(i, randomAccessFile);
			}

			/* Create the last file for miscellaneous characters */
			termIndexFile = new File(indexDirectory + termIndexFileNamePrefix + "_" + ".txt");
			if (termIndexFile.exists()) {
				termIndexFile.delete();
			}
			termIndexFile.createNewFile();
			RandomAccessFile randomAccessFile = new RandomAccessFile(termIndexFile, "rw");
			listOfFileRWs.put('_', randomAccessFile);

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
			fieldNameList.add(FieldNames.CONTENT);

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
								termMetadataForThisDoc = new TermMetadataForThisDoc(1, boosterScore, token.getTermText());
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
		} catch (IOException e) {
			e.printStackTrace();
			throw new IndexerException("IndexerException occured while writing to indexer files");
		}
	}

	private void writeToTermIndex(Map<Long, TermMetadataForThisDoc> termsInThisDoc, long documentId) throws IndexerException, IOException {
		Iterator<Long> keySetIterator = termsInThisDoc.keySet().iterator();

		/* Move through each term in this doc */
		while (keySetIterator.hasNext()) {
			Long keyTermId = keySetIterator.next();
			String keyTerm = termsInThisDoc.get(keyTermId).getTermText();
			RandomAccessFile fileRW = null;

			if (keyTerm != null && keyTerm.length() > 0) {
				char firstChar = keyTerm.toLowerCase().charAt(0);
				if (firstChar >= 'a' && firstChar <= 'z') {
					fileRW = listOfFileRWs.get(firstChar);
				} else {
					fileRW = listOfFileRWs.get('_');
				}

				boolean found = false;
				String fileLine = null;
				long previousLinePos = 0;

				/* Find the keyTerm in the file */
				while ((fileLine = fileRW.readLine()) != null) {
					if (fileLine.matches("^(\\d+) .*$")) {
						String firstTermInLine = fileLine.substring(0, fileLine.indexOf(' '));						
						if (firstTermInLine.trim().length() > 0 && firstTermInLine.equals(String.valueOf(keyTermId))) {
							/*
							 * Term already exists in the index file. We
							 * need to transfer the metadata from the
							 * local index to the file
							 */
							String modifiedLine = "\n" + fileLine + " --> " + documentId + ";" + termsInThisDoc.get(keyTermId).getTermFrequency() + ";" + termsInThisDoc.get(keyTermId).getBoosterScore() + "\n";

							fileRW.seek(previousLinePos);
							String blankString = "";
							for (int i = 0; i < fileLine.length(); i++)
								blankString += " ";
							fileRW.writeBytes(blankString);
							fileRW.seek(fileRW.length());
							fileRW.writeBytes(modifiedLine);
//							fileRW.seek(previousLinePos + fileLine.length() + 2);

							fileRW.seek(0);
							found = true;
							break;
						}
					}
					previousLinePos = fileRW.getFilePointer();
				}
				
				/* Term wasn't found in the index file. We need to append it */
				if (!found) {
					String writerString = keyTermId + " " + documentId + ";" + termsInThisDoc.get(keyTermId).getTermFrequency() + ";" + termsInThisDoc.get(keyTermId).getBoosterScore() + "\n";
					fileRW.seek(fileRW.length());
					fileRW.writeBytes(writerString);
					fileRW.seek(0);
				}
			}

		}

	}

//	private void writeToTermIndex2(Map<Long, TermMetadataForThisDoc> termsInThisDoc, long documentId) throws IndexerException {
//		try {
//			String fileLine;
//			if (randomAccessFile != null) {
//				while ((line = termIndexReader.readLine()) != null) {
//					if (line.contains(" ")) {
//						String firstTerm = line.substring(0, line.indexOf(' '));
//						if (termsInThisDoc.containsKey(firstTerm)) {
//							/*
//							 * Term already exists in the index file. We
//							 * need to transfer the metadata from the
//							 * local index to the file
//							 */
//
//							/* Remove from the tracker for this doc */
//							termsInThisDoc.remove(firstTerm);
//						}
//					}
//				}

//				long previousLinePos = 0;
//				while ((fileLine = randomAccessFile.readLine()) != null) {
//					/* Process line only if it begins with a digit */
//					if (fileLine.matches("^[0-9] .*$")) {
//
//						String termId = fileLine.substring(0, fileLine.indexOf(' '));
//						if (termsInThisDoc.containsKey(termId)) {
//							/*
//							 * Term already exists in the index file. We
//							 * need to transfer the metadata from the
//							 * local index to the file
//							 */
//							String modifiedLine = fileLine + " --> " + documentId + ";" + termsInThisDoc.get(termId).getTermFrequency() + ";" + termsInThisDoc.get(termId).getBoosterScore();
//
//							randomAccessFile.seek(previousLinePos);
//							String blankString = "";
//							for (int i = 0; i < fileLine.length(); i++)
//								blankString += " ";
//							randomAccessFile.writeBytes(blankString);
//							randomAccessFile.seek(termIndexFile.length());
//							randomAccessFile.writeBytes(modifiedLine);
//							randomAccessFile.seek(previousLinePos + fileLine.length() + 2);
//
//							/* Remove the term from the tracker */
//							termsInThisDoc.remove(termId);
//						}
//
//					}
//					previousLinePos = randomAccessFile.getFilePointer();
//				}
//
//				/*
//				 * Write the remaining terms here from termsInThisDoc, that
//				 * weren't in the index file
//				 */
//				Iterator<Long> termIDIterator = termsInThisDoc.keySet().iterator();
//				while (termIDIterator.hasNext()) {
//					Long termId = termIDIterator.next();
//					String writerString = termId + " " + documentId + ";" + termsInThisDoc.get(termId).getTermFrequency() + ";" + termsInThisDoc.get(termId).getBoosterScore() + "\n";
//					termIndexWriter.write(writerString);
//					randomAccessFile.writeBytes(writerString);
//				}
//				randomAccessFile.seek(0);
//			}
//
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//			throw new IndexerException("IndexerException occured while writing to indexer files");
//		} catch (IOException e) {
//			e.printStackTrace();
//			throw new IndexerException("IndexerException occured while writing to indexer files");
//		}
//	}

	/**
	 * Method that indicates that all open resources must be closed and cleaned
	 * and that the entire indexing operation has been completed.
	 * 
	 * @throws IndexerException
	 *              : In case any error occurs
	 */
	public void close() throws IndexerException {

		System.out.println("\ntermDictionary : " + termDictionary);
		System.out.println("\ndocDictionary: " + documentDictionary);
		
		System.out.println("\nTime for filtering ==> " + analyzerTime);
		System.out.println("Time for writing ==> " + writeTime);

		// try {
		// if (randomAccessFile != null) {
		// randomAccessFile.close();
		// }
		// } catch (IOException e) {
		// e.printStackTrace();
		// throw new
		// IndexerException("IndexerException occured while writing to indexer files");
		// }
	}

	class TermMetadataForThisDoc {
		int termFrequency;
		int boosterScore;
		String termText;

		public TermMetadataForThisDoc(int termFrequency, int boosterScore, String termText) {
			super();
			this.termFrequency = termFrequency;
			this.boosterScore = boosterScore;
			this.termText = termText;
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

		public String getTermText() {
			return termText;
		}

		public void setTermText(String termText) {
			this.termText = termText;
		}
	}
}
