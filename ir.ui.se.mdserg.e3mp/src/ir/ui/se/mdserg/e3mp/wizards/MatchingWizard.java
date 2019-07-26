package ir.ui.se.mdserg.e3mp.wizards;

import org.eclipse.core.resources.IProject;
import org.eclipse.epsilon.ecl.trace.MatchTrace;
import org.eclipse.jface.wizard.Wizard;


public class MatchingWizard extends Wizard {
	public IProject selectedProject ; 
	MatchingModelSelectionPage matchingModelSelectionPage;
	MatchingModelSelectionFinishPage matchingModelSelectionFinishPage;
    ProjectSelectionWizardPage projectSelectionPage ; 
    public MatchTrace matchTraces ; 

    public void addPages() {
    		projectSelectionPage = new ProjectSelectionWizardPage("Project Selection"); 
    		addPage(projectSelectionPage) ; 
    	
    		matchingModelSelectionPage = new MatchingModelSelectionPage("Models Selection");
    		projectSelectionPage.MachingSelectionPage = matchingModelSelectionPage ; 
    		addPage(matchingModelSelectionPage);
    		
    		matchingModelSelectionFinishPage = new MatchingModelSelectionFinishPage("Models Selection Finished"); 
    		addPage(matchingModelSelectionFinishPage);
    }
    
	public boolean canFinish()
	{
		if(getContainer().getCurrentPage() == projectSelectionPage || getContainer().getCurrentPage() == matchingModelSelectionPage )
			return false;
		else
			return true;
	}
	
	@Override
	public boolean performFinish() {
		// TODO Auto-generated method stub

		return true ; 
	}
	
}