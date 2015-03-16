package qpo.processor;

import ParserGenerator.SyntaxNode;

public class QueryProcessor {
	
	public SyntaxNode getQuerySyntax(String query) throws Exception{
		SyntaxNode node = getParser().recognize(query);
		if(!node.isRecognized)
			throw new Exception("SyntaxError");
		return node;
	}
	
	public PlanTableNode getInitialPlan(String query) throws Exception{
		return getInitialPlan(getQuerySyntax(query));
	}

	public PlanTableNode getInitialPlan(SyntaxNode rootNode) throws Exception{
		PlanTableNode plan = getParser().createPlan(rootNode);
		plan.getTable();
		return plan;
	}

	public PlanTableNode getOptimizedPlan(String query) throws Exception{
		return getOptimizedPlan(getQuerySyntax(query));
	}
	public PlanTableNode getOptimizedPlan(SyntaxNode rootNode) throws Exception{
		return getInitialPlan(rootNode);
	}
	
	public PlanTableNode getOptimizedPlan(PlanTableNode initialPlan) throws Exception{
		PlanTableNode plan = initialPlan;
		plan = plan.moveSelectsDown();
		plan = plan.moveProjectsDown();
		plan.optimizeJoinsOrientation();
		plan.getTable();
		return plan;
	}
	
	
	public QueryParser getParser(){
		if(m_parser==null)
			m_parser = new QueryParser();
		return m_parser;
	}
	private QueryParser m_parser;
}
