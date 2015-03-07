package qpo.processor;

import java.util.List;

import qpo.data.model.*;

public class PlanProjectNode extends PlanTableNode {
	public PlanProjectNode(){
		super();
		table = null;
		projectedAttributes = null;
	}
	public PlanTableNode table;
	public List<PlanAttributeNode> projectedAttributes;
}
