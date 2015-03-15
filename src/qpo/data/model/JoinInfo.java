package qpo.data.model;


public class JoinInfo {

	
	private JoinTypeEnum 	joinType;
	private Integer 		costTime;
	
	
	
	public JoinInfo(JoinTypeEnum joinType, Integer cost) {
		super();
		this.joinType = joinType;
		this.costTime = cost;
	}
	
	public JoinTypeEnum getJoinType() {
		return joinType;
	}

	public Integer getCostTime() {
		return costTime;
	}

	
	
}