package ParserGenerator;
import java.util.ArrayList;

public class SyntaxElementToken extends SyntaxElement {
	public SyntaxElementToken(){
		super();
	}
	public SyntaxElementToken(String name){
		super(name);
	}
	public SyntaxElementToken(int id){
		super(id);
	}
	public SyntaxElementToken(String name, int id){
		super(name, id);
	}
	public SyntaxElementToken(int id, String name){
		super(id, name);
	}
	protected void init(){
		super.init();
		type = SyntaxElementType.TOKEN;
	}

	public SyntaxElementToken setToken(Token token){
		this.token = token;
		if(parser!=null)
			parser.addToken(token);
		return this;
	}
	
	@Override
	protected boolean registerToParser(Parser parser){
		if(!super.registerToParser(parser))
			return false;
		if(token!=null)
			parser.addToken(token);
		return true;
	}
		
	@Override
	protected void recognize(SyntaxNode node, char[] text, IntRef start, IntRef length){
		Token.TokenResult res = token.recognize(node.text);
		node.isRecognized = res.isRecognized;
		node.data = res;
	}
	public Token token;
}