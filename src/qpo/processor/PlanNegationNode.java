package qpo.processor;

public class PlanNegationNode extends PlanPredicateNode {
	public PlanNegationNode(){
		super();
		predicate = null;
	}
	
	protected String toString(int padding){
		String ps = new String(new char[padding*2]).replace('\0', ' ');
		return "not "+predicate.toString();
	}
	
	public PlanPredicateNode predicate;
}
