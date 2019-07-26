package ir.ui.se.mdserg.e3mp.wizards ; 

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import ir.ui.se.mdserg.e3mp.wizards.ProjectSelectionWizardPage;

public class ProjectSelectPageListener implements Listener {

	private ProjectSelectionWizardPage page;
	private int projectIndex = -1;
	
	public ProjectSelectPageListener(ProjectSelectionWizardPage page) {
		this.page = page;
	}

	public void handleEvent(Event event) {
		// TODO Auto-generated method stub
		projectIndex = event.index;
		if (this.page.getCurrentProjectIndex() != this.projectIndex) {
			this.page.loadProject();
		}
		this.page.dialogChanged();
	}
	
	public int getJavaProjectIndex() {
		return this.projectIndex;
	}
	
}
