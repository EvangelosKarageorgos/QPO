package qpo.processor;

public class PlanStringLiteralValueNode extends PlanLiteralValueNode {
	public PlanStringLiteralValueNode(){
		super();
		value = null;
	}
	
	protected String toString(int padding){
		String ps = new String(new char[padding*2]).replace('\0', ' ');
		return "'"+value+"'";
	}
	
	public String value;
}
