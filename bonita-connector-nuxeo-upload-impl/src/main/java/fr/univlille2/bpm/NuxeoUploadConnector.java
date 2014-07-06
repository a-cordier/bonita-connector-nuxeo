package fr.univlille2.bpm;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bonitasoft.engine.api.ProcessAPI;import org.bonitasoft.engine.connector.ConnectorException;
import org.nuxeo.ecm.automation.client.Session;
import org.nuxeo.ecm.automation.client.adapters.DocumentService;
import org.nuxeo.ecm.automation.client.model.Document;
import org.nuxeo.ecm.automation.client.model.FileBlob;

// TODO: write definition file
public class NuxeoUploadConnector extends NuxeoConnector {
	
	private String attachment;
	private String path;
	private String title;
	private String type;
 	@SuppressWarnings("rawtypes")
	private ArrayList properties;
	
	private Logger logger = Logger.getLogger(this.getClass().getName());
	
 	@SuppressWarnings("rawtypes")
	@Override
	public void setInputParameters(final Map<String, Object> parameters) {
		super.setInputParameters(parameters);
		attachment = (String) parameters.get("attachment");
		path = (String) parameters.get("path");
		title = (String) parameters.get("title");
		type = (String) parameters.get("type");
	 	properties = (ArrayList) parameters.get("properties");
	
	}

		
	@SuppressWarnings("rawtypes")
	@Override
	protected void executeConnector(final Session session) throws Exception {
		logger.info("Starting nuxeo upload connector for document " + attachment);
		
		
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
		logger.info("Mime type: " + mimeType);
		DocumentService documentService = session.getAdapter(DocumentService.class);
		org.nuxeo.ecm.automation.client.model.Document nxDocument = new Document(title, type);
		
		path = checkParentPath(path); // Correct path if user forgot the last file-separator
		nxDocument.set("dc:title", title);
		for(Object o : properties){
			logger.info(o.getClass().getName());
			ArrayList row = (ArrayList)o;
		    nxDocument.set((String)row.get(0),(String)row.get(1));
		}
		nxDocument = documentService.createDocument(path, nxDocument);
		
		File tempFile = new File(fileName);
		FileOutputStream fos = new FileOutputStream(tempFile);

		int data;
		while((data=inputStream.read())!=-1){
			char currentChar = (char)data;
			fos.write(currentChar);
		}
		fos.flush();
		fos.close();
		FileBlob blob = new FileBlob(tempFile); 
		//StreamBlob blob = new StreamBlob(inputStream, fileName, mimeType);
		documentService.setBlob(nxDocument, blob);
		tempFile.delete();
		documentService.update(nxDocument);
		if(logger.isLoggable(Level.INFO)){
			logger.info(String.format("File %s sent to %s", fileName, path));
		}
	}
         
    
	private static String checkParentPath(String path){
		return path.charAt(path.length()-1)!='/'?path+"/":path;
	}

}
