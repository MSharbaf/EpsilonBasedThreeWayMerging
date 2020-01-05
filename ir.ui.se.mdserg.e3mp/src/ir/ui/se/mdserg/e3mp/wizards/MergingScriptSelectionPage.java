package ir.ui.se.mdserg.e3mp.wizards;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.EPackage.Registry;
import org.eclipse.emf.ecore.provider.EcoreItemProviderAdapterFactory;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
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
import org.eclipse.epsilon.emc.emf.InMemoryEmfModel;
import org.eclipse.epsilon.eml.EmlModule;
import org.eclipse.epsilon.eol.exceptions.models.EolModelElementTypeNotFoundException;
import org.eclipse.epsilon.eol.exceptions.models.EolModelLoadingException;
import org.eclipse.epsilon.eol.models.IModel;
import org.eclipse.epsilon.eol.models.IRelativePathResolver;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.resource.UMLResource;
import org.eclipse.uml2.uml.resources.util.UMLResourcesUtil;

import ir.ui.se.mdserg.e3mp.helper.EditSelectedTreeView;
import ir.ui.se.mdserg.e3mp.helper.MatchMember;
import ir.ui.se.mdserg.e3mp.helper.TreeViewComp;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;


public class MergingScriptSelectionPage extends WizardPage {
	public String path ; 
	private Composite container;

	private Group MergeModelSection ;
	private Group EclSection; 
	private Group EmlSection;
	private Group EvlSection;
	
	public FileChooser MergedModelFC ;
	public FileChooser EclScriptFC ;
	public FileChooser EmlScriptFC ; 
	public FileChooser EvlScriptFC ; 
	
	public FileChooser MetaModelFC ;
	public FileChooser BaseModelFC ; 
	
	public IProject selectedProject;
	public ArrayList<MatchMember> matchMemberList;
	public EmfModel tModel = null, modelV1, modelV2  ;

	private static final Class<?> IItemLabelProviderClass = IItemLabelProvider.class;
	private static final Class<?> ITreeItemContentProviderClass = ITreeItemContentProvider.class;

    protected MergingScriptSelectionPage(String pageName) {
             super(pageName);
             setTitle("Merging Phase: Select Merge Scripts");
             setDescription("Please select required files ...");
    }
	
	@Override
	public void createControl(Composite parent) {
		// TODO Auto-generated method stub	
		container = new Composite(parent, SWT.NULL);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		container.setLayoutData(data);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
					
		EclSection = new Group(container, SWT.NULL);
		EclSection.setText("Select ECL File");
		EclSection.setLayout(new GridLayout());
		EclSection.setLayoutData(data);
		EclScriptFC = new FileChooser(EclSection) ; 
		EclScriptFC.Extensions = "*.ecl" ; 
		EclScriptFC.ExtensionsName = "ECL Script" ; 
		GridData EclgridData = new GridData(GridData.FILL_HORIZONTAL) ; 
		EclScriptFC.setLayoutData(EclgridData);
		
		EmlSection = new Group(container, SWT.NULL);
		EmlSection.setText("Select EML File");
		EmlSection.setLayout(new GridLayout());
		EmlSection.setLayoutData(data);
		EmlScriptFC = new FileChooser(EmlSection) ; 
		EmlScriptFC.Extensions = "*.eml" ; 
		EmlScriptFC.ExtensionsName = "EML Script" ; 
		GridData EmlgridData = new GridData(GridData.FILL_HORIZONTAL) ; 
		EmlScriptFC.setLayoutData(EmlgridData);
		
		EvlSection = new Group(container, SWT.NULL);
		EvlSection.setText("Select EVL File (optional)");
		EvlSection.setLayout(new GridLayout());
		EvlSection.setLayoutData(data);
		EvlScriptFC = new FileChooser(EvlSection) ; 
		EvlScriptFC.Extensions = "*.evl" ; 
		EvlScriptFC.ExtensionsName = "EVL Script" ; 
		GridData EvlgridData = new GridData(GridData.FILL_HORIZONTAL) ; 
		EvlScriptFC.setLayoutData(EvlgridData);
		
		MergeModelSection = new Group(container, SWT.NULL);
		MergeModelSection.setText("Select Target Model to Save Merged Version");
		MergeModelSection.setLayout(new GridLayout());
		MergeModelSection.setLayoutData(data);
		MergedModelFC = new FileChooser(MergeModelSection) ; 
		MergedModelFC.Extensions = "*.xmi; *.model; *.uml; *.ecore" ; 
		MergedModelFC.ExtensionsName = "Model" ; 
		GridData ML1gridData = new GridData(GridData.FILL_HORIZONTAL) ; 
		MergedModelFC.setLayoutData(ML1gridData);
		
		setControl(container);		
	}

