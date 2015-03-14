package qpo.processor;

public class PlanTableValueNode extends PlanValueNode {
	public PlanTableValueNode(){
		super();
		table = null;
	}
	
	public PlanTableValueNode clone(){
		PlanTableValueNode result = new PlanTableValueNode();
		cloneValuesTo(result);
		return result;
	}

	protected void cloneValuesTo(PlanTableValueNode node){
		super.cloneValuesTo(node);
		node.table = table.clone();
	}

	PlanTableNode table;
}
