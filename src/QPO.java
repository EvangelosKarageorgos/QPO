import java.util.Scanner;

import qpo.processor.*;
import qpo.test.QPOtester;
import ParserGenerator.SyntaxNode;


public class QPO {
	public static QueryProcessor processor = new QueryProcessor();
	public static void main(String[] args) throws Exception {
		QPOtester tester = new QPOtester();
		tester.runQPO();
		
		QueryParser parser = new QueryParser();
		
		String query;
		
		// moni periptwsi poy pleon kanei bug sto optimization
		//query = "proj[Category.ID, pname,cat_name](join[Product.category_fk=Category.ID or Product.ID=ID](sel [ID<5 and (pcode like 'c0%' or not 1=2)](Product))(Category))";
		
		// poly aplo poy deixnei ti diafora toy select me index
		//query = "sel [ID<2 and (pcode like 'c0%' or not 1=2)](proj[ID, pcode](Product))";
		
		// paradeigma me union
		//query = "proj[ID, pcode](sel[ID=1](proj[ID, pcode](union(proj[ID, pcode, pname, category_fk](Product))(sel[ID<5 and (pname like 'c0%' or not 1=2)](proj[ID, pcode, pname, category_fk](Product))))))";
		
		// kapoio sfalme me estimation
		//query = "proj[Product.ID, pcode](sel[Product.ID<50](join[category_fk=Category.ID and Category.id<20](Product)(Category)))";
		
		// paradeigma poy i seria twn joins kanei diafora
		//query = "join[category_fk=Category.ID](Category)(Product)";
		
		// paradeigma megalis poliplokotitas
		//query = "proj[cname, pname, cat_name](sel[Customer.ID=5](join[category_fk=Category.ID](join[Product.ID=product_fk](join[OrderItem.customer_fk=Customer.ID](join[Nationality.ID=nationality_fk](Customer)(Nationality))(OrderItem))(Product))(Category)))";

		//allo ena xaraktiristiko paradeigma
		//query = "sel[Product.Id=10](join[category_fk=Category.Id](Category)(Product))";

		
		// gamw ta paradeigmata!!!
		query = "proj[OrderItem.id, Product.pname, cat_name](sel[OrderItem.ID=10](join[product_fk=Product.Id](join[category_fk=Category.Id](Category)(Product))(OrderItem)))";
		
		
		
		presentQuery(query);
		
		//commandLoop();
	}
	
	public static void commandLoop()
	{
	    Scanner scan = new Scanner(System.in);
	    String query = scan.nextLine();
	 
	    while(query.trim().length() >0){
	    	presentQuery(query);
	      query = scan.nextLine();
	    }
	}

	public static void presentQuery(String query){
    	try{
	    	SyntaxNode snode = processor.getQuerySyntax(query);
	    	System.out.println("------Query syntax-----");
	    	System.out.println(snode);

	    	PlanTableNode plan = processor.getInitialPlan(snode);
	    	System.out.println("------Initial plan "+plan.getCost()+" msec ("+plan.getTable().getStatistics().getCardinality()+" records)-----");
	    	System.out.println(plan);
	    	
	    	plan = processor.getOptimizedPlan(plan);
	    	System.out.println("-----Optimized plan "+plan.getCost()+" msec ("+plan.getTable().getStatistics().getCardinality()+" records)-----");
	    	System.out.println(plan);
    		
    	} catch(Exception ex){
    		System.out.println("Error :"+ex.getMessage());
    		ex.printStackTrace();
    	}
		
	}

}
