package ParserGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Parser {
	private Map<String, SyntaxElement> syntaxElementsByName;
	private Map<Integer, SyntaxElement> syntaxElementsById;
	private List<SyntaxElement> syntaxElements;

	private Map<String, Token> tokensByName;
	private Map<Integer, Token> tokensById;
	private List<Token> tokens;

	private SyntaxElement root;
	
	protected void init(){
		syntaxElementsByName = new HashMap<String, SyntaxElement>();
		syntaxElementsById = new HashMap<Integer, SyntaxElement>();
		syntaxElements = new ArrayList<SyntaxElement>();
		tokensByName = new HashMap<String, Token>();
		tokensById = new HashMap<Integer, Token>();
		tokens = new ArrayList<Token>();
		root = null;
	}
	public Parser(){
		init();
	}

	public void addSyntaxElement(SyntaxElement element){
		if(element.id>0 && !syntaxElementsById.containsKey(element.id))
			syntaxElementsById.put(element.id, element);
		if(element.name!=null && element.name.length()>0 && !syntaxElementsByName.containsKey(element.name))
			syntaxElementsByName.put(element.name, element);
		syntaxElements.add(element);
		element.registerToParser(this);
	}

	public void addToken(Token token){
		if(token.id>0 && !tokensById.containsKey(token.id))
			tokensById.put(token.id, token);
		if(token.name!=null && token.name.length()>0 && !tokensByName.containsKey(token.name))
			tokensByName.put(token.name, token);
		tokens.add(token);
		token.registerToParser(this);
	}

	public void setRootElement(SyntaxElement element){
		root = element;
	}

	public Token getToken(int id){
		if(tokensById.containsKey(id))
			return tokensById.get(id);
		return null;
	}

	public Token getToken(String name){
		if(tokensByName.containsKey(name))
			return tokensByName.get(name);
		return null;
	}

	public SyntaxElement getSyntaxElement(int id){
		if(syntaxElementsById.containsKey(id))
			return syntaxElementsById.get(id);
		return null;
	}

	public SyntaxElement getSyntaxElement(String name){
		if(syntaxElementsByName.containsKey(name))
			return syntaxElementsByName.get(name);
		return null;
	}
	
	public SyntaxNode recognize(String text){
		SyntaxNode result = root.recognize(text);
		result.setTags();
		result.prune();
		return result;
	}

}
