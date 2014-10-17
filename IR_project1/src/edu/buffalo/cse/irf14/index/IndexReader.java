/**
 * 
 */
package edu.buffalo.cse.irf14.index;

import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import edu.buffalo.cse.irf14.analysis.util.DictionaryMetadata;
import edu.buffalo.cse.irf14.analysis.util.TermMetadataForThisDoc;

/**
 * @author nikhillo Class that emulates reading data back from a written index
 */
public class IndexReader {
	public final String className = this.getClass().getName();

	public Map<String, DictionaryMetadata> termDictionary = new HashMap<String, DictionaryMetadata>();
	public Map<Long, String> documentDictionary = new HashMap<Long, String>();
	public Map<Character, Map<Long, Map<Long, TermMetadataForThisDoc>>> index;

	Map<Character, ObjectInputStream> listOfReaders = null;
	ObjectInputStream termDictionaryReader = null;
	ObjectInputStream docuDictionaryReader = null;

	/**
	 * Default constructor
	 * 
	 * @param indexDir
	 *             : The root directory from which the index is to be read.
	 *             This will be exactly the same directory as passed on
	 *             IndexWriter. In case you make subdirectories etc., you will
	 *             have to handle it accordingly.
	 * @param type
	 *             The {@link IndexType} to read from
	 */
	String indexDirectory = null;
	IndexType indexType = null;

