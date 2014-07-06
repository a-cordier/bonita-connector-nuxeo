package fr.univlille2.bpm;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
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
//	private ArrayList properties;
	
	private Logger logger = Logger.getLogger(this.getClass().getName());
	
//	@SuppressWarnings("unchecked")
	@Override
	public void setInputParameters(final Map<String, Object> parameters) {
		super.setInputParameters(parameters);
		attachment = (String) parameters.get("attachment");
		path = (String) parameters.get("path");
		title = (String) parameters.get("title");
		type = (String) parameters.get("type");
	//	properties = (ArrayList) parameters.get("properties");
	
	}

		
	@Override
	protected void executeConnector(final Session session) throws Exception {
		logger.info("Starting nuxeo upload connector for file " + attachment);
		
		
		ProcessAPI processAPI = getAPIAccessor().getProcessAPI();
		long processInstanceId = getExecutionContext().getProcessInstanceId();
		org.bonitasoft.engine.bpm.document.Document srcDocument = 
				processAPI.getLastDocument(processInstanceId, this.attachment);	
		
		if(srcDocument==null){
			throw new ConnectorException("Unable to retrieve process a attachment referenced by " + attachment);
		}		
		final byte[] content = processAPI.getDocumentContent(srcDocument.getContentStorageId());
		final ByteArrayInputStream inputStream = new ByteArrayInputStream(content);
		String fileName=srcDocument.getContentFileName();
		String mimeType = srcDocument.getContentMimeType();
		DocumentService documentService = session.getAdapter(DocumentService.class);
		org.nuxeo.ecm.automation.client.model.Document nxDocument = new Document(fileName, type);
		
		path = checkParentPath(path); // Correct path if user forgot the last file-separator
		nxDocument.set("dc:title", title);
//		for(Object o : properties){
//			//nxDocument.set(xPath, properties.get(xPath));
//			logger.warning(o.toString());
//		}
		nxDocument = documentService.createDocument(path, nxDocument);
		
		File tempFile = File.createTempFile(fileName, ".tmp");
		FileOutputStream fos = new FileOutputStream(tempFile);

		int data;
		while((data=inputStream.read())!=-1){
			char currentChar = (char)data;
			fos.write(currentChar);
		}
		fos.flush();
		fos.close();
		
		FileBlob fileBlob = new FileBlob(tempFile);  
		fileBlob.setFileName(fileName);
		fileBlob.setMimeType(mimeType);
		documentService.setBlob(nxDocument, fileBlob);
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
