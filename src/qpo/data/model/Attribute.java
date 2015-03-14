package qpo.data.model;

public class Attribute {

	
	private String 				name;
	private AttributeTypeEnum	type;
	private KeyStatusEnum		keyStatus;
	private Integer				size;
	
	//References
	private String 				referencedAttributeName;
	private String 				referencedTableName;
	
	private String				relationName;
	
	private Table table = null;
	
	//Statistics
	private AttributeStatistics			statistics;
	
	public Attribute(){
		name = "";
		relationName = "";
		referencedAttributeName = "";
		referencedTableName = "";
	}
	
	public Attribute clone(){
		Attribute result = new Attribute();
		result.name = name;
		result.type = type;
		result.keyStatus = keyStatus;
		result.size = size;
		result.referencedAttributeName = referencedAttributeName;
		result.referencedTableName = referencedTableName;
		result.relationName = relationName;
		result.table = table;
		result.statistics = statistics.clone();
		return result;
	}
	
	public String getName() {
		return name;
	}
	
	public String getRelationName() {
		return relationName;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	public void setRelationName(String name) {
		this.relationName = name;
	}
	
	public Table getTable(){
		return table;
	}
	
	public void setTable(Table table){
		this.table = table;
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
	
	
	
	public AttributeStatistics getStatistics() {
		return statistics;
	}
	public void setStatistics(AttributeStatistics statistics) {
		this.statistics = statistics;
	}
	public String toString(){
		return toString(true);
	}
	
	public String toString(boolean hasRelationName){
		String output = ((relationName.length()>0&&hasRelationName)?relationName+".":"")+name+ ": ";
		switch(type){
		case Integer:
			output = output + "Integer";
			break;
		case BigInt:
			output = output + "BigInt";
			break;
		case Character:
			output = output + "Character("+size+")";
			break;
		case Date:
			output = output + "Date";
			break;
		case Timestamp:
			output = output + "Timestamp";
			break;
		case Boolean:
			output = output + "Boolean";
			break;
		default:
			break;
		}
		return output;
	}
	
	
	
}
