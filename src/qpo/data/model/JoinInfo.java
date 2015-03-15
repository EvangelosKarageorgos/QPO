package qpo.data.model;


public class JoinInfo {

	
	private JoinTypeEnum 	joinType;
	private Integer 		costIO;
	
	
	
	public JoinInfo(JoinTypeEnum joinType, Integer costIO) {
		super();
		this.joinType = joinType;
		this.costIO = costIO;
	}
	
	public JoinTypeEnum getJoinType() {
		return joinType;
	}

	public Integer getCostIO() {
		return costIO;
	}

	
	
}