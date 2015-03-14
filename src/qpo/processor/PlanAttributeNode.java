package qpo.processor;

import qpo.data.model.*;

public class PlanAttributeNode extends PlanNode{
	public PlanAttributeNode(){
		super();
		attributeName = null;
		tableName = "";
	}
	
	protected String toString(int padding){
		String ps = new String(new char[padding*2]).replace('\0', ' ');
		return ps+(tableName.length()==0?"":tableName+".")+attributeName;
	}
	
	public String attributeName;
	public String tableName;
}
