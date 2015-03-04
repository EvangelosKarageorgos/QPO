package qpo.data.model;

public class Attribute {

	
	private String 				name;
	private AttributeTypeEnum	type;
	private KeyStatusEnum		keyStatus;
	private Integer				size;
	
	//References
	private String 				referencedAttributeName;
	private String 				referencedTableName;
	
	//Statistics
	private Statistics			statistics;
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public AttributeTypeEnum getType() {
		return type;
	}
	public void setType(AttributeTypeEnum type) {
		this.type = type;
	}
	
	public KeyStatusEnum getKeyStatus() {
		return keyStatus;
	}
	public void setKeyStatus(KeyStatusEnum keyStatus) {
		this.keyStatus = keyStatus;
	}
	
	public Integer getSize() {
		return size;
	}
	public void setSize(Integer size) {
		this.size = size;
	}
	
	
	public String getReferencedAttributeName() {
		return referencedAttributeName;
	}
	public void setReferencedAttributeName(String referencedAttributeName) {
		this.referencedAttributeName = referencedAttributeName;
	}
	
	public String getReferencedTableName() {
		return referencedTableName;
	}
	public void setReferencedTableName(String referencedTableName) {
		this.referencedTableName = referencedTableName;
	}
	
	
	
	public Statistics getStatistics() {
		return statistics;
	}
	public void setStatistics(Statistics statistics) {
		this.statistics = statistics;
	}
	

	
	
	
}
