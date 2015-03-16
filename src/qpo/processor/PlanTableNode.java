package qpo.processor;

import java.util.List;

import qpo.data.model.*;

public class PlanTableNode extends PlanNode {
	public PlanTableNode(){
		m_table = null;
		m_parent = null;
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
	
	public PlanTableNode getParent(){
		return m_parent;
	}
	
	public PlanTableNode getRootNode(){
		PlanTableNode result = this;
		while(result.getParent()!=null)
			result = result.getParent();
		return result;
	}
	
	public void setParent(PlanTableNode node){
		m_parent = node;
	}
	
	public Table getTable() throws Exception{
		if(m_table==null || isValid==false){
			m_table = constructTable();
			if(getParent()!=null)
				getParent().invalidate();
			isValid = true;
		}
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

	public int getNumOfChildren(){
		return 0;
	}
	
	public PlanTableNode getChild(int index){
		return null;
	}
	
	public void setChild(int index, PlanTableNode node){
		
	}
	
	public PlanTableNode moveSelectsDown() throws Exception{
		PlanTableNode plan = moveSelectsDown(this);
		plan.getTable();
		return plan;
	}
	public PlanTableNode moveProjectsDown() throws Exception{
		PlanTableNode plan = moveProjectsDown(this);
		plan.getTable();
		return plan;
	}
	
	public PlanTableNode optimizeJoinsOrientation() throws Exception{
		if(getNumOfChildren()==1){
			getChild(0).optimizeJoinsOrientation();
		} else if(getNumOfChildren()==2){
			getChild(0).optimizeJoinsOrientation();
			getChild(1).optimizeJoinsOrientation();
		}
		return this;
	}
	
	public PlanTableNode moveSelectsDown(PlanTableNode root) throws Exception{
		if(getNumOfChildren()==1){
			root = getChild(0).moveSelectsDown(root);
		} else if(getNumOfChildren()==2){
			root = getChild(0).moveSelectsDown(root);
			root = getChild(1).moveSelectsDown(root);
		}
		return root;
	}

	public PlanTableNode moveProjectsDown(PlanTableNode root) throws Exception{
		if(getNumOfChildren()==1){
			root = getChild(0).moveProjectsDown(root);
		} else if(getNumOfChildren()==2){
			root = getChild(0).moveProjectsDown(root);
			root = getChild(1).moveProjectsDown(root);
		}
		return root;
	}
	
	public void invalidate(){
		isValid = false;
		if(getParent()!=null)
			getParent().invalidate();
	}
	protected void moveDownwardsOperation(int index, int childIndex){
		PlanTableNode child = getChild(index);
		if(child!=null){
			PlanTableNode parent = getParent();
			if(parent!=null){
				if(parent.getChild(0)==this){
					parent.setChild(0, child);
				} else if(parent.getChild(1)==this){
					parent.setChild(1, child);
				}
				//parent.invalidate();
			}
			child.setParent(parent);
			//child.invalidate();
			setParent(child);
			setChild(index, child.getChild(childIndex));
			child.setChild(childIndex, this);
			invalidate();
		}
	}
	protected boolean isAttributeNecessary(PlanAttributeNode pan){
		boolean result = false;
		if(getParent()!=null)
			result = result || getParent().isAttributeNecessary(pan);
		if(!result){
			if(this instanceof PlanSelectNode){
				List<Attribute> attrs = ((PlanSelectNode)this).predicate.getUniqueAttributes();
				for(Attribute a : attrs){
					if((pan.tableName.length()==0||pan.tableName.equalsIgnoreCase(a.getRelationName())) && pan.attributeName.equalsIgnoreCase(a.getName())){
						return true;
					}
				}
			} else
			if(this instanceof PlanJoinNode){
				List<Attribute> attrs = ((PlanJoinNode)this).predicate.getUniqueAttributes();
				for(Attribute a : attrs){
					if((pan.tableName.length()==0||pan.tableName.equalsIgnoreCase(a.getRelationName())) && pan.attributeName.equalsIgnoreCase(a.getName())){
						return true;
					}
				}
			}
		}
		return result;
			
	}
	private boolean isValid;
	private PlanTableNode m_parent;
	private Table m_table;
	private int m_cost=0;
	
	

	public Integer getCost() throws Exception{
		return null;
	}
	
	
	
}
