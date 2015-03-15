import ParserGenerator.*;
import qpo.processor.*;
import qpo.test.QPOtester;


public class QPO {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		QPOtester tester = new QPOtester();
		tester.runQPO();
		QueryParser parser = new QueryParser();
		//System.out.println(parser.recognize("proj[ID, pname, cat_name](join[Product.category_fk=Category.ID](Product)(Category))"));
		
		String query = "proj[Category.ID, pname,cat_name](join[Product.category_fk=Category.ID or Product.ID=ID](sel [ID<5 and (pcode like 'c0%' or not 1=2)](Product))(Category))";
//		query = "sel [ID<5 and (pcode like 'c0%' or not 1=2)](Product)";
		query = "sel [not ID<500 AND pname like 'Shampoo'](Product)";
		
		System.out.println(query);
		SyntaxNode planSyntax = parser.recognize(query);
		System.out.println(planSyntax);
		PlanNode plan = parser.createPlan(planSyntax);
		System.out.println(plan);
		((PlanSelectNode)plan).moveDownwards();
		System.out.println(((PlanTableNode)plan).getParent());
	}

}
