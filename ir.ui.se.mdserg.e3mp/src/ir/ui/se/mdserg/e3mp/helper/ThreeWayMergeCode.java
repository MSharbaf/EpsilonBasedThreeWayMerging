package ir.ui.se.mdserg.e3mp.helper;

import java.awt.Frame;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JFrame;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.epsilon.ecl.EclModule;
import org.eclipse.epsilon.ecl.dom.MatchRule;
import org.eclipse.epsilon.ecl.trace.Match;
import org.eclipse.epsilon.ecl.trace.MatchTrace;
import org.eclipse.epsilon.emc.emf.EmfModel;
import org.eclipse.epsilon.emc.emf.InMemoryEmfModel;
import org.eclipse.epsilon.eml.EmlModule;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.*;  
import org.eclipse.uml2.uml.resource.UMLResource;
import org.eclipse.epsilon.eol.models.IModel;
import org.eclipse.epsilon.evl.EvlModule;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.uml2.uml.resources.util.UMLResourcesUtil;

import ir.ui.se.mdserg.e3mp.helper.*;

public class ThreeWayMergeCode {

	
//	public static String Base = "C:\\Users/MohammadReza/Desktop/Example/Case2/Base.uml" ; 
//	public static String Left = "C:\\Users/MohammadReza/Desktop/Example/Case2/Dev1.uml" ; 
//	public static String Right = "C:\\Users/MohammadReza/Desktop/Example/Case2/Base.uml" ;
//	public static String Target = "C:\\Users/MohammadReza/Desktop/Example/Case2/Merged.uml";
//	public static String Ecl = "C:/Users/MohammadReza/Desktop/Example/Comp.ecl"; 
//	public static String Eml = "C:/Users/MohammadReza/Desktop/Example/Merge.eml"; 
//	public static String Evl ="C:/Users/MohammadReza/Desktop/Example/Validation.evl"; 
	//public static JFrame frame = this ;
	
//	public static void main(String[] args) throws Exception {
//		// TODO Auto-generated method stub
//		ThreeWayMergeCode t = new ThreeWayMergeCode(Base, Left, Right, Target, Ecl, Eml, Evl, null);
//		t.run();
//	}
	
	private String addrBase, addrLeft, addrRight, addrTarget, addrECL, addrEML, addrEVL; 
	public Frame parent ; 

	
	public ThreeWayMergeCode(){
		
	}
	
	public ThreeWayMergeCode(String Base, String Left, String Right, String Target, String Ecl, String Eml, String Evl, Frame par) {
		// TODO Auto-generated constructor stub
		addrBase = Base ; 
		addrLeft = Left ; 
		addrRight = Right ; 
		addrTarget = Target ;
		addrECL = "file:/" + Ecl ; 
		addrEML = "file:/" + Eml ; 
		addrEVL = "file:/" + Evl ; 
		parent = par ; 
	}

