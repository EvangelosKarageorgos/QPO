package qpo.processor;

public class PlanNumberLiteralValueNode extends PlanLiteralValueNode {
	public PlanNumberLiteralValueNode(){
		super();
		value = 0;
	}
	
	protected String toString(int padding){
		String ps = new String(new char[padding*2]).replace('\0', ' ');
		return value.toString();
	}
	
	public Number value;
}
