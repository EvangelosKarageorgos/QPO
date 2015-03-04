package ParserGenerator;
public class SyntaxElementAny extends SyntaxElement {
	public SyntaxElementAny(){
		super();
	}
	public SyntaxElementAny(String name){
		super(name);
	}

	public SyntaxElementAny(int id){
		super(id);
	}

	public SyntaxElementAny(String name, int id){
		super(name, id);
	}

	public SyntaxElementAny(int id, String name){
		super(id, name);
	}

	protected void init(){
		super.init();
		type = SyntaxElementType.ANY;
	}

	@Override
	protected void recognize(SyntaxNode node, char[] text, IntRef start, IntRef length){
		node.isRecognized = length.value>0;
	}
}