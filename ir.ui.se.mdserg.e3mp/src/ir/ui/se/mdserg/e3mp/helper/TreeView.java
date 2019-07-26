package ir.ui.se.mdserg.e3mp.helper;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.beans.XMLEncoder;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.internal.core.commands.ForEachCommand;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.provider.EcoreItemProviderAdapterFactory;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;
import org.eclipse.emf.edit.provider.ReflectiveItemProviderAdapterFactory;
import org.eclipse.emf.edit.provider.resource.ResourceItemProviderAdapterFactory;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.epsilon.ecl.trace.Match;
import org.eclipse.epsilon.ecl.trace.MatchTrace;
import org.eclipse.epsilon.emc.emf.InMemoryEmfModel;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.widgets.TreeColumn;
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

public class TreeView extends Dialog {
	public InMemoryEmfModel BaseModel ; 
	public InMemoryEmfModel NewModel ;
	public String MMpath ; 
	public IProject selectedProject ; 
	public MatchTrace Matched; 
	public Point initPoint ; 
	public ArrayList<MatchMember> matchMemberList ; 
	public TreeItem previousMatchedItem ; 
	TreeViewer viewer1, viewer2 ;
	List<TreeItem> v1MatchList, v2MatchList, v1NewMatchList ; 
	List<TreeItem> v1AllItems, v2AllItems;
	List<TreeItem> predictedPotentialItems ; 

	
	private static final Class<?> IItemLabelProviderClass = IItemLabelProvider.class;
	private static final Class<?> ITreeItemContentProviderClass = ITreeItemContentProvider.class;

	
	/**
	 * Create the dialog.
	 */
	public TreeView(Shell parentShell,InMemoryEmfModel baseM,InMemoryEmfModel newM, 
			String MMpath, IProject selectedProject, MatchTrace ReducedMatched){
		super(parentShell);
		this.BaseModel = baseM ;
		this.NewModel = newM ; 
		this.MMpath = MMpath ; 
		this.selectedProject = selectedProject ; 
		this.Matched = ReducedMatched ; 
		this.initPoint = parentShell.getLocation() ; 
		this.previousMatchedItem = null ; 
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
		
		// Convert ECL matches that have true matching to a Match Model  
		matchMemberList = new ArrayList<MatchMember>(); 
	
    	MatchMember matchMember ; 
		ComposedAdapterFactory composedAdapterFactory ; 
	    IItemLabelProvider itemLabelProvider  ;  
	    ITreeItemContentProvider treeItemContentProvider ;

		if(MMpath == null)
			composedAdapterFactory = new ComposedAdapterFactory(ComposedAdapterFactory.Descriptor.Registry.INSTANCE);
		else
			composedAdapterFactory = new ComposedAdapterFactory(factories);
    	AdapterFactory adapterFactory = composedAdapterFactory;
    		    

	    for(int i=0 ; i<Matched.getMatches().size(); i++)
	    {
	    	Match m = Matched.getMatches().get(i) ; 

    	    itemLabelProvider = (IItemLabelProvider)adapterFactory.adapt(m.getLeft(), IItemLabelProviderClass);
    	    String LeftText =    itemLabelProvider.getText(m.getLeft()) ; 
    	    		   
	        treeItemContentProvider = (ITreeItemContentProvider)adapterFactory.adapt(m.getLeft(), ITreeItemContentProviderClass);
	        Object LeftParent = treeItemContentProvider.getParent(m.getLeft()) ; 
	        itemLabelProvider = (IItemLabelProvider)adapterFactory.adapt(LeftParent, IItemLabelProviderClass);
	        String LeftParentText = itemLabelProvider.getText(LeftParent); 
	        
    	    itemLabelProvider = (IItemLabelProvider)adapterFactory.adapt(m.getRight(), IItemLabelProviderClass);
    	    String RightText =    itemLabelProvider.getText(m.getRight()) ; 
    	    		   
	        treeItemContentProvider = (ITreeItemContentProvider)adapterFactory.adapt(m.getRight(), ITreeItemContentProviderClass);
	        Object RightParent = treeItemContentProvider.getParent(m.getRight()) ; 
	        itemLabelProvider = (IItemLabelProvider)adapterFactory.adapt(RightParent, IItemLabelProviderClass);
	        String RightParentText = itemLabelProvider.getText(RightParent); 
	        
	       	matchMember = new MatchMember(LeftParentText, LeftText, RightParentText, RightText, 0) ;
		    matchMemberList.add(matchMember);
	    }
	    
	    ///////////////////////////////////////////////////////////////////////////////////////////////    
	    
        viewer1 = new TreeViewer(composite);
        viewer1.setContentProvider(new MyAdapterFactoryContentProvider(composedAdapterFactory));
        viewer1.getTree().setHeaderVisible(true);
        viewer1.getTree().setLinesVisible(true);
        TreeViewerColumn viewerColumn1 = new TreeViewerColumn(viewer1,SWT.NONE);
        viewerColumn1.getColumn().setWidth(376);
        String Name1[] = BaseModel.getModelImpl().getURI().toString().split("/") ; 
        viewerColumn1.getColumn().setText(Name1[Name1.length-1]);
        viewer1.setLabelProvider(new AdapterFactoryLabelProvider(composedAdapterFactory));	
        viewer1.setInput(BaseModel.getResource());
        viewer1.expandAll();
        
        viewer2 = new TreeViewer(composite);
        viewer2.setContentProvider(new MyAdapterFactoryContentProvider(composedAdapterFactory));
        viewer2.getTree().setHeaderVisible(true);
        viewer2.getTree().setLinesVisible(true);
        TreeViewerColumn viewerColumn2 = new TreeViewerColumn(viewer2,SWT.NONE);
        viewerColumn2.getColumn().setWidth(376);
        String Name2[] = NewModel.getModelImpl().getURI().toString().split("/") ;
        viewerColumn2.getColumn().setText(Name2[Name2.length-1]);
        viewer2.setLabelProvider(new AdapterFactoryLabelProvider(composedAdapterFactory));	
        viewer2.setInput(NewModel.getResource());
        viewer2.expandAll();
	    
		/////////////////////////////////////////////////////////////////////////////////////////////	
	
        v1AllItems = new ArrayList<TreeItem>();
		getAllItems(viewer1.getTree(), v1AllItems);	
		v2AllItems = new ArrayList<TreeItem>();
		getAllItems(viewer2.getTree(), v2AllItems);
		v1MatchList = new ArrayList<TreeItem>();
		v2MatchList = new ArrayList<TreeItem>();
		v1NewMatchList = new ArrayList<TreeItem>() ; 
		
		initMatchingList(matchMemberList);    
	        
		viewer1.refresh();
		viewer2.refresh();
		
		Display display = Display.getCurrent();
    	Color GreenColor = new Color(display,0, 160, 40);
		Color RedColor = new Color(display,255, 0, 0);
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
            	if(index != -1){
            		v2MatchList.get(index).setForeground(GreenColor);
        			previousMatchedItem = v2MatchList.get(index) ; 
                	viewer2.refresh();
            		createContextMenu1(viewer2) ; 
            	}else{
            		// search for potential equivalent items 
                    predictedPotentialItems = new ArrayList<TreeItem>();
            		
    				TreeItem selectedItemParentMatchinV2 = null ; 
        			int SI_index = v1AllItems.indexOf(selectedItem) ; 
        			if(SI_index != -1){
        				TreeItem selectedItemParent = v1AllItems.get(SI_index).getParentItem() ; 
        				if(selectedItemParent != null){
        					int SI2_index = v1MatchList.indexOf(selectedItemParent) ; 
        					if(SI2_index != -1)
        						selectedItemParentMatchinV2 = v2MatchList.get(SI2_index) ; 
        				}
        			}
        			
        			String sType = "" ; 
        			if(BaseModel.getModelImpl().getURI().toString().contains(".uml"))
        				sType = selectedItem.getText().split(">")[0] ; 
        			else
        				sType = selectedItem.getText().split(" ")[0] ;
        			
            		for(int j=0; j<v2AllItems.size(); j++)
            		{
            			if(selectedItemParentMatchinV2 == null){
            				if(v2AllItems.get(j).getText().contains(sType)) {
                				predictedPotentialItems.add(v2AllItems.get(j)) ;
                				v2AllItems.get(j).setForeground(RedColor);
            				}
                			
            			}else{
            				if(v2AllItems.get(j).getText().contains(sType) && v2AllItems.get(j).getParentItem().equals(selectedItemParentMatchinV2)){
            					predictedPotentialItems.add(v2AllItems.get(j)) ;
            					v2AllItems.get(j).setForeground(RedColor);
                			}
            			}
            		}
            		createContextMenu2(viewer2) ; 
            	}
            }
        });
    	//////////////////////////////////////////////////////////////////////////////////
        
        return composite;
    }  
   
    protected void createContextMenu1(TreeViewer viewer) {
        MenuManager contextMenu = new MenuManager("#ViewerMenu"); //$NON-NLS-1$
        contextMenu.setRemoveAllWhenShown(true);
       
        contextMenu.addMenuListener(new IMenuListener() {
            @Override
            public void menuAboutToShow(IMenuManager mgr) {
                fillContextMenu1(mgr);
            }
        });

        Menu menu = contextMenu.createContextMenu(viewer.getControl());
        viewer.getControl().setMenu(menu);
    }

    protected void fillContextMenu1(IMenuManager contextMenu) {
        contextMenu.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
      
        contextMenu.add(new Action("Remove Exsited Match") {
            @Override
            public void run() {
    			Display display = Display.getCurrent();
             	Color BlackColor = new Color(display,0, 0, 0);

            	TreeItem[] v2Selection = viewer2.getTree().getSelection() ;
            	TreeItem TIV2 = v2Selection[0] ;

            	TreeItem[] v1Selection = viewer1.getTree().getSelection() ;
            	TreeItem TIV1 = v1Selection[0] ;
                       
        		int delIndex = v2MatchList.indexOf(TIV2) ; 
        		if(delIndex != -1){
        			v1MatchList.get(delIndex).setForeground(BlackColor);
        			v2MatchList.get(delIndex).setForeground(BlackColor);
        			v1AllItems.add(TIV1) ;
        			v2AllItems.add(TIV2) ;
        			v1MatchList.remove(delIndex) ;
        			v2MatchList.remove(delIndex) ; 
        			int delIndexNew = v1NewMatchList.indexOf(TIV1) ; 
        			if(delIndexNew != -1)
        				v1NewMatchList.remove(delIndexNew) ;
        		}
        		
             	previousMatchedItem = null ; 
            	
            	viewer2.setSelection(StructuredSelection.EMPTY);
                	
            	viewer1.refresh();
            	viewer2.refresh();
            }
        });
        
//        contextMenu.add(new Action("Set as Equivalent") {
//            @Override
//            public void run() {
//    			Display display = Display.getCurrent();
//            	Color GreenColor = new Color(display,0, 160, 40);
//             	Color BlackColor = new Color(display,0, 0, 0);
//            	Color RedColor = new Color(display,255, 0, 0);
//            	
//            	TreeItem[] v2Selection = viewer2.getTree().getSelection() ;
//            	TreeItem TIV2 = v2Selection[0] ;
//
//            	TreeItem[] v1Selection = viewer1.getTree().getSelection() ;
//            	TreeItem TIV1 = v1Selection[0] ;
//                       
////        		int delIndex = v2MatchList.indexOf(TIV2) ; 
////        		if(delIndex != -1){
////        			v1MatchList.get(delIndex).setForeground(BlackColor);
////        			v2MatchList.get(delIndex).setForeground(BlackColor);
////        			v1MatchList.remove(delIndex) ;
////        			v2MatchList.remove(delIndex) ; 
////        		}
//        		
//            	int index = v1MatchList.indexOf(TIV1) ;
//            	if(index == -1){
//            		v1MatchList.add(TIV1) ;
//            	}
//        		v2MatchList.add(TIV2) ;
//        		v1AllItems.remove(TIV1) ;
//        		v2AllItems.remove(TIV2) ;
//            	
//            	
////            	else{
////            		v2MatchList.get(index).setForeground(BlackColor);
////            		v2AllItems.add(TIV2) ; 
////            		v2MatchList.set(index, TIV2) ;
////            	}
//            	
////            	if(previousMatchedItem != null){
////            		previousMatchedItem.setForeground(BlackColor);
////            	}
//            	if(predictedPotentialItems != null){
//            		for(int k=0; k<predictedPotentialItems.size(); k++){
//            			predictedPotentialItems.get(k).setForeground(BlackColor);
//            		}
//            		predictedPotentialItems.clear();
//            		predictedPotentialItems = null ; 
//            	}
//            	
//            	TIV1.setForeground(GreenColor);
//            	TIV2.setForeground(GreenColor);
//            	
//            	previousMatchedItem = TIV2 ; 
//            	
//            	viewer2.setSelection(StructuredSelection.EMPTY);
//                	
//            	viewer1.refresh();
//            	viewer2.refresh();
//            }
//        });
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
            	Color GreenColor = new Color(display,0, 160, 40);
             	Color BlackColor = new Color(display,0, 0, 0);
            	Color RedColor = new Color(display,255, 0, 0);
            	
            	TreeItem[] v2Selection = viewer2.getTree().getSelection() ;
            	TreeItem TIV2 = v2Selection[0] ;

            	TreeItem[] v1Selection = viewer1.getTree().getSelection() ;
            	TreeItem TIV1 = v1Selection[0] ;
                       
//        		int delIndex = v2MatchList.indexOf(TIV2) ; 
//        		if(delIndex != -1){
//        			v1MatchList.get(delIndex).setForeground(BlackColor);
//        			v2MatchList.get(delIndex).setForeground(BlackColor);
//        			v1MatchList.remove(delIndex) ;
//        			v2MatchList.remove(delIndex) ; 
//        		}
        		
            	int index = v1MatchList.indexOf(TIV1) ;
            	if(index == -1){
            		v1MatchList.add(TIV1) ;
            		v2MatchList.add(TIV2) ;
            		v1NewMatchList.add(TIV1) ;
            		v1AllItems.remove(TIV1) ;
            		v2AllItems.remove(TIV2) ;
            	}
            	
//            	else{
//            		v2MatchList.get(index).setForeground(BlackColor);
//            		v2AllItems.add(TIV2) ; 
//            		v2MatchList.set(index, TIV2) ;
//            	}
            	
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
            	
            	TIV1.setForeground(GreenColor);
            	TIV2.setForeground(GreenColor);
            	
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
    
//    void initMatchingList(List<TreeItem> v1, List<TreeItem> v2, ArrayList<MatchMember> mList)
    void initMatchingList(ArrayList<MatchMember> mList)
    {
    	for(int i=0; i<mList.size(); i++)
    	{
    		TreeItem TIV1 = null, TIV2 = null ;
    		for(int j=0; j<v1AllItems.size(); j++)
    		{
    			if(v1AllItems.get(j).getText().equals(mList.get(i).getLeftText()) && (v1AllItems.get(j).getParentItem()== null 
    					|| v1AllItems.get(j).getParentItem().getText().equals(mList.get(i).getLeftParent()))){
    				TIV1 = v1AllItems.get(j); 
    				break ;
    			}
    		}
    		for(int j=0; j<v2AllItems.size(); j++)
    		{
    			if(v2AllItems.get(j).getText().equals(mList.get(i).getRightText()) && (v2AllItems.get(j).getParentItem()== null
    					|| v2AllItems.get(j).getParentItem().getText().equals(mList.get(i).getRightParent()))){
    				TIV2 = v2AllItems.get(j); 
    				break ;
    			}
    		}
    		if(TIV1!=null && TIV2!=null){
    			Display display = Display.getCurrent();
            	Color GreenColor = new Color(display,0, 160, 40);
    			TIV1.setForeground(GreenColor);
    			v1AllItems.remove(TIV1) ;
    			v2AllItems.remove(TIV2) ;
    			v1MatchList.add(TIV1) ;
    			v2MatchList.add(TIV2) ;
    		}
    	}
    }
    
    void finalizeOutMatchList()
    {
    	matchMemberList = null ; 
    	matchMemberList = new ArrayList<MatchMember>();
    	MatchMember matchMember ; 

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
	        
    	    int isSelected = 0 ;
    	    int index = v1NewMatchList.indexOf(v1MatchList.get(i)) ; 
    	    if(index != -1){
    	    	isSelected = 1 ; 
    	    	v1NewMatchList.remove(index) ;
    	    }
    	    
	       	matchMember = new MatchMember(LeftParentText, LeftText, RightParentText, RightText, isSelected) ;
		    matchMemberList.add(matchMember);
	    }
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("Matching Window");
        newShell.setSize(800, 600);
        newShell.setLocation(300,50);
    }
	
    @Override
    protected void okPressed() {
    	finalizeOutMatchList() ; 
    	
		String dir = NewModel.getModelImpl().getURI().toString() ;
		dir = dir.replace("file:/", "") ;
		dir = dir.replace(".uml", "Matches.match") ;
		dir = dir.replace(".ecore", "Matches.match") ;
		dir = dir.replace(".xmi", "Matches.match") ;
		dir = dir.replace(".model", "Matches.match") ;
    	
//		XMLEncoder encoder=null;
//		try{
//			encoder=new XMLEncoder(new BufferedOutputStream(new FileOutputStream(dir)));
//		}catch(FileNotFoundException fileNotFound){
//			System.out.println("ERROR: While Creating the File Matches.xml");
//		}
//		encoder.writeObject(matchMemberList);
//		encoder.close();
		
		try {
		    FileOutputStream outputStream=new FileOutputStream(dir);
		    ObjectOutputStream objectOutputStream= new ObjectOutputStream(outputStream);
		    objectOutputStream.writeObject(matchMemberList);
		    objectOutputStream.close(); 
		} catch (FileNotFoundException e) {
		    e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		}
		
		try {
			this.selectedProject.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
        super.okPressed();
    }
}
