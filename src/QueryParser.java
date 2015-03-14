import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import qpo.data.info.*;
import qpo.data.model.*;
import qpo.processor.*;
import ParserGenerator.NumberTypes;
import ParserGenerator.Parser;
import ParserGenerator.SyntaxElement;
import ParserGenerator.SyntaxElementAny;
import ParserGenerator.SyntaxElementChoice;
import ParserGenerator.SyntaxElementLoop;
import ParserGenerator.SyntaxElementNone;
import ParserGenerator.SyntaxElementSyntax;
import ParserGenerator.SyntaxElementToken;
import ParserGenerator.SyntaxElementWord;
import ParserGenerator.SyntaxNode;
import ParserGenerator.Token;


public class QueryParser extends Parser {
	
	private static final int T_LITERAL = 0; 
	private static final int T_NUMBER = 1; 
	private static final int T_STRING = 2; 
	private static final int T_TABLE = 3; 
	private static final int T_ATTRIBUTE = 4; 
	private static final int T_RELATION = 5; 
	private static final int T_JOIN = 6; 
	private static final int T_SELECT = 7; 
	private static final int T_PROJECT = 8; 
	private static final int T_UNION = 9;
	private static final int T_DIFF = 10;
	private static final int T_INTERSECT = 11; 


	
	public QueryParser(){
		init();

		String delims = " \t\n\r";
		String terms = "+-*/=<>()[],'.";

		SyntaxElement bropen = new SyntaxElementWord()
			.setWord("[")
			.setMaxLength(1)
			.setDelimiters(delims)
			.setImportant(false);

		SyntaxElement brclose = new SyntaxElementWord()
			.setWord("]")
			.setMaxLength(1)
			.setDelimiters(delims)
			.setImportant(false);

		SyntaxElement popen = new SyntaxElementWord()
		.setWord("(")
		.setMaxLength(1)
		.setDelimiters(delims)
		.setImportant(false);

		SyntaxElement pclose = new SyntaxElementWord()
			.setWord(")")
			.setMaxLength(1)
			.setDelimiters(delims)
			.setImportant(false);

		SyntaxElement comma = new SyntaxElementWord()
			.setWord(",")
			.setMaxLength(1)
			.setDelimiters(delims)
			.setImportant(false);

		SyntaxElement dot = new SyntaxElementWord()
			.setWord(".")
			.setMaxLength(1)
			.setDelimiters(delims)
			.setImportant(false);

		SyntaxElement quote = new SyntaxElementWord()
			.setWord("'")
			.setMaxLength(1)
			.setImportant(false);

		SyntaxElement string_literal = new SyntaxElementSyntax()
			.addElement(
				new SyntaxElementWord().setWord("'").setMaxLength(1).setImportant(false)
			)
			.addElement(
				new SyntaxElementChoice()
					.addElement(
						new SyntaxElementAny("string")
							.setTerminators("'")
							.setImportant(true)
					)
					.addElement(new SyntaxElementNone())
			)
			.addElement(
				new SyntaxElementWord().setWord("'").setMaxLength(1).setImportant(false)
			)
			.setDelimiters(delims)
			.setImportant(false);

		SyntaxElement number_literal = new SyntaxElementToken("number")
			.setToken(
				new Token().setNumberTypes(NumberTypes.ANY)
			)
			.setDelimiters(delims)
			.setTerminators(terms);
		
		SyntaxElement literal = new SyntaxElementChoice()
			.addElement(number_literal)
			.addElement(string_literal);
		
		SyntaxElementChoice value = new SyntaxElementChoice();
		value
			.addElement(
				new SyntaxElementSyntax()
					.addElement(popen)
					.addElement(value)
					.addElement(pclose)
					.setImportant(false)
			)
			.addElement(literal);
		
		SyntaxElement word_or = new SyntaxElementToken()
			.setToken(
				new Token()
				.setCaseSensitivity(false)
				.addSymbol("or", null)
			)
			.setMaxLength(2)
			.setDelimiters(delims)
			.setImportant(true);

		SyntaxElement word_and = new SyntaxElementToken()
			.setToken(
				new Token()
				.setCaseSensitivity(false)
				.addSymbol("and", null)
			)
			.setMaxLength(3)
			.setDelimiters(delims)
			.setImportant(true);

		SyntaxElement word_not = new SyntaxElementToken()
			.setToken(
				new Token()
				.setCaseSensitivity(false)
				.addSymbol("not", null)
			)
			.setMaxLength(3)
			.setDelimiters(delims)
			.setImportant(true);

		
		Token relation_t = new Token("relations");
		
		Token attribute_t = new Token("attributes");

		SyntaxElement relation = new SyntaxElementToken("relation")
			.setToken(relation_t)
			.setDelimiters(delims)
			.setTerminators(terms)
			.setImportant(true);
	
		SyntaxElement attribute = new SyntaxElementToken("attribute")
			.setToken(attribute_t)
			.setDelimiters(delims)
			.setTerminators(terms)
			.setImportant(true);
		
		SyntaxElement namedAttribute = new SyntaxElementSyntax("named-attribute")
			.addElement(relation)
			.addElement(dot)
			.addElement(attribute);
		

		SyntaxElementChoice table = new SyntaxElementChoice("table");

		
		//---------------Predicate----------------------
		
		SyntaxElement comparison = new SyntaxElementChoice("comparison-operator")
			.addElement(new SyntaxElementWord().setWord("=").setMaxLength(1).setName("equals"))
			.addElement(new SyntaxElementWord().setWord("<>").setMaxLength(2).setName("not-equal"))
			.addElement(new SyntaxElementWord().setWord(">").setMaxLength(1).setName("greater-than"))
			.addElement(new SyntaxElementWord().setWord("<").setMaxLength(1).setName("less-than"))
			.addElement(new SyntaxElementWord().setWord("like").setMaxLength(4).setName("like"))
			.setDelimiters(delims);
		
		SyntaxElementChoice predicate = new SyntaxElementChoice("predicate");

		SyntaxElementChoice predicate_conjunction = new SyntaxElementChoice();
		
		
		SyntaxElement predicateElement = new SyntaxElementChoice()
			.addElement(namedAttribute)
			.addElement(attribute)
			.addElement(value);
 
		
		SyntaxElement atomicPredicate = new SyntaxElementSyntax("comparison")
			.addElement(predicateElement)
			.addElement(comparison)
			.addElement(predicateElement);
			
		SyntaxElement parenPredicate = new SyntaxElementChoice()
			.addElement(
				new SyntaxElementSyntax()
				.addElement(popen)
				.addElement(predicate_conjunction)
				.addElement(pclose)
				.setImportant(false)
			)
			.addElement(
				new SyntaxElementSyntax()
				.addElement(popen)
				.addElement(predicate)
				.addElement(pclose)
				.setImportant(true)
			);

		SyntaxElement predicate_single_positive = new SyntaxElementChoice()
			.addElement(parenPredicate)
			.addElement(atomicPredicate)
			.setImportant(false);

		SyntaxElement predicate_single = new SyntaxElementChoice()
			.addElement(
				new SyntaxElementSyntax("negative")
					.addElement(word_not)
					.addElement(predicate_single_positive)
			)
			.addElement(predicate_single_positive);
		
		
		predicate_conjunction
			.addElement(
				new SyntaxElementSyntax("conjunction")
					.addElement(predicate_single)
					.addElement(
						new SyntaxElementLoop()
							.setElement(
								new SyntaxElementSyntax()
									.addElement(word_and)
									.addElement(predicate_single)
									.setImportant(false)
							)
					)
					.setImportant(true)
			)
			.addElement(predicate_single);

		predicate
			.addElement(
				new SyntaxElementSyntax("disjunction")
					.addElement(predicate_conjunction)
					.addElement(
						new SyntaxElementLoop()
							.setElement(
								new SyntaxElementSyntax()
									.addElement(word_or)
									.addElement(predicate_conjunction)
									.setImportant(false)
							)
					)
					.setImportant(false)
			)
			.addElement(predicate_conjunction)
			.setImportant(false);
		
		SyntaxElement condition = new SyntaxElementSyntax("condition")
			.addElement(predicate);
		
		//---------------------------------------------
		
		
		SyntaxElement word_sel = new SyntaxElementToken()
			.setToken(
				new Token()
				.setCaseSensitivity(false)
				.addSymbol("sel", null)
			)
			.setMaxLength(3)
			.setDelimiters(delims)
			.setImportant(false);

		
		SyntaxElement word_proj = new SyntaxElementToken()
			.setToken(
				new Token()
				.setCaseSensitivity(false)
				.addSymbol("proj", null)
			)
			.setMaxLength(4)
			.setDelimiters(delims)
			.setImportant(false);
	
		SyntaxElement word_join = new SyntaxElementToken()
			.setToken(
				new Token()
				.setCaseSensitivity(false)
				.addSymbol("join", null)
			)
			.setMaxLength(4)
			.setDelimiters(delims)
			.setImportant(false);

		SyntaxElement word_inter = new SyntaxElementToken()
			.setToken(
				new Token()
				.setCaseSensitivity(false)
				.addSymbol("inter", null)
			)
			.setMaxLength(5)
			.setDelimiters(delims)
			.setImportant(false);

		SyntaxElement word_union = new SyntaxElementToken()
			.setToken(
				new Token()
				.setCaseSensitivity(false)
				.addSymbol("union", null)
			)
			.setMaxLength(5)
			.setDelimiters(delims)
			.setImportant(false);

		SyntaxElement word_diff = new SyntaxElementToken()
			.setToken(
				new Token()
				.setCaseSensitivity(false)
				.addSymbol("diff", null)
			)
			.setMaxLength(4)
			.setDelimiters(delims)
			.setImportant(false);
	
		SyntaxElement simple_or_named_attribute = new SyntaxElementChoice()
			.addElement(namedAttribute)
			.addElement(attribute);
		
		SyntaxElement attribute_list = new SyntaxElementSyntax("attribute_list")
			.addElement(simple_or_named_attribute)
			.addElement(
				new SyntaxElementChoice()
					.addElement(
						new SyntaxElementLoop()
							.setElement(
								new SyntaxElementSyntax()
									.addElement(comma)
									.addElement(simple_or_named_attribute)
									.setImportant(false)
							)
					)
					.addElement(new SyntaxElementNone())
					.setImportant(false)
			);
		
		
		SyntaxElement selection = new SyntaxElementSyntax("select")
			.addElement(word_sel)
			.addElement(
				new SyntaxElementSyntax()
					.addElement(bropen)
					.addElement(condition)
					.addElement(brclose)
					.setImportant(false)
			)
			.addElement(
				new SyntaxElementSyntax()
					.addElement(popen)
					.addElement(table)
					.addElement(pclose)
					.setImportant(false)
			);

		SyntaxElement projection = new SyntaxElementSyntax("project")
			.addElement(word_proj)
			.addElement(
				new SyntaxElementSyntax()
					.addElement(bropen)
					.addElement(attribute_list)
					.addElement(brclose)
					.setImportant(false)
			)
			.addElement(
				new SyntaxElementSyntax()
					.addElement(popen)
					.addElement(table)
					.addElement(pclose)
					.setImportant(false)
			);
		
		SyntaxElement join = new SyntaxElementSyntax("join")
			.addElement(word_join)
			.addElement(
				new SyntaxElementSyntax()
					.addElement(bropen)
					.addElement(condition)
					.addElement(brclose)
					.setImportant(false)
			)
			.addElement(
				new SyntaxElementSyntax()
					.addElement(popen)
					.addElement(table)
					.addElement(pclose)
					.setImportant(false)
			)
			.addElement(
				new SyntaxElementSyntax()
					.addElement(popen)
					.addElement(table)
					.addElement(pclose)
					.setImportant(false)
			);

		
		SyntaxElement intersection = new SyntaxElementSyntax("intersection")
			.addElement(word_inter)
			.addElement(
				new SyntaxElementSyntax()
					.addElement(popen)
					.addElement(table)
					.addElement(pclose)
					.setImportant(false)
			)
			.addElement(
				new SyntaxElementSyntax()
					.addElement(popen)
					.addElement(table)
					.addElement(pclose)
					.setImportant(false)
			);
		
		SyntaxElement union = new SyntaxElementSyntax("union")
			.addElement(word_union)
			.addElement(
				new SyntaxElementSyntax()
					.addElement(popen)
					.addElement(table)
					.addElement(pclose)
					.setImportant(false)
			)
			.addElement(
				new SyntaxElementSyntax()
					.addElement(popen)
					.addElement(table)
					.addElement(pclose)
					.setImportant(false)
			);

		SyntaxElement difference = new SyntaxElementSyntax("diff")
			.addElement(word_diff)
			.addElement(
				new SyntaxElementSyntax()
					.addElement(popen)
					.addElement(table)
					.addElement(pclose)
					.setImportant(false)
			)
			.addElement(
				new SyntaxElementSyntax()
					.addElement(popen)
					.addElement(table)
					.addElement(pclose)
					.setImportant(false)
			);

		
		table
			.addElement(selection)
			.addElement(projection)
			.addElement(join)
			.addElement(intersection)
			.addElement(union)
			.addElement(difference)
			.addElement(relation);
		
		
		this.addSyntaxElement(table);
		this.setRootElement(table);
		
		Map<String, Object> attributes = new HashMap<String, Object>();
		
		Map<String, Relation> tables = Catalog.getInstance().getCatalogMap();
		for ( String key : tables.keySet() ) {
			Relation t = tables.get(key);
		    relation_t.addSymbol(key, t);
		    for(Attribute a : t.getAttributes()){
		    	if(!attributes.containsKey(a.getName()))
		    		attributes.put(a.getName(), null);
		    }
		}
		for(String a : attributes.keySet()){
			attribute_t.addSymbol(a, attributes.get(a));
		}
		
	}
	
