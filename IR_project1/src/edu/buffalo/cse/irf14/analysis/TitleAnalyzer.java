package edu.buffalo.cse.irf14.analysis;

public class TitleAnalyzer implements Analyzer {

	TokenStream tokenStream=null;
	TokenStream tStream=new TokenStream();
	public TitleAnalyzer(TokenStream stream) {
		// TODO Auto-generated constructor stub
		this.tokenStream=stream;
	}
	public TokenStream titleAnalyzer(TokenStream tokenStream)
	{
	
		SymbolFilter symbolFilter=new SymbolFilter(tokenStream);
		return tokenStream;
	}
	@Override
	public boolean increment() throws TokenizerException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public TokenStream getStream() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void processThroughFilters() {
		// TODO Auto-generated method stub
		
	}

}
