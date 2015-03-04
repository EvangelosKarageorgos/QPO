package qpo.data.model;

public class Block {

	
	private Integer blockNo;
	private Integer usage;
	
	
	public Block(Integer blckNo, Integer usg) {
		this.blockNo = blckNo;
		this.usage = usg;
	}
	
	
	public Integer getBlockNo() {
		return blockNo;
	}

	public Integer getUsage() {
		return usage;
	}
	
	
}
