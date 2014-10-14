package fr.univlille2.bpm;


import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bonitasoft.engine.connector.AbstractConnector;
import org.bonitasoft.engine.connector.ConnectorException;
import org.bonitasoft.engine.connector.ConnectorValidationException;
import org.nuxeo.ecm.automation.client.jaxrs.impl.HttpAutomationClient;
import org.nuxeo.ecm.automation.client.jaxrs.spi.auth.PortalSSOAuthInterceptor;
import org.nuxeo.ecm.automation.client.Session;



public abstract class NuxeoConnector extends AbstractConnector{
	
	private String username;
	private String password;
	protected String url;
	private boolean useSSO;
	
	protected static Logger logger = Logger.getLogger(NuxeoConnector.class.getName());

	@Override
	public void setInputParameters(final Map<String, Object> parameters) {
		url = (String) parameters.get("url");
		username = (String)parameters.get("username");
		password = (String) parameters.get("password");
		useSSO = (Boolean)parameters.get("useSSO");		
	}

	@Override
	public void validateInputParameters() throws ConnectorValidationException {

	}
	/*
	 * This common task provides session for any action needed against nuxeo repository
	 */
	@Override 
	public void executeBusinessLogic() throws ConnectorException {
		if(logger.isLoggable(Level.INFO)){
			logger.info(String.format(
					"Nuxeo connector is running with parameters {url:%s, username:%s, useSSO:%s}",
					url, username, useSSO));
		}
		String automationURL = String.format("%s/site/automation", url); 
		HttpAutomationClient client = new HttpAutomationClient(automationURL);		

		/*
		 * If the so called SSO options is checked,
		 * try to handle a shared secret authentication
		 */
	
		try {
			if(useSSO){
				client.setRequestInterceptor(new PortalSSOAuthInterceptor(password, username));
				
			}else{
				client.setBasicAuth(username, password);
			}
			final Session session  = client.getSession();
			executeConnector(session);
		} catch (Exception e) {
			logger.severe("Could not execute task");
			e.printStackTrace();
		}
	}

	protected abstract void executeConnector(Session session) throws Exception;

	@Override
	public void connect() throws ConnectorException {
	}

	@Override
	public void disconnect() throws ConnectorException {
	}


}

