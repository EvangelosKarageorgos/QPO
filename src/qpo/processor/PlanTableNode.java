package qpo.processor;

import qpo.data.model.*;

public class PlanTableNode extends PlanNode {
	public PlanTableNode(){
		m_table = null;
	}
	
	public PlanTableNode clone(){
		PlanTableNode result = new PlanTableNode();
		cloneValuesTo(result);
		return result;
	}

	protected void cloneValuesTo(PlanTableNode node){
		super.cloneValuesTo(node);
		node.m_table = m_table==null?null:m_table.clone();
	}

	public Table constructTable() throws Exception{
		return new Table();
	}
	
	public Table getTable() throws Exception{
		if(m_table==null)
			m_table = constructTable();
		return m_table;
	}
	
	protected String toString(int padding){
		String ps = new String(new char[padding*2]).replace('\0', ' ');

		String output = "";

		try{
			output= ps+getTable().toString(padding);
		}
		catch(Exception ex){}
		
		return output;
	}
	
	
	private Table m_table;
}
