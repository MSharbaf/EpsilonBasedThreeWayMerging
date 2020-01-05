package ir.ui.se.mdserg.e3mp.wizards;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;


public class MatchingModelSelectionFinishPage extends WizardPage {
	public String path ; 
	private Composite container;
	
	public FileChooser MetaModelFC ;
	public FileChooser BaseModelFC ; 
	public FileChooser VersionModelFC ; 
	public FileChooser EclScriptFC ; 
	
	public IProject selectedProject;


    protected MatchingModelSelectionFinishPage(String pageName) {
             super(pageName);
             setTitle("Comparison Phase Finished Successfully!");
             setDescription("The created Match-List saved in the project directory ...");
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
	
	public void onLoad(IProject selectedPrj)
	{
	}
}
