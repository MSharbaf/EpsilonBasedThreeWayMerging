package ir.ui.se.mdserg.e3mp.wizards;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.beans.XMLDecoder;
import java.io.BufferedInputStream;

import org.eclipse.core.resources.IProject;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EPackage.Registry;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.provider.EcoreItemProviderAdapterFactory;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.BasicExtendedMetaData;
import org.eclipse.emf.ecore.util.ExtendedMetaData;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;
import org.eclipse.emf.edit.provider.ReflectiveItemProviderAdapterFactory;
import org.eclipse.emf.edit.provider.resource.ResourceItemProviderAdapterFactory;
import org.eclipse.epsilon.common.util.StringProperties;
import org.eclipse.epsilon.ecl.EclModule;
import org.eclipse.epsilon.ecl.dom.MatchRule;
import org.eclipse.epsilon.ecl.trace.Match;
import org.eclipse.epsilon.ecl.trace.MatchTrace;
import org.eclipse.epsilon.emc.emf.EmfModel;
import org.eclipse.epsilon.emc.emf.EmfUtil;
import org.eclipse.epsilon.emc.emf.InMemoryEmfModel;
import org.eclipse.epsilon.eml.EmlModule;
import org.eclipse.epsilon.eol.exceptions.EolRuntimeException;
import org.eclipse.epsilon.eol.exceptions.models.EolModelElementTypeNotFoundException;
import org.eclipse.epsilon.eol.exceptions.models.EolModelLoadingException;
import org.eclipse.epsilon.eol.models.IModel;
import org.eclipse.epsilon.eol.models.IRelativePathResolver;
import org.eclipse.epsilon.evl.EvlModule;
import org.eclipse.epsilon.evl.IEvlFixer;
import org.eclipse.epsilon.evl.IEvlModule;
import org.eclipse.epsilon.evl.dt.views.ValidationViewFixer;
import org.eclipse.epsilon.evl.execute.FixInstance;
import org.eclipse.epsilon.evl.execute.UnsatisfiedConstraint;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.resource.UMLResource;
import org.eclipse.uml2.uml.resources.util.UMLResourcesUtil;

import ir.ui.se.mdserg.e3mp.helper.*;

public class MergingWizard extends Wizard {
	public IProject selectedProject ; 
	MergingModelSelectionFinishPage mergingModelSelectionFinishPage;
	InferringScriptSelectionPage InferringScriptSelectionPage; 
	MergingScriptSelectionPage mergingScriptSelectionPage; 
	MergingModelSelectionPage mergingModelSelectionPage;
	ProjectSelectionWizardPage projectSelectionPage ;  
    public MatchTrace matchTraces ; 
	public ArrayList<MatchMember> MatchList1, MatchList2, InferedMatchList, UnEquMatchList, TempMatchList1; 
	public EmfModel bModel = null ; 
	public EmfModel modelV1 = null ; 
	public EmfModel modelV2 = null ; 
	public EmfModel tModel = null ; 
	

    public void addPages() {
    		projectSelectionPage = new ProjectSelectionWizardPage("Project Selection"); 
    		addPage(projectSelectionPage) ; 
    	
    		mergingModelSelectionPage = new MergingModelSelectionPage("Model Selection");
    		projectSelectionPage.MergingSelectionPage = mergingModelSelectionPage ; 
    		addPage(mergingModelSelectionPage);
    		
    		InferringScriptSelectionPage = new InferringScriptSelectionPage("Match files and Conflict Script Selection");
    		mergingModelSelectionPage.InferringScriptSelectionPage = InferringScriptSelectionPage ; 
//    		InferringScriptSelectionPage.MergingModelSelectionPage = mergingModelSelectionPage ; 
    		addPage(InferringScriptSelectionPage); 
    		
    		mergingScriptSelectionPage = new MergingScriptSelectionPage("Script Selection");
    		InferringScriptSelectionPage.MergingScriptSelectionPage = mergingScriptSelectionPage ; 
    		addPage(mergingScriptSelectionPage);   
    		
    		mergingModelSelectionFinishPage = new MergingModelSelectionFinishPage("Models Merging Finished"); 
    		addPage(mergingModelSelectionFinishPage);
    }
    
