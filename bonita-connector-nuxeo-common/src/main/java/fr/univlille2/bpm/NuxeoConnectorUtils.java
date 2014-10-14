/**
 * 
 */
package fr.univlille2.bpm;

import java.util.Collections;
import java.util.List;

import org.nuxeo.ecm.automation.client.adapters.DocumentService;
import org.nuxeo.ecm.automation.client.model.Document;
import org.nuxeo.ecm.automation.client.model.PathRef;

/**
 * @author acordier Jul 24, 2014 NuxeoConnectorUtils.java
 */
public class NuxeoConnectorUtils {

	public static String trail(String path) {
		return path.charAt(path.length() - 1) != '/' ? path + "/" : path;
	}

	public static String unTrail(String path) {
		return path.charAt(path.length() - 1) == '/' ? path.substring(0,
				path.length() - 1) : path;
	}

	public static String getPermanentUrl(String baseUrl, Document doc) {
		return String.format("%s/nxdoc/default/%s/view_documents", baseUrl,
				doc.getId());
	}

	public static List<?> nullToEmpty(List<?> input) {
		return input == null ? Collections.EMPTY_LIST : input;
	}
	
	/* throws exception if found document has a deleted lifecycle state */
	public static Document getDocumentNotDeleted(
			DocumentService documentService, String path) throws Exception {	
		try{
			Document document = documentService.getDocument(new PathRef(path));
			NuxeoConnector.logger.info(document.getState());
			if(document.getState().equals("deleted")) throw new Exception();
			return document;
		}catch(Exception e){
			throw e;
		}
	}

}
