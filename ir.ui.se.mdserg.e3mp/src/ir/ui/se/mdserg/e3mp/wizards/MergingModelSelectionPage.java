package ir.ui.se.mdserg.e3mp.wizards;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.eclipse.core.resources.IProject;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.EPackage.Registry;
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
import org.eclipse.emf.edit.provider.ReflectiveItemProviderAdapterFactory;
import org.eclipse.emf.edit.provider.resource.ResourceItemProviderAdapterFactory;
import org.eclipse.epsilon.common.util.StringProperties;
import org.eclipse.epsilon.emc.emf.EmfModel;
import org.eclipse.epsilon.emc.emf.InMemoryEmfModel;
import org.eclipse.epsilon.eol.exceptions.models.EolModelLoadingException;
import org.eclipse.epsilon.eol.models.IRelativePathResolver;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.resource.UMLResource;
import org.eclipse.uml2.uml.resources.util.UMLResourcesUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;


public class MergingModelSelectionPage extends WizardPage {
	public String path ; 
	private Composite container;

	private Group MMSection;
	private Group BMSection;
	private Group V1MSection;
	private Group V2MSection; 

	public FileChooser MetaModelFC ;
	public FileChooser BaseModelFC ;  
	public FileChooser Version1ModelFC ;
	public FileChooser Version2ModelFC ;  
	
	public IProject selectedProject;
	public InferringScriptSelectionPage InferringScriptSelectionPage ;

	public EmfModel bModel = null ; 
	public EmfModel modelV1 = null ; 
	public EmfModel modelV2 = null ; 
	
    protected MergingModelSelectionPage(String pageName) {
             super(pageName);
             setTitle("Model Selection");
             setDescription("Please select required files");
             
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
		BMSection.setText("Select Base model");
		BMSection.setLayout(new GridLayout());
		BMSection.setLayoutData(data);
		BaseModelFC = new FileChooser(BMSection) ; 
		BaseModelFC.Extensions = "*.xmi; *.model; *.uml; *.ecore" ; 
		BaseModelFC.ExtensionsName = "Model" ; 
		GridData BMgridData = new GridData(GridData.FILL_HORIZONTAL) ; 
		BaseModelFC.setLayoutData(BMgridData);
		
		V1MSection = new Group(container, SWT.NULL);
		V1MSection.setText("Select Version 1");
		V1MSection.setLayout(new GridLayout());
		V1MSection.setLayoutData(data);
		Version1ModelFC = new FileChooser(V1MSection) ; 
		Version1ModelFC.Extensions = "*.xmi; *.model; *.uml; *.ecore" ; 
		Version1ModelFC.ExtensionsName = "Model" ; 
		GridData V1MgridData = new GridData(GridData.FILL_HORIZONTAL) ; 
		Version1ModelFC.setLayoutData(V1MgridData);

		V2MSection = new Group(container, SWT.NULL);
		V2MSection.setText("Select Version 2");
		V2MSection.setLayout(new GridLayout());
		V2MSection.setLayoutData(data);
		Version2ModelFC = new FileChooser(V2MSection) ; 
		Version2ModelFC.Extensions = "*.xmi; *.model; *.uml; *.ecore" ; 
		Version2ModelFC.ExtensionsName = "Model" ; 
		GridData V2MgridData = new GridData(GridData.FILL_HORIZONTAL) ; 
		Version2ModelFC.setLayoutData(V2MgridData);
		
		setControl(container);		
	}

