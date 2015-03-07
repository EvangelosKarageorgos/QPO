package qpo.data.model;

public class Index {

	
	private String 			name;
	private IndexTypeEnum 	type;
	private String 			attributeName;
	
	private IndexStatistics		statistics;
	
	
	
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
