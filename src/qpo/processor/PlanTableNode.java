package qpo.processor;

import qpo.data.model.*;

public class PlanTableNode extends PlanNode {
	public PlanTableNode(){
		m_table = null;
		
	}
	public Table constructTable(){
		return new Table();
	}
	
	public Table getTable(){
		if(m_table==null)
			m_table = constructTable();
		return m_table;
	}
	
	private Table m_table;
}