	@Override
	public SyntaxNode recognize(String text){
		SyntaxNode result = super.recognize(text);
		return result;
	}
	
	public PlanNode createPlan(String query) throws Exception{
		SyntaxNode rootNode = this.recognize(query);
		return createPlan(rootNode);
	}

	public PlanNode createPlan(SyntaxNode rootNode) throws Exception{
		if(!rootNode.isRecognized)
			throw new Exception("Query syntax error");
		PlanTableNode result = createPlanTableNode(rootNode);
		result.constructTable();
		return result;
	}
	private PlanTableNode createPlanTableNode(SyntaxNode snode){
		switch(snode.syntaxElement.name){
		case "table":
			return createPlanTableNode(snode.children.get(0));
		case "project":
			return createPlanProjectNode(snode);
		case "join":
			return createPlanJoinNode(snode);
		case "select":
			return createPlanSelectNode(snode);
		case "union":
			return createPlanUnionNode(snode);
		case "diff":
			return createPlanDiffNode(snode);
		case "intersect":
			return createPlanIntersectNode(snode);
		case "relation":
			return createPlanRelationNode(snode);
		default:
			break;
		}
		return new PlanTableNode();
	}

	private PlanSelectNode createPlanSelectNode(SyntaxNode snode){
		PlanSelectNode node = new PlanSelectNode();
		node.predicate = createPlanPredicateNode(snode.children.get(0));
		node.table = createPlanTableNode(snode.children.get(1));
		return node;
	}

