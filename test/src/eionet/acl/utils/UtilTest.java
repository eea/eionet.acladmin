package eionet.acl.utils;

import java.util.Hashtable;

import junit.framework.TestCase;

/**
 * 
 * @author Jaanus Heinlaid, e-mail: <a href="mailto:jaanus.heinlaid@tietoenator.com">jaanus.heinlaid@tietoenator.com</a>
 *
 */
public class UtilTest extends TestCase{
	
	/**
	 *
	 */
	public void test_replaceTags(){
		
		// test simple ampersand
		assertEquals(Util.replaceTags("Fruit & Vegetables"), "Fruit &amp; Vegetables");
		
		// test an ampersand that is actually the beginning to an escape sequence
		assertEquals(Util.replaceTags("Fruit &amp; Vegetables"), "Fruit &amp; Vegetables");

		// test greater than, less than, quotes and apostrophe
		assertEquals(Util.replaceTags("<a href=\"http://\">A link to 'nothing'</a>"),
				"&lt;a href=&quot;http://&quot;&gt;A link to &#039;nothing&#039;&lt;/a&gt;");
		
		// test Unicode char
		assertEquals(Util.replaceTags("�"),"�");
	}
	
	/**
	 *
	 */
	public void test_getPrmDescrs(){
		
		Hashtable hash = new Hashtable();
		hash.put("a", "Add");
		hash.put("a;", "Add ;special");
		hash.put("d", "Delete");
		
		assertEquals(Util.getPrmDescrs("a,a;,d", hash), "Add; Add ;special; Delete; ");
	}
}