package qpo.processor;

import qpo.data.model.*;

public class PlanAttributeValueNode extends PlanValueNode {
	public PlanAttributeValueNode(){
		super();
		attributeName = null;
		tableName = "";
		attribute = null;
	}
	
	protected String toString(int padding){
		String ps = new String(new char[padding*2]).replace('\0', ' ');
		return ps+(tableName.length()==0?"":tableName+".")+attributeName;
	}
	
	public String attributeName;
	public String tableName;
	public Attribute attribute;

}
