/**
 * 
 */
package fr.univlille2.bpm;

import org.nuxeo.ecm.automation.client.Session;
import org.nuxeo.ecm.automation.client.adapters.DocumentService;
import org.nuxeo.ecm.automation.client.jaxrs.impl.HttpAutomationClient;
import org.nuxeo.ecm.automation.client.model.Document;

/**
 * @author acordier Jul 24, 2014
 * App.java
 */
public class App {

	/**
	 * @param args
	 */
	
	public static String EXAMPLE_TEST="/default-domain/UserWorkspaces/Administrator/bonita";
	public static void main(String[] args) {
		String pattern = "/(.+)*/?$";
		System.out.println(EXAMPLE_TEST.replaceAll(pattern, "foo")); 
		System.out.println(EXAMPLE_TEST.substring(0, EXAMPLE_TEST.lastIndexOf("bonita")));
		HttpAutomationClient client = new HttpAutomationClient("http://schubert.univ-lille2.fr/nuxeo/site/automation");
		Session session = null;

		session = client.getSession("Administrator", "Administrator");
		
		DocumentService ds = session.getAdapter(DocumentService.class);
		
		try {
			Document doc = ds.getDocument("/default-domain/UserWorkspaces/Administrator/bonita");
			System.out.println(doc.getPath());
			
		} catch (Exception e) {
			System.out.println("hello");
		}
		
		
	}
	
	public static String checkPath(String path){
		return path.charAt(path.length()-1)=='/'?path.substring(0,path.length()-1):path;
	}

}
