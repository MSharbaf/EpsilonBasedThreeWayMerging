package ir.ui.se.mdserg.e3mp.wizards;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;


public class MergingModelSelectionFinishPage extends WizardPage {
	public String path ; 
	private Composite container;
	public IProject selectedProject;


    protected MergingModelSelectionFinishPage(String pageName) {
             super(pageName);
             setTitle("Merging Process is Fineshed");
             setDescription("Your Merged model has been saved ...");
    }
	
	@Override
	public void createControl(Composite parent) {
		// TODO Auto-generated method stub
		
		container = new Composite(parent, SWT.NULL);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		container.setLayoutData(data);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
			
		setControl(container);		
	}

	public void setSelectedProject(IProject project) {
		this.selectedProject = project;
	}
	
}
