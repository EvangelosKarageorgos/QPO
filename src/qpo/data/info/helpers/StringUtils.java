package qpo.data.info.helpers;


public class StringUtils {
	
	
	// XML Tags
	public static final String EMPTY_STRING 		= "";
	
	
	
	public static Boolean notEmptyString(String str) {
		return (str==null || EMPTY_STRING.equalsIgnoreCase(str.trim())) ? Boolean.FALSE : Boolean.TRUE;
	}
	
	

}
