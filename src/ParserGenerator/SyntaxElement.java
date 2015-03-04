package ParserGenerator;

public class SyntaxElement {
	SyntaxElement(){
		init();
	}
	public SyntaxElement(String name){
		init();
		this.name = name;
	}

	public SyntaxElement(int id){
		init();
		this.id = id;
	}

	public SyntaxElement(String name, int id){
		init();
		this.name = name;
		this.id = id;
	}
	public SyntaxElement(int id, String name){
		init();
		this.name = name;
		this.id = id;
	}
	
	protected void init(){
		terminators = new char[0];
		delimiters = new char[0];
		tags = new Object[0];
		ascendingTags = new Object[0];
		descendingTags = new Object[0];
		maxLength = -1;
		type = SyntaxElementType.NONE;
		name = "";
		id = 0;
		parser = null;
		isImportant = true;
	}
	public SyntaxElement setName(String name){
		this.name = name;
		return this;
	}
	
	public SyntaxElement setId(int id){
		this.id = id;
		return this;
	}
	
	public SyntaxElementType getType(){
		return type;
	}

	public SyntaxElement setTerminators(String terminators){
		this.terminators = terminators.toCharArray();
		return this;
	}
	
	public SyntaxElement setDelimiters(String delimiters){
		this.delimiters = delimiters.toCharArray();
		return this;
	}
	
	public SyntaxElement setMaxLength(int maxLength){
		this.maxLength = maxLength;
		return this;
	}
	
	public SyntaxElement setImportant(boolean isImportant){
		this.isImportant = isImportant;
		return this;
	}
	
	public SyntaxElement setTags(Object[] tags){
		this.tags = tags;
		return this;
	}

	public SyntaxElement setAscendingTags(Object[] tags){
		this.ascendingTags = tags;
		return this;
	}

	public SyntaxElement setDescendingTags(Object[] tags){
		this.descendingTags = tags;
		return this;
	}

	public final SyntaxNode recognize(String text){
		Integer start = 0;
		Integer length = text.length();
		char[] arrayText = text.toCharArray();
		return recognize(arrayText, new IntRef(start), new IntRef(length));
	}
	
	protected void restrict(char[] text, IntRef start, IntRef length){
		int l;
		if(maxLength>=0)
			length.value = length.value>maxLength?maxLength:length.value;
			
		length.value = findUntilNextFromList(text, terminators, start.value, length.value);
		
		l = findUntilNextOutsideList(text, delimiters, start.value, length.value);
		length.value -= l;
		start.value += l;
		
		l = findUntilNextFromList(text, delimiters, start.value, length.value);
		length.value = l;
		
		
		
	}
	
	public SyntaxNode recognize(char[] text, IntRef start, IntRef length){
		IntRef st = new IntRef(start.value);
		IntRef len = new IntRef(length.value);
		restrict(text, st, len);
		SyntaxNode result = new SyntaxNode(this);
		result.text = new String(text, st.value, len.value);
		recognize(result, text, st, len);
		

		if(result.isRecognized){
			st.value = st.value+len.value;
			len.value = length.value - (st.value-start.value);

			int l = findUntilNextOutsideList(text, delimiters, st.value, len.value);
			length.value = st.value-start.value+l;

			//start.value = st.value;
			//length.value = len.value;
		}
		return result;
	}
	
	protected static int findUntilNextFromList(char[] text, char[] lst, int start, int length){
		int l = 0;
		for(int i=start; i<start+length; i++){
			boolean found = false;
			for(int j=0; j<lst.length; j++){
				if(lst[j]==text[i]){
					found = true;
					break;
				}
			}
			if(found)
				break;
			l++;
		}
		return l;
	}
	
	protected static int findUntilNextOutsideList(char[] text, char[] lst, int start, int length){
		int l = 0;
		for(int i=start; i<start+length; i++){
			boolean found = false;
			for(int j=0; j<lst.length; j++){
				if(lst[j]==text[i]){
					found = true;
					break;
				}
			}
			if(!found)
				break;
			l++;
		}
		return l;
	}

	
	protected boolean registerToParser(Parser parser){
		if(this.parser==null){
			this.parser = parser;
			return true;
		}
		return false;
	}

	/*protected void registerToParser(SyntaxElement element){
		if(parser!=null)
			element.registerToParser(parser);
	}*/
	
	protected void recognize(SyntaxNode node, char[] text, IntRef start, IntRef length){
	}
	protected Object[] tags;
	protected Object[] ascendingTags;
	protected Object[] descendingTags;
	public String name;
	public int id;
	protected Parser parser;
	public boolean isImportant;
	private int maxLength;
	private char[] terminators;
	protected char[] delimiters;
	protected SyntaxElementType type;
}
