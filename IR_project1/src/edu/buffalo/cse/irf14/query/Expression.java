package edu.buffalo.cse.irf14.query;

import java.util.HashMap;
import java.util.Map;

import edu.buffalo.cse.irf14.analysis.util.TermMetadataForThisDoc;

public interface Expression {

	@Override
	public String toString();
	public Map<Character, Map<Long, Map<Long, TermMetadataForThisDoc>>> getPostings();

}