	public boolean canFinish()
	{
		if(getContainer().getCurrentPage() == projectSelectionPage || getContainer().getCurrentPage() == mergingModelSelectionPage 
				|| getContainer().getCurrentPage() == InferringScriptSelectionPage || getContainer().getCurrentPage() == mergingScriptSelectionPage)
			return false;
		else
			return true;
	}
	
	@Override
	public boolean performFinish() {
		// TODO Auto-generated method stub
//		if((mergingScriptSelectionPage.ML1ScriptFC.path == null) || (mergingScriptSelectionPage.ML2ScriptFC.path == null) 
//				|| mergingScriptSelectionPage.EclScriptFC.path == null || mergingScriptSelectionPage.EmlScriptFC.path == null){
//			mergingModelSelectionPage.setDescription("You must select all the required files ...");
//		}
//		else
//		{	
//			//************** Register Meta Model ***********	
//			if(mergingModelSelectionPage.BaseModelFC.path.endsWith(".uml") == false)
//			{
//				try {
//					registerMetamodel(mergingModelSelectionPage.MetaModelFC.path);
////					EmfUtil.register(URI.createFileURI(mergingModelSelectionPage.MetaModelFC.path), EPackage.Registry.INSTANCE);
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//			
//			//************* Read Input Models (version1 and version2) ***********
////			EmfModel bModel = null ; 
////			EmfModel modelV1 = null ; 
////			EmfModel modelV2 = null ; 
////			EmfModel tModel = null ; 
//
//			ComposedAdapterFactory composedAdapterFactory ; 
//		    IItemLabelProvider itemLabelProvider  ;  
//		    ITreeItemContentProvider treeItemContentProvider ;
//
//			ArrayList<AdapterFactory> factories = new ArrayList<AdapterFactory>();
//			factories.add(new ResourceItemProviderAdapterFactory());
//			factories.add(new EcoreItemProviderAdapterFactory());
//			factories.add(new ReflectiveItemProviderAdapterFactory());
//			
//			if(mergingModelSelectionPage.BaseModelFC.path.endsWith(".uml") == true)
//				composedAdapterFactory = new ComposedAdapterFactory(ComposedAdapterFactory.Descriptor.Registry.INSTANCE);
//			else
//				composedAdapterFactory = new ComposedAdapterFactory(factories);
//	    	AdapterFactory adapterFactory = composedAdapterFactory;
//		
//			
//			if(mergingModelSelectionPage.BaseModelFC.path.endsWith(".uml")){
//				try {
//					bModel = getUmlModel("Base", new File(mergingModelSelectionPage.BaseModelFC.path));
//					modelV1 = getUmlModel("Version1", new File(mergingModelSelectionPage.Version1ModelFC.path)) ; 
//					modelV2 = getUmlModel("Version2", new File(mergingModelSelectionPage.Version2ModelFC.path));
//					tModel = getUmlModel("Target", new File(mergingModelSelectionPage.TargetModelFC.path)) ; 
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}		
//			
//			}else{
////				try {
////					bModel = getInMemoryEmfModel("Base",new File(mergingModelSelectionPage.BaseModelFC.path),
////							mergingModelSelectionPage.MetaModelFC.path);
////					modelV1 = getInMemoryEmfModel("Version1",new File(mergingModelSelectionPage.Version1ModelFC.path),
////							mergingModelSelectionPage.MetaModelFC.path);
////					modelV2 = getInMemoryEmfModel("Version2",new File(mergingModelSelectionPage.Version2ModelFC.path),
////							mergingModelSelectionPage.MetaModelFC.path);
////					tModel = getInMemoryEmfModel("Target",new File(mergingModelSelectionPage.TargetModelFC.path),
////							mergingModelSelectionPage.MetaModelFC.path);
////				} catch (IOException e) {
////					// TODO Auto-generated catch block
////					e.printStackTrace();
////				}
//				
//				try {
//					bModel = getEmfModel("Base",mergingModelSelectionPage.BaseModelFC.path, mergingModelSelectionPage.MetaModelFC.path, true, false);
//					modelV1 = getEmfModel("Version1",mergingModelSelectionPage.Version1ModelFC.path, mergingModelSelectionPage.MetaModelFC.path, true, false);
//					modelV2 = getEmfModel("Version2",mergingModelSelectionPage.Version2ModelFC.path, mergingModelSelectionPage.MetaModelFC.path, true, false);
//					tModel = getEmfModel("Target",mergingModelSelectionPage.TargetModelFC.path, mergingModelSelectionPage.MetaModelFC.path, false, true);
//
//				} catch (EolModelLoadingException | URISyntaxException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//			bModel.getAliases().add("Source"); 	
//			modelV1.getAliases().add("Source");
//			modelV2.getAliases().add("Source");
//			tModel.getAliases().add("Target");
//			tModel.setStoredOnDisposal(true);
			
//			//************* Read Match Files ****************		
//			try {
//			    FileInputStream inputStream=new FileInputStream(mergingScriptSelectionPage.ML1ScriptFC.path);
//			    ObjectInputStream objectInputStream =new ObjectInputStream(inputStream);
//			    MatchList1 = (ArrayList<MatchMember>) objectInputStream.readObject();
//			} catch (FileNotFoundException e) {
//			    e.printStackTrace();
//			} catch (IOException e) {
//			    e.printStackTrace();
//			} catch (ClassNotFoundException e) {
//			    e.printStackTrace();
//			}
//			
//			try {
//			    FileInputStream inputStream=new FileInputStream(mergingScriptSelectionPage.ML2ScriptFC.path);
//			    ObjectInputStream objectInputStream =new ObjectInputStream(inputStream);
//			    MatchList2 = (ArrayList<MatchMember>) objectInputStream.readObject();
//			} catch (FileNotFoundException e) {
//			    e.printStackTrace();
//			} catch (IOException e) {
//			    e.printStackTrace();
//			} catch (ClassNotFoundException e) {
//			    e.printStackTrace();
//			}
//			
//			//********** Inferring with MatchLists and Reconcile Two models *********    	
//			InferedMatchList = new ArrayList<MatchMember>() ; 
//			UnEquMatchList = new ArrayList<MatchMember>() ; 
//			TempMatchList1 = new ArrayList<MatchMember>() ; 
//			for(int i=0; i<MatchList1.size(); i++)
//			{
//				int index = -1 ; 
//				for(int j=0; j<MatchList2.size(); j++)
//				{
//					if(MatchList1.get(i).getLeftText().equals(MatchList2.get(j).getLeftText()) && 
//							MatchList1.get(i).getLeftParent().equals(MatchList2.get(j).getLeftParent())){
//						InferedMatchList.add(new MatchMember(MatchList1.get(i).getRightParent(), MatchList1.get(i).getRightText(),MatchList2.get(j).getRightParent(), MatchList2.get(j).getRightText())) ;
//						
//						if(MatchList1.get(i).getRightText().equals(MatchList2.get(j).getRightText()) == false){
//							UnEquMatchList.add(new MatchMember(MatchList1.get(i).getRightParent(), MatchList1.get(i).getRightText(),MatchList2.get(j).getRightParent(), MatchList2.get(j).getRightText())) ;
//						}
//						index = j ; 
//						break ; 
//					}
//				}
//				if(index != -1)
//					MatchList2.remove(index) ;
//				else
//					TempMatchList1.add(MatchList1.get(i)) ;	
//			}
//			
//			// Show to user to select one of two options 
//			IWorkbench wb = PlatformUI.getWorkbench();
//		   	IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
//			EditEquivalentTreeView EETV = new EditEquivalentTreeView(win.getShell(), modelV1, modelV2, 
//					mergingModelSelectionPage.MetaModelFC.path, mergingModelSelectionPage.selectedProject , UnEquMatchList) ;
//			
//			EETV.open() ; 
//
//			for(int i=0; i<TempMatchList1.size(); i++){		    	
//				String classType1 = TempMatchList1.get(i).getLeftText().split(" ")[0] ; 
//				classType1 = classType1.replace("<","") ;
//				classType1 = classType1.replace(">","") ;
//				try {					
//					for (EObject modelV1Element : modelV1.getAllOfKind(classType1)) {
//					    itemLabelProvider = (IItemLabelProvider)adapterFactory.adapt(modelV1Element, IItemLabelProviderClass);
//					    String Text =  itemLabelProvider.getText(modelV1Element) ; 
//					    		   
//					    treeItemContentProvider = (ITreeItemContentProvider)adapterFactory.adapt(modelV1Element, ITreeItemContentProviderClass);
//					    Object Parent = treeItemContentProvider.getParent(modelV1Element) ; 
//					    itemLabelProvider = (IItemLabelProvider)adapterFactory.adapt(Parent, IItemLabelProviderClass);
//					    String ParentText = itemLabelProvider.getText(Parent);
//					    
//					    if(TempMatchList1.get(i).getLeftText().equals(Text) && (TempMatchList1.get(i).getLeftParent().equals("File") || TempMatchList1.get(i).getLeftParent().equals(ParentText))){
//					    	try {
//								modelV1.deleteElement(modelV1Element);
//							} catch (EolRuntimeException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							} 
//					    	break ; 
//					    }
//					}
//				} catch (EolModelElementTypeNotFoundException e) {
//				// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
			
//			for(int i=0; i<MatchList2.size(); i++){		    	
//				String classType2 = MatchList2.get(i).getLeftText().split(" ")[0] ; 
//				classType2 = classType2.replace("<","") ;
//				classType2 = classType2.replace(">","") ;
//				try {					
//					for (EObject modelV2Element : modelV2.getAllOfKind(classType2)) {
//					    itemLabelProvider = (IItemLabelProvider)adapterFactory.adapt(modelV2Element, IItemLabelProviderClass);
//					    String Text =  itemLabelProvider.getText(modelV2Element) ; 
//					    		   
//					    treeItemContentProvider = (ITreeItemContentProvider)adapterFactory.adapt(modelV2Element, ITreeItemContentProviderClass);
//					    Object Parent = treeItemContentProvider.getParent(modelV2Element) ; 
//					    itemLabelProvider = (IItemLabelProvider)adapterFactory.adapt(Parent, IItemLabelProviderClass);
//					    String ParentText = itemLabelProvider.getText(Parent);
//					    
//					    if(MatchList2.get(i).getLeftText().equals(Text) && (MatchList2.get(i).getLeftParent().equals("File") || MatchList2.get(i).getLeftParent().equals(ParentText))){
//					    	try {
//								modelV2.deleteElement(modelV2Element);
//							} catch (EolRuntimeException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							} 
//					    	break ; 
//					    }
//					}
//				} catch (EolModelElementTypeNotFoundException e) {
//				// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//			
//			performAllEdits(modelV1, modelV2, UnEquMatchList, EETV.Decision) ;
//			
//			System.out.println("All tasks are done ...") ; 
//			System.out.println("Version1:") ;
//			for (Object modelV1Element : modelV1.allInstances()) {
//			    itemLabelProvider = (IItemLabelProvider)adapterFactory.adapt(modelV1Element, IItemLabelProviderClass);
//			    String leftText =    itemLabelProvider.getText(modelV1Element) ; 
//			    		   
//			    treeItemContentProvider = (ITreeItemContentProvider)adapterFactory.adapt(modelV1Element, ITreeItemContentProviderClass);
//			    Object leftParent = treeItemContentProvider.getParent(modelV1Element) ; 
//			    itemLabelProvider = (IItemLabelProvider)adapterFactory.adapt(leftParent, IItemLabelProviderClass);
//			    String leftParentText = itemLabelProvider.getText(leftParent);
//			    
//			    System.out.println(leftText + "  : " + leftParentText); 
//			}
//
//			
//			System.out.println("Version2:") ;
//			for (Object modelV2Element : modelV2.allInstances()) {
//			    itemLabelProvider = (IItemLabelProvider)adapterFactory.adapt(modelV2Element, IItemLabelProviderClass);
//			    String leftText =    itemLabelProvider.getText(modelV2Element) ; 
//			    		   
//			    treeItemContentProvider = (ITreeItemContentProvider)adapterFactory.adapt(modelV2Element, ITreeItemContentProviderClass);
//			    Object leftParent = treeItemContentProvider.getParent(modelV2Element) ; 
//			    itemLabelProvider = (IItemLabelProvider)adapterFactory.adapt(leftParent, IItemLabelProviderClass);
//			    String leftParentText = itemLabelProvider.getText(leftParent);
//			    
//			    System.out.println(leftText + "  : " + leftParentText); 
//			}
//			
			
//			//********** Conflict Detection & Resolution with EVL Script ***********
//			if(mergingScriptSelectionPage.EvlScriptFC.path != null)
//				try {
//					Validate(bModel, modelV1, modelV2, mergingScriptSelectionPage.EvlScriptFC.path);
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
			
//			//********** Merging with EML Script *********
//				//Compare left and right models 
//				MatchTrace MTF = null ;
//				try {
//					MTF = Comparison(modelV1, modelV2, mergingScriptSelectionPage.EclScriptFC.path);
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} 
//				MatchTrace MatchedF = MTF.getReduced() ; 
//				MatchedF = delNullmatch(MatchedF.getMatches());
//			
//				// Merge and Save merged model 
//				try {
//					Merge(modelV1, modelV2, tModel, MatchedF, mergingScriptSelectionPage.EmlScriptFC.path) ;
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} 
//				tModel.dispose(); 
//			
			System.out.println("Finish") ; 
		    
			return true ; 
