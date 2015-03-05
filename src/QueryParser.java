import java.util.HashMap;
import java.util.Map;

import qpo.data.info.Catalog;
import qpo.data.model.Attribute;
import qpo.data.model.Table;
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

		SyntaxElementChoice table = new SyntaxElementChoice("table");

		
		//---------------Predicate----------------------
		
		SyntaxElement comparison = new SyntaxElementChoice("comparison")
			.addElement(new SyntaxElementWord().setWord("=").setMaxLength(1))
			.addElement(new SyntaxElementWord().setWord("<>").setMaxLength(2))
			.addElement(new SyntaxElementWord().setWord(">").setMaxLength(1))
			.addElement(new SyntaxElementWord().setWord("<").setMaxLength(1))
			.setDelimiters(delims);
		
		SyntaxElementChoice predicate = new SyntaxElementChoice("predicate");

		SyntaxElementChoice predicate_conjunction = new SyntaxElementChoice();
		
		
		SyntaxElement predicateElement = new SyntaxElementChoice()
			.addElement(
				new SyntaxElementSyntax()
					.addElement(relation)
					.addElement(dot)
					.addElement(attribute)
			)
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
	
		SyntaxElement attribute_list = new SyntaxElementSyntax("attribute_list")
			.addElement(attribute)
			.addElement(
				new SyntaxElementChoice()
					.addElement(
						new SyntaxElementLoop()
							.setElement(
								new SyntaxElementSyntax()
									.addElement(comma)
									.addElement(attribute)
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
			)
			.addElement(
				new SyntaxElementSyntax()
					.addElement(popen)
					.addElement(table)
					.addElement(pclose)
			);

		
		SyntaxElement intersection = new SyntaxElementSyntax("intersection")
			.addElement(word_inter)
			.addElement(
				new SyntaxElementSyntax()
					.addElement(popen)
					.addElement(table)
					.addElement(pclose)
			)
			.addElement(
				new SyntaxElementSyntax()
					.addElement(popen)
					.addElement(table)
					.addElement(pclose)
			);
		
		SyntaxElement union = new SyntaxElementSyntax("union")
			.addElement(word_union)
			.addElement(
				new SyntaxElementSyntax()
					.addElement(popen)
					.addElement(table)
					.addElement(pclose)
			)
			.addElement(
				new SyntaxElementSyntax()
					.addElement(popen)
					.addElement(table)
					.addElement(pclose)
			);

		SyntaxElement difference = new SyntaxElementSyntax("difference")
			.addElement(word_diff)
			.addElement(
				new SyntaxElementSyntax()
					.addElement(popen)
					.addElement(table)
					.addElement(pclose)
			)
			.addElement(
				new SyntaxElementSyntax()
					.addElement(popen)
					.addElement(table)
					.addElement(pclose)
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
		
		Map<String, Table> tables = Catalog.getInstance().getCatalogMap();
		for ( String key : tables.keySet() ) {
			Table t = tables.get(key);
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
}
