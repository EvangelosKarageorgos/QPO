package qpo.processor;

public class PlanNode {
	public PlanNode(){
		
	}
	public String toString(){
		return toString(0);
	}
	protected String toString(int padding){
		String ps = new String(new char[padding*2]).replace('\0', ' ');
		return ps;
	}
	public PlanNode clone(){
		PlanNode result = new PlanNode();
		cloneValuesTo(result);
		return result;
	}
	protected void cloneValuesTo(PlanNode node){
		
	}

}
