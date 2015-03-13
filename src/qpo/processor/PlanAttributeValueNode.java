package qpo.processor;

import qpo.data.model.*;

public class PlanAttributeValueNode extends PlanValueNode {
	public PlanAttributeValueNode(){
		attributeName = null;
		tableName = "";
	}
	public String attributeName;
	public String tableName;

}
