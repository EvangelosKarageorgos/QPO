package qpo.data.model;


public class FileInfo {

	
	private String filename;
	private Integer size;
	private Integer blocksNo;
	
//	private List<Block> listOfDiskBlocks;

	
	
	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	
	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public Integer getBlocksNo() {
		return blocksNo;
	}

	public void setBlocksNo(Integer blocksNo) {
		this.blocksNo = blocksNo;
	}
	
	
//	public void calculateSize() {
//		size = 0;
//		for(Block bl : getListOfDiskBlocks())
//			size = size + bl.getUsage();
//	}

	
//	public List<Block> getListOfDiskBlocks() {
//		
//		if(listOfDiskBlocks==null)
//			listOfDiskBlocks = new ArrayList<Block>();
//		
//		return listOfDiskBlocks;
//	}
//
//	public void addBlock(Block block) {
//		getListOfDiskBlocks().add(block);
//	}
//	
//	public void addBlock(Integer blockNo, Integer blockUsage) {
//		addBlock( new Block(blockNo, blockUsage) );
//	}
	
	
	
	
	
	
	
}
