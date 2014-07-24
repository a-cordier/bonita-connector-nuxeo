/**
 * 
 */
package fr.univlille2.bpm;

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
		
	}
	
	public static String checkPath(String path){
		return path.charAt(path.length()-1)=='/'?path.substring(0,path.length()-1):path;
	}

}
