package ir.ui.se.mdserg.e3mp.helper;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.epsilon.emc.emf.EmfUtil;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class CreateModelinkFiles {
	
	public CreateModelinkFiles(IProject project, String baseAddress, String versionAddress,/*IFile sourceName, 
			IFile targetName,*/ String sourceContent, String targetContent)
	{
		try {
			//register GeneralWeaving metamodel epackage
			try {
				URL GWUrl = FileLocator.find(Platform.getBundle("ir.ui.se.mdserg.e3mp"), new Path("resources/metamodels/GeneralWeaving.ecore"), null);
				java.net.URI javaURI = GWUrl.toURI();
				org.eclipse.emf.common.util.URI GWuri = org.eclipse.emf.common.util.URI.createURI(javaURI.toString());
				
				EmfUtil.register(GWuri, EPackage.Registry.INSTANCE);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			// Create a weaving model (copy it from resource)
			URL MMUrl = FileLocator.find(Platform.getBundle("ir.ui.se.mdserg.e3mp"), new Path("resources/metamodels/weavingModel.xmi"), null);
			String contents =readFile(MMUrl);
			
			File newVersion = new File(versionAddress);
			IPath versionPath = new Path(newVersion.getAbsolutePath());
			IFile versionFile = ResourcesPlugin.getWorkspace().getRoot().getFile(versionPath);
			
			String tName = versionFile.getName() ;
			String[] wnParts = tName.split("\\.") ; 
			String weavingName = wnParts[0] + "weavingModel.xmi" ; 
			
			IFile weavingModel = ResourceHelper.createFile(project, weavingName, contents);
			
			String weavingAddress = "\\"+project.getName()+"\\"+weavingModel.getName();
	
			String directory = project.getLocation().toString(); 
			directory = directory.replace("/", "\\") ; 
			String BA = "\\"+project.getName()+baseAddress.replace(directory, "");
			String VA = "\\"+project.getName()+versionAddress.replace(directory, "");
		//	String sourceAddress1 = "\\"+project.getName()+"\\"+versionFile.getName();
		//	String targetAddress1 = "\\"+project.getName()+"\\"+baseFile.getName();
		
			String doc = getStringFromXMLDoc(createModelinkFile(BA,weavingAddress,VA));
			IFile fModeLink = ResourceHelper.createFile(project, wnParts[0]+"weavingModel.modelink", doc);
	
			//opening the modelink editor
			project.refreshLocal(IProject.DEPTH_ONE, null);
			Display.getDefault().asyncExec(new Runnable() {
			    @Override
			    public void run() {
			    	try {
						IDE.openEditor(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(), fModeLink);
					} catch (PartInitException e) {
						e.printStackTrace();
					}
			    }
			});
		} catch (CoreException | TransformerException e) {
			e.printStackTrace();
		}
	}
	
	
	public Document createModelinkFile(String leftMetamodelUri,String weavingModel ,String rightMetamodelUri) 
	{
		DocumentBuilderFactory icFactory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder icBuilder;
	    Document doc=null; 
	    try {
	        icBuilder = icFactory.newDocumentBuilder();
	         doc = icBuilder.newDocument();
	        
	        
	        Element mainRootElement = doc.createElement("modelink");
	        mainRootElement.setAttribute("threeWay", "true");
	        mainRootElement.setAttribute("forceExeedL", "true");
	        mainRootElement.setAttribute("forceExeedM", "true");
	        mainRootElement.setAttribute("forceExeedR", "true");
	        doc.appendChild(mainRootElement);

	        // append child elements to root element
	        mainRootElement.appendChild(getModelinkChild(doc,leftMetamodelUri,"LEFT"));
	        mainRootElement.appendChild(getModelinkChild(doc, weavingModel,"MIDDLE"));
	        mainRootElement.appendChild(getModelinkChild(doc, rightMetamodelUri,"RIGHT"));

	     // output DOM XML to console 
	        Transformer transformer = TransformerFactory.newInstance().newTransformer();
	        transformer.setOutputProperty(OutputKeys.INDENT, "yes"); 
	        
	        return doc;

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
		return  doc;
	}// end of create modelink function

	private Node getModelinkChild (Document doc,String path, String position)
	{
		Element e = doc.createElement("model");
		e.setAttribute("path", path);
		e.setAttribute("position", position);
		return e;
	}

	private String readFile(URL url)
	{
		String out="";
		try {
		    InputStream inputStream = url.openConnection().getInputStream();
		    BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
		    String inputLine;
		 
		    while ((inputLine = in.readLine()) != null) {
		        //System.out.println(inputLine);
		    	out +=inputLine+"\n";
		    }
		    in.close();
	 
		} catch (IOException e) {
		    e.printStackTrace();
		}
		return out;
	}
	
	private String getStringFromXMLDoc(Document doc) throws TransformerException{
		String content="";
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		StringWriter writer = new StringWriter();
		transformer.transform(new DOMSource(doc), new StreamResult(writer));
		 content = writer.getBuffer().toString();
		return content;
	}
	
}
