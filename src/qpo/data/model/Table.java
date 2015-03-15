package qpo.data.model;

import java.util.ArrayList;
import java.util.List;

public class Table {

	
	private TableStatistics		statistics;
	
	private List<Attribute> attributes;
	private List<Index>		indexes;
	
	
	private String uid=null;
	public String getUid(){
		if(uid==null)
			uid=java.util.UUID.randomUUID().toString();
		return uid;
	}
	public Table clone(){
		Table result = new Table();
		result.statistics = statistics.clone();
		result.attributes = new ArrayList<Attribute>();
		for(Attribute a : attributes){
			result.addAttribute(a.clone());
		}
		return result;
	}
	
	public List<Attribute> getAttributes() {
		
		if(attributes==null)
			attributes = new ArrayList<Attribute>();
		
		return attributes;
	}
	public void setAttributes(List<Attribute> attributes) {
		this.attributes = attributes;
	}
	
	public void addAttribute(Attribute attribute){
		getAttributes().add(attribute);
		attribute.setTable(this);
	}
	
	public Attribute getAttribute(String attributeName){
		
		for(Attribute attr : getAttributes())
			if(attr.getName().equalsIgnoreCase(attributeName))
				return attr;
		
		return null;
	}
	
	
	public List<Index> getIndexes() {
		
		if(indexes==null)
			indexes = new ArrayList<Index>();
			
		return indexes;
	}
	public void setIndexes(List<Index> indexes) {
		this.indexes = indexes;
	}
	
	
	public void addIndex(Index index){
		getIndexes().add(index);
	}
	
	public Index getIndex(String attributeName){
		for(Index idx : getIndexes())
			if(idx.getAttributeName().equalsIgnoreCase(attributeName))
				return idx;
		
		return null;
	}
	
	
	public TableStatistics getStatistics() {
		if(statistics==null)
			statistics = new TableStatistics();
		return statistics;
	}
	public void setStatistics(TableStatistics statistics) {
		this.statistics = statistics;
	}
	
	public String toString(){
		return toString(0);
	}

	public String toString(int padding){
		String ps = new String(new char[padding*2]).replace('\0', ' ');
		String output = "(";
		boolean first = true;
		for(Attribute a : getAttributes()){
			output = output + (first?"":", ")+a.toString(true);
			first = false;
		}
		output = output + ") "+getStatistics().getCardinality()+" records";
		return output;
	}

	
}