//		}	
//		return false;
	}
//	
//	public InMemoryEmfModel getUmlModel(String name, File file) throws Exception {
//		ResourceSet set = new ResourceSetImpl();
//		UMLResourcesUtil.init(set);
//		set.getPackageRegistry().put(UMLPackage.eNS_URI, UMLPackage.eINSTANCE);
//		set.getResourceFactoryRegistry().getExtensionToFactoryMap().
//		         put(UMLResource.FILE_EXTENSION, UMLResource.Factory.INSTANCE);
//	
//		Resource r = set.getResource(URI.createFileURI(file.getAbsolutePath()), true);
//		r.load(null);
//		
//		Collection<EPackage> ePackages = new ArrayList<EPackage>();
//		for (Object ePackage : set.getPackageRegistry().values()) {
//			ePackages.add((EPackage) ePackage);
//		}
//		return new InMemoryEmfModel(name, r, ePackages);
//	}
//		
//	public InMemoryEmfModel getInMemoryEmfModel(String name, File modelFile, String metamodelPath) throws IOException
//	{
//		ResourceSet rs = new ResourceSetImpl();
//		rs.getPackageRegistry().put(EcorePackage.eINSTANCE.getNsURI(), EcorePackage.eINSTANCE);
//		rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", new EcoreResourceFactoryImpl()) ; //XMIResourceFactoryImpl());
//		Resource r = null;
//		EPackage metamodel = null;
//		
//		java.net.URI javaURI = new File(metamodelPath).toURI() ; 
////		org.eclipse.emf.common.util.URI uri = org.eclipse.emf.common.util.URI.createURI(javaURI.toString());
//		
//			r = rs.createResource(URI.createURI(javaURI.toString()));
//			r.load(Collections.emptyMap());
//			metamodel = (EPackage) r.getContents().get(0);
//
//			rs = new ResourceSetImpl();
//			rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", new EcoreResourceFactoryImpl()) ;
//					//XMIResourceFactoryImpl());
//			
//			Registry packageRegistry = rs.getPackageRegistry();
//			EPackage pack = (EPackage) packageRegistry.get(EcorePackage.eINSTANCE.getNsURI());
//			if (!(pack instanceof EcorePackage)) {
//				packageRegistry.put(EcorePackage.eINSTANCE.getNsURI(), EcorePackage.eINSTANCE);
//			}
//			
//			rs.getPackageRegistry().put(metamodel.getNsURI(), metamodel);
//			r = rs.createResource(URI.createURI(modelFile.toURI().toString()));
//			r.load(Collections.emptyMap());
//		
//			InMemoryEmfModel model = new InMemoryEmfModel(name, r, metamodel);
//			
//			model.setMetamodelFile(URI.createFileURI(metamodelPath).toString());
//			model.setMetamodelFileBased(true);
//
//		return model ; 
//	}
//	
//	protected EmfModel getEmfModel(String name, String model, 
//			String metamodel, boolean readOnLoad, boolean storeOnDisposal) 
//					throws EolModelLoadingException, URISyntaxException {
//		EmfModel emfModel = new  EmfModel();
//		StringProperties properties = new StringProperties();
//		properties.put(EmfModel.PROPERTY_NAME, name);
//		properties.put(EmfModel.PROPERTY_FILE_BASED_METAMODEL_URI,URI.createFileURI(metamodel).toString());
//		properties.put(EmfModel.PROPERTY_MODEL_URI,URI.createFileURI(model).toString());
//		properties.put(EmfModel.PROPERTY_READONLOAD, readOnLoad + "");
//		properties.put(EmfModel.PROPERTY_STOREONDISPOSAL, 
//				storeOnDisposal + "");
//		emfModel.load(properties, (IRelativePathResolver) null);
//		return emfModel;
//	}
//	
//	private void registerMetamodel(String MetaModelPath)
//	{
//		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put(
//		    "*", new EcoreResourceFactoryImpl());
//
//		ResourceSet rs = new ResourceSetImpl();
//		final ExtendedMetaData extendedMetaData = new BasicExtendedMetaData(EPackage.Registry.INSTANCE);
//		rs.getLoadOptions().put(XMLResource.OPTION_EXTENDED_META_DATA,
//		    extendedMetaData);
//
//		java.net.URI javaURI = new File(MetaModelPath).toURI() ; 
//		org.eclipse.emf.common.util.URI uri = org.eclipse.emf.common.util.URI.createURI(javaURI.toString());
//		
//		Resource r = rs.getResource(uri, true);
//		EObject eObject = r.getContents().get(0);
//		if (eObject instanceof EPackage) {
//		    EPackage p = (EPackage)eObject;
//		    EPackage.Registry.INSTANCE.put(p.getNsURI(), p);
//		}
//		
//		Registry packageRegistry = rs.getPackageRegistry();
//		EPackage pack = (EPackage) packageRegistry.get(EcorePackage.eINSTANCE.getNsURI());
//		if (!(pack instanceof EcorePackage)) {
//			packageRegistry.put(EcorePackage.eINSTANCE.getNsURI(), EcorePackage.eINSTANCE);
//		}
//	}
//
//	private void performAllEdits(EmfModel V1, EmfModel V2, ArrayList<MatchMember> Matches, int[] Decision)
//	{    	
//		ComposedAdapterFactory composedAdapterFactory ; 
//	    IItemLabelProvider itemLabelProvider  ;  
//	    ITreeItemContentProvider treeItemContentProvider ;
//
//		ArrayList<AdapterFactory> factories = new ArrayList<AdapterFactory>();
//		factories.add(new ResourceItemProviderAdapterFactory());
//		factories.add(new EcoreItemProviderAdapterFactory());
//		factories.add(new ReflectiveItemProviderAdapterFactory());
//		
//		if(mergingModelSelectionPage.BaseModelFC.path.endsWith(".uml") == true)
//			composedAdapterFactory = new ComposedAdapterFactory(ComposedAdapterFactory.Descriptor.Registry.INSTANCE);
//		else
//			composedAdapterFactory = new ComposedAdapterFactory(factories);
//    	AdapterFactory adapterFactory = composedAdapterFactory;
//	
//    	ArrayList<EObject> tmodelV1ElementList = new ArrayList<EObject>();
//    	ArrayList<EObject> tmodelV2ElementList = new ArrayList<EObject>();
//		for(int i=0; i<Matches.size(); i++){
//	    	EObject	tmodelV1Element = null , tmodelV2Element = null;
//	    	
//			String classType1 = Matches.get(i).getLeftText().split(" ")[0] ; 
//			classType1 = classType1.replace("<","") ;
//			classType1 = classType1.replace(">","") ;
//			try {
////				Collection<EObject> tes = V1.getAllOfKind(classType1) ; 
//				
//				for (EObject modelV1Element : V1.getAllOfKind(classType1)) {
//				    itemLabelProvider = (IItemLabelProvider)adapterFactory.adapt(modelV1Element, IItemLabelProviderClass);
//				    String Text =  itemLabelProvider.getText(modelV1Element) ; 
//				    		   
//				    treeItemContentProvider = (ITreeItemContentProvider)adapterFactory.adapt(modelV1Element, ITreeItemContentProviderClass);
//				    Object Parent = treeItemContentProvider.getParent(modelV1Element) ; 
//				    itemLabelProvider = (IItemLabelProvider)adapterFactory.adapt(Parent, IItemLabelProviderClass);
//				    String ParentText = itemLabelProvider.getText(Parent);
//				    
//				    if(Matches.get(i).getLeftText().equals(Text) && (Matches.get(i).getLeftParent().equals("File") || Matches.get(i).getLeftParent().equals(ParentText))){
//				    	tmodelV1Element = modelV1Element ; 
//				    	break ; 
//				    }
//				}
//			} catch (EolModelElementTypeNotFoundException e) {
//			// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			
//			String classType2 = Matches.get(i).getRightText().split(" ")[0] ; 
//			classType2 = classType2.replace("<","") ;
//			classType2 = classType2.replace(">","") ;
//			try {
////				Collection<EObject> tes = V2.getAllOfKind(classType1) ; 
//				
//				for (EObject modelV2Element : V2.getAllOfKind(classType1)) {
//				    itemLabelProvider = (IItemLabelProvider)adapterFactory.adapt(modelV2Element, IItemLabelProviderClass);
//				    String Text =  itemLabelProvider.getText(modelV2Element) ; 
//				    		   
//				    treeItemContentProvider = (ITreeItemContentProvider)adapterFactory.adapt(modelV2Element, ITreeItemContentProviderClass);
//				    Object Parent = treeItemContentProvider.getParent(modelV2Element) ; 
//				    itemLabelProvider = (IItemLabelProvider)adapterFactory.adapt(Parent, IItemLabelProviderClass);
//				    String ParentText = itemLabelProvider.getText(Parent);
//				
//				    if(Matches.get(i).getRightText().equals(Text) && (Matches.get(i).getRightParent().equals("File") || Matches.get(i).getRightParent().equals(ParentText))){
//				    	tmodelV2Element = modelV2Element ; 
//				    	break ; 
//				    }
//				}
//			} catch (EolModelElementTypeNotFoundException e) {
//			// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			if(tmodelV1Element != null && tmodelV2Element != null){
//				tmodelV1ElementList.add(tmodelV1Element) ; 
//				tmodelV2ElementList.add(tmodelV2Element) ; 
//			}
//		}
//		
//		for(int i=0; i<tmodelV1ElementList.size(); i++)
//		{
//			if(Decision[i] == 0 || Decision[i] == 1)
//				editV1WithV2(tmodelV2ElementList.get(i), tmodelV1ElementList.get(i));
//			else // Decision[i] == 2
//				editV1WithV2(tmodelV1ElementList.get(i), tmodelV2ElementList.get(i));
//		}
//	}
//	
//	private void editV1WithV2(EObject V1, EObject V2)
//	{
//		for(EStructuralFeature esf : V1.eClass().getEAllAttributes())//  .getEAllStructuralFeatures())
//		{		
//			if(V2.eGet(esf)!=null){
//				if(esf.isChangeable() && V1.eIsSet(esf))
//					V1.eSet(esf, V2.eGet(esf));
//			}				
//		}
//	}
	
