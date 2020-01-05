package ir.ui.se.mdserg.e3mp.wizards;

import java.util.ArrayList;

import org.eclipse.core.resources.IProject;
import org.eclipse.epsilon.ecl.trace.MatchTrace;
import org.eclipse.epsilon.emc.emf.EmfModel;
import org.eclipse.jface.wizard.Wizard;
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
			System.out.println("Finish") ; 		    
			return true ; 
	}	
}