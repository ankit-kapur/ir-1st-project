package edu.buffalo.cse.irf14.analysis.util;

import java.io.Serializable;

public class DictionaryMetadata implements Serializable {
	private static final long serialVersionUID = 1L;
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