//	private void Validate(IModel baseModel, IModel leftModel, 
//			IModel rightModel, String evlFile) throws Exception{
//		baseModel.setName("Base");
//		leftModel.setName("Left");
//		rightModel.setName("Right");
//	
//		EvlModule evlModule = new EvlModule() ; 
//		
//		String st = URI.createFileURI(evlFile).toString(); 
//		evlFile = evlFile.replaceAll("\\\\", "/");
//		java.net.URI evlURI = java.net.URI.create("file:/"+evlFile) ; 
//		evlModule.parse(evlURI) ; 
////		java.net.URI b = java.net.URI.create(evlFile) ; 
////		evlModule.parse(b) ;
////		evlModule.parse(EpsilonStandalone.class.getResource(evlFile).toURI());
//		evlModule.getContext().getModelRepository().addModel(baseModel);
//		evlModule.getContext().getModelRepository().addModel(leftModel);
//		evlModule.getContext().getModelRepository().addModel(rightModel);
//
//		evlModule.execute()  ;
//		evlModule.setUnsatisfiedConstraintFixer(new ValidationViewFixer());
//		evlModule.setUnsatisfiedConstraintFixer(new IEvlFixer() {
//			public void fix(IEvlModule module) throws EolRuntimeException {
//				// Do nothing
//			}
//		});
//		
//		Collection<UnsatisfiedConstraint> unsatisfied = evlModule.getContext().getUnsatisfiedConstraints();
//		
//		if (unsatisfied.size() > 0) {
//			System.err.println(unsatisfied.size() + " constraint(s) have not been satisfied");
//			for (UnsatisfiedConstraint uc : unsatisfied) {
//				System.err.println(uc.getMessage());
//				for(FixInstance fi : uc.getFixes()){
//					System.out.println(fi.getTitle()) ;
//				}
//			}
//		}
//		else {
//			System.out.println("All constraints have been satisfied");
//		}
//		
//		
//		IWorkbench wb = PlatformUI.getWorkbench();
//	   	IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
//		ValidationView VV = new ValidationView(win.getShell(), mergingModelSelectionPage.selectedProject) ;	
//		VV.unSConstraint = unsatisfied ; 
//		VV.open() ; 
//		
//	   	
//	   	
////		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().
////				showView("org.eclipse.epsilon.evl.dt.views.ValidationView");
//		
////		evlModule.setUnsatisfiedConstraintFixer(new IEvlFixer() {
////			public void fix(IEvlModule module) throws EolRuntimeException {
////				// Do nothing
////			}
////});
//		
////		ValidationViewFixer fixer = new ValidationViewFixer();
////		validationView.fix(evlModule, fixer);
//	}

