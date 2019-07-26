package ir.ui.se.mdserg.e3mp.wizards;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.resources.IProject;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.provider.EcoreItemProviderAdapterFactory;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;
import org.eclipse.emf.edit.provider.ReflectiveItemProviderAdapterFactory;
import org.eclipse.emf.edit.provider.resource.ResourceItemProviderAdapterFactory;
import org.eclipse.epsilon.emc.emf.EmfModel;
import org.eclipse.epsilon.eol.exceptions.EolRuntimeException;
import org.eclipse.epsilon.eol.exceptions.models.EolModelElementTypeNotFoundException;
import org.eclipse.epsilon.eol.models.IModel;
import org.eclipse.epsilon.evl.EvlModule;
import org.eclipse.epsilon.evl.IEvlFixer;
import org.eclipse.epsilon.evl.IEvlModule;
import org.eclipse.epsilon.evl.dt.views.ValidationViewFixer;
import org.eclipse.epsilon.evl.execute.FixInstance;
import org.eclipse.epsilon.evl.execute.UnsatisfiedConstraint;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import ir.ui.se.mdserg.e3mp.helper.EditEquivalentTreeView;
import ir.ui.se.mdserg.e3mp.helper.MatchMember;
import ir.ui.se.mdserg.e3mp.helper.UserInterDialog;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;


public class InferringScriptSelectionPage extends WizardPage {
	public String path ; 
	private Composite container;

	private Group ML1Section ;
	private Group ML2Section ; 
	private Group DetectionSection; 
	private Group ResolutionSection;
	
	public FileChooser ML1ScriptFC ;
	public FileChooser ML2ScriptFC ; 
	public FileChooser DetectionPatternScriptFC ;
	public FileChooser ResolutionPatternScriptFC ; 
	public FileChooser MetaModelFC ;
	public FileChooser BaseModelFC ; 
	
	public IProject selectedProject;
//	public MergingModelSelectionPage MergingModelSelectionPage ; 
	public MergingScriptSelectionPage MergingScriptSelectionPage ;
	public ArrayList<MatchMember> MatchList1, MatchList2, InferedMatchList, AutoInferedMatch, UnEquMatchList, TempMatchList1;
	public EmfModel bModel, modelV1, modelV2;

	private static final Class<?> IItemLabelProviderClass = IItemLabelProvider.class;
	private static final Class<?> ITreeItemContentProviderClass = ITreeItemContentProvider.class;
	public ComposedAdapterFactory composedAdapterFactory ;


    protected InferringScriptSelectionPage(String pageName) {
             super(pageName);
             setTitle("Reconsilation Phase: Select Match Lists and Pattern Scripts");
             setDescription("Please select required files");
    }
	
	@Override
	public void createControl(Composite parent) {
		// TODO Auto-generated method stub	
		container = new Composite(parent, SWT.NULL);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		container.setLayoutData(data);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		
		
		ML1Section = new Group(container, SWT.NULL);
		ML1Section.setText("Select Match model for Version 1");
		ML1Section.setLayout(new GridLayout());
		ML1Section.setLayoutData(data);
		ML1ScriptFC = new FileChooser(ML1Section) ; 
		ML1ScriptFC.Extensions = "*.match" ; 
		ML1ScriptFC.ExtensionsName = "MatchList" ; 
		GridData ML1gridData = new GridData(GridData.FILL_HORIZONTAL) ; 
		ML1ScriptFC.setLayoutData(ML1gridData);
		
		ML2Section = new Group(container, SWT.NULL);
		ML2Section.setText("Select Match model for Version 2");
		ML2Section.setLayout(new GridLayout());
		ML2Section.setLayoutData(data);
		ML2ScriptFC = new FileChooser(ML2Section) ; 
		ML2ScriptFC.Extensions = "*.match" ; 
		ML2ScriptFC.ExtensionsName = "MatchList" ; 
		GridData ML2gridData = new GridData(GridData.FILL_HORIZONTAL) ; 
		ML2ScriptFC.setLayoutData(ML2gridData);
		
		DetectionSection = new Group(container, SWT.NULL);
		DetectionSection.setText("Select Pattern Detection Script (EVL or EPL) File (optional)");
		DetectionSection.setLayout(new GridLayout());
		DetectionSection.setLayoutData(data);
		DetectionPatternScriptFC = new FileChooser(DetectionSection) ; 
		DetectionPatternScriptFC.Extensions = "*.evl; *.epl" ; 
		DetectionPatternScriptFC.ExtensionsName = "Pattern Detection Script" ; 
		GridData EclgridData = new GridData(GridData.FILL_HORIZONTAL) ; 
		DetectionPatternScriptFC.setLayoutData(EclgridData);
		
		ResolutionSection = new Group(container, SWT.NULL);
		ResolutionSection.setText("Select Pattern Resolution Script (EVL or EOL) File (optional)");
		ResolutionSection.setLayout(new GridLayout());
		ResolutionSection.setLayoutData(data);
		ResolutionPatternScriptFC = new FileChooser(ResolutionSection) ; 
		ResolutionPatternScriptFC.Extensions = "*.evl; *.eol" ; 
		ResolutionPatternScriptFC.ExtensionsName = "Pattern Resolution Script" ; 
		GridData EvlgridData = new GridData(GridData.FILL_HORIZONTAL) ; 
		ResolutionPatternScriptFC.setLayoutData(EvlgridData);
		
		setControl(container);		
	}

