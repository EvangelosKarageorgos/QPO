package qpo.data.model;

public enum AttributeTypeEnum {

	Integer, BigInt, Character, Date, Timestamp, Boolean; 
	public static int getTypeClass(AttributeTypeEnum e){
		switch(e){
		case Integer:
		case BigInt:
		case Boolean:
			return 0;
		case Character:
		case Date:
		case Timestamp:
			return 1;
		default:
			return 2;
		}
	}
}
