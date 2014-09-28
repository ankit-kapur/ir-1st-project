package edu.buffalo.cse.irf14.analysis.util;

import java.io.Serializable;

public class TermMetadataForThisDoc implements Serializable {
	private static final long serialVersionUID = 1L;
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
