package ir.ui.se.mdserg.e3mp.wizards;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.eclipse.core.resources.IProject;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.epsilon.common.util.StringProperties;
import org.eclipse.epsilon.ecl.trace.MatchTrace;
import org.eclipse.epsilon.emc.emf.EmfModel;
import org.eclipse.epsilon.emc.emf.EmfUtil;
import org.eclipse.epsilon.emc.emf.InMemoryEmfModel;
import org.eclipse.epsilon.eol.exceptions.EolRuntimeException;
import org.eclipse.epsilon.eol.exceptions.models.EolModelLoadingException;
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

import ir.ui.se.mdserg.e3mp.helper.CompareModels;
import ir.ui.se.mdserg.e3mp.helper.TreeView;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;


public class MatchingModelSelectionPage extends WizardPage {
	public String path ; 
	private Composite container;
	private Group MMSection;
	private Group BMSection;
	private Group VMSection;
	private Group EclSection; 
	
	public FileChooser MetaModelFC ;
	public FileChooser BaseModelFC ; 
	public FileChooser VersionModelFC ; 
	public FileChooser EclScriptFC ; 
	
	public IProject selectedProject;
	private CompareModels comparsion;


    protected MatchingModelSelectionPage(String pageName) {
             super(pageName);
             setTitle("Model Selection");
             setDescription("Please select required files ...");
    }
	
	@Override
	public void createControl(Composite parent) {
		// TODO Auto-generated method stub
//		
		container = new Composite(parent, SWT.NULL);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		container.setLayoutData(data);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		
		MMSection = new Group(container, SWT.NULL);
		MMSection.setText("Select the Meta-Model for Non-UML models");
		MMSection.setLayout(new GridLayout());
		MMSection.setLayoutData(data);
		MetaModelFC = new FileChooser(MMSection) ; 
		MetaModelFC.Extensions = "*.ecore" ; 
		MetaModelFC.ExtensionsName = "Ecore" ; 
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL) ; 
		MetaModelFC.setLayoutData(gridData);
		
		BMSection = new Group(container, SWT.NULL);
		BMSection.setText("Select Base Model");
		BMSection.setLayout(new GridLayout());
		BMSection.setLayoutData(data);
		BaseModelFC = new FileChooser(BMSection) ; 
		BaseModelFC.Extensions = "*.xmi; *.model; *.uml; *.ecore" ; 
		BaseModelFC.ExtensionsName = "Model" ; 
		GridData BMgridData = new GridData(GridData.FILL_HORIZONTAL) ; 
		BaseModelFC.setLayoutData(BMgridData);

		VMSection = new Group(container, SWT.NULL);
		VMSection.setText("Select New Version");
		VMSection.setLayout(new GridLayout());
		VMSection.setLayoutData(data);
		VersionModelFC = new FileChooser(VMSection) ; 
		VersionModelFC.Extensions = "*.xmi; *.model; *.uml; *.ecore" ; 
		VersionModelFC.ExtensionsName = "Model" ; 
		GridData VMgridData = new GridData(GridData.FILL_HORIZONTAL) ; 
		VersionModelFC.setLayoutData(VMgridData);

		EclSection = new Group(container, SWT.NULL);
		EclSection.setText("Select ECL File");
		EclSection.setLayout(new GridLayout());
		EclSection.setLayoutData(data);
		EclScriptFC = new FileChooser(EclSection) ; 
		EclScriptFC.Extensions = "*.ecl" ; 
		EclScriptFC.ExtensionsName = "ECL Script" ; 
		GridData EclgridData = new GridData(GridData.FILL_HORIZONTAL) ; 
		EclScriptFC.setLayoutData(EclgridData);
		
		setControl(container);		
	}

	public void setSelectedProject(IProject project) {
		this.selectedProject = project;
	}
	
	public void onLoad(IProject selectedPrj)
	{
		this.selectedProject = selectedPrj ;
		this.path = selectedPrj.getLocation().toString() ; 

		MetaModelFC.directory = this.path ; 
		BaseModelFC.directory = this.path ; 
		VersionModelFC.directory = this.path ; 
		EclScriptFC.directory = this.path ; 
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
		boolean umlFlag = false, ecoreFlag = false ; 
		if(this.MetaModelFC.getText()== "")
			this.MetaModelFC.path = null ; 
		if(this.BaseModelFC.path.endsWith(".uml") && this.VersionModelFC.path.endsWith(".uml"))
			umlFlag = true ; 
		else if(this.BaseModelFC.path.endsWith(".ecore") && this.VersionModelFC.path.endsWith(".ecore"))
			ecoreFlag = true ; 

		if((this.MetaModelFC.path == null && umlFlag == false && ecoreFlag == false) || this.BaseModelFC.path == null 
				|| this.VersionModelFC.path == null || this.EclScriptFC.path == null){
			this.setDescription("You must select all the required files ...");
		}
		else
		{	
			//************ Register Meta Model *************	
			if(umlFlag == false && ecoreFlag == false)
			{
				try {
					EmfUtil.register(URI.createFileURI(this.MetaModelFC.path), EPackage.Registry.INSTANCE);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			//********** Comparison with ECL Script ***********
			InMemoryEmfModel baseModel = null ; 
			InMemoryEmfModel newVersion = null ; 
					
			if(umlFlag == true){
				try {
					baseModel = getUmlModel("Base", new File(this.BaseModelFC.path));
					newVersion = getUmlModel("NewVersion", new File(this.VersionModelFC.path)) ; 
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}		
			}else if(ecoreFlag == true){
				try {
					baseModel = getEcoreModel("Base", new File(this.BaseModelFC.path));
					newVersion = getEcoreModel("NewVersion", new File(this.VersionModelFC.path)) ; 
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else{
				try {
					baseModel = getInMemoryEmfModel("Base",new File(this.BaseModelFC.path),
							this.MetaModelFC.path);
					newVersion = getInMemoryEmfModel("NewVersion",new File(this.VersionModelFC.path),
							this.MetaModelFC.path);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			baseModel.setName("Left");
			newVersion.setName("Right");
			
			comparsion = new CompareModels(baseModel, newVersion, this.EclScriptFC.path) ;
			
			MatchTrace MatchesWithoutNull = comparsion.delNullmatch(comparsion.matchTraces.getMatches());
			
			//*********************************************************
			
			MatchTrace ReducedMatched = MatchesWithoutNull.getReduced();  
			ReducedMatched = comparsion.delNullmatch(ReducedMatched.getMatches());
			
			IWorkbench wb = PlatformUI.getWorkbench();
		   	IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
			TreeView TV = new TreeView(win.getShell(), baseModel, newVersion, 
				this.MetaModelFC.path, this.selectedProject , ReducedMatched) ;
			
			if(TV.open() == Window.OK)
			{
				return true ; 
			}
			
		}	
		return false;
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
	
	public InMemoryEmfModel getInMemoryEmfModel(String name, File modelFile, String metamodelPath) throws IOException
	{
		ResourceSet rs = new ResourceSetImpl();
		rs.getPackageRegistry().put(EcorePackage.eINSTANCE.getNsURI(), EcorePackage.eINSTANCE);
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", new XMIResourceFactoryImpl());
		Resource r = null;
		EPackage metamodel = null;
		
		java.net.URI javaURI = new File(metamodelPath).toURI() ; 
		
			r = rs.createResource(URI.createURI(javaURI.toString()));
			r.load(Collections.emptyMap());
			metamodel = (EPackage) r.getContents().get(0);

			rs = new ResourceSetImpl();
			rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", new XMIResourceFactoryImpl());
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
	
}
