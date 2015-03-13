package qpo.processor;

import java.util.List;

import qpo.data.model.*;

public class PlanProjectNode extends PlanTableNode {
	public PlanProjectNode(){
		super();
		table = null;
		projectedAttributes = null;
	}
	
	@Override
	public Table constructTable() throws Exception{
		Table t = new Table();

		Table mainTable = table.getTable();
		for(PlanAttributeNode pan : projectedAttributes){
			Boolean found = false;
			for(Attribute a : mainTable.getAttributes()){
				if(a.getName().equalsIgnoreCase(pan.attributeName) && (pan.tableName.length()>0 ? a.getRelationName().equalsIgnoreCase(pan.tableName) : true)){
					if(found)
						throw new Exception("Attribute ambiguity");
					found = true;
					t.addAttribute(a.clone());
				}
			}
		}
	
		// TODO project logic construct table

		return t;
	}

	
	public PlanTableNode table;
	public List<PlanAttributeNode> projectedAttributes;
}
