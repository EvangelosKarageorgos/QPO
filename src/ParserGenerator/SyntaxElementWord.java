package ParserGenerator;
public class SyntaxElementWord extends SyntaxElement {
	public SyntaxElementWord(){
		super();
	}
	public SyntaxElementWord(String name){
		super(name);
	}
	public SyntaxElementWord(int id){
		super(id);
	}
	public SyntaxElementWord(String name, int id){
		super(name, id);
	}
	public SyntaxElementWord(int id, String name){
		super(id, name);
	}
	protected void init(){
		super.init();
		type = SyntaxElementType.WORD;
		this.word = "";
	}
	
	public SyntaxElementWord setWord(String word){
		this.word = word;
		return this;
	}
	
	@Override
	protected void recognize(SyntaxNode node, char[] text, IntRef start, IntRef length){
		node.isRecognized = node.text.equals(word);
	}
	public String word;
}