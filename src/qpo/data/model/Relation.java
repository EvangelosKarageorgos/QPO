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
	
}
