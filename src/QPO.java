import qpo.processor.*;
import qpo.processor.PlanProjectNode;
import qpo.processor.PlanSelectNode;
import qpo.processor.PlanTableNode;
import qpo.test.QPOtester;
import ParserGenerator.SyntaxNode;


public class QPO {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		QPOtester tester = new QPOtester();
		tester.runQPO();
		QueryParser parser = new QueryParser();
		//System.out.println(parser.recognize("proj[ID, pname, cat_name](join[Product.category_fk=Category.ID](Product)(Category))"));
		
		String query = "proj[Category.ID, pname,cat_name](join[Product.category_fk=Category.ID or Product.ID=ID](sel [ID<5 and (pcode like 'c0%' or not 1=2)](Product))(Category))";
		query = "sel [ID<2 and (pcode like 'c0%' or not 1=2)](proj[ID, pcode](Product))";
		query = "sel [pcode='yes'](sel[ID<500](Product))";
		query = "proj[ID, pcode](sel[ID=1](proj[ID, pcode](union(proj[ID, pcode, pname, category_fk](Product))(sel[ID<5 and (pname like 'c0%' or not 1=2)](proj[ID, pcode, pname, category_fk](Product))))))";
		query = "proj[Product.ID, pcode](sel[Product.ID>6](join[category_fk=Category.ID and cat_name='yes'](Product)(Category)))";
		//query = "join[category_fk=Category.ID and cat_name='yes'](Product)(Category)";
				
		System.out.println(query);
		SyntaxNode planSyntax = parser.recognize(query);
		System.out.println(planSyntax);
		PlanTableNode plan = parser.createPlan(planSyntax);
		System.out.println(plan);
		System.out.println(plan.getCost());
		//((PlanSelectNode)(plan)).moveDownwards();
		//((((PlanProjectNode)(plan))) ).moveDownwards();
		//plan = ((PlanJoinNode)(((PlanSelectNode) (((PlanProjectNode)(plan)).getChild(0)) ).table)).moveSelectsDown();
		//((PlanJoinNode)plan).extractSelectOperations();
		plan = ((PlanTableNode)plan).moveSelectsDown();
		plan = ((PlanTableNode)plan).moveProjectsDown();
		//plan = ((PlanTableNode)plan).getRootNode();
		((PlanTableNode)plan).getTable();
		System.out.println(plan);
		System.out.println(plan.getCost());
	}

}
