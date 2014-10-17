package edu.buffalo.cse.irf14.query;

import edu.buffalo.cse.irf14.SearchRunner.ScoringModel;
import edu.buffalo.cse.irf14.analysis.Analyzer;
import edu.buffalo.cse.irf14.analysis.AnalyzerFactory;
import edu.buffalo.cse.irf14.analysis.TokenStream;
import edu.buffalo.cse.irf14.analysis.analyzer.AuthorAnalyzer;
import edu.buffalo.cse.irf14.analysis.analyzer.CategoryAnalyzer;
import edu.buffalo.cse.irf14.analysis.analyzer.DateAnalyzer;
import edu.buffalo.cse.irf14.analysis.analyzer.PlaceAnalyzer;
import edu.buffalo.cse.irf14.analysis.analyzer.TermAnalyzer;
import edu.buffalo.cse.irf14.analysis.analyzer.TitleAnalyzer;
import edu.buffalo.cse.irf14.document.FieldNames;

public class ModelFactory {
	
	private static ModelFactory modelfactoryInstance;
	
	public static ModelFactory getInstance() {
		// TODO: YOU NEED TO IMPLEMENT THIS METHOD
		if (modelfactoryInstance == null) {
			modelfactoryInstance = new ModelFactory();
		}
		return modelfactoryInstance;
	}
	
	public ScoreModel getModelForQuery(ScoringModel model) {
		try {
			if (model.equals(ScoringModel.TFIDF))
				return new TfIdfModel();
			else if (model.equals(ScoringModel.OKAPI))
				return new OkapiModel();		
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
