package ParserGenerator;
import java.util.ArrayList;

public class SyntaxElementLoop extends SyntaxElement {
	public SyntaxElementLoop(){
		super();
	}
	public SyntaxElementLoop(String name){
		super(name);
	}
	public SyntaxElementLoop(int id){
		super(id);
	}
	public SyntaxElementLoop(String name, int id){
		super(name, id);
	}
	public SyntaxElementLoop(int id, String name){
		super(id, name);
	}
	protected void init(){
		super.init();
		type = SyntaxElementType.LOOP;
		isImportant = false;
		element = null;
	}
	
	public SyntaxElementLoop setElement(SyntaxElement element){
		this.element = element;
		if(parser!=null)
			parser.addSyntaxElement(element);
		return this;
	}
	
	@Override
	protected boolean registerToParser(Parser parser){
		if(!super.registerToParser(parser))
			return false;
		parser.addSyntaxElement(element);
		return true;
	}
	
	@Override
	protected void recognize(SyntaxNode node, char[] text, IntRef start, IntRef length){
		node.children = new ArrayList<SyntaxNode>();
		IntRef st = new IntRef(start.value);
		IntRef len = new IntRef(length.value);
		int l;
		int elementsRecognized = 0;
		

		
		
		while(true){

			/*l = findUntilNextOutsideList(text, delimiters, st.value, len.value);
			len.value -= l;
			st.value += l;
			
			l = findUntilNextFromList(text, delimiters, st.value, len.value);
			len.value = l;*/
			
			SyntaxNode child = element.recognize(text, st, len);
			if(!child.isRecognized)
				break;
			st.value = st.value+len.value;
			len.value = length.value - (st.value-start.value);
			node.children.add(child);
			elementsRecognized++;
		}
		//l = findUntilNextOutsideList(text, delimiters, st.value, len.value);
		if(elementsRecognized==0)
			return;
		length.value = st.value-start.value;// + l;
		
		node.isRecognized = true;
	}
	public SyntaxElement element;
}