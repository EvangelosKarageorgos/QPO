package ParserGenerator;

public class SyntaxElementNone extends SyntaxElement {
	public SyntaxElementNone(){
		super();
	}
	public SyntaxElementNone(String name){
		super(name);
	}
	public SyntaxElementNone(int id){
		super(id);
	}
	public SyntaxElementNone(String name, int id){
		super(name, id);
	}
	public SyntaxElementNone(int id, String name){
		super(id, name);
	}
	protected void init(){
		super.init();
		type = SyntaxElementType.NONE;
		isImportant = false;
	}
	
	@Override
	protected void recognize(SyntaxNode node, char[] text, IntRef start, IntRef length){
		node.isRecognized = length.value==0;
	}
}