//	public MatchTrace Comparison(IModel leftModel, IModel rightModel, String eclFile) throws Exception {
//		leftModel.setName("Left");
//		rightModel.setName("Right");
//		EclModule eclModule = new EclModule();
//		
//		String st = URI.createFileURI(eclFile).toString(); 
//		eclFile = eclFile.replaceAll("\\\\", "/");
//		java.net.URI eclURI = java.net.URI.create("file:/"+eclFile) ; 
//		eclModule.parse(eclURI) ; 
//		
////		java.net.URI b = java.net.URI.create(eclFile) ; 
////		eclModule.parse(b) ; 
//	//	eclModule.parse(EpsilonStandalone.class.getResource(eclFile).toURI());
//		eclModule.getContext().getModelRepository().addModel(leftModel);
//		eclModule.getContext().getModelRepository().addModel(rightModel);
//		eclModule.execute();
//
//		MatchTrace Matched = eclModule.getContext().getMatchTrace(); 
//		return Matched ; 
//	}
//	
//	public MatchTrace delNullmatch(List<Match> matches) { 
//		MatchTrace reduced = new MatchTrace();
//		for (Match match : matches) {
//			MatchRule gr = match.getRule() ;
//			if (gr != null ) {
//				reduced.getMatches().add(match);
//			}
//		}
//		return reduced;
//	}
//	
//	public void Merge(IModel leftModel, IModel rightModel, IModel mergedModel, 
//			MatchTrace MT, String emlFile) throws Exception{
//		leftModel.setName("Left");
//		rightModel.setName("Right");
//		mergedModel.setName("Target");
//		
//		mergedModel.getAliases().add("Target");
//		
//		EmlModule emlModule = new EmlModule();
//		
//		String st = URI.createFileURI(emlFile).toString(); 
//		emlFile = emlFile.replaceAll("\\\\", "/");
//		java.net.URI emlURI = java.net.URI.create("file:/"+emlFile) ; 
//		emlModule.parse(emlURI) ; 
////		java.net.URI b = java.net.URI.create(emlFile) ; 
////		emlModule.parse(b) ;
////		emlModule.parse(EpsilonStandalone.class.getResource(emlFile).toURI());
//		emlModule.getContext().getModelRepository().addModel(leftModel);
//		emlModule.getContext().getModelRepository().addModel(rightModel);
//		emlModule.getContext().setMatchTrace(MT);
//		emlModule.getContext().getModelRepository().addModel(mergedModel);
//		emlModule.execute();
//	}
	
}