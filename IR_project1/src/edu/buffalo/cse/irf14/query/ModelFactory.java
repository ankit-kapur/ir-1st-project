package edu.buffalo.cse.irf14.query;

import edu.buffalo.cse.irf14.SearchRunner.ScoringModel;


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
