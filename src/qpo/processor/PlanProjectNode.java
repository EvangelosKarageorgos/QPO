package qpo.processor;

import java.util.ArrayList;
import java.util.List;

import qpo.data.info.Catalog;
import qpo.data.info.CostEstimator;
import qpo.data.info.SizeEstimator;
import qpo.data.model.*;

public class PlanProjectNode extends PlanTableNode {
	public PlanProjectNode(){
		super();
		table = null;
		projectedAttributes = null;
	}

	public PlanProjectNode clone(){
		PlanProjectNode result = new PlanProjectNode();
		cloneValuesTo(result);
		return result;
	}

	protected void cloneValuesTo(PlanProjectNode node){
		super.cloneValuesTo(node);
		node.table = table==null?null:table.clone();
		if(node.table!=null)node.table.setParent(node);
		node.projectedAttributes = null;
		if(projectedAttributes!=null){
			node.projectedAttributes = new ArrayList<PlanAttributeNode>();
			for(PlanAttributeNode a : projectedAttributes)
				node.projectedAttributes.add(a.clone());
		}
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
		
		t.getStatistics().setCardinality(mainTable.getStatistics().getCardinality());
		t.getStatistics().setTupleSize(SizeEstimator.getTupleSize(t.getAttributes()));
		t.getStatistics().setTuplesPerBlock(Catalog.getSystemProperties().getPageSize() / t.getStatistics().getTupleSize());
	
		// TODO project logic construct table

		return t;
	}

	protected String toString(int padding){
		String ps = new String(new char[padding*2]).replace('\0', ' ');
		
		String output = "";
		try{
			output = ps+"Projection "+getTable().toString(padding)+" "+getCost()+" cost";
		}catch(Exception ex){}
		
		output = output + "{\n";
		output = output + table.toString(padding+1);
		output = output + "\n"+ps+"}";
		
		return output;
	}
	
	public int getNumOfChildren(){
		return 1;
	}
	public PlanTableNode getChild(int index){
		if(index==0)
			return table;
		else return null;
	}
	
	public void setChild(int index, PlanTableNode node){
		if(index==0)
			table = node;
	}
	
	public PlanTableNode moveProjectsDown(PlanTableNode root) throws Exception{
		moveDownwards();
		root = getRootNode();
		root = super.moveProjectsDown(root);
		return root;
	}

