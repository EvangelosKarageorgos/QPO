package qpo.processor;

public class PlanLiteralValueNode extends PlanValueNode {
	public PlanLiteralValueNode(){
		super();
	}
	
	public PlanLiteralValueNode clone(){
		PlanLiteralValueNode result = new PlanLiteralValueNode();
		cloneValuesTo(result);
		return result;
	}

	protected void cloneValuesTo(PlanLiteralValueNode node){
		super.cloneValuesTo(node);
	}
	
	public Object getValue(){
		return null;
	}
}
