package ir.ui.se.mdserg.e3mp.helper;

import java.util.ArrayList;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;


public class UserInterDialog extends Dialog {

	public String result ; 
	public int index ; 
	public String eLabel ;
	public String title ; 
	public ArrayList<String> eList ;
	public Button[] radios ; 
	
	
	/**
	 * Create the dialog.
	 */
	public UserInterDialog(Shell parentShell,String eLabel, String etitle, ArrayList<String> eList) {
		super(parentShell);
		this.eLabel = eLabel ; 
		this.title = etitle ; 
		this.eList = eList ; 
	}
	
    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);
        
        Label label = new Label(container, SWT.NULL);
        label.setText(this.eLabel);
        
        Composite composite = new Composite(container, SWT.NULL);
        radios = new Button[this.eList.size()];

        for(int i=0 ; i<this.eList.size(); i++)
        {
        	radios[i] = new Button(composite, SWT.RADIO);
            radios[i].setText(this.eList.get(i));
            radios[i].setBounds(10, 5+i*30,500, 30);
        }
        return container;
    }

    // overriding this methods allows you to set the
    // title of the custom dialog
    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(title);
    }

	
    @Override
    protected void okPressed() {
    	for(int i=0; i<this.eList.size(); i++)
    	{
    		if(radios[i].getSelection()==true)
    		{
    			result = this.eList.get(i) ; 
    			index = i ; 
    			break ; 
    		}
    	}	
        super.okPressed();
    }
    
}

