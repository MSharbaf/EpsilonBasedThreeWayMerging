package ir.ui.se.mdserg.e3mp.helper;

import java.awt.Frame;
import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.epsilon.common.util.StringProperties;
import org.eclipse.epsilon.ecl.EclModule;
import org.eclipse.epsilon.ecl.dom.MatchRule;
import org.eclipse.epsilon.ecl.trace.Match;
import org.eclipse.epsilon.ecl.trace.MatchTrace;
import org.eclipse.epsilon.emc.emf.EmfModel;
import org.eclipse.epsilon.emc.emf.InMemoryEmfModel;
import org.eclipse.epsilon.eol.exceptions.models.EolModelLoadingException;
import org.eclipse.epsilon.eol.models.IModel;
import org.eclipse.epsilon.eol.models.IRelativePathResolver;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.resource.UMLResource;
import org.eclipse.uml2.uml.resources.util.UMLResourcesUtil;


public class CompareModels {
	public MatchTrace matchTraces ; 
	
	
//	public CompareModels(String metaModelPath, String baseModelPath, String baseModelAliases,
//			String newVersionPath, String newVersionAliases, String eclFilePath ){

	public CompareModels(InMemoryEmfModel baseModel,InMemoryEmfModel newVersion, String eclFilePath ){

//		InMemoryEmfModel baseModel = null ; 
//		InMemoryEmfModel newVersion = null ; 
//		
		
		try {
//			if(metaModelPath == null){
//				baseModel = getUmlModel("Base", new File(baseModelPath));		
//				newVersion = getUmlModel("NewVersion", new File(newVersionPath)) ; 
//			}else{
//				baseModel = (InMemoryEmfModel)getEmfModel("Base", baseModelPath, metaModelPath, true, false) ; 
//				newVersion = (InMemoryEmfModel)getEmfModel("NewVersion", newVersionPath, metaModelPath, true, false) ; 
//			}
			
//			baseModel.setName(baseModelAliases);
//			newVersion.setName(newVersionAliases);
			
			EclModule eclModule = new EclModule();
//			eclModule.parse(URI.createFileURI(eclFilePath).toString()) ; 
			String st = URI.createFileURI(eclFilePath).toString(); 
			eclFilePath = eclFilePath.replaceAll("\\\\", "/");
			java.net.URI eclURI = java.net.URI.create("file:/"+eclFilePath) ; 
			eclModule.parse(eclURI) ; 
		//	eclModule.parse(EpsilonStandalone.class.getResource(eclFile).toURI());
			eclModule.getContext().getModelRepository().addModel(baseModel);
			eclModule.getContext().getModelRepository().addModel(newVersion);
			eclModule.execute();
	
			this.matchTraces = eclModule.getContext().getMatchTrace(); 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public CompareModels(IModel baseModel,IModel newVersion, String eclFilePath ){
		try {
			EclModule eclModule = new EclModule();
//			eclModule.parse(URI.createFileURI(eclFilePath).toString()) ; 
			String st = URI.createFileURI(eclFilePath).toString(); 
			eclFilePath = eclFilePath.replaceAll("\\\\", "/");
			java.net.URI eclURI = java.net.URI.create("file:/"+eclFilePath) ; 
			eclModule.parse(eclURI) ; 
		//	eclModule.parse(EpsilonStandalone.class.getResource(eclFile).toURI());
			eclModule.getContext().getModelRepository().addModel(baseModel);
			eclModule.getContext().getModelRepository().addModel(newVersion);
			eclModule.execute();
	
			this.matchTraces = eclModule.getContext().getMatchTrace(); 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
//	public MatchTrace Comparison(IModel leftModel, IModel rightModel, String eclFile) throws Exception {
//		leftModel.setName("Left");
//		rightModel.setName("Right");
//		EclModule eclModule = new EclModule();
//		java.net.URI b = java.net.URI.create(eclFile) ; 
//		eclModule.parse(b) ; 
//	//	eclModule.parse(EpsilonStandalone.class.getResource(eclFile).toURI());
//		eclModule.getContext().getModelRepository().addModel(leftModel);
//		eclModule.getContext().getModelRepository().addModel(rightModel);
//		eclModule.execute();
//
//		MatchTrace Matched = eclModule.getContext().getMatchTrace(); 
//		return Matched ; 
//	}
	
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
	
	public MatchTrace getUnmatch(List<Match> matches) { 
		MatchTrace reduced = new MatchTrace();
		for (Match match : matches) {
			if (!match.isMatching()) {
				reduced.getMatches().add(match);
			}
		}
		return reduced;
	}
	
	public void printMT(MatchTrace MT, String title)
	{
		System.out.println("************" + title + "***************");
		System.out.println(MT.getMatches().size()) ; 
		for (Match match : MT.getMatches()) { 
			System.out.print("Left is : " + match.getLeft().toString()) ; 
			System.out.println(" <=> Right is : " + match.getRight().toString()) ; 
		}
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
	
	protected EmfModel getEmfModel(String name, String model, 
			String metamodel, boolean readOnLoad, boolean storeOnDisposal) 
					throws EolModelLoadingException, URISyntaxException {
		EmfModel emfModel = new EmfModel();
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
	
	public File delelteMatchedElements(File file, MatchTrace mt)
	{
		
		
		return file ; 
	}


	public MatchTrace userComparison(MatchTrace Matched, MatchTrace UnMatch, InMemoryEmfModel leftModel, InMemoryEmfModel rightModel) throws Exception
	{
	//	System.out.println("************user Comparison***************");
		for(Match EachFalseMatch : UnMatch.getMatches()){ 
			for	(Match EachTrueMatch : Matched.getMatches()){
				if(EachFalseMatch.getLeft().equals(EachTrueMatch.getLeft()) 
						|| EachFalseMatch.getRight().equals(EachTrueMatch.getRight()) ){
						// Element is existed in match list and don't need to check for adding it to this list. 
						EachFalseMatch.setMatching(true);
						break ;
				}
			}
		}		
		List<MatchTrace> UnMTArray = new ArrayList<MatchTrace>() ;  
		for (Match EachFalseMatch : UnMatch.getMatches()) {
			if(!EachFalseMatch.isMatching())
			{
				Boolean flag = true ; 
				for (int i = 0; i < UnMTArray.size() ; i++) {
					if(UnMTArray.get(i).getMatches().get(0).getLeft().equals(EachFalseMatch.getLeft())){
						UnMTArray.get(i).getMatches().add(EachFalseMatch) ; 
						flag = false ; 
						break ;
					}
				}
				if(flag){
					MatchTrace nMT = new MatchTrace() ;
					nMT.getMatches().add(EachFalseMatch); 
					UnMTArray.add(nMT) ; 
				}
			}
		}
		ArrayList<Match> msgList = new ArrayList<Match>() ;
		for (int i = 0; i < UnMTArray.size() ; i++) {
			Match firstMatch = UnMTArray.get(i).getMatches().get(0) ;
			
			if(haveMatchParameter(firstMatch.getLeft().toString(), "name")){
				String classType = firstMatch.getLeft().getClass().getSimpleName(); 
				classType = classType.replace("Impl", "") ;
				
				String 	lName = getMatchParameter(firstMatch.getLeft().toString(), "name") ;
				String lOwnerName = ""; 
				String lOwnerType = "" ;
				for (EObject rawElement : leftModel.getAllOfKind(classType)) {
					org.eclipse.uml2.uml.Element umlElement = (org.eclipse.uml2.uml.Element)rawElement;
					//org.eclipse.uml2.uml.NamedElement umlElement = (org.eclipse.uml2.uml.NamedElement)rawElement;
					if(firstMatch.getLeft().equals(umlElement)){
	//				if(lName.equals(umlElement.getName())){
						lOwnerName = getMatchParameter(umlElement.getOwner().toString(), "name") ; 
					 //   System.out.println("New name: " + umlElement.getOwner());
					    lOwnerType = umlElement.getOwner().getClass().getSimpleName() ; 
					    lOwnerType = lOwnerType.replace("Impl","") ; 
					    break ;
					}
				}
				String leftSt = classType + " <" + lName + "> with owner " + lOwnerType +
						"<" + lOwnerName + "> in Model <" + leftModel.getName() + "> "; 
				
				ArrayList<String> StList = new ArrayList<String>() ;
				ArrayList<String> tmpList = new ArrayList<String>() ;
				StList.add("No One") ; 
				tmpList.add("No One");
				for(Match iMatch : UnMTArray.get(i).getMatches()){
					boolean msgFlag = true ; 
					for(Match msg : msgList)
						if(msg.getRight().equals(iMatch.getRight()))
						{
							msgFlag = false ; 
							break ;
						}
					if(msgFlag){						
						String rName = getMatchParameter(iMatch.getRight().toString(), "name") ;
						String rOwnerName = ""; 
						String rOwnerType = "" ;
						for (EObject rawElement : rightModel.getAllOfKind(classType)) {
							org.eclipse.uml2.uml.Element umlElement = (org.eclipse.uml2.uml.Element)rawElement;
							//org.eclipse.uml2.uml.NamedElement umlElement = (org.eclipse.uml2.uml.NamedElement)rawElement;
							if(iMatch.getRight().equals(umlElement)){
		//					if(rName.equals(umlElement.getName())){
								rOwnerName = getMatchParameter(umlElement.getOwner().toString(), "name") ; 
							//    System.out.println("New name: " + umlElement.getOwner());
							    rOwnerType = umlElement.getOwner().getClass().getSimpleName() ; 
							    rOwnerType = rOwnerType.replace("Impl","") ; 
							    break ; 
							}
						}
						String rightSt = classType + " <" + rName + "> with owner " + rOwnerType +
								"<" + rOwnerName + "> in Model <" + rightModel.getName() + "> ";  
						StList.add(rightSt);
						tmpList.add(rightSt) ; 
					}
					else
					{
						tmpList.add(" ") ; 
					}
				}
				
				if(StList.size() > 1){
				//	WaitForGUI userInteractionGUI = new WaitForGUI("Which one is equivalent to this?",leftSt, StList);
				//	String message = userInteractionGUI.getStringFromGUI() ; 
					
					System.out.println("Befooorrrrrrrrr") ; 
					
				   IWorkbench wb = PlatformUI.getWorkbench();
				   IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
//					   IWorkbenchPage page = win.getActivePage();
					
					UserInterDialog UD = new UserInterDialog(win.getShell(),"Which one is equivalent to { ",leftSt, StList) ;
//					UserInterDialog UD = new UserInterDialog(win.getShell(),"Which one is equivalent to {" + leftSt + " }", StList) ;

					UD.open() ; 
					
					String message = UD.result ; 
					
					System.out.println("Afterrrrrrrrrrrrrr");

					
				    for (int j = 0; j < tmpList.size(); j++) {
						if(tmpList.get(j).equals(message))
						{
							if(j>0)
							{
								Matched.getMatches().add(UnMTArray.get(i).getMatches().get(j-1)); 
								msgList.add(UnMTArray.get(i).getMatches().get(j-1)) ; 
								break ;
							}
						}
					}
				}
				
			}
		}
		return Matched  ;
	}
	
	public boolean haveMatchParameter(String matchString, String parameter)
	{	
		int start = matchString.indexOf(parameter) ;
		if(start == -1)
			return false ; 
		
		return true ; 
	}
	
	public String getMatchParameter(String matchString, String parameter)
	{
/*		int start = matchString.indexOf(parameter) ; 
		int end = matchString.indexOf(",") ;
		int len = parameter.length() ;
		len = len + 2 ; 
		String res = matchString.substring(start + len , end);
		return res ; */
		
		int start = matchString.indexOf(parameter) ;
		matchString = matchString.substring(start) ; 
		int end = matchString.indexOf(",") ; 
		int len = parameter.length() ;
		len = len + 2 ; 
		String res = matchString.substring(len , end);
		return res ; 
	}
		
	public void setChanges(Match EachMatch1, Match EachMatch2, InMemoryEmfModel Model) throws Exception
	{
		String classType = EachMatch1.getLeft().getClass().getSimpleName() ; 
		classType = classType.replace("Impl", "") ;
		
//		String oldName = getMatchParameter(EachMatch1.getRight().toString(), "name") ;   
		String newName = getMatchParameter(EachMatch2.getRight().toString(), "name") ;
		for (EObject rawElement : Model.getAllOfKind(classType)) {
			org.eclipse.uml2.uml.NamedElement umlElement = (org.eclipse.uml2.uml.NamedElement) rawElement ; 
			if (EachMatch1.getRight().equals(umlElement)) {				
			//	System.out.println("Old name: " + umlElement.getName());
				umlElement.setName(newName);
			//	System.out.println("New name: " + umlElement.getName());
			}
		}
		
	}

	
//	protected EmfModel getEmfModelByURI(String name, String model, 
//			String metamodel, boolean readOnLoad, boolean storeOnDisposal) 
//					throws EolModelLoadingException, URISyntaxException {
//		EmfModel emfModel = new EmfModel();
//		StringProperties properties = new StringProperties();
//		properties.put(EmfModel.PROPERTY_NAME, name);
//		properties.put(EmfModel.PROPERTY_METAMODEL_URI, metamodel);
//		properties.put(EmfModel.PROPERTY_MODEL_URI,URI.createFileURI(model).toString());
//		properties.put(EmfModel.PROPERTY_READONLOAD, readOnLoad + "");
//		properties.put(EmfModel.PROPERTY_STOREONDISPOSAL, 
//				storeOnDisposal + "");
//		emfModel.load(properties, (IRelativePathResolver) null);
//		return emfModel;
//	}


}
