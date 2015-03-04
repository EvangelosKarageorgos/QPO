package ParserGenerator;
import java.util.ArrayList;
import java.util.List;

public class SyntaxElementSyntax extends SyntaxElement {
	public SyntaxElementSyntax(){
		super();
	}
	public SyntaxElementSyntax(String name){
		super(name);
	}
	public SyntaxElementSyntax(int id){
		super(id);
	}
	public SyntaxElementSyntax(String name, int id){
		super(name, id);
	}
	public SyntaxElementSyntax(int id, String name){
		super(id, name);
	}
	protected void init(){
		super.init();
		type = SyntaxElementType.SYNTAX;
		elements = new ArrayList<SyntaxElement>();
	}
	
	public SyntaxElementSyntax addElement(SyntaxElement element){
		elements.add(element);
		if(parser!=null)
			parser.addSyntaxElement(element);
		return this;
	}

	@Override
	protected boolean registerToParser(Parser parser){
		if(!super.registerToParser(parser))
			return false;
		for(SyntaxElement element : elements){
			parser.addSyntaxElement(element);
		}
		return true;
	}
	
	@Override
	protected void recognize(SyntaxNode node, char[] text, IntRef start, IntRef length){
		node.children = new ArrayList<SyntaxNode>();
		IntRef st = new IntRef(start.value);
		IntRef len = new IntRef(length.value);
		int l;
		

		
		
		for(SyntaxElement element : elements){

			/*l = findUntilNextOutsideList(text, delimiters, st.value, len.value);
			len.value -= l;
			st.value += l;
			
			l = findUntilNextFromList(text, delimiters, st.value, len.value);
			len.value = l;*/
			
			SyntaxNode child = element.recognize(text, st, len);
			if(!child.isRecognized)
				return;
			st.value = st.value+len.value;
			len.value = length.value - (st.value-start.value);
			node.children.add(child);
		}
		//l = findUntilNextOutsideList(text, delimiters, st.value, len.value);
		length.value = st.value-start.value;//+l;
		//if(st.value-start.value+l+1==length.value)
		//	return;
		node.isRecognized = true;
	}
	public List<SyntaxElement> elements;
}