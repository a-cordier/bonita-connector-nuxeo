package fr.univlille2.bpm;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bonitasoft.engine.api.ProcessAPI;import org.bonitasoft.engine.connector.ConnectorException;
import org.nuxeo.ecm.automation.client.Session;
import org.nuxeo.ecm.automation.client.adapters.DocumentService;
import org.nuxeo.ecm.automation.client.model.Document;
import org.nuxeo.ecm.automation.client.model.StreamBlob;

/**
 * 
 * @author acordier Jul 7, 2014
 * NuxeoUploadConnector.java
 */
public class NuxeoUploadConnector extends NuxeoConnector {
	// inputs
	public static final String ATTACHMENT = "attachment";
	public static final String PATH = "path";
	public static final String TITLE = "title";
	public static final String TYPE = "type";
	public static final String PROPERTIES = "properties";
	// outputs
	public static final String DOC_URL = "documentURL";
	public static final String DOC_OBJECT = "documentObject";
	
	private String attachment;
	private String path;
	private String title;
	private String type;
	private ArrayList<Object> properties;

	private Logger logger = Logger.getLogger(this.getClass().getName());


	@SuppressWarnings("unchecked")
	@Override
	public void setInputParameters(final Map<String, Object> parameters) {
		super.setInputParameters(parameters);
		attachment = (String) parameters.get(ATTACHMENT);
		path = (String) parameters.get(PATH);
		title = (String) parameters.get(TITLE);
		type = (String) parameters.get(TYPE);
		properties = (ArrayList<Object>) parameters.get(PROPERTIES);

	}

	@Override
	@SuppressWarnings("rawtypes")
	protected void executeConnector(final Session session) throws Exception {
		if(logger.isLoggable(Level.INFO)){
			logger.info("Starting nuxeo upload connector for a document named " + attachment);
		}
		// bonita document retrieval
		ProcessAPI processAPI = getAPIAccessor().getProcessAPI();
		long processInstanceId = getExecutionContext().getProcessInstanceId();
		org.bonitasoft.engine.bpm.document.Document srcDocument = 
				processAPI.getLastDocument(processInstanceId, this.attachment);			
		if(srcDocument==null){
			throw new ConnectorException("Unable to retrieve process a attachment referenced by " + attachment);
		}			
		final byte[] content = processAPI.getDocumentContent(srcDocument.getContentStorageId());		
		final ByteArrayInputStream inputStream = new ByteArrayInputStream(content);
		String fileName = srcDocument.getContentFileName();
		String mimeType = srcDocument.getContentMimeType();

		// nuxeo document API call
		DocumentService documentService = session.getAdapter(DocumentService.class);
		org.nuxeo.ecm.automation.client.model.Document nxDocument = new Document(title, type);

		path = NuxeoConnectorUtils.trail(path); 
		nxDocument.set("dc:title", title);
		// additional metadata attachment
		for(Object object : properties){
			ArrayList row = (ArrayList)object;
			nxDocument.set((String)row.get(0),(String)row.get(1));
		}
		// document creation
		nxDocument = documentService.createDocument(path, nxDocument);
		// blob attachment
		StreamBlob blob = new StreamBlob(inputStream, fileName, mimeType);
		documentService.setBlob(nxDocument, blob);
		documentService.update(nxDocument);
		String docURL = String.format("%s/nxdoc/default/%s/view_documents", url, nxDocument.getId());
		// setting connector outputs
		this.getOutputParameters().put(DOC_OBJECT, nxDocument);
		this.getOutputParameters().put(DOC_URL, docURL);
		
		if(logger.isLoggable(Level.INFO)){
			logger.info(String.format("File %s sent to %s", fileName, path));
		}
	}



}
