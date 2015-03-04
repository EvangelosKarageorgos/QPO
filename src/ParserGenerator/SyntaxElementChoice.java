package ParserGenerator;
import java.util.ArrayList;
import java.util.List;

public class SyntaxElementChoice extends SyntaxElement {
	public SyntaxElementChoice(){
		super();
	}
	public SyntaxElementChoice(String name){
		super(name);
	}
	public SyntaxElementChoice(int id){
		super(id);
	}
	public SyntaxElementChoice(String name, int id){
		super(name, id);
	}
	public SyntaxElementChoice(int id, String name){
		super(id, name);
	}
	protected void init(){
		super.init();
		isImportant = false;
		type = SyntaxElementType.CHOICE;
		elements = new ArrayList<SyntaxElement>();
	}
	
	public SyntaxElementChoice addElement(SyntaxElement element){
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
		int l;
		for(SyntaxElement element : elements){
			IntRef st = new IntRef(start.value);
			IntRef len = new IntRef(length.value);
			
			SyntaxNode child = element.recognize(text, st, len);
			if(child.isRecognized){
				node.children.add(child);
				node.isRecognized = true;
				start.value = st.value;
				length.value = len.value;
				return;
			}
		}
	}
	public List<SyntaxElement> elements;
}