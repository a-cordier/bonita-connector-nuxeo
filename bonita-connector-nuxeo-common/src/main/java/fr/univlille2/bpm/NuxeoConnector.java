package fr.univlille2.bpm;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.bonitasoft.engine.connector.Connector;
import org.bonitasoft.engine.connector.ConnectorException;
import org.bonitasoft.engine.connector.ConnectorValidationException;
import org.nuxeo.ecm.automation.client.jaxrs.impl.HttpAutomationClient;
import org.nuxeo.ecm.automation.client.jaxrs.spi.auth.PortalSSOAuthInterceptor;
import org.nuxeo.ecm.automation.client.Session;


public abstract class NuxeoConnector implements Connector{

	private String username;
	private String password;
	private String url;
	private boolean useSSL;
	private boolean useSSO;


	@Override
	public void setInputParameters(final Map<String, Object> parameters) {
		final Object urlObject = parameters.get("url");
		url = urlObject != null ? (String) urlObject : "";
		final Object usernameObject = parameters.get("username");
		username = usernameObject != null ? (String) usernameObject : "";
		final Object passwordObject = parameters.get("password");
		password = passwordObject != null ? (String) passwordObject : "";
		final Object useSSLObject = parameters.get("use-ssl");
		useSSL = useSSLObject != null ? Boolean.valueOf((String) useSSLObject) : false;
		final Object useSSOObject = parameters.get("use-sso");
		useSSO = useSSOObject != null ? Boolean.valueOf((String) useSSOObject) : false;	
	}

	@Override
	public void validateInputParameters() throws ConnectorValidationException {

	}
	/*
	 * This common task provides session for any action needed against nuxeo repository
	 */
	@Override 
	public Map<String, Object> execute() throws ConnectorException {
		HttpAutomationClient client = new HttpAutomationClient(url);		
		if(useSSL){
			wrapClient(client.http());
		}

		/*
		 * If no password try to handle a shared secret authentication
		 */
		Session session = null;
		try {
			if(useSSO){
				client.setRequestInterceptor(new PortalSSOAuthInterceptor(password, username));
				session  = client.getSession();
			}else{
				session = client.getSession(username, password);		
			}
			executeTask(session);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Collections.emptyMap();
	}

	protected abstract void executeTask(Session session) throws Exception;

	@Override
	public void connect() throws ConnectorException {
	}

	@Override
	public void disconnect() throws ConnectorException {
	}



	@SuppressWarnings("deprecation")
	private static HttpClient wrapClient(HttpClient client) {

		DefaultHttpClient clientReference = null;

		try {
			SSLContext context = SSLContext.getInstance("TLS");
			X509TrustManager trustManager = new X509TrustManager() {

				public void checkClientTrusted(X509Certificate[] xcs, String string) {
				}

				public void checkServerTrusted(X509Certificate[] xcs, String string) {
				}

				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
			};
			context.init(null, new TrustManager[]{trustManager}, null);
			SSLSocketFactory socketFactory = new SSLSocketFactory(context);
			socketFactory.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			ClientConnectionManager connectionManager = client.getConnectionManager();
			SchemeRegistry schemeRegistry = connectionManager.getSchemeRegistry();
			schemeRegistry.register(new Scheme("https", socketFactory, 443));
			clientReference = new DefaultHttpClient(connectionManager,client.getParams());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();

		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return clientReference;
	}
}

