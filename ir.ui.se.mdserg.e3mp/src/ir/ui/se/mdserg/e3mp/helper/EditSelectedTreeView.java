package ir.ui.se.mdserg.e3mp.helper;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.provider.EcoreItemProviderAdapterFactory;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;
import org.eclipse.emf.edit.provider.ReflectiveItemProviderAdapterFactory;
import org.eclipse.emf.edit.provider.resource.ResourceItemProviderAdapterFactory;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.epsilon.ecl.trace.MatchTrace;
import org.eclipse.epsilon.emc.emf.EmfModel;
import org.eclipse.epsilon.emc.emf.InMemoryEmfModel;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.graphics.Color;

public class EditSelectedTreeView extends TitleAreaDialog{ //Dialog {
	public EmfModel Model1 ; 
	public EmfModel Model2 ;
	public String MMpath ; 
	public IProject selectedProject ; 
	public MatchTrace Matched; 
	public Point initPoint ; 
	public ArrayList<MatchMember> allMatchList, newMatchList;
	public MatchTrace allMatchTrace; 
	public TreeItem previousMatchedItem ; 
	TreeViewer viewer1, viewer2;
	List<TreeItem> v1MatchList, v2MatchList, v1UnMatchList, v2UnMatchList , v1UnMatchResList, v2UnMatchResList; 
	List<TreeItem> predictedPotentialItems ; 
//	public int[] Decision ; 
	public List<Integer> Decision ;
	
	
	private static final Class<?> IItemLabelProviderClass = IItemLabelProvider.class;
	private static final Class<?> ITreeItemContentProviderClass = ITreeItemContentProvider.class;

	
	/**
	 * Create the dialog.
	 */
	public EditSelectedTreeView(Shell parentShell, EmfModel leftM, EmfModel rightM, 
			String MMpath, IProject selectedProject, ArrayList<MatchMember> allmatchList){
		super(parentShell);
		this.Model1 = leftM ;
		this.Model2 = rightM ; 
		this.MMpath = MMpath ; 
		this.selectedProject = selectedProject ; 
		this.allMatchList = allmatchList ; 
		this.initPoint = parentShell.getLocation() ; 
		this.previousMatchedItem = null ; 
	}
	
	   @Override
	    public void create() {
	        super.create();
	        setTitle("Please Review the Created Match-List, Revise it, and Choose Your Choice for Equivalent Elements ...");
	          
	        StringBuffer text = new StringBuffer();
	          text.append("Elements Color Guideline:") ; 
	          text.append("   \u2022 Black: Auto Matched Elements");
	          text.append("    \u2022 Yellow: Equivalent Elements");
	          text.append("    \u2022 Violet: Without Matched Elements\n");
	          text.append("                                                 \u2022 Green: Accepted in Equiv Case ");
	          text.append("    \u2022 Red: Rejected in Equiv Case  ");
	          text.append("    \u2022 Orange: Potential Similar Elements");
	          
	        setMessage(text.toString(),IMessageProvider.INFORMATION) ;
	    }
	