	public IndexReader(String indexDir, IndexType type) {
		this.indexDirectory = indexDir;
		this.indexType = type;

		try {
			readFromFilesIntoObjects();
		} catch (IndexerException e) {
			System.err.println(e.getMessage());
		} catch (IOException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	private void readFromFilesIntoObjects() throws IndexerException, IOException {

		/* Build file names, according to the index type */
		String dictionaryFileName = null;
		String fileNamePrefix = indexDirectory;
		if (indexType.equals(IndexType.TERM)) {
			fileNamePrefix += IndexWriter.termIndexFileNamePrefix;
			dictionaryFileName = IndexWriter.termDictFileName;
		} else if (indexType.equals(IndexType.CATEGORY)) {
			fileNamePrefix += IndexWriter.categoryIndexFileNamePrefix;
			dictionaryFileName = IndexWriter.categDictFileName;
		} else if (indexType.equals(IndexType.AUTHOR)) {
			fileNamePrefix += IndexWriter.authorIndexFileNamePrefix;
			dictionaryFileName = IndexWriter.authorDictFileName;
		} else if (indexType.equals(IndexType.PLACE)) {
			fileNamePrefix += IndexWriter.placeIndexFileNamePrefix;
			dictionaryFileName = IndexWriter.placesDictFileName;
		}

		index = new HashMap<Character, Map<Long, Map<Long, TermMetadataForThisDoc>>>();
		try {
			/* Term dictionary */
			File termDictFile = new File(indexDirectory + dictionaryFileName);
			if (termDictFile.exists()) {
				termDictionaryReader = new ObjectInputStream(new BufferedInputStream(new FileInputStream(termDictFile)));
				termDictionary = (Map<String, DictionaryMetadata>) termDictionaryReader.readObject();
			}

			/* Document dictionary */
			File docDictFile = new File(indexDirectory + IndexWriter.docuDictFileName);
			if (docDictFile.exists()) {
				docuDictionaryReader = new ObjectInputStream(new BufferedInputStream(new FileInputStream(docDictFile)));
				documentDictionary = (Map<Long, String>) docuDictionaryReader.readObject();
			}

			/* Get the index */
			for (char c = 'a'; c <= 'z'; c++) {
				File indexFileForAlphabet = new File(fileNamePrefix + c + ".txt");
				if (indexFileForAlphabet.exists()) {
					ObjectInputStream inputStream = null;
					try {
						inputStream = new ObjectInputStream(new FileInputStream(indexFileForAlphabet));
						Map<Long, Map<Long, TermMetadataForThisDoc>> termMapForAlphabet = (Map<Long, Map<Long, TermMetadataForThisDoc>>) inputStream.readObject();
						index.put(c, termMapForAlphabet);
					} catch (EOFException e) {
						e.printStackTrace();
					} finally {
						if (inputStream != null) {
							inputStream.close();
						}
					}
				}
			}
			/* For miscellaneous terms */
			File indexFileForAlphabet = new File(fileNamePrefix + "_" + ".txt");
			if (indexFileForAlphabet.exists()) {
				ObjectInputStream inputStream = null;
				try {
					inputStream = new ObjectInputStream(new FileInputStream(indexFileForAlphabet));
					Map<Long, Map<Long, TermMetadataForThisDoc>> termMapForAlphabet = (Map<Long, Map<Long, TermMetadataForThisDoc>>) inputStream.readObject();
					index.put('_', termMapForAlphabet);
				} catch (EOFException e) {
					e.printStackTrace();
				} finally {
					if (inputStream != null) {
						inputStream.close();
					}
				}
			}

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new IndexerException("IndexerException occured while reading from indexer files");
		} finally {
			if (termDictionaryReader != null) {
				termDictionaryReader.close();
			}
			if (docuDictionaryReader != null) {
				docuDictionaryReader.close();
			}
		}
	}

	/**
	 * Get total number of terms from the "key" dictionary associated with this
	 * index. A postings list is always created against the "key" dictionary
	 * 
	 * @return The total number of terms
	 */
	public int getTotalKeyTerms() {
		int totalKeyTerms = 0;
		String gMethodName = "getTotalKeyTerms";
		try {

			if (indexType != null) {
				for (int i = 0; i < termDictionary.keySet().size(); i++)

					totalKeyTerms++;


			}
		} catch (Exception e) {
			System.err.println("Error in" + className + gMethodName);
			e.printStackTrace();
		}
		return totalKeyTerms;
		// TODO : YOU MUST IMPLEMENT THIS
	}

	/**
	 * Get total number of terms from the "value" dictionary associated with
	 * this index. A postings list is always created with the "value"
	 * dictionary
	 * 
	 * @return The total number of terms
	 */
	public int getTotalValueTerms() {
		int totalvalueTerms = 0;
		String gMethodName = "getTotalValueTerms";
		try {

			if (indexType != null) {
				for (int i = 0; i < documentDictionary.keySet().size(); i++) {
					totalvalueTerms++;
				}

			}
		} catch (Exception e) {
			System.err.println("Error in" + className + gMethodName);
			e.printStackTrace();
		}
		return totalvalueTerms;
	}

	/**
	 * Method to get the postings for a given term. You can assume that the raw
	 * string that is used to query would be passed through the same Analyzer
	 * as the original field would have been.
	 * 
	 * @param term
	 *             : The "analyzed" term to get postings for
	 * @return A Map containing the corresponding fileid as the key and the
	 *         number of occurrences as values if the given term was found,
	 *         null otherwise.
	 */
	public Map<String, Integer> getPostings(String term) {
		// TODO:YOU MUST IMPLEMENT THIS
		Map<String, Integer> postingsMap = null;
		long termId = -1, docId = -1;
		Map<Long, TermMetadataForThisDoc> documentIdToObjectMap = null;
		if (term == null) {
			return null;
		} else {
			if (termDictionary.get(term) != null) {
				postingsMap = new HashMap<String, Integer>();
				termId = termDictionary.get(term).getTermId();
				char firstChar = term.toLowerCase().charAt(0);

				Map<Long, Map<Long, TermMetadataForThisDoc>> indexAlphabetMap = index.get(firstChar);
				if (indexAlphabetMap != null) {
					documentIdToObjectMap = indexAlphabetMap.get(termId);

					if (documentIdToObjectMap != null) {
						Iterator<Long> docIterator = documentIdToObjectMap.keySet().iterator();
						while (docIterator.hasNext()) {
							docId = docIterator.next();
							TermMetadataForThisDoc metadataForDocTerm = documentIdToObjectMap.get(docId);
							postingsMap.put(documentDictionary.get(docId), metadataForDocTerm.getTermFrequency());
						}
					}
				}
			}
		}
		return postingsMap;
	}

	/**
	 * Method to get the top k terms from the index in terms of the total
	 * number of occurrences.
	 * 
	 * @param k
	 *             : The number of terms to fetch
	 * @return : An ordered list of results. Must be <=k fr valid k values null
	 *         for invalid k values
	 */
	public List<String> getTopK(int k) {
		// TODO YOU MUST IMPLEMENT THIS
		List<String> finalList = new ArrayList<String>();
		if (k == -1 || k == 0) {

			return null;
		} else {
			List<Map.Entry<String, DictionaryMetadata>> list = new ArrayList<Map.Entry<String, DictionaryMetadata>>(termDictionary.entrySet());
			Collections.sort(list, new Comparator<Map.Entry<String, DictionaryMetadata>>() {
				public int compare(Map.Entry<String, DictionaryMetadata> o1, Map.Entry<String, DictionaryMetadata> o2) {
					return o2.getValue().getFrequency() > o1.getValue().getFrequency() ? 1 : (o2.getValue().getFrequency() < o1.getValue().getFrequency() ? -1 : 0);
				}
			});

			// Convert sorted map back to a Map
			Map<String, DictionaryMetadata> sortedMap = new LinkedHashMap<String, DictionaryMetadata>();
			java.util.Iterator<Entry<String, DictionaryMetadata>> iterator = list.iterator();

			while (iterator.hasNext()) {
				Map.Entry<String, DictionaryMetadata> entry = iterator.next();
				sortedMap.put(entry.getKey(), entry.getValue());
			}

			for (Entry<String, DictionaryMetadata> entry : sortedMap.entrySet()) {
				finalList.add(entry.getKey());
				if (finalList.size() >= k)
					break;

			}
		}
		return finalList;

	}

	/**
	 * Method to implement a simple boolean AND query on the given index
	 * 
	 * @param terms
	 *             The ordered set of terms to AND, similar to getPostings()
	 *             the terms would be passed through the necessary Analyzer.
	 * 
	 * @return A Map (if all terms are found) containing FileId as the key and
	 *         number of occurrences as the value, the number of occurrences
	 *         would be the sum of occurrences for each participating term.
	 *         return null if the given term list returns no results BONUS ONLY
	 */

	public Map<String, Integer> query(String... terms) {

		Map<String, Integer> returnMap = null;

		/* TermID vs FileID */
		Map<Long, List<Long>> termVsDocumentMap = new HashMap<Long, List<Long>>();

		for (String term : terms) {
			if (term != null && term.length() > 0) {
				Map<Long, Map<Long, TermMetadataForThisDoc>> alphabetIndex = index.get(term.charAt(0));
				if (alphabetIndex != null) {
					DictionaryMetadata metadata = termDictionary.get(term);
					if (metadata != null) {
						Long termID = metadata.getTermId();
						Map<Long, TermMetadataForThisDoc> docMap = alphabetIndex.get(termID);
						if (docMap != null) {
							List<Long> docIdList = new ArrayList<Long>();
							for (Long docId : docMap.keySet()) {
								docIdList.add(docId);
							}
							termVsDocumentMap.put(termID, docIdList);
						}
					}
				}
			}
		}

		if (termVsDocumentMap != null && !termVsDocumentMap.isEmpty()) {
			Iterator<Long> mapIterator = termVsDocumentMap.keySet().iterator();
			Long firstTermId = mapIterator.next();
			List<Long> firstDocList = termVsDocumentMap.get(firstTermId);
			List<Long> docIDsMatched = new ArrayList<Long>();
			docIDsMatched.addAll(firstDocList);

			while (mapIterator.hasNext()) {

				Long currentTermId = mapIterator.next();
				List<Long> currentDocList = termVsDocumentMap.get(currentTermId);
				for (Long firstDocsId : firstDocList) {
					if (!currentDocList.contains(firstDocsId)) {
						docIDsMatched.remove(firstDocsId);
					}
				}
			}

			/* Create the return map based on the list docIDsMatched */
			returnMap = new HashMap<String, Integer>();
			for (Long docId : docIDsMatched) {
				String termId = documentDictionary.get(docId);
				Integer frequency = 0;

				/* Calculate frequency */
				for (String term : terms) {
					if (term != null && term.length() > 0) {
						Map<Long, Map<Long, TermMetadataForThisDoc>> alphabetIndex = index.get(term.charAt(0));
						if (alphabetIndex != null) {
							DictionaryMetadata metadata = termDictionary.get(term);
							if (metadata != null) {
								Long termID = metadata.getTermId();
								Map<Long, TermMetadataForThisDoc> docMap = alphabetIndex.get(termID);
								if (docMap != null) {
									TermMetadataForThisDoc termMetadata = docMap.get(docId);
									if (termMetadata != null) {
										frequency += termMetadata.getTermFrequency();
									}
								}
							}
						}
					}
				}

				returnMap.put(termId, frequency);
			}
		}

		if (returnMap != null && returnMap.isEmpty()) {
			returnMap = null;
		}
		return returnMap;
	}
}