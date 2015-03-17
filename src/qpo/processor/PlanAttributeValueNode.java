package qpo.processor;

import java.util.List;

import qpo.data.model.*;

public class PlanAttributeValueNode extends PlanValueNode {
	public PlanAttributeValueNode(){
		super();
		attributeName = null;
		tableName = "";
		attribute = null;
	}
	
	public PlanAttributeValueNode clone(){
		PlanAttributeValueNode result = new PlanAttributeValueNode();
		cloneValuesTo(result);
		return result;
	}

	protected void cloneValuesTo(PlanAttributeValueNode node){
		super.cloneValuesTo(node);
		node.attributeName = attributeName;
		node.tableName = tableName;
		node.attribute = attribute==null?null:attribute.clone();
	}

	protected String toString(int padding){
		String ps = new String(new char[padding*2]).replace('\0', ' ');
		return ps+(tableName.length()==0?"":tableName+".")+attributeName;
	}
	public boolean bindToTable(PlanTableNode table) throws Exception{
		boolean found = false;
		for(Attribute a : table.getTable().getAttributes()){
			if((tableName.length()==0 || a.getRelationName().length()==0 || a.getRelationName().equalsIgnoreCase(tableName)) && attributeName.equalsIgnoreCase(a.getName())){
				if(found)
					throw new Exception("Attribute ambiguity");
				found = true;
				attribute = a;
				tableName = a.getRelationName();
			}
		}
		return found;
	}
	public AttributeTypeEnum getType(){
		return attribute==null?AttributeTypeEnum.Integer:attribute.getType();
	}
	
	protected void fillUniqueAttributes(List<Attribute> attributeList){
		boolean found = false;
		for(Attribute a : attributeList){
			//if(a.getName().equalsIgnoreCase(attribute.getName()) && a.getName().equalsIgnoreCase(attribute.getName())){
			if((attribute.getRelationName().length()==0||a.getRelationName().length()==0||attribute.getRelationName().equalsIgnoreCase(a.getRelationName())) && attribute.getName().equalsIgnoreCase(a.getName())){
				found = true;
				break;
			}
		}
		if(!found)
			attributeList.add(attribute);
	}

	public String attributeName;
	public String tableName;
	public Attribute attribute;

}
