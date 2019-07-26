package ir.ui.se.mdserg.e3mp.wizards ; 

import java.util.LinkedList;
import java.util.List;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

public class ProjectSelectionWizardPage extends WizardPage {
	
	private Composite container;
	private Group projectSection;
	
	private org.eclipse.swt.widgets.List listProjects;
	private ProjectSelectPageListener listener;
	private IProject selectedProject;
	private int currentProjectIndex = -1;
	
	public MatchingModelSelectionPage MachingSelectionPage ; 
	public MergingModelSelectionPage MergingSelectionPage ; 
	
	protected ProjectSelectionWizardPage(String pageName) {
		super(pageName);
		super.setTitle("Project Selection");
		// TODO Auto-generated constructor stub
	}
	
	public int getCurrentProjectIndex() {
		return this.currentProjectIndex;
	}
	
	public ProjectSelectPageListener getListener() {
		return this.listener;
	}
	
	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NULL);
		GridData data = new GridData(GridData.FILL_BOTH);
		container.setLayoutData(data);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		
		projectSection = new Group(container, SWT.NULL);
		projectSection.setText("Select a project in the workspace");
		projectSection.setLayout(new GridLayout());
		projectSection.setLayoutData(data);
		 
		listProjects = new org.eclipse.swt.widgets.List(projectSection, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL);
		
		GridData  gridData = new GridData(GridData.FILL, GridData.FILL, true, true);
		listProjects.setLayoutData(gridData);
		
		if (!getProjectsInWorkspace().isEmpty()) {
			initProjectList();
	        // create listener
	        listener = new ProjectSelectPageListener(this);
	        listProjects.addListener(SWT.Selection, listener);
		}
		
		dialogChanged();
		setControl(container);
	}
	
	private void initProjectList() {
		listProjects.removeAll();
		for(IProject Project : getProjectsInWorkspace())
	    	listProjects.add(Project.getName(), getProjectsInWorkspace().lastIndexOf(Project));      
	}
	
	public List<IProject> getProjectsInWorkspace() {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject[] projects = root.getProjects();
		
		List<IProject> Projects = new LinkedList<IProject>();
		for(int i = 0; i < projects.length; i++) {
			Projects.add(projects[i]) ; 
		}
		
		return Projects; 
	}
	
	public IProject getProjectByIndex(int index) {
		return getProjectsInWorkspace().get(index);
	}
	
	
	public void dialogChanged() {
		//if there aren't any projects
		if (getProjectsInWorkspace().isEmpty()) {
			updateStatus("No projects exist in the workspace ...");
			return;
		}
		
		if (this.selectedProject == null) {
			updateStatus("A project must be specified ...");
			return;
		}
		
		if(this.selectedProject!= null){
			updateStatus("");
		}
		
		updateStatus(null);
	} 
	
	private void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}

	public void loadProject() {
		this.selectedProject = getProjectByIndex(listProjects.getFocusIndex());
		this.setPageComplete(true);
	}
	
	public IProject getSelectedProject() {
		return this.selectedProject;
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
        boolean validatedNextPressed = true;
        try { 
        	if(this.MachingSelectionPage != null)
        		this.MachingSelectionPage.onLoad(selectedProject);
        	else if(this.MergingSelectionPage != null)
        		this.MergingSelectionPage.onLoad(selectedProject);
        		
        } catch (Exception ex) {
            System.out.println("Error validation when pressing Next: " + ex);
        }
        return validatedNextPressed;
    }
  
}