	public boolean moveDownwards() throws Exception{
		if(table.getNumOfChildren()==1){
			boolean canMove = true;
			if(table instanceof PlanSelectNode){
				if(table.getChild(0) instanceof PlanRelationNode)
					return false;
				List<Attribute> attrList = ((PlanSelectNode)table).predicate.getUniqueAttributes();
				List<PlanAttributeNode> newAttrList = new ArrayList<PlanAttributeNode>();
				for(Attribute a : attrList){
					boolean found = false;
					for(PlanAttributeNode pan : projectedAttributes){
						if((pan.tableName.length()==0||a.getRelationName().length()==0||pan.tableName.equalsIgnoreCase(a.getRelationName())) && pan.attributeName.equalsIgnoreCase(a.getName())){
							found = true;
							break;
						}
					}
					if(!found){
						PlanAttributeNode pan = new PlanAttributeNode();
						pan.tableName = a.getRelationName();
						pan.attributeName = a.getName();
						newAttrList.add(pan);
						canMove = false;
						//break;
					}
				}
				if(canMove){
					PlanSelectNode sel = (PlanSelectNode)table;
					moveDownwardsOperation(0, 0);
					sel.predicate.distributeAttributeReferences(sel.table.getTable(), null);
					return moveDownwards();
				} else {
					PlanProjectNode newProject = new PlanProjectNode();
					newProject.table = table;
					newProject.projectedAttributes = new ArrayList<PlanAttributeNode>();
					for(PlanAttributeNode a : projectedAttributes)
						newProject.projectedAttributes.add(a);
					for(PlanAttributeNode a : newAttrList)
						newProject.projectedAttributes.add(a);
					table.setParent(newProject);
					newProject.table = table;
					table = newProject;
					newProject.setParent(this);
					newProject.moveDownwardsOperation(0, 0);
					//newProject.invalidate();
					newProject.moveDownwards();
					return false;
				}
			} else if(table instanceof PlanProjectNode){
				List<PlanAttributeNode> attrList = ((PlanProjectNode)table).projectedAttributes;
				List<PlanAttributeNode> finalAttrList = new ArrayList<PlanAttributeNode>();
				//for(PlanAttributeNode a : projectedAttributes)
				//	finalAttrList.add(a);
				for(PlanAttributeNode a : attrList){
					boolean found = false;
					for(PlanAttributeNode pan : projectedAttributes){
						if((pan.tableName.length()==0||a.tableName.length()==0||pan.tableName.equalsIgnoreCase(a.tableName)) && pan.attributeName.equalsIgnoreCase(a.attributeName)){
							found = true;
							break;
						}
					}
					if(found){
						finalAttrList.add(a);
					}
				}
				for(PlanAttributeNode a : projectedAttributes){
					boolean f = false;
					for(PlanAttributeNode pan : finalAttrList){
						if((pan.tableName.length()==0||a.tableName.length()==0||pan.tableName.equalsIgnoreCase(a.tableName)) && pan.attributeName.equalsIgnoreCase(a.attributeName)){
							f = true;
							break;
						}
					}
					if(!f && isAttributeNecessary(a))
						finalAttrList.add(a.clone());
				}
				for(PlanAttributeNode a : attrList){
					boolean f = false;
					for(PlanAttributeNode pan : finalAttrList){
						if((pan.tableName.length()==0||a.tableName.length()==0||pan.tableName.equalsIgnoreCase(a.tableName)) && pan.attributeName.equalsIgnoreCase(a.attributeName)){
							f = true;
							break;
						}
					}
					if(!f && isAttributeNecessary(a))
						finalAttrList.add(a.clone());
				}

				projectedAttributes = finalAttrList;
				table.getChild(0).setParent(this);
				table = table.getChild(0);
				invalidate();
				return moveDownwards();
			}
			if(canMove){
				moveDownwardsOperation(0, 0);
				return moveDownwards();
			} else
				return false;
		}
		if(table instanceof PlanJoinNode){
			List<Attribute> attrList = ((PlanJoinNode)table).predicate.getUniqueAttributes();
			List<Attribute> leftAttrList = new ArrayList<Attribute>();
			List<Attribute> rightAttrList = new ArrayList<Attribute>();
			for(Attribute a : attrList){
				if(a.getTable().getUid().equals(((PlanJoinNode)table).left.getTable().getUid()))
					leftAttrList.add(a.clone());
				if(a.getTable().getUid().equals(((PlanJoinNode)table).right.getTable().getUid()))
					rightAttrList.add(a.clone());
			}
			boolean canMove = false;
			boolean leftCanMove = true;
			boolean rightCanMove = true;
			List<PlanAttributeNode> newleftAttrList = new ArrayList<PlanAttributeNode>();
			List<PlanAttributeNode> newrightAttrList = new ArrayList<PlanAttributeNode>();
			for(Attribute a : leftAttrList){
				boolean found = false;
				for(PlanAttributeNode pan : projectedAttributes){
					if((pan.tableName.length()==0||a.getRelationName().length()==0||pan.tableName.equalsIgnoreCase(a.getRelationName())) && pan.attributeName.equalsIgnoreCase(a.getName())){
						found = true;
						break;
					}
				}
				if(!found){
					PlanAttributeNode pan = new PlanAttributeNode();
					pan.tableName = a.getRelationName();
					pan.attributeName = a.getName();
					newleftAttrList.add(pan);
					leftCanMove = false;
					canMove = false;
					//break;
				}
			}
			for(Attribute a : rightAttrList){
				boolean found = false;
				for(PlanAttributeNode pan : projectedAttributes){
					if((pan.tableName.length()==0||a.getRelationName().length()==0||pan.tableName.equalsIgnoreCase(a.getRelationName())) && pan.attributeName.equalsIgnoreCase(a.getName())){
						found = true;
						break;
					}
				}
				if(!found){
					PlanAttributeNode pan = new PlanAttributeNode();
					pan.tableName = a.getRelationName();
					pan.attributeName = a.getName();
					newrightAttrList.add(pan);
					rightCanMove = false;
					canMove = false;
					//break;
				}
			}
			boolean didMovement = false;
			//if(!leftCanMove && !(((PlanJoinNode)table).left instanceof PlanRelationNode)){
			if(!leftCanMove){
				PlanProjectNode newProject = new PlanProjectNode();
				newProject.table = table;
				newProject.projectedAttributes = new ArrayList<PlanAttributeNode>();
				for(PlanAttributeNode a : projectedAttributes)
					newProject.projectedAttributes.add(a);
				for(PlanAttributeNode a : newleftAttrList)
					newProject.projectedAttributes.add(a);
				if(!(((PlanJoinNode)table).left instanceof PlanRelationNode) || (((double)((((PlanJoinNode)table).left).getTable().getAttributes().size()) / (double)(newProject.projectedAttributes.size())) > 2)){
					didMovement = true;
					table.setParent(newProject);
					newProject.table = table;
					table = newProject;
					newProject.setParent(this);
					newProject.moveDownwardsOperation(0, 0);
					//newProject.invalidate();
					newProject.moveDownwards();
				}
			}
			//if(!rightCanMove && !(((PlanJoinNode)table).right instanceof PlanRelationNode)){
			if(!rightCanMove){
	
				PlanProjectNode newProject = new PlanProjectNode();
				newProject.table = table;
				newProject.projectedAttributes = new ArrayList<PlanAttributeNode>();
				for(PlanAttributeNode a : projectedAttributes)
					newProject.projectedAttributes.add(a);
				for(PlanAttributeNode a : newrightAttrList)
					newProject.projectedAttributes.add(a);
				if(!(((PlanJoinNode)table).right instanceof PlanRelationNode) || (((double)((((PlanJoinNode)table).right).getTable().getAttributes().size()) / (double)(newProject.projectedAttributes.size())) > 2)){
					didMovement = true;
					table.setParent(newProject);
					newProject.table = table;
					table = newProject;
					newProject.setParent(this);
					newProject.moveDownwardsOperation(0, 1);
					//newProject.invalidate();
					newProject.moveDownwards();
				}
			}
			if(didMovement)
				((PlanJoinNode)table).extractSelectOperations();
		}
		if(table instanceof PlanUnionNode){
			PlanProjectNode newProject = new PlanProjectNode();
			newProject.table = table;
			newProject.projectedAttributes = new ArrayList<PlanAttributeNode>();
			for(PlanAttributeNode a : projectedAttributes)
				newProject.projectedAttributes.add(a.clone());
			table.setParent(newProject);
			table = newProject;
			newProject.setParent(this);
			newProject.moveDownwardsOperation(0, 1);
			newProject.invalidate();
			newProject.moveDownwards();
			moveDownwardsOperation(0, 0);
			invalidate();
			return moveDownwards();
		}
		if(table instanceof PlanDiffNode){
			moveDownwardsOperation(0, 0);
			invalidate();
			return moveDownwards();
		}
		if(table.getNumOfChildren()!=1)
			return false;
		return false;
	}

	public PlanTableNode table;
	public List<PlanAttributeNode> projectedAttributes;
	
	
	
	@Override
	public Integer getCost() throws Exception{
		return getChild(0).getCost() + getMyCost();
	}
	
	
	private Integer getMyCost() throws Exception {
		return !(getChild(0) instanceof PlanRelationNode) ? 0 
					: CostEstimator.getCostOfLinear( getChild(0).getTable().getStatistics().getBlocksOnDisk() ); 
	}
	
	
}
