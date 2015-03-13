package qpo.processor;

import qpo.data.model.*;

public class PlanTableNode extends PlanNode {
	public PlanTableNode(){
		m_table = null;
		
	}
	public Table constructTable() throws Exception{
		return new Table();
	}
	
	public Table getTable() throws Exception{
		if(m_table==null)
			m_table = constructTable();
		return m_table;
	}
	
	private Table m_table;
}
