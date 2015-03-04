package ParserGenerator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;


public class SyntaxNode {
	public SyntaxNode(SyntaxElement element){
		syntaxElement = element;
		children = null;
		parent = null;
		text = "";
		isRecognized = false;
		data = null;
		tags = new ArrayList<Object>();
		ascendingTags = new ArrayList<Object>();
		descendingTags = new ArrayList<Object>();
	}
	
	public void prune(){
		if(children!=null){
			for (ListIterator<SyntaxNode> iterator = children.listIterator(); iterator.hasNext(); ) {
			    SyntaxNode child = iterator.next();
			    child.prune();
			    if (!child.syntaxElement.isImportant) {
			        iterator.remove();
			    	if(child.children!=null)
				    	for(SyntaxNode n : child.children)
				    		iterator.add(n);
			    }
			}
		}
	}
	
	public void setTags(){
	    mergeTags(tags, Arrays.asList(syntaxElement.tags));
	    mergeTags(ascendingTags, Arrays.asList(syntaxElement.ascendingTags));
	    mergeTags(descendingTags, Arrays.asList(syntaxElement.descendingTags));
		if(children!=null){
			for (ListIterator<SyntaxNode> iterator = children.listIterator(); iterator.hasNext(); ) {
			    SyntaxNode child = iterator.next();
			    mergeTags(child.descendingTags, descendingTags);
			    child.setTags();
			    mergeTags(ascendingTags, child.ascendingTags);
			}
		}
		mergeTags(tags, ascendingTags);
		mergeTags(tags, descendingTags);
	}
	
	private void mergeTags(List<Object> dst, List<Object> src){
		for(Object sn : src){
			boolean found = false;
			for(Object dn : dst){
				if(dn==sn){
					found = true;
					break;
				}
			}
			if(!found)
				dst.add(sn);
		}
	}
	
	public SyntaxElement syntaxElement;
	public String text;
	public boolean isRecognized;
	public List<SyntaxNode> children;
	public SyntaxNode parent;
	public Object data;
	public List<Object> tags;
	private List<Object> ascendingTags;
	private List<Object> descendingTags;
	@Override
	public String toString(){
		return toString(0);
	}
	private String toString(int padding){
		String ps = new String(new char[padding*2]).replace('\0', ' ');
		String name = syntaxElement.name.length()==0?null:syntaxElement.name;
		if(isRecognized==false)
			return ps+"unrecognized: "+text+"\n";
		String ts = "";
		boolean f = true;
		for(Object o : this.tags){
			ts = ts + (f?" ":",") + o.toString();
			f = false;
		}
		switch(syntaxElement.getType()){
			case NONE:
				return ps+(name==null?"none":name)+": "+ts+"\n";
			case ANY:
				return ps+(name==null?"any":name)+": "+text+ts+"\n";
			case WORD:
				return ps+(name==null?"word":name)+": "+text+ts+"\n";
			case TOKEN:
				return ps+(name==null?"token":name)+": "+(Token.TokenResult)(data)+ts+"\n";
			case SYNTAX:
			case CHOICE:
			case LOOP:
			{
				String res = ps+(name==null?"syntax":name)+": "+ts+" {\n";
					for(SyntaxNode node : children){
						res = res + node.toString(padding+1); 
					}
				res = res + ps+"}\n";
				return res;
			}
			default:
				return "";
		}
	}
}
