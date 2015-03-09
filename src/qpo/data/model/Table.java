package qpo.data.model;

import java.util.ArrayList;
import java.util.List;

public class Table {

	
	private TableStatistics		statistics;
	
	private List<Attribute> attributes;
	private List<Index>		indexes;
	
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
		return statistics;
	}
	public void setStatistics(TableStatistics statistics) {
		this.statistics = statistics;
	}
	

	
}