	private PlanProjectNode createPlanProjectNode(SyntaxNode snode){
		PlanProjectNode node = new PlanProjectNode();
		node.projectedAttributes = new ArrayList<PlanAttributeNode>();
		for(SyntaxNode n : snode.children.get(0).children){
			node.projectedAttributes.add(createPlanAttributeNode(n));
		}
		node.table = createPlanTableNode(snode.children.get(1));
		return node;
	}

	private PlanJoinNode createPlanJoinNode(SyntaxNode snode){
		PlanJoinNode node = new PlanJoinNode();
		node.predicate = createPlanPredicateNode(snode.children.get(0));
		node.left = createPlanTableNode(snode.children.get(1));
		node.right = createPlanTableNode(snode.children.get(2));
		return node;
	}

	private PlanUnionNode createPlanUnionNode(SyntaxNode snode){
		PlanUnionNode node = new PlanUnionNode();
		node.left = createPlanTableNode(snode.children.get(0));
		node.right = createPlanTableNode(snode.children.get(1));
		return node;
	}

	private PlanDiffNode createPlanDiffNode(SyntaxNode snode){
		PlanDiffNode node = new PlanDiffNode();
		node.left = createPlanTableNode(snode.children.get(0));
		node.right = createPlanTableNode(snode.children.get(1));
		return node;
	}

