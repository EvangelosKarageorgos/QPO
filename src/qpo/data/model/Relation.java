package qpo.data.model;

import java.util.ArrayList;
import java.util.List;

public class Relation extends Table{

	
	private String 			name;
	private FileInfo		fileInfo;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public FileInfo getFileInfo() {
		return fileInfo;
	}
	public void setFileInfo(FileInfo fileInfo) {
		this.fileInfo = fileInfo;
	}
	@Override
	public void addAttribute(Attribute attribute){
		super.addAttribute(attribute);
		attribute.setRelationName(name);
	}	
	
	public String toString(int padding){
		String ps = new String(new char[padding*2]).replace('\0', ' ');
		String output = name+" (";
		boolean first = true;
		for(Attribute a : getAttributes()){
			output = output + (first?"":", ")+a.toString(false);
			first = false;
		}
		output = output + ")";
		return output;
	}
}
