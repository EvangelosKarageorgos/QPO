package qpo.processor;

import qpo.data.model.*;

public class PlanAttributeNode extends PlanNode{
	public PlanAttributeNode(){
		super();
		attributeName = null;
		tableName = "";
	}
	public String attributeName;
	public String tableName;
}