	private PlanIntersectNode createPlanIntersectNode(SyntaxNode snode){
		PlanIntersectNode node = new PlanIntersectNode();
		node.left = createPlanTableNode(snode.children.get(0));
		node.right = createPlanTableNode(snode.children.get(1));
		return node;
	}

	private PlanRelationNode createPlanRelationNode(SyntaxNode snode){
		PlanRelationNode node = new PlanRelationNode();
		Token.TokenResult t = (Token.TokenResult)snode.data;
		node.relation = (Relation)t.data;
		return node;
	}

	
	private PlanPredicateNode createPlanPredicateNode(SyntaxNode snode){
		if(snode.syntaxElement.name.equals("comparison"))
			return createPlanComparisonNode(snode);
		if(snode.syntaxElement.name.equals("negative")){
			PlanNegationNode node = new PlanNegationNode();
			node.predicate = createPlanPredicateNode(snode.children.get(1));
			return node;
		}
			
		if(snode.children.size()==1)
			return createPlanPredicateNode(snode.children.get(0));

		List<PlanPredicateNode> nodes = new ArrayList<PlanPredicateNode>();
		int i=0;
		for(SyntaxNode sn : snode.children){
			if(i%2==0){
				nodes.add(createPlanPredicateNode(sn));
			}
			i++;
		}
		switch(snode.syntaxElement.name){
			case "conjunction":{
				PlanConjunctionNode node = new PlanConjunctionNode();
				node.predicates = nodes;
				return node;
				
			}
			default:{
				PlanDisjunctionNode node = new PlanDisjunctionNode();
				node.predicates = nodes;
				return node;
			}
		}
	}
	