	public void setSelectedProject(IProject project) {
		this.selectedProject = project;
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
		if(((this.MetaModelFC.path == null) && this.BaseModelFC.path.endsWith(".uml") == false && this.BaseModelFC.path.endsWith(".ecore") == false)
				|| (this.BaseModelFC.path == null) || this.Version1ModelFC.path == null || this.Version2ModelFC.path == null){
			this.setDescription("You must select all the required files ...");
		}
		else
		{	
			boolean umlFlag = false , ecoreFlag = false ;
			if(BaseModelFC.path.endsWith(".uml") && Version1ModelFC.path.endsWith(".uml") && 
					Version2ModelFC.path.endsWith(".uml"))
				umlFlag = true ; 
			else if(this.BaseModelFC.path.endsWith(".ecore") && this.Version1ModelFC.path.endsWith(".ecore") && 
					this.Version2ModelFC.path.endsWith(".ecore"))
				ecoreFlag = true ; 

			if(MetaModelFC.getText()== "")
				MetaModelFC.path = null ;
			if((MetaModelFC.path == null && umlFlag==false && ecoreFlag==false) || BaseModelFC.path == null || Version1ModelFC.path == null || Version2ModelFC.path == null){
				this.setDescription("You must select all the required files ...");
			}
			else
			{
				//************** Register Meta Model ***********	
				if(this.BaseModelFC.path.endsWith(".uml") == false && ecoreFlag == false)
				{
					try {
						registerMetamodel(this.MetaModelFC.path);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				ComposedAdapterFactory composedAdapterFactory ; 
	
				ArrayList<AdapterFactory> factories = new ArrayList<AdapterFactory>();
				factories.add(new ResourceItemProviderAdapterFactory());
				factories.add(new EcoreItemProviderAdapterFactory());
				factories.add(new ReflectiveItemProviderAdapterFactory());
				
				if(this.BaseModelFC.path.endsWith(".uml") == true)
					composedAdapterFactory = new ComposedAdapterFactory(ComposedAdapterFactory.Descriptor.Registry.INSTANCE);
				else
					composedAdapterFactory = new ComposedAdapterFactory(factories);
	
				
				if(this.BaseModelFC.path.endsWith(".uml")){
					try {
						bModel = getUmlModel("Base", new File(this.BaseModelFC.path));
						modelV1 = getUmlModel("Version1", new File(this.Version1ModelFC.path)) ; 
						modelV2 = getUmlModel("Version2", new File(this.Version2ModelFC.path));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}		
				
				}else if(ecoreFlag == true){
					try {
						bModel = getEcoreModel("Base", new File(this.BaseModelFC.path));
						modelV1 = getEcoreModel("Version1", new File(this.Version1ModelFC.path)) ; 
						modelV2 = getEcoreModel("Version2", new File(this.Version2ModelFC.path)) ; 
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else{
					try {
						bModel = getEmfModel("Base",this.BaseModelFC.path, this.MetaModelFC.path, true, false);
						modelV1 = getEmfModel("Version1",this.Version1ModelFC.path, this.MetaModelFC.path, true, false);
						modelV2 = getEmfModel("Version2",this.Version2ModelFC.path, this.MetaModelFC.path, true, false);	
					} catch (EolModelLoadingException | URISyntaxException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				bModel.getAliases().add("Source"); 	
				modelV1.getAliases().add("Source");
				modelV2.getAliases().add("Source");
				
				try { 
					if(this.InferringScriptSelectionPage != null)
						this.InferringScriptSelectionPage.onLoad(selectedProject, bModel,
								modelV1, modelV2, MetaModelFC, BaseModelFC, composedAdapterFactory);
			     		
				} catch (Exception ex) {
					System.out.println("Error validation when pressing Next: " + ex);
				}
				return true;
			}
	 	}
		return false ; 
	}
	 
	public void onLoad(IProject selectedPrj)
	{
		this.selectedProject = selectedPrj ;
		this.path = selectedPrj.getLocation().toString() ; 
 
		MetaModelFC.directory = this.path ;
		BaseModelFC.directory = this.path ; 
		Version1ModelFC.directory = this.path ; 
		Version2ModelFC.directory = this.path ; 
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
	
	private void registerMetamodel(String MetaModelPath)
	{
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put(
		    "*", new EcoreResourceFactoryImpl());

		ResourceSet rs = new ResourceSetImpl();
		final ExtendedMetaData extendedMetaData = new BasicExtendedMetaData(EPackage.Registry.INSTANCE);
		rs.getLoadOptions().put(XMLResource.OPTION_EXTENDED_META_DATA,
		    extendedMetaData);

		java.net.URI javaURI = new File(MetaModelPath).toURI() ; 
		org.eclipse.emf.common.util.URI uri = org.eclipse.emf.common.util.URI.createURI(javaURI.toString());
		
		Resource r = rs.getResource(uri, true);
		EObject eObject = r.getContents().get(0);
		if (eObject instanceof EPackage) {
		    EPackage p = (EPackage)eObject;
		    EPackage.Registry.INSTANCE.put(p.getNsURI(), p);
		}
		
		Registry packageRegistry = rs.getPackageRegistry();
		EPackage pack = (EPackage) packageRegistry.get(EcorePackage.eINSTANCE.getNsURI());
		if (!(pack instanceof EcorePackage)) {
			packageRegistry.put(EcorePackage.eINSTANCE.getNsURI(), EcorePackage.eINSTANCE);
		}
	}
	
}
