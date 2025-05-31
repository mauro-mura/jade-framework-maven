package jade.content.lang.xml;

import java.io.StringReader;
import java.math.BigInteger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

public class Test {

	public static void main(String[] args) {
		try {
//			String a = "A"+System.lineSeparator()+"B";
//			System.out.println(a);
//			System.out.println(toHex(a));
//
//			System.out.println("-------------");
//			
//			a = "A\r\nB";
//			System.out.println(a);
//			System.out.println(toHex(a));
//			
//			System.out.println("-------------");
//			
//			String xml = "<primitive type=\"STRING\" value=\"AAA&#013;&#010;BBB\"/>";
//			//String xml = "<primitive type=\"STRING\" value=\"&amp;&#039;&quot;&lt;AAA&#13;BBB"+System.lineSeparator()+"CCC\"/>";
//			System.out.println(xml);
//			
//			System.out.println("-------------");
//			
//			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//			factory.setValidating(false);
//			factory.setNamespaceAware(false);
//			DocumentBuilder builder = factory.newDocumentBuilder();
//			Document doc = builder.parse(new InputSource(new StringReader(xml)));
//			Element node = doc.getDocumentElement();
//			NamedNodeMap attributes = node.getAttributes();
//			Node attrType = attributes.getNamedItem(XMLCodec.TYPE_ATTR);
//			Node attrValue = attributes.getNamedItem(XMLCodec.VALUE_ATTR);
//			String nodeValue = attrValue.getNodeValue();
//			System.out.println(nodeValue);
//			System.out.println(toHex(nodeValue));
			
			
			String obj = "A"+System.lineSeparator()+"B";
			System.out.println(toHex(obj));
			System.out.println("-------------");
			
			XMLManager xmlManager = new XMLManager();
			String xml = xmlManager.encode(obj);
			System.out.println(xml);
			
			Object obj1 = xmlManager.decode(xml);
			System.out.println("-------------");
			System.out.println(toHex((String)obj1));
			
			if (obj.equals((String)obj1)) {
				System.out.println("OK");
			}
			else {
				System.out.println("ERRORE");
			}
			
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	public static String toHex(String arg) {
	    return String.format("%x", new BigInteger(1, arg.getBytes()));
	}
}