	private PlanComparisonNode createPlanComparisonNode(SyntaxNode snode){
		PlanComparisonNode node = new PlanComparisonNode();
		switch(snode.children.get(1).syntaxElement.name){
		case "equals":
			node.operator = ComparisonOperatorType.equals;
			break;
		case "not-equal":
			node.operator = ComparisonOperatorType.notEqual;
			break;
		case "greater-than":
			node.operator = ComparisonOperatorType.greaterThan;
			break;
		case "less-than":
			node.operator = ComparisonOperatorType.lessThan;
			break;
		case "like":
			node.operator = ComparisonOperatorType.like;
			break;
		default:
			node.operator = ComparisonOperatorType.equals;
			break;
		}
		node.left = createPlanValueNode(snode.children.get(0));
		node.right = createPlanValueNode(snode.children.get(2));
		return node;
	}
	
	private PlanValueNode createPlanValueNode(SyntaxNode snode){
		switch(snode.syntaxElement.name){
		case "string":
		{
			PlanStringLiteralValueNode node = new PlanStringLiteralValueNode();
			node.value = snode.text.replace("''", "'");
			return node;
		}
		case "number":
		{
			PlanNumberLiteralValueNode node = new PlanNumberLiteralValueNode();
			Token.TokenResult res = (Token.TokenResult)snode.data;
			node.value = res.number;
			return node;
		}
		case "named-attribute":
		{
			PlanAttributeValueNode node = new PlanAttributeValueNode();
			node.tableName = snode.children.get(0).text;
			node.attributeName = snode.children.get(1).text;
			return node;
		}
		case "attribute":
		{
			PlanAttributeValueNode node = new PlanAttributeValueNode();
			node.attributeName = snode.text;
			return node;
		}
		default:
			return null;
		}
	}
	
	

	private PlanAttributeNode createPlanAttributeNode(SyntaxNode snode){
		switch(snode.syntaxElement.name){
		case "named-attribute":
		{
			PlanAttributeNode node = new PlanAttributeNode();
			node.tableName = snode.children.get(0).text;
			node.attributeName = snode.children.get(1).text;
			return node;
		}
		case "attribute":
		{
			PlanAttributeNode node = new PlanAttributeNode();
			node.attributeName = snode.text;
			return node;
		}
		default:
			return null;
		}
	}

	private PlanNode createPlanNode(SyntaxNode snode){

		return new PlanNode();
	}
}
