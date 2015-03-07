package qpo.processor;

public class PlanTableValueNode extends PlanValueNode {
	public PlanTableValueNode(){
		super();
		table = null;
	}
	PlanTableNode table;
}
