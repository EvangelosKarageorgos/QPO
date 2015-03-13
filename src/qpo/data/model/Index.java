package qpo.data.model;

import java.util.ArrayList;

public class Index {

	
	private String 			name;
	private IndexTypeEnum 	type;
	private String 			attributeName;
	
	private IndexStatistics		statistics;
	
	public Index clone(){
		Index result = new Index();
		result.statistics = statistics.clone();
		result.name = name;
		result.type = type;
		result.attributeName = attributeName;
		return result;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public IndexTypeEnum getType() {
		return type;
	}
	public void setType(IndexTypeEnum type) {
		this.type = type;
	}
	
	public String getAttributeName() {
		return attributeName;
	}
	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}
	
	
	public IndexStatistics getStatistics() {
		return statistics;
	}
	public void setStatistics(IndexStatistics statistics) {
		this.statistics = statistics;
	}
	
	
}