    @Override
    protected Control createDialogArea(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
		GridData data = new GridData(GridData.FILL_BOTH);
		composite.setLayoutData(data);
		composite.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		ArrayList<AdapterFactory> factories = new ArrayList<AdapterFactory>();
		factories.add(new ResourceItemProviderAdapterFactory());
		factories.add(new EcoreItemProviderAdapterFactory());
		factories.add(new ReflectiveItemProviderAdapterFactory());
		 
		ComposedAdapterFactory composedAdapterFactory ; 

		if(MMpath == null)
			composedAdapterFactory = new ComposedAdapterFactory(ComposedAdapterFactory.Descriptor.Registry.INSTANCE);
		else
			composedAdapterFactory = new ComposedAdapterFactory(factories);

	    ///////////////////////////////////////////////////////////////////////////////////////////////    
	    
	    // Show input models in User Interaction Dialog 	    
        viewer1 = new TreeViewer(composite);
        viewer1.setContentProvider(new MyAdapterFactoryContentProvider(composedAdapterFactory));
        viewer1.getTree().setHeaderVisible(true);
        viewer1.getTree().setLinesVisible(true);
        TreeViewerColumn viewerColumn1 = new TreeViewerColumn(viewer1,SWT.NONE);
        viewerColumn1.getColumn().setWidth(376);
        String Name1[] = Model1.getModelImpl().getURI().toString().split("/") ; 
        viewerColumn1.getColumn().setText(Name1[Name1.length-1]);
        viewer1.setLabelProvider(new AdapterFactoryLabelProvider(composedAdapterFactory));	
        viewer1.setInput(Model1.getResource());
        viewer1.expandAll();
        
        viewer2 = new TreeViewer(composite);
        viewer2.setContentProvider(new MyAdapterFactoryContentProvider(composedAdapterFactory));
        viewer2.getTree().setHeaderVisible(true);
        viewer2.getTree().setLinesVisible(true);
        TreeViewerColumn viewerColumn2 = new TreeViewerColumn(viewer2,SWT.NONE);
        viewerColumn2.getColumn().setWidth(376);
        String Name2[] = Model2.getModelImpl().getURI().toString().split("/") ;
        viewerColumn2.getColumn().setText(Name2[Name2.length-1]);
        viewer2.setLabelProvider(new AdapterFactoryLabelProvider(composedAdapterFactory));	
        viewer2.setInput(Model2.getResource());
        viewer2.expandAll();
	
		/////////////////////////////////////////////////////////////////////////////////////////////	
		
		List<TreeItem> v1AllItems = new ArrayList<TreeItem>();
		getAllItems(viewer1.getTree(), v1AllItems);
		
		List<TreeItem> v2AllItems = new ArrayList<TreeItem>();
		getAllItems(viewer2.getTree(), v2AllItems);
		
		v1MatchList = new ArrayList<TreeItem>();
		v2MatchList = new ArrayList<TreeItem>();
		v1UnMatchList = new ArrayList<TreeItem>();
		v2UnMatchList = new ArrayList<TreeItem>();
		v1UnMatchResList = new ArrayList<TreeItem>();
		v2UnMatchResList = new ArrayList<TreeItem>();
		
		newMatchList = new ArrayList<MatchMember>();
		
		initMatchingList(v1AllItems, v2AllItems, allMatchList, newMatchList);
		
		viewer1.refresh();
		viewer2.refresh();
		
		Display display = Display.getCurrent();
		Color YellowColor = new Color(display, 225, 225, 0);
		Color RedColor = new Color(display,255, 0, 0);
		Color OrangeColor = new Color(display,255,128,0);
		Color GreenColor = new Color(display,0, 160, 40);
		Color BlackColor = new Color(display,0, 0, 0);
		
		// Add Listener for viewer 1 to show pair item that are match and select new match 
        viewer1.getTree().addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
       
               	if(predictedPotentialItems != null){
            		for(int k=0; k<predictedPotentialItems.size(); k++){
            			predictedPotentialItems.get(k).setForeground(BlackColor);
            		}
            		predictedPotentialItems.clear();
            		predictedPotentialItems = null ; 
            	}
            	
             	viewer2.setSelection(StructuredSelection.EMPTY);
            	if(previousMatchedItem != null)
            		previousMatchedItem.setForeground(BlackColor);
            	
            	TreeItem selectedItem =  (TreeItem) e.item ;
        		
            	int index = v1MatchList.indexOf(selectedItem); 
            	int indexUn = v1UnMatchList.indexOf(selectedItem) ; 
            	if(index != -1){
            		if(Decision.get(index)==0){
                   		v2MatchList.get(index).setForeground(YellowColor);
            		} else if(Decision.get(index)==1){
            			v2MatchList.get(index).setForeground(RedColor);
            		}else{
            			v2MatchList.get(index).setForeground(GreenColor);
            		}
        			previousMatchedItem = v2MatchList.get(index) ; 
                	viewer2.refresh();
            		createContextMenu(viewer2, v1MatchList.get(index), v2MatchList.get(index)) ; 
            	}else if(indexUn != -1){
            		// search for potential equivalent items 
                    predictedPotentialItems = new ArrayList<TreeItem>();
        			
        			String sType = "" ; 
        			if(Model1.getModelImpl().getURI().toString().contains(".uml")){
        				sType = selectedItem.getText().split(">")[0] ;
        			}else
        				sType = selectedItem.getText().split(" ")[0] ;
        			
            		for(int j=0; j<v2UnMatchList.size(); j++)
            		{
        				if(v2UnMatchList.get(j).getText().contains(sType)) {
            				predictedPotentialItems.add(v2UnMatchList.get(j)) ;
            				v2UnMatchList.get(j).setForeground(OrangeColor);
        				}
            		}
            		createContextMenu2(viewer2) ; 
            	}
            }
        });
            
        return composite;
    }  
   
    protected void createContextMenu(TreeViewer viewer, TreeItem TIV1, TreeItem TIV2) {
        MenuManager contextMenu = new MenuManager("#ViewerMenu"); //$NON-NLS-1$
        contextMenu.setRemoveAllWhenShown(true);
       
        contextMenu.addMenuListener(new IMenuListener() {
            @Override
            public void menuAboutToShow(IMenuManager mgr) {
                fillContextMenu(mgr, TIV1, TIV2);
            }
        });

        Menu menu = contextMenu.createContextMenu(viewer.getControl());
        viewer.getControl().setMenu(menu);
    }

    protected void fillContextMenu(IMenuManager contextMenu, TreeItem TIV1, TreeItem TIV2) {
        contextMenu.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));

        contextMenu.add(new Action("This pair sets as: " + TIV1.getText()) {
            @Override
            public void run() {
    			Display display = Display.getCurrent();
            	Color GreenColor = new Color(display,0, 160, 40);
            	Color RedColor = new Color(display,255, 0, 0);
                       
        		int Index = v2MatchList.indexOf(TIV2) ; 
        		if(Index != -1){
        			v1MatchList.get(Index).setForeground(GreenColor);
        			v2MatchList.get(Index).setForeground(RedColor);
        			Decision.set(Index,1); 
        		}
            	
            	viewer2.setSelection(StructuredSelection.EMPTY);	
            	viewer1.refresh();
            	viewer2.refresh();
            }
        });
        
        contextMenu.add(new Action("This pair sets as: " + TIV2.getText()) {
            @Override
            public void run() {
    			Display display = Display.getCurrent();
            	Color GreenColor = new Color(display,0, 160, 40);
            	Color RedColor = new Color(display,255, 0, 0);
                       
        		int Index = v2MatchList.indexOf(TIV2) ; 
        		if(Index != -1){
        			v1MatchList.get(Index).setForeground(RedColor);
        			v2MatchList.get(Index).setForeground(GreenColor);
        			Decision.set(Index, 2) ; 
        		}
            	
            	viewer2.setSelection(StructuredSelection.EMPTY);	
            	viewer1.refresh();
            	viewer2.refresh();
            }
        });
        
        contextMenu.add(new Action("Remove Existing Match") {
            @Override
            public void run() {
    			Display display = Display.getCurrent();
             	Color BlackColor = new Color(display,0, 0, 0);
            	Color VioletColor = new Color(display, 190, 0, 190);
            	
            	TreeItem[] v2Selection = viewer2.getTree().getSelection() ;
            	TreeItem TIV2 = v2Selection[0] ;

            	TreeItem[] v1Selection = viewer1.getTree().getSelection() ;
            	TreeItem TIV1 = v1Selection[0] ;
                       
        		int delIndex = v2MatchList.indexOf(TIV2) ; 
        		if(delIndex != -1){
        			v1MatchList.get(delIndex).setForeground(VioletColor);
        			v2MatchList.get(delIndex).setForeground(BlackColor);
        			Decision.remove(delIndex) ;
        			v1UnMatchList.add(TIV1) ;
        			v2UnMatchList.add(TIV2) ;
        			v1MatchList.remove(delIndex) ;
        			v2MatchList.remove(delIndex) ; 
        		}
        		        		
             	previousMatchedItem = null ; 
            	
            	viewer2.setSelection(StructuredSelection.EMPTY);
                	
            	viewer1.refresh();
            	viewer2.refresh();
            }
        });
    }
        
    
    protected void createContextMenu2(TreeViewer viewer) {
        MenuManager contextMenu = new MenuManager("#ViewerMenu"); //$NON-NLS-1$
        contextMenu.setRemoveAllWhenShown(true);
       
        contextMenu.addMenuListener(new IMenuListener() {
            @Override
            public void menuAboutToShow(IMenuManager mgr) {
                fillContextMenu2(mgr);
            }
        });

        Menu menu = contextMenu.createContextMenu(viewer.getControl());
        viewer.getControl().setMenu(menu);
    }

    protected void fillContextMenu2(IMenuManager contextMenu) {
        contextMenu.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));

        contextMenu.add(new Action("Set as Equivalent") {
            @Override
            public void run() {
    			Display display = Display.getCurrent();
             	Color BlackColor = new Color(display,0, 0, 0);
            	Color YellowColor = new Color(display, 225, 225, 0);
            	
            	TreeItem[] v2Selection = viewer2.getTree().getSelection() ;
            	TreeItem TIV2 = v2Selection[0] ;

            	TreeItem[] v1Selection = viewer1.getTree().getSelection() ;
            	TreeItem TIV1 = v1Selection[0] ;
                       
            	int index = v1MatchList.indexOf(TIV1) ;
            	if(index == -1){
            		v1MatchList.add(TIV1) ;
            		v2MatchList.add(TIV2) ;
            		Decision.add(0) ;
            		
            		v2UnMatchList.remove(TIV2) ;
            	}
            	
            	if(previousMatchedItem != null){
            		previousMatchedItem.setForeground(BlackColor);
            	}
            	if(predictedPotentialItems != null){
            		for(int k=0; k<predictedPotentialItems.size(); k++){
            			predictedPotentialItems.get(k).setForeground(BlackColor);
            		}
            		predictedPotentialItems.clear();
            		predictedPotentialItems = null ; 
            	}
            	
            	TIV1.setForeground(YellowColor);
            	TIV2.setForeground(YellowColor);
            	
            	previousMatchedItem = TIV2 ; 
            	
            	viewer2.setSelection(StructuredSelection.EMPTY);
                	
            	viewer1.refresh();
            	viewer2.refresh();
            }
        });
    }
    
    
    
    void getAllItems(Tree tree, List<TreeItem> allItems)
    {
        for(TreeItem item : tree.getItems())
        {
        	allItems.add(item) ; 
            getAllItems(item, allItems);
        }
    }
    
    void getAllItems(TreeItem currentItem, List<TreeItem> allItems)
    {
        TreeItem[] children = currentItem.getItems();

        for(int i = 0; i < children.length; i++)
        {
            allItems.add(children[i]);

            getAllItems(children[i], allItems);
        }
    }
    
    //aList is all matched elements
    //mList is matched element that two modification occurred on one element in Base version 
    void initMatchingList(List<TreeItem> v1, List<TreeItem> v2, ArrayList<MatchMember> aList, ArrayList<MatchMember> mList)
    {
    	TreeItem TIV = null ;
		for(int j=0; j<v1.size(); j++)
		{
			boolean flag = true ; 
			for(int i=0; i<aList.size(); i++)
			{
				if(v1.get(j).getText().equals(aList.get(i).getLeftText()) && (v1.get(j).getParentItem()== null 
						|| v1.get(j).getParentItem().getText().equals(aList.get(i).getLeftParent()))){
					flag = false ; 
					break ;
				}
			}
	   		if(flag){
    			Display display = Display.getCurrent();
    			Color VioletColor = new Color(display, 200, 0, 200);
    			TIV = v1.get(j) ;
    			TIV.setForeground(VioletColor);
    			v1UnMatchList.add(TIV) ; 
    		}
		}
    	
		for(int j=0; j<v2.size(); j++)
		{
			boolean flag = true ; 
			for(int i=0; i<aList.size(); i++)
			{
				if(v2.get(j).getText().equals(aList.get(i).getRightText()) && (v2.get(j).getParentItem()== null 
						|| v2.get(j).getParentItem().getText().equals(aList.get(i).getRightParent()))){
					flag = false ; 
					break ;
				}
			}
	   		if(flag){
    			TIV = v2.get(j) ;
    			v2UnMatchList.add(TIV) ; 
    		}
		}
		
    	Decision = new ArrayList<Integer>() ; 
    	for(int i=0; i<mList.size(); i++)
    	{
    		Decision.add(0) ;
    		TreeItem TIV1 = null, TIV2 = null ;
    		for(int j=0; j<v1.size(); j++)
    		{
    			if(v1.get(j).getText().equals(mList.get(i).getLeftText()) && (v1.get(j).getParentItem()== null 
    					|| v1.get(j).getParentItem().getText().equals(mList.get(i).getLeftParent()))){
    				TIV1 = v1.get(j); 
    				break ;
    			}
    		}
    		for(int j=0; j<v2.size(); j++)
    		{
    			if(v2.get(j).getText().equals(mList.get(i).getRightText()) && (v2.get(j).getParentItem()== null
    					|| v2.get(j).getParentItem().getText().equals(mList.get(i).getRightParent()))){
    				TIV2 = v2.get(j); 
    				break ;
    			}
    		}
    		if(TIV1!=null && TIV2!=null){
    			Display display = Display.getCurrent();
    			Color YellowColor = new Color(display, 225, 225, 0);
    			TIV1.setForeground(YellowColor);
    			v1MatchList.add(TIV1) ;
    			v2MatchList.add(TIV2) ;
    		}
    	}
    }
    
    void finalizeOutMatchList()
    {
    	newMatchList = null ; 
    	newMatchList = new ArrayList<MatchMember>();
    	MatchMember matchMember ; 

    	for(int i=0 ; i<v1UnMatchResList.size(); i++)
    	{
    	    String LeftText =  v1UnMatchResList.get(i).getText() ;  
    	    String LeftParentText = "File" ; 
    	    if(v1UnMatchResList.get(i).getParentItem() != null)
    	    	LeftParentText = v1UnMatchResList.get(i).getParentItem().getText() ;  
	        
    	    String RightText =  v2UnMatchResList.get(i).getText() ;  
    	    String RightParentText = "File" ; 
    	    if(v2UnMatchResList.get(i).getParentItem() != null)
    	    	RightParentText = v2UnMatchResList.get(i).getParentItem().getText() ; 
	        
    	    int selection = 2 ; 
	       	matchMember = new MatchMember(LeftParentText, LeftText, RightParentText, RightText, selection) ;
	       	newMatchList.add(matchMember);
    	}
    	
	    for(int i=0 ; i<v1MatchList.size(); i++)
	    {
    	    String LeftText =  v1MatchList.get(i).getText() ;  
    	    String LeftParentText = "File" ; 
    	    if(v1MatchList.get(i).getParentItem() != null)
    	    	LeftParentText = v1MatchList.get(i).getParentItem().getText() ;  
	        
    	    String RightText =  v2MatchList.get(i).getText() ;  
    	    String RightParentText = "File" ; 
    	    if(v2MatchList.get(i).getParentItem() != null)
    	    	RightParentText = v2MatchList.get(i).getParentItem().getText() ; 
	        
    	    int selection = Decision.get(i) ; 
	       	matchMember = new MatchMember(LeftParentText, LeftText, RightParentText, RightText, selection) ;
	       	newMatchList.add(matchMember);
	    }
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("Selection Window");
        newShell.setSize(800, 600);
        newShell.setLocation(300,50);
    }
	
    @Override
    protected void okPressed() {
    	finalizeOutMatchList() ; 	
        super.okPressed();
    }
}
