package ParserGenerator;
import java.util.HashMap;
import java.util.Map;

public class Token {
	public class TokenResult
	{
		public TokenResult(String w){
			isRecognized = false;
			isSymbol = true;
			word = w;
			data = null;
			index = -1;
			number = 0;
		}
		@Override
		public String toString(){
			return isRecognized?(isSymbol?("symbol: \""+word+"\""):("number: "+number.toString())):("none");
		}
		public boolean isRecognized;
		public boolean isSymbol;
		public String word;
		public int index;
		public Object data;
		public Number number;
	}

	public Token(){
		init();
	}
	public Token(String name){
		init();
		this.name = name;
	}

	public Token(int id){
		init();
		this.id = id;
	}

	public Token(String name, int id){
		init();
		this.name = name;
		this.id = id;
	}
	public Token(int id, String name){
		init();
		this.name = name;
		this.id = id;
	}
	
	
	protected void init(){
		parser = null;
		name = null;
		id = 0;
		numberTypes = NumberTypes.NONE;
		isCaseSensitive = true;
		dictionary = new HashMap<String, Pair<Integer, Object>>();
	}
	public Token setName(String name){
		this.name = name;
		return this;
	}
	
	public Token setId(int id){
		this.id = id;
		return this;
	}
	
	public Token setCaseSensitivity(boolean isCaseSensitive){
		this.isCaseSensitive = isCaseSensitive;
		return this;
	}
	
	public Token setNumberTypes(NumberTypes types){
		numberTypes = types;
		return this;
	}
	
	public Token addSymbol(String word, Object data){
		int l = dictionary.size();
		dictionary.put(isCaseSensitive?word:word.toLowerCase(), new Pair<Integer, Object>(l, data));
		return this;
	}
	
	public TokenResult recognize(String word){
		String w = isCaseSensitive?word:word.toLowerCase();
		TokenResult result = new TokenResult(w);
		if(dictionary.containsKey(w)){
			Pair<Integer, Object> p = dictionary.get(w);
			result.isSymbol = true;
			result.index = p.getLeft();
			result.data = p.getRight();
			result.isRecognized = true;
		} else if(numberTypes!=NumberTypes.NONE && isNumeric(w)){
			try{
				switch(numberTypes){
				case NONE:
					break;
				case UNSIGNED_INTEGER:
					{
						int n = Integer.parseInt(w);
						result.number = n;
						if(n<0)
							throw new Exception();
					}
					
					break;
				case INTEGER:
					result.number = Integer.parseInt(w);
					break;
				case FLOAT:
					result.number = Float.parseFloat(w);
					break;
				case ANY:
					result.number = Float.parseFloat(w);
					break;
				default:
					break;
				}
				result.isRecognized = true;
				result.isSymbol = false;
			}
			catch(Exception ex){
			
			}
		}
		return result;
	}
	public static boolean isNumeric(String str)
	{
	  return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
	}

	protected void registerToParser(Parser parser){
		this.parser = parser;
	}

	public String name;
	public int id;
	private Parser parser;
	private Map<String, Pair<Integer, Object>> dictionary;
	private NumberTypes numberTypes;
	private boolean isCaseSensitive;
}
