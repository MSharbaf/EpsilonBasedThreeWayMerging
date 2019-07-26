package ir.ui.se.mdserg.e3mp.handlers;

import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.BasicExtendedMetaData;
import org.eclipse.emf.ecore.util.ExtendedMetaData;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.w3c.dom.Document;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;

import ir.ui.se.mdserg.e3mp.wizards.*;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class MatchingHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		
		try {
			MatchingWizard wizard = new MatchingWizard() ; 
			wizard.setWindowTitle("Matching Process");
			WizardDialog dialog = new WizardDialog(window.getShell(), wizard);

			if (dialog.open() == Window.OK) {
				// Doing something 
				return true ; 
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		return null;
	}

	
	
	
	
	
/*	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		MessageDialog.openInformation(
				window.getShell(),
				"E3mp",
				"Hello, Welcome to Equivalent Matching Process (EquiMatchProc)");
		return null;
	}
*/
	
//	public Object execute(ExecutionEvent event) throws ExecutionException {
//		HandlerUtil.getActiveWorkbenchWindowChecked(event);
//		
//		IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
//        IProject[] projects = workspaceRoot.getProjects();
//        
//        System.out.println("Sta"); 
//        
//        for(int i=0; i<projects.length ; i++)
//        	System.out.println(projects[i].getName());
//        
//        System.out.println("Projects") ; 
//	
//		try {
//			
//			//String projectName =JOptionPane.showInputDialog("Insert the name of your project:");
//			//IProject project = ResourceHelper.createProject(projectName);
//			
//			IProject project = projects[0] ; 
//			if (project.exists())
//			{
//			
//				//loading weaving metamodel
//				URL urll = FileLocator.find(Platform.getBundle("ir.ui.se.mdserg.e3mp"), new Path("resources/metamodels/GeneralWeaving.ecore"), null);
//				
//				//register metamodel epackage
//				java.net.URI javaURI = urll.toURI();
//				org.eclipse.emf.common.util.URI uri = org.eclipse.emf.common.util.URI.createURI(javaURI.toString());
//				registerEPackages(uri);
//				
//				new EcoreFileChooser(project);
//			    project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
//			}
//			
//		} catch (CoreException  | URISyntaxException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return null;
//	}
	
	public String getStringFromXMLDoc(Document doc) throws TransformerException{
		String content="";
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		StringWriter writer = new StringWriter();
		transformer.transform(new DOMSource(doc), new StreamResult(writer));
		 content = writer.getBuffer().toString();
		return content;
	}
	
	public void registerEPackages(org.eclipse.emf.common.util.URI uriOfYourModel)
	{
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put(
			    "ecore", new EcoreResourceFactoryImpl());
		
			ResourceSet rs = new ResourceSetImpl();
			// enable extended metadata
			final ExtendedMetaData extendedMetaData = new BasicExtendedMetaData(rs.getPackageRegistry());
			rs.getLoadOptions().put(XMLResource.OPTION_EXTENDED_META_DATA,
			    extendedMetaData);
			
			
			
			Resource r = rs.getResource(uriOfYourModel, true);
			EObject eObject = r.getContents().get(0);
			if (eObject instanceof EPackage) {
			    EPackage p = (EPackage)eObject;
			    rs.getPackageRegistry().put(p.getNsURI(), p);
			    rs.getPackageRegistry().putIfAbsent(p.getNsURI(), p);
			   EPackage.Registry.INSTANCE.put(p.getNsURI(), p);
			    
			 boolean test= EPackage.Registry.INSTANCE.containsKey(p.getNsURI());
			System.out.print(test+"\t");
			 System.out.println("registery: "+p.getName());
			}
			else {System.out.println("registery2: "+eObject.toString());}
	}
	
	public Resource createXmiInstance (String metamodel,String model)
	{
		
		ResourceSet resourceSet = new ResourceSetImpl(); 
		
		Resource myMetaModel= resourceSet.getResource(org.eclipse.emf.common.util.URI.createFileURI(metamodel), true);
		
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put(
			    "xmi", new XMIResourceFactoryImpl());
		
		EPackage univEPackage = (EPackage) myMetaModel.getContents().get(0);
		resourceSet.getPackageRegistry().put("", univEPackage);
		
		Resource myModel = resourceSet.getResource( org.eclipse.emf.common.util.URI.createURI(model), true);
	
	return myModel;
	}
	
}
