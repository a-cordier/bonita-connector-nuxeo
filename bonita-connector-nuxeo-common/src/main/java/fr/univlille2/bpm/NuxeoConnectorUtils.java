/**
 * 
 */
package fr.univlille2.bpm;

/**
 * @author acordier Jul 24, 2014
 * NuxeoConnectorUtils.java
 */
public class NuxeoConnectorUtils {
	
	public static String trail(String path){
		return path.charAt(path.length()-1)!='/'?path+"/":path;
	}
	
	public static String unTrail(String path){
		return path.charAt(path.length()-1)=='/'?path.substring(0,path.length()-1):path;
	}
}