	public void setSelectedProject(IProject project) {
		this.selectedProject = project;
	}
	
	public void onLoad(IProject selectedPrj, FileChooser MetaModelFC, FileChooser BaseModelFC, EmfModel modelV1, EmfModel modelV2)
	{
		this.selectedProject = selectedPrj ;
		this.path = selectedPrj.getLocation().toString() ;
		
		MergedModelFC.directory = this.path ;  
		EclScriptFC.directory = this.path ; 
		EmlScriptFC.directory = this.path ; 
		EvlScriptFC.directory = this.path ; 
		
		this.MetaModelFC = MetaModelFC ;
		this.BaseModelFC = BaseModelFC ;
		this.modelV1 = modelV1 ;
		this.modelV2 = modelV2 ;
	}
	
	
    /** @override */
    public IWizardPage getNextPage() {
        boolean isNextPressed = "nextPressed".equalsIgnoreCase(Thread.currentThread().getStackTrace()[2].getMethodName());
        if (isNextPressed) {
            boolean validatedNextPress = this.nextPressed();
            if (!validatedNextPress) {
                return this;
            }
        }
        return super.getNextPage();
    }
	   
	/**
	 * @see WizardDialog#nextPressed()
	 * @see WizardPage#getNextPage()
	 */
	 protected boolean nextPressed() {
		if((this.EclScriptFC.path == null) || (this.EmlScriptFC.path == null) || this.MergedModelFC.path == null){
			this.setDescription("You must select all the required files ...");
		}
		else
		{	
			if(this.BaseModelFC.path.endsWith(".uml")){
				try {
					tModel = getUmlModel("Target", new File(this.MergedModelFC.path)) ; 
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}		
			
			}else if(this.BaseModelFC.path.endsWith(".ecore")){
				try {
					tModel = getEcoreModel("Target", new File(this.MergedModelFC.path)) ; 
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}		
			
			}else{
				try {
					tModel = getEmfModel("Target",this.MergedModelFC.path, this.MetaModelFC.path, false, true);
		
				} catch (EolModelLoadingException | URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			tModel.getAliases().add("Target");
			tModel.setStoredOnDisposal(true);
			
			
			//********** Merging with EML Script *********
			//Compare left and right models 
			long startTime = System.currentTimeMillis() ;
			
			MatchTrace MTF = null ;
			try {
				MTF = Comparison(modelV1, modelV2, this.EclScriptFC.path);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			MatchTrace Matched = MTF.getReduced() ; 
			Matched = delNullmatch(Matched.getMatches());
								
		
			//Select equivalent element in left and right models without considering Original model 
			ArrayList<AdapterFactory> factories = new ArrayList<AdapterFactory>();
			factories.add(new ResourceItemProviderAdapterFactory());
			factories.add(new EcoreItemProviderAdapterFactory());
			factories.add(new ReflectiveItemProviderAdapterFactory());
			
			matchMemberList = new ArrayList<MatchMember>() ; 
	    	MatchMember matchMember ; 
			ComposedAdapterFactory composedAdapterFactory ; 
		    IItemLabelProvider itemLabelProvider  ;  
		    ITreeItemContentProvider treeItemContentProvider ;
			
			if(MetaModelFC.path == null)
				composedAdapterFactory = new ComposedAdapterFactory(ComposedAdapterFactory.Descriptor.Registry.INSTANCE);
			else
				composedAdapterFactory = new ComposedAdapterFactory(factories);
	    	AdapterFactory adapterFactory = composedAdapterFactory;
		    
		    for(int i=0 ; i<Matched.getMatches().size(); i++)
		    {
		    	Match m = Matched.getMatches().get(i) ; 

	    	    itemLabelProvider = (IItemLabelProvider)adapterFactory.adapt(m.getLeft(), IItemLabelProviderClass);
	    	    String LeftText =    itemLabelProvider.getText(m.getLeft()) ; 
	    	    		   
		        treeItemContentProvider = (ITreeItemContentProvider)adapterFactory.adapt(m.getLeft(), ITreeItemContentProviderClass);
		        Object LeftParent = treeItemContentProvider.getParent(m.getLeft()) ; 
		        itemLabelProvider = (IItemLabelProvider)adapterFactory.adapt(LeftParent, IItemLabelProviderClass);
		        String LeftParentText = itemLabelProvider.getText(LeftParent); 
		        
	    	    itemLabelProvider = (IItemLabelProvider)adapterFactory.adapt(m.getRight(), IItemLabelProviderClass);
	    	    String RightText =    itemLabelProvider.getText(m.getRight()) ; 
	    	    		   
		        treeItemContentProvider = (ITreeItemContentProvider)adapterFactory.adapt(m.getRight(), ITreeItemContentProviderClass);
		        Object RightParent = treeItemContentProvider.getParent(m.getRight()) ; 
		        itemLabelProvider = (IItemLabelProvider)adapterFactory.adapt(RightParent, IItemLabelProviderClass);
		        String RightParentText = itemLabelProvider.getText(RightParent); 
		        
		       	matchMember = new MatchMember(LeftParentText, LeftText, RightParentText, RightText, 0) ;
			    matchMemberList.add(matchMember);
		    }

			// Show to user to select one of two options 
			IWorkbench wb = PlatformUI.getWorkbench();
		   	IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
			EditSelectedTreeView EETV = new EditSelectedTreeView(win.getShell(), modelV1, modelV2, 
					this.MetaModelFC.path, this.selectedProject , matchMemberList) ;
			
			EETV.open() ; 

		
			// Update model according to the newMatchList and adding correspondence Matches to the Matched 
			int iss = EETV.newMatchList.size() ; 
			//*
			
			for(int i=0; i<EETV.newMatchList.size(); i++){				
				for(Match m : MTF.getMatches()) {
		    	    itemLabelProvider = (IItemLabelProvider)adapterFactory.adapt(m.getLeft(), IItemLabelProviderClass);
		    	    String LeftText =    itemLabelProvider.getText(m.getLeft()) ; 
		    	    		   
			        treeItemContentProvider = (ITreeItemContentProvider)adapterFactory.adapt(m.getLeft(), ITreeItemContentProviderClass);
			        Object LeftParent = treeItemContentProvider.getParent(m.getLeft()) ; 
			        itemLabelProvider = (IItemLabelProvider)adapterFactory.adapt(LeftParent, IItemLabelProviderClass);
			        String LeftParentText = itemLabelProvider.getText(LeftParent); 
			        
			        if(EETV.newMatchList.get(i).getLeftText().equals(LeftText) && (EETV.newMatchList.get(i).getLeftParent().equals("File") || EETV.newMatchList.get(i).getLeftParent().equals(LeftParentText))){
			        
			    	    itemLabelProvider = (IItemLabelProvider)adapterFactory.adapt(m.getRight(), IItemLabelProviderClass);
			    	    String RightText =    itemLabelProvider.getText(m.getRight()) ; 
			    	    		   
				        treeItemContentProvider = (ITreeItemContentProvider)adapterFactory.adapt(m.getRight(), ITreeItemContentProviderClass);
				        Object RightParent = treeItemContentProvider.getParent(m.getRight()) ; 
				        itemLabelProvider = (IItemLabelProvider)adapterFactory.adapt(RightParent, IItemLabelProviderClass);
				        String RightParentText = itemLabelProvider.getText(RightParent); 
			        
				        if(EETV.newMatchList.get(i).getRightText().equals(RightText) && (EETV.newMatchList.get(i).getRightParent().equals("File") || EETV.newMatchList.get(i).getRightParent().equals(RightParentText))){
				        	m.setMatching(true);
				        }
			        }
				}
			}
			
			performAllEdits(modelV1, modelV2, EETV.newMatchList) ;
			
			
			Matched = MTF.getReduced() ; 
			Matched = delNullmatch(Matched.getMatches());
			
			// Merge and Save merged model 
			try {
				Merge(modelV1, modelV2, tModel, Matched, this.EmlScriptFC.path) ;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
//			long endTime   = System.nanoTime();
			long endTime   = System.currentTimeMillis() ;
			long totalTime = endTime - startTime;
			System.out.println("Merging time is : " + totalTime + " in nano second ...");
			
			tModel.dispose(); 
			
			// Validate Consistency 
			if(this.EclScriptFC.path != null){
				;
			}
			
			return true ; 
	 	}
		return false ; 
	 }
	
	public InMemoryEmfModel getUmlModel(String name, File file) throws Exception {
		ResourceSet set = new ResourceSetImpl();
		UMLResourcesUtil.init(set);
		set.getPackageRegistry().put(UMLPackage.eNS_URI, UMLPackage.eINSTANCE);
		set.getResourceFactoryRegistry().getExtensionToFactoryMap().
		         put(UMLResource.FILE_EXTENSION, UMLResource.Factory.INSTANCE);
	
		Resource r = set.getResource(URI.createFileURI(file.getAbsolutePath()), true);
		r.load(null);
		
		Collection<EPackage> ePackages = new ArrayList<EPackage>();
		for (Object ePackage : set.getPackageRegistry().values()) {
			ePackages.add((EPackage) ePackage);
		}
		return new InMemoryEmfModel(name, r, ePackages);
	}
		
	
	private void performAllEdits(EmfModel V1, EmfModel V2, ArrayList<MatchMember> Matches/*, int[] Decision*/)
	{    	
		ComposedAdapterFactory composedAdapterFactory ; 
	    IItemLabelProvider itemLabelProvider  ;  
	    ITreeItemContentProvider treeItemContentProvider ;

		ArrayList<AdapterFactory> factories = new ArrayList<AdapterFactory>();
		factories.add(new ResourceItemProviderAdapterFactory());
		factories.add(new EcoreItemProviderAdapterFactory());
		factories.add(new ReflectiveItemProviderAdapterFactory());
		
		if(this.BaseModelFC.path.endsWith(".uml") == true)
			composedAdapterFactory = new ComposedAdapterFactory(ComposedAdapterFactory.Descriptor.Registry.INSTANCE);
		else
			composedAdapterFactory = new ComposedAdapterFactory(factories);
    	AdapterFactory adapterFactory = composedAdapterFactory;
	
    	ArrayList<EObject> tmodelV1ElementList = new ArrayList<EObject>();
    	ArrayList<EObject> tmodelV2ElementList = new ArrayList<EObject>();
		for(int i=0; i<Matches.size(); i++){
	    	EObject	tmodelV1Element = null , tmodelV2Element = null;
	    	
	    	String classType1 = "" ; 
	    	if(this.BaseModelFC.path.endsWith(".uml") == true){
	    		classType1 = Matches.get(i).getLeftText().split("> ")[0] ;
	    		classType1 = classType1.replace(" ", "") ;
	    	}else
	    		classType1 = Matches.get(i).getLeftText().split(" ")[0] ; 
			classType1 = classType1.replace("<","") ;
			classType1 = classType1.replace(">","") ;
			
			try {
				
				for (EObject modelV1Element : V1.getAllOfKind(classType1)) {
				    itemLabelProvider = (IItemLabelProvider)adapterFactory.adapt(modelV1Element, IItemLabelProviderClass);
				    String Text =  itemLabelProvider.getText(modelV1Element) ; 
				    		   
				    treeItemContentProvider = (ITreeItemContentProvider)adapterFactory.adapt(modelV1Element, ITreeItemContentProviderClass);
				    Object Parent = treeItemContentProvider.getParent(modelV1Element) ; 
				    itemLabelProvider = (IItemLabelProvider)adapterFactory.adapt(Parent, IItemLabelProviderClass);
				    String ParentText = itemLabelProvider.getText(Parent);
				    
				    if(Matches.get(i).getLeftText().equals(Text) && (Matches.get(i).getLeftParent().equals("File") || Matches.get(i).getLeftParent().equals(ParentText))){
				    	tmodelV1Element = modelV1Element ; 
				    	break ; 
				    }
				}
			} catch (EolModelElementTypeNotFoundException e) {
			// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
	    	String classType2 = "" ; 
	    	if(this.BaseModelFC.path.endsWith(".uml") == true){
	    		classType2 = Matches.get(i).getRightText().split("> ")[0] ; 
	    		classType2 = classType1.replace(" ", "") ;
	    	}else
	    		classType2 = Matches.get(i).getRightText().split(" ")[0] ;
			classType2 = classType2.replace("<","") ;
			classType2 = classType2.replace(">","") ;
			try {
				for (EObject modelV2Element : V2.getAllOfKind(classType2)) {
				    itemLabelProvider = (IItemLabelProvider)adapterFactory.adapt(modelV2Element, IItemLabelProviderClass);
				    String Text =  itemLabelProvider.getText(modelV2Element) ; 
				    		   
				    treeItemContentProvider = (ITreeItemContentProvider)adapterFactory.adapt(modelV2Element, ITreeItemContentProviderClass);
				    Object Parent = treeItemContentProvider.getParent(modelV2Element) ; 
				    itemLabelProvider = (IItemLabelProvider)adapterFactory.adapt(Parent, IItemLabelProviderClass);
				    String ParentText = itemLabelProvider.getText(Parent);
				
				    if(Matches.get(i).getRightText().equals(Text) && (Matches.get(i).getRightParent().equals("File") || Matches.get(i).getRightParent().equals(ParentText))){
				    	tmodelV2Element = modelV2Element ; 
				    	break ; 
				    }
				}
			} catch (EolModelElementTypeNotFoundException e) {
			// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(tmodelV1Element != null && tmodelV2Element != null){
				tmodelV1ElementList.add(tmodelV1Element) ; 
				tmodelV2ElementList.add(tmodelV2Element) ; 
			}
		}
		
		for(int i=0; i<tmodelV1ElementList.size(); i++)
		{
			if(Matches.get(i).getisSelected() == 0 || Matches.get(i).getisSelected() == 1)
				editV1WithV2(tmodelV2ElementList.get(i), tmodelV1ElementList.get(i));
			else // Matches.get(i).getisSelected() == 2
				editV1WithV2(tmodelV1ElementList.get(i), tmodelV2ElementList.get(i));
		}
	}
	
	private void editV1WithV2(EObject V1, EObject V2)
	{	
		for(EStructuralFeature esf : V1.eClass().getEAllAttributes())
		{		
			if(V2.eClass().getEAllAttributes().contains(esf))
				if(V2.eGet(esf)!=null){
					String st = esf.getName() ; 
					if(st.equals("visibility") == false)
						if(esf.isChangeable())
							V1.eSet(esf, V2.eGet(esf));
				}	
		}
	}

	
	public InMemoryEmfModel getEcoreModel(String name, File file) throws Exception {
		ResourceSet rs = new ResourceSetImpl();
		rs.getPackageRegistry().put(EcorePackage.eINSTANCE.getNsURI(), EcorePackage.eINSTANCE);
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", new XMIResourceFactoryImpl());
	
		Resource r = rs.getResource(URI.createFileURI(file.getAbsolutePath()), true);
		r.load(null);
		
		Collection<EPackage> ePackages = new ArrayList<EPackage>();
		for (Object ePackage : rs.getPackageRegistry().values()) {
			ePackages.add((EPackage) ePackage);
		}
		return new InMemoryEmfModel(name, r, ePackages);
	}
	
	public InMemoryEmfModel getInMemoryEmfModel(String name, File modelFile, String metamodelPath) throws IOException
	{
		ResourceSet rs = new ResourceSetImpl();
		rs.getPackageRegistry().put(EcorePackage.eINSTANCE.getNsURI(), EcorePackage.eINSTANCE);
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", new EcoreResourceFactoryImpl()) ; //XMIResourceFactoryImpl());
		Resource r = null;
		EPackage metamodel = null;
		
		java.net.URI javaURI = new File(metamodelPath).toURI() ; 
		
		r = rs.createResource(URI.createURI(javaURI.toString()));
		r.load(Collections.emptyMap());
		metamodel = (EPackage) r.getContents().get(0);

		rs = new ResourceSetImpl();
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", new EcoreResourceFactoryImpl()) ;
				//XMIResourceFactoryImpl());
		
		Registry packageRegistry = rs.getPackageRegistry();
		EPackage pack = (EPackage) packageRegistry.get(EcorePackage.eINSTANCE.getNsURI());
		if (!(pack instanceof EcorePackage)) {
			packageRegistry.put(EcorePackage.eINSTANCE.getNsURI(), EcorePackage.eINSTANCE);
		}
		
		rs.getPackageRegistry().put(metamodel.getNsURI(), metamodel);
		r = rs.createResource(URI.createURI(modelFile.toURI().toString()));
		r.load(Collections.emptyMap());
	
		InMemoryEmfModel model = new InMemoryEmfModel(name, r, metamodel);
		
		model.setMetamodelFile(URI.createFileURI(metamodelPath).toString());
		model.setMetamodelFileBased(true);

		return model ; 
	}
	
	protected EmfModel getEmfModel(String name, String model, 
			String metamodel, boolean readOnLoad, boolean storeOnDisposal) 
					throws EolModelLoadingException, URISyntaxException {
		EmfModel emfModel = new  EmfModel();
		StringProperties properties = new StringProperties();
		properties.put(EmfModel.PROPERTY_NAME, name);
		properties.put(EmfModel.PROPERTY_FILE_BASED_METAMODEL_URI,URI.createFileURI(metamodel).toString());
		properties.put(EmfModel.PROPERTY_MODEL_URI,URI.createFileURI(model).toString());
		properties.put(EmfModel.PROPERTY_READONLOAD, readOnLoad + "");
		properties.put(EmfModel.PROPERTY_STOREONDISPOSAL, 
				storeOnDisposal + "");
		emfModel.load(properties, (IRelativePathResolver) null);
		return emfModel;
	}
	
	public MatchTrace Comparison(IModel leftModel, IModel rightModel, String eclFile) throws Exception {
		leftModel.setName("Left");
		rightModel.setName("Right");
		EclModule eclModule = new EclModule();
		
		String st = URI.createFileURI(eclFile).toString(); 
		eclFile = eclFile.replaceAll("\\\\", "/");
		java.net.URI eclURI = java.net.URI.create("file:/"+eclFile) ; 
		eclModule.parse(eclURI) ; 
		
		eclModule.getContext().getModelRepository().addModel(leftModel);
		eclModule.getContext().getModelRepository().addModel(rightModel);
		eclModule.execute();

		MatchTrace Matched = eclModule.getContext().getMatchTrace(); 
		return Matched ; 
	}
	
	public MatchTrace delNullmatch(List<Match> matches) { 
		MatchTrace reduced = new MatchTrace();
		for (Match match : matches) {
			MatchRule gr = match.getRule() ;
			if (gr != null ) {
				reduced.getMatches().add(match);
			}
		}
		return reduced;
	}
	
	public void Merge(IModel leftModel, IModel rightModel, IModel mergedModel, 
			MatchTrace MT, String emlFile) throws Exception{
		leftModel.setName("Left");
		rightModel.setName("Right");
		mergedModel.setName("Target");
		
		mergedModel.getAliases().add("Target");
		
		EmlModule emlModule = new EmlModule();
		
		String st = URI.createFileURI(emlFile).toString(); 
		emlFile = emlFile.replaceAll("\\\\", "/");
		java.net.URI emlURI = java.net.URI.create("file:/"+emlFile) ; 
		emlModule.parse(emlURI) ; 
		emlModule.getContext().getModelRepository().addModel(leftModel);
		emlModule.getContext().getModelRepository().addModel(rightModel);
		emlModule.getContext().setMatchTrace(MT);
		emlModule.getContext().getModelRepository().addModel(mergedModel);
		emlModule.execute();
	}
	
}
