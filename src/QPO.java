import qpo.test.QPOtester;


public class QPO {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		QPOtester tester = new QPOtester();
		tester.runQPO();
		QueryParser parser = new QueryParser();
		System.out.println(parser.recognize("proj[ID, pname, cat_name](join[Product.category_fk=Category.ID](Product)(Category))"));
	}

}