	public void setSelectedProject(IProject project) {
		this.selectedProject = project;
	}
	
	public void onLoad(IProject selectedPrj, EmfModel bModel, EmfModel modelV1, EmfModel modelV2, 
			FileChooser MetaModelFC, FileChooser BaseModelFC, ComposedAdapterFactory composedAdapterFactory)
	{
		this.selectedProject = selectedPrj ;
		this.path = selectedPrj.getLocation().toString() ;
		
		ML1ScriptFC.directory = this.path ; 
		ML2ScriptFC.directory = this.path ; 
		DetectionPatternScriptFC.directory = this.path ; 
		ResolutionPatternScriptFC.directory = this.path ; 
		this.bModel = bModel ; 
		this.modelV1 = modelV1 ; 
		this.modelV2 = modelV2 ; 
		this.MetaModelFC = MetaModelFC ;
		this.BaseModelFC = BaseModelFC ;
		this.composedAdapterFactory = composedAdapterFactory ; 
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
		if((this.ML1ScriptFC.path == null) || (this.ML2ScriptFC.path == null)) 
		{
			this.setDescription("You must select all the required files ...");
		}
		else
		{	
			//************* Read Match Files ****************		
			try {
			    FileInputStream inputStream=new FileInputStream(this.ML1ScriptFC.path);
			    ObjectInputStream objectInputStream =new ObjectInputStream(inputStream);
			    MatchList1 = (ArrayList<MatchMember>) objectInputStream.readObject();
			} catch (FileNotFoundException e) {
			    e.printStackTrace();
			} catch (IOException e) {
			    e.printStackTrace();
			} catch (ClassNotFoundException e) {
			    e.printStackTrace();
			}
			
			try {
			    FileInputStream inputStream=new FileInputStream(this.ML2ScriptFC.path);
			    ObjectInputStream objectInputStream =new ObjectInputStream(inputStream);
			    MatchList2 = (ArrayList<MatchMember>) objectInputStream.readObject();
			} catch (FileNotFoundException e) {
			    e.printStackTrace();
			} catch (IOException e) {
			    e.printStackTrace();
			} catch (ClassNotFoundException e) {
			    e.printStackTrace();
			}
			
			//********** Inferring with MatchLists and Reconcile Two models *********    	
			InferedMatchList = new ArrayList<MatchMember>() ; 
			AutoInferedMatch = new ArrayList<MatchMember>() ;
			UnEquMatchList = new ArrayList<MatchMember>() ; 
			TempMatchList1 = new ArrayList<MatchMember>() ; 
			for(int i=0; i<MatchList1.size(); i++)
			{
				int index = -1 ; 
				for(int j=0; j<MatchList2.size(); j++)
				{
					if(MatchList1.get(i).getLeftText().equals(MatchList2.get(j).getLeftText()) && 
							MatchList1.get(i).getLeftParent().equals(MatchList2.get(j).getLeftParent())){
						InferedMatchList.add(new MatchMember(MatchList1.get(i).getRightParent(), MatchList1.get(i).getRightText(),MatchList2.get(j).getRightParent(), MatchList2.get(j).getRightText(), 0)) ;
						
						if(MatchList1.get(i).getisSelected() == 1 && MatchList2.get(j).getisSelected() != 1)
							AutoInferedMatch.add(new MatchMember(MatchList1.get(i).getRightParent(), MatchList1.get(i).getRightText(),MatchList2.get(j).getRightParent(), MatchList2.get(j).getRightText(), 1));
						else if (MatchList1.get(i).getisSelected() != 1 && MatchList2.get(j).getisSelected() == 1)
							AutoInferedMatch.add(new MatchMember(MatchList1.get(i).getRightParent(), MatchList1.get(i).getRightText(),MatchList2.get(j).getRightParent(), MatchList2.get(j).getRightText(), 2));
						else if(MatchList1.get(i).getisSelected() == 1 && MatchList2.get(j).getisSelected() == 1){
							if(MatchList1.get(i).getRightText().equals(MatchList2.get(j).getRightText()) == false)
								UnEquMatchList.add(new MatchMember(MatchList1.get(i).getRightParent(), MatchList1.get(i).getRightText(),MatchList2.get(j).getRightParent(), MatchList2.get(j).getRightText(), 0));
							else
								AutoInferedMatch.add(new MatchMember(MatchList1.get(i).getRightParent(), MatchList1.get(i).getRightText(),MatchList2.get(j).getRightParent(), MatchList2.get(j).getRightText(), 1));
						}
						index = j ; 
						break ; 
					}
				}
				if(index != -1)
					MatchList2.remove(index) ;
				else
					TempMatchList1.add(MatchList1.get(i)) ;	
			}
			
			// Show to user to select one of two options 
			IWorkbench wb = PlatformUI.getWorkbench();
		   	IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
			EditEquivalentTreeView EETV = new EditEquivalentTreeView(win.getShell(), modelV1, modelV2, 
					this.MetaModelFC.path, this.selectedProject , InferedMatchList, UnEquMatchList) ;
			
			EETV.open() ; 

			
		    IItemLabelProvider itemLabelProvider  ;  
		    ITreeItemContentProvider treeItemContentProvider ;
	    	AdapterFactory adapterFactory = this.composedAdapterFactory;
			
//			long startTime = System.nanoTime();
			long startTime = System.currentTimeMillis() ;
	    	
	    	// delete removed element in other model 
			for(int i=0; i<TempMatchList1.size(); i++){		    	
				String classType1 = TempMatchList1.get(i).getLeftText().split(" ")[0] ; 
				classType1 = classType1.replace("<","") ;
				classType1 = classType1.replace(">","") ;
				try {					
					for (EObject modelV1Element : modelV1.getAllOfKind(classType1)) {
					    itemLabelProvider = (IItemLabelProvider)adapterFactory.adapt(modelV1Element, IItemLabelProviderClass);
					    String Text =  itemLabelProvider.getText(modelV1Element) ; 
					    		   
					    treeItemContentProvider = (ITreeItemContentProvider)adapterFactory.adapt(modelV1Element, ITreeItemContentProviderClass);
					    Object Parent = treeItemContentProvider.getParent(modelV1Element) ; 
					    itemLabelProvider = (IItemLabelProvider)adapterFactory.adapt(Parent, IItemLabelProviderClass);
					   
					    if(Parent==null && TempMatchList1.get(i).getLeftText().contains(Text)){
					    	try {
					    		
					    		for(EObject eo : modelV1Element.eContents()){
					    			modelV1.deleteElement(eo);
					    		}
//					    		for(EObject eo : modelV1Element.eCrossReferences()){
//					    			modelV1.deleteElement(eo);
//					    		}
//					    		if(classType1.equals("EClass")){
//					    			EClass ec = (EClass) modelV1Element ;
//					    			
//					    			for(EObject er : ec.getEAllReferences())
//					    				modelV1.deleteElement(er);
//					    		}
					    		
								modelV1.deleteElement(modelV1Element);
								
							} catch (EolRuntimeException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} 
					    	break ; 
					    }else{  		
					    	String ParentText = itemLabelProvider.getText(Parent);					    
						    if(TempMatchList1.get(i).getLeftText().equals(Text) && (TempMatchList1.get(i).getLeftParent().equals("File") || TempMatchList1.get(i).getLeftParent().equals(ParentText))){
						    	try {
						    								    	
						    		for(EObject eo : modelV1Element.eContents()){
						    			modelV1.deleteElement(eo);
						    		}
//						    		for(EObject eo : modelV1Element.eCrossReferences()){
//						    			modelV1.deleteElement(eo);
//						    		}
//						    		if(classType1.equals("EClass")){
//						    			EClass ec = (EClass) modelV1Element ;
//						    			
//						    			for(EObject er : ec.getEAllReferences())
//						    				modelV1.deleteElement(er);
//						    		}
						    		
									modelV1.deleteElement(modelV1Element);
								} catch (EolRuntimeException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} 
						    	break ; 
						    }
					    }
					}
				} catch (EolModelElementTypeNotFoundException e) {
				// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			for(int i=0; i<MatchList2.size(); i++){		
						
				String classType2 = MatchList2.get(i).getLeftText().split(" ")[0] ; 
				classType2 = classType2.replace("<","") ;
				classType2 = classType2.replace(">","") ;
				try {					
					for (EObject modelV2Element : modelV2.getAllOfKind(classType2)) {
					    itemLabelProvider = (IItemLabelProvider)adapterFactory.adapt(modelV2Element, IItemLabelProviderClass);
					    String Text =  itemLabelProvider.getText(modelV2Element) ; 
					    		   
					    treeItemContentProvider = (ITreeItemContentProvider)adapterFactory.adapt(modelV2Element, ITreeItemContentProviderClass);
					    Object Parent = treeItemContentProvider.getParent(modelV2Element) ; 
					    itemLabelProvider = (IItemLabelProvider)adapterFactory.adapt(Parent, IItemLabelProviderClass);
					   
					    if(Parent==null && MatchList2.get(i).getLeftText().contains(Text)){
					    	try {
					    		
					    		for(EObject eo : modelV2Element.eContents()){
					    			modelV2.deleteElement(eo);
					    		}
								modelV2.deleteElement(modelV2Element);
							} catch (EolRuntimeException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} 
					    	break ; 
					    }else{  		
					    	String ParentText = itemLabelProvider.getText(Parent);					    
						    if(MatchList2.get(i).getLeftText().equals(Text) && (MatchList2.get(i).getLeftParent().equals("File") || MatchList2.get(i).getLeftParent().equals(ParentText))){
						    	try {
						    								    	
						    		for(EObject eo : modelV2Element.eContents()){
						    			modelV2.deleteElement(eo);
						    		}
						    		
									modelV2.deleteElement(modelV2Element);
								} catch (EolRuntimeException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} 
						    	break ; 
						    }
					    }
					}
				} catch (EolModelElementTypeNotFoundException e) {
				// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			for(int i=0; i<EETV.matchMemberList.size(); i++){
				AutoInferedMatch.add(EETV.matchMemberList.get(i)) ;
			}
			
			performAllEdits(modelV1, modelV2, AutoInferedMatch) ;
			
//			long endTime   = System.nanoTime();
			long endTime   = System.currentTimeMillis() ; 
			long totalTime = endTime - startTime;
			System.out.println("Inferring time is : " + totalTime + " in nano second ...");
/*			
			System.out.println("All tasks are done ...") ; 
			System.out.println("Version1:") ;
			for (Object modelV1Element : modelV1.allInstances()) {
			    itemLabelProvider = (IItemLabelProvider)adapterFactory.adapt(modelV1Element, IItemLabelProviderClass);
			    String leftText =    itemLabelProvider.getText(modelV1Element) ; 
			    		   
			    treeItemContentProvider = (ITreeItemContentProvider)adapterFactory.adapt(modelV1Element, ITreeItemContentProviderClass);
			    Object leftParent = treeItemContentProvider.getParent(modelV1Element) ; 
			    itemLabelProvider = (IItemLabelProvider)adapterFactory.adapt(leftParent, IItemLabelProviderClass);
			    String leftParentText = itemLabelProvider.getText(leftParent);
			    
			    System.out.println(leftText + "  : " + leftParentText); 
			}

			
			System.out.println("Version2:") ;
			for (Object modelV2Element : modelV2.allInstances()) {
			    itemLabelProvider = (IItemLabelProvider)adapterFactory.adapt(modelV2Element, IItemLabelProviderClass);
			    String leftText =    itemLabelProvider.getText(modelV2Element) ; 
			    		   
			    treeItemContentProvider = (ITreeItemContentProvider)adapterFactory.adapt(modelV2Element, ITreeItemContentProviderClass);
			    Object leftParent = treeItemContentProvider.getParent(modelV2Element) ; 
			    itemLabelProvider = (IItemLabelProvider)adapterFactory.adapt(leftParent, IItemLabelProviderClass);
			    String leftParentText = itemLabelProvider.getText(leftParent);
			    
			    System.out.println(leftText + "  : " + leftParentText); 
			}
	*/		
			
			//********** Conflict Detection & Resolution with EVL Script ***********
			if(this.DetectionPatternScriptFC.path != null){
				try {
					Validate(bModel, modelV1, modelV2, this.DetectionPatternScriptFC.path);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(this.ResolutionPatternScriptFC.path != null)
			{
				;
			}
			
			try { 
				if(this.MergingScriptSelectionPage != null)
					this.MergingScriptSelectionPage.onLoad(selectedProject, this.MetaModelFC, this.BaseModelFC, modelV1, modelV2);
		     		
			} catch (Exception ex) {
				System.out.println("Error validation when pressing Next: " + ex);
			}
			return true ; 
    	}
		return false ; 
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
//					Collection<EObject> tes = V2.getAllOfKind(classType1) ; 
				
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
//		for(EStructuralFeature esf : V1.eClass().getEAllStructuralFeatures())
		{		
			if(V2.eClass().getEAllAttributes().contains(esf))
//			if(V2.eClass().getEAllStructuralFeatures().contains(esf))
				if(V2.eGet(esf)!=null){
					String st = esf.getName() ; 
					if(st.equals("visibility") == false)
//						if(esf.isChangeable() && V1.eIsSet(esf))
						if(esf.isChangeable())
							V1.eSet(esf, V2.eGet(esf));
				}	
		}
	}

	
	private void Validate(IModel baseModel, IModel leftModel, 
			IModel rightModel, String evlFile) throws Exception{
		baseModel.setName("Base");
		leftModel.setName("Left");
		rightModel.setName("Right");
	
		EvlModule evlModule = new EvlModule() ; 
		
		String st = URI.createFileURI(evlFile).toString(); 
		evlFile = evlFile.replaceAll("\\\\", "/");
		java.net.URI evlURI = java.net.URI.create("file:/"+evlFile) ; 
		evlModule.parse(evlURI) ; 
//		java.net.URI b = java.net.URI.create(evlFile) ; 
//		evlModule.parse(b) ;
//		evlModule.parse(EpsilonStandalone.class.getResource(evlFile).toURI());
		evlModule.getContext().getModelRepository().addModel(baseModel);
		evlModule.getContext().getModelRepository().addModel(leftModel);
		evlModule.getContext().getModelRepository().addModel(rightModel);

		evlModule.execute()  ;
		evlModule.setUnsatisfiedConstraintFixer(new ValidationViewFixer());
		evlModule.setUnsatisfiedConstraintFixer(new IEvlFixer() {
			public void fix(IEvlModule module) throws EolRuntimeException {
				// Do nothing
			}
		});
		
		Collection<UnsatisfiedConstraint> unsatisfied = evlModule.getContext().getUnsatisfiedConstraints();
		
		if (unsatisfied.size() > 0) {
			System.out.println("Befooorrrrrrrrr") ; 
			
			IWorkbench wb = PlatformUI.getWorkbench();
			IWorkbenchWindow win = wb.getActiveWorkbenchWindow();

			System.err.println(unsatisfied.size() + " constraint(s) have not been satisfied");
			int i = 0 ; 
			for (UnsatisfiedConstraint uc : unsatisfied) {
				ArrayList<String> StList = new ArrayList<String>() ;
				i++ ; 
				String title = i + " of " + unsatisfied.size() + " conflict(s) pattern that have been detected" ; 
				StList.add("No Fix Required ...") ; 
				System.err.println(uc.getMessage());
				for(FixInstance fi : uc.getFixes()){
					
					System.out.println(fi.getTitle()) ;
//					fi.perform();
					StList.add(fi.getTitle()) ; 
				}
				
				UserInterDialog UD = new UserInterDialog(win.getShell(),"Select a fix for <" + uc.getMessage() + ">", title, StList) ;
				UD.open() ; 
				
				String message = UD.result ; 
				int index = UD.index ; 
				if(index != 0)
					uc.getFixes().get(index-1).perform(); 
				///// run fix
			}
		}
		else {
			System.out.println("All constraints have been satisfied");
		}
		
		
//		IWorkbench wb = PlatformUI.getWorkbench();
//	   	IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
//		ValidationView VV = new ValidationView(win.getShell(), this.selectedProject) ;	
//		VV.unSConstraint = unsatisfied ; 
//		VV.open() ; 
		

		
			
		System.out.println("Afterrrrrrrrrrrrrr");

	   	
	   	
//		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().
//				showView("org.eclipse.epsilon.evl.dt.views.ValidationView");
		
//		evlModule.setUnsatisfiedConstraintFixer(new IEvlFixer() {
//			public void fix(IEvlModule module) throws EolRuntimeException {
//				// Do nothing
//			}
//});
		
//		ValidationViewFixer fixer = new ValidationViewFixer();
//		validationView.fix(evlModule, fixer);
	}
	
}