	public void run() throws Exception {		
		System.out.println("Start") ; 
		
		String BMName, LMName, RMName ; 
		BMName = "BaseModel" ;
		LMName = "LeftModel" ;
		RMName = "RightModel" ; 
		
	// load Models
		InMemoryEmfModel baseModel = getUmlModel(BMName, new File(addrBase));
		InMemoryEmfModel leftModel = getUmlModel(LMName, new File(addrLeft));
		InMemoryEmfModel rightModel = getUmlModel(RMName, new File(addrRight));
		
		InMemoryEmfModel mergedModel = getUmlModel("Target", new File(addrTarget));
		mergedModel.setStoredOnDisposal(true);
		
		baseModel.getAliases().add("Source"); 	
		leftModel.getAliases().add("Source");
		rightModel.getAliases().add("Source");
		mergedModel.getAliases().add("Target");

		System.out.println("Loaded") ; 
		
	//Compare Base and Left models and modify matchTraces by User decision 
		MatchTrace MT1 = Comparison(baseModel, leftModel, addrECL) ; 
		baseModel.setName(BMName);
		leftModel.setName(LMName);
		MatchTrace Matched1 = MT1.getReduced() ; 
		Matched1 = delNullmatch(Matched1.getMatches());
		MatchTrace UnMatch1 = getUnmatch(MT1.getMatches());
		
		//printMT(Matched1, "Matched1");
		//printMT(UnMatch1, "UnMatched1");

		Matched1 = userComparison(Matched1, UnMatch1, baseModel, leftModel); 
		//printMT(Matched1, "Final Matched1");
		
		
	//Compare Base and Right models and modify matchTraces by User decision 
		MatchTrace MT2 = Comparison(baseModel, rightModel, addrECL) ; 
		baseModel.setName(BMName);
		rightModel.setName(RMName);
		MatchTrace Matched2 = MT2.getReduced() ; 
		Matched2 = delNullmatch(Matched2.getMatches());
		MatchTrace UnMatch2 = getUnmatch(MT2.getMatches());

		//printMT(Matched2, "Matched2");
		//printMT(UnMatch2, "UnMatched2");

		Matched2 = userComparison(Matched2, UnMatch2, baseModel, rightModel); 
		//printMT(Matched2, "Final Matched2");
		
		
		List<Match> ExMatched1 = new ArrayList<Match>() ; 
		List<Match> ExMatched2 = new ArrayList<Match>() ; 
		
	//Validation -- Behavioral Semantic Conflicts Detection and Resolution 
		Validate(baseModel, leftModel, rightModel, addrEVL);
		
	//Start Conclusion 
		for	(Match EachMatch1 : Matched1.getMatches()){
			for	(Match EachMatch2 : Matched2.getMatches()){
				if(EachMatch1.getLeft().equals(EachMatch2.getLeft())){
					ExMatched1.add(EachMatch1) ;
					ExMatched2.add(EachMatch2) ;
		
					//A Base model Element Changed in Right and Permanent in Left 
					if(EachMatch1.isMatching() && !EachMatch2.isMatching())
					{						
						setChanges(EachMatch1, EachMatch2, leftModel);
					}
					//A Base model Element Changed in Left and Permanent in Right 
					else if(!EachMatch1.isMatching() && EachMatch2.isMatching())
					{
						setChanges(EachMatch2, EachMatch1, rightModel);
					}
					//A Base model Element Changed in Both
					else if(!EachMatch1.isMatching() && !EachMatch2.isMatching())
					{
						String classType = EachMatch1.getLeft().getClass().getSimpleName(); 
						classType = classType.replace("Impl", "") ;				
						
						//left match and base model element
						String 	bName = " "; 
						if(haveMatchParameter(EachMatch1.getLeft().toString(), "name")){
							bName = getMatchParameter(EachMatch1.getLeft().toString(), "name") ;
						}
						String bOwnerName = " "; 
						String bOwnerType = " ";
						for (EObject rawElement : baseModel.getAllOfKind(classType)) {
							org.eclipse.uml2.uml.Element umlElement = (org.eclipse.uml2.uml.Element)rawElement;
							if(EachMatch1.getLeft().equals(umlElement)){
								if(haveMatchParameter(umlElement.getOwner().toString(), "name")){
									bOwnerName = getMatchParameter(umlElement.getOwner().toString(), "name") ; 
								}
								
							    bOwnerType = umlElement.getOwner().getClass().getSimpleName() ; 
							    bOwnerType = bOwnerType.replace("Impl","") ; 
							    break ;
							}
						}
						String baseSt = classType + " <" + bName + "> with owner " + bOwnerType +
								"<" + bOwnerName + "> in Model <" + baseModel.getName() + "> "; 


						ArrayList<String> StList = new ArrayList<String>() ;
						
						//right match and left model element
						String 	lName = " "; 
						if(haveMatchParameter(EachMatch1.getRight().toString(), "name")){
							lName = getMatchParameter(EachMatch1.getRight().toString(), "name") ;
						}
						String lOwnerName = " "; 
						String lOwnerType = " ";
						for (EObject rawElement : leftModel.getAllOfKind(classType)) {
							org.eclipse.uml2.uml.Element umlElement = (org.eclipse.uml2.uml.Element)rawElement;
							if(EachMatch1.getRight().equals(umlElement)){
								if(haveMatchParameter(umlElement.getOwner().toString(), "name")){
									lOwnerName = getMatchParameter(umlElement.getOwner().toString(), "name") ; 
								}
								
							    lOwnerType = umlElement.getOwner().getClass().getSimpleName() ; 
							    lOwnerType = lOwnerType.replace("Impl","") ; 
							    break ;
							}
						}
						String leftSt = classType + " <" + lName + "> with owner " + lOwnerType +
								"<" + lOwnerName + "> in Model <" + leftModel.getName() + "> "; 
						StList.add(leftSt) ;
						
						
						//right match and right model element
						String 	rName = " "; 
						if(haveMatchParameter(EachMatch2.getRight().toString(), "name")){
							rName = getMatchParameter(EachMatch2.getRight().toString(), "name") ;
						}
						String rOwnerName = " "; 
						String rOwnerType = " ";
						for (EObject rawElement : rightModel.getAllOfKind(classType)) {
							org.eclipse.uml2.uml.Element umlElement = (org.eclipse.uml2.uml.Element)rawElement;
						//	org.eclipse.uml2.uml.NamedElement umlElement = (org.eclipse.uml2.uml.NamedElement)rawElement;
							if(EachMatch2.getRight().equals(umlElement)){
								if(haveMatchParameter(umlElement.getOwner().toString(), "name")){
									rOwnerName = getMatchParameter(umlElement.getOwner().toString(), "name") ; 
								}
								
							    rOwnerType = umlElement.getOwner().getClass().getSimpleName() ; 
							    rOwnerType = rOwnerType.replace("Impl","") ; 
							    break ;
							}
						}
						String rightSt = classType + " <" + rName + "> with owner " + rOwnerType +
								"<" + rOwnerName + "> in Model <" + rightModel.getName() + "> "; 
						StList.add(rightSt) ;		
						
//						WaitForGUI userInteractionGUI = new WaitForGUI("Which one is better?" ,baseSt, StList);
//					    String message = userInteractionGUI.getStringFromGUI(); 
						
						if(!rName.equals(lName))
						{
						//	UserInterDialog UD = new UserInterDialog(parent , true,"Which one is better?" ,baseSt, StList) ;
						//	String message = UD.result ; 
							
						//	if(message.equals(StList.get(1)))
								setChanges(EachMatch1, EachMatch2, leftModel);
						//	else 
						//		setChanges(EachMatch2, EachMatch1, rightModel);
						}
					}
				}
			}
		}
		
	// deleted in one model and contained in another model 
		Matched1.getMatches().removeAll(ExMatched1);
		for (Match delMatch1 : Matched1.getMatches()) {
			String classType = delMatch1.getRight().getClass().getSimpleName(); 
			classType = classType.replace("Impl", "") ;				

			for (EObject rawElement : leftModel.getAllOfKind(classType)) {
				org.eclipse.uml2.uml.Element umlElement = (org.eclipse.uml2.uml.Element)rawElement;
				if(delMatch1.getRight().equals(umlElement)){
					System.out.println(umlElement.toString()) ;
					leftModel.deleteElement(rawElement);
					break ;
				}
			}
		}
		
		Matched2.getMatches().removeAll(ExMatched2);
		for (Match delMatch2 : Matched2.getMatches()) {
			String classType = delMatch2.getRight().getClass().getSimpleName(); 
			classType = classType.replace("Impl", "") ;				

			for (EObject rawElement : rightModel.getAllOfKind(classType)) {
				org.eclipse.uml2.uml.Element umlElement = (org.eclipse.uml2.uml.Element)rawElement;
				if(delMatch2.getRight().equals(umlElement)){
					System.out.println(umlElement.toString()) ;
					rightModel.deleteElement(rawElement);
					break ;
				}
			}
		}

	//Compare left and right models 
		MatchTrace MTF = Comparison(leftModel, rightModel, addrECL) ; 
		MatchTrace MatchedF = MTF.getReduced() ; 
		MatchedF = delNullmatch(MatchedF.getMatches());

	//	printMT(MatchedF, "Matched");
	//	printMT(UnMatchF, "UnMatched");
		
		
	// Merge and Save merged model 
		Merge(leftModel, rightModel, mergedModel, MatchedF, addrEML) ; 
		mergedModel.dispose(); 
		
		System.out.println("Finish") ; 
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
	
	
	public MatchTrace Comparison(IModel leftModel, IModel rightModel, String eclFile) throws Exception {
		leftModel.setName("Left");
		rightModel.setName("Right");
		EclModule eclModule = new EclModule();
		java.net.URI b = java.net.URI.create(eclFile) ; 
		eclModule.parse(b) ; 
	//	eclModule.parse(EpsilonStandalone.class.getResource(eclFile).toURI());
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

	public void Validate(IModel baseModel, IModel leftModel, 
			IModel rightModel, String evlFile) throws Exception{
		baseModel.setName("Base");
		leftModel.setName("Left");
		rightModel.setName("Right");
	
		EvlModule evlModule = new EvlModule() ; 
		java.net.URI b = java.net.URI.create(evlFile) ; 
		evlModule.parse(b) ;
//		evlModule.parse(EpsilonStandalone.class.getResource(evlFile).toURI());
		evlModule.getContext().getModelRepository().addModel(baseModel);
		evlModule.getContext().getModelRepository().addModel(leftModel);
		evlModule.getContext().getModelRepository().addModel(rightModel);

		evlModule.execute()  ;
	
	}
	
	public void Merge(IModel leftModel, IModel rightModel, IModel mergedModel, 
			MatchTrace MT, String emlFile) throws Exception{
		leftModel.setName("Left");
		rightModel.setName("Right");
		mergedModel.setName("Target");
		
		mergedModel.getAliases().add("Target");
		
		EmlModule emlModule = new EmlModule();
		java.net.URI b = java.net.URI.create(emlFile) ; 
		emlModule.parse(b) ;
//		emlModule.parse(EpsilonStandalone.class.getResource(emlFile).toURI());
		emlModule.getContext().getModelRepository().addModel(leftModel);
		emlModule.getContext().getModelRepository().addModel(rightModel);
		emlModule.getContext().setMatchTrace(MT);
		emlModule.getContext().getModelRepository().addModel(mergedModel);
		emlModule.execute();
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
					
//					 IWorkbench wb = PlatformUI.getWorkbench();
//					   IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
//					   UserInterDialog UD = new UserInterDialog(win.getShell(),"Which one is equivalent to { ",leftSt, StList) ;
//						UD.open() ; 
			
//					UserInterDialog UD = new UserInterDialog(parent , true,"Which one is equivalent to this?",leftSt, StList) ;
					
					
//					String message = UD.result ; 
					
					System.out.println("Afterrrrrrrrrrrrrr");

					
				    for (int j = 0; j < tmpList.size(); j++) {
//						if(tmpList.get(j).equals(message))
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
	
//	public void setGeneralizationChange(Match EachMatch1, Match EachMatch2, InMemoryEmfModel leftModel) throws Exception
//	{
//		String oldName = getMatchParameter(EachMatch1.getRight().toString(), "general") ;   
//		String newName = getMatchParameter(EachMatch2.getRight().toString(), "general") ;
//		for (EObject rawGeneralization : leftModel.getAllOfKind("Generalization")) {
//			org.eclipse.uml2.uml.Generalization umlGeneralization = (org.eclipse.uml2.uml.Generalization)rawGeneralization;
//			if (oldName.equals(umlGeneralization.getGeneral().getName())) {
//				System.out.println("Old name: " + umlGeneralization.getGeneral().getName());
//				//umlGeneralization.setGeneral(value);
//				System.out.println("New name: " + umlGeneralization.getGeneral().getName());
//			}
//		}
//	}


}

