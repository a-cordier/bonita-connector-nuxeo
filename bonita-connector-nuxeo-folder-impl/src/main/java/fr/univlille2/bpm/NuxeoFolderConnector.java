/**
 * 
 */
package fr.univlille2.bpm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.nuxeo.ecm.automation.client.Session;

/**
 * @author acordier Jul 24, 2014
 * NuxeoFolderConnector.java
 */
public class NuxeoFolderConnector extends NuxeoConnector {

	// inputs
	public static final String PATH = "path";
	public static final String CREATE = "create";
	public static final String PERMISSIONS = "permissions";
	public static final String USERNAMES = "usernames";
	
	// outputs
	public static final String DOC_URL = "documentURL";
	public static final String DOC_OBJECT = "documentObject";
	
	private String path;
	private boolean create;
	private String permissions;
	private ArrayList<Object> usernames;


	private Logger logger = Logger.getLogger(this.getClass().getName());

	@SuppressWarnings("unchecked")
	@Override
	public void setInputParameters(final Map<String, Object> parameters) {
		super.setInputParameters(parameters);
		path = (String) parameters.get(PATH);
		create = (Boolean)parameters.get(CREATE);	
		permissions = (String) parameters.get(PERMISSIONS);
		usernames = (ArrayList<Object>) parameters.get(USERNAMES);

	}
	
	/* (non-Javadoc)
	 * @see fr.univlille2.bpm.NuxeoConnector#executeConnector(org.nuxeo.ecm.automation.client.Session)
	 */
	@Override
	protected void executeConnector(final Session session) throws Exception {
		if(logger.isLoggable(Level.INFO)){
			logger.info("Starting nuxeo folder connector");
		}	
		path = NuxeoConnectorUtils.unTrail(path);
		String folderName = path.substring(path.lastIndexOf('/')+1);
		String parentPath = path.substring(0, path.lastIndexOf(folderName));
	}
	
	public static String checkPath(String path){
		return path.charAt(path.length()-1)=='/'?path.substring(0,path.length()-1):path;
	}
}
