package ir.ui.se.mdserg.e3mp.helper;

import java.awt.Container;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;


import javax.swing.JTextField;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import java.awt.Toolkit;

public class UserInterDialog extends Dialog {

//	private final JPanel contentPanel = new JPanel();
//	private JTextField textField;
	public String result ; 
	public int index ; 
	public String eLabel ;
	public String title ; 
	public ArrayList<String> eList ;
	public Button[] radios ; 
	
//	private JLabel eLabelQ , eLabelE ; 
//    private JButton btnSubmit;    
//    private ButtonGroup btnGroup ; 


	/**
	 * Launch the application.
	 */
//	public static void main(String[] args) {
//		try {
//			UserDialog dialog = new UserDialog();
//			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
//			dialog.setVisible(true);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}


	
	/**
	 * Create the dialog.
	 */
//	public UserInterDialog(Frame parent, boolean modal,String eLabel, String eName, ArrayList<String> eList) {
	public UserInterDialog(Shell parentShell,String eLabel, String etitle, ArrayList<String> eList) {
		super(parentShell);
		this.eLabel = eLabel ; 
		this.title = etitle ; 
		this.eList = eList ; 
	}
	
    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);
//        Button button = new Button(container, SWT.PUSH);
//        button.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false,
//                false));
//        button.setText("Press me");
//        button.addSelectionListener(new SelectionAdapter() {
//            @Override
//            public void widgetSelected(SelectionEvent e) {
//                System.out.println("Pressed");
//            }
//        });

        
        Label label = new Label(container, SWT.NULL);
        label.setText(this.eLabel);
//        label.setBackground(display.getSystemColor(SWT.COLOR_YELLOW));
        
        Composite composite = new Composite(container, SWT.NULL);
//        composite.setLayout(new RowLayout());
        
//        Button mrButton = new Button(composite, SWT.RADIO);
//        mrButton.setText("Mr.");
//        Button mrsButton = new Button(composite, SWT.RADIO);
//        mrsButton.setText("Mrs.");
//        Button msButton = new Button(composite, SWT.RADIO);
//        msButton.setText("Ms.");
//        Button drButton = new Button(composite, SWT.RADIO);
//        drButton.setText("Dr."); 
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

//    @Override
//    protected Point getInitialSize() {
//        return new Point(450, 300);
//    }
	
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
    
    
    
	
//    	Container cp = getContentPane();
//  		cp.setLayout(null);
//  		
//		setResizable(false);
//		setBounds(100, 100, 500, 310);
//		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
//		setTitle("Equivalent Selection");
//        setLocationRelativeTo(null);
//
//		
//		
//  		eLabelQ = new JLabel(eLabel);
//  		eLabelQ.setBounds(10, 7, 474, 15);
//  		eLabelQ.setToolTipText(eLabelQ.getText());
//  		getContentPane().add(eLabelQ);
//  		
//  		eLabelE = new JLabel(eName);
//  		eLabelE.setBounds(10, 25, 474, 15);
//  		eLabelE.setToolTipText(eLabelE.getText());
//  		getContentPane().add(eLabelE);
//  		
//  		textField = new JTextField();
//  		textField.setEditable(false);
//  		textField.setBounds(10, 51, 374, 20);
//  		getContentPane().add(textField);
//  		textField.setColumns(10);
//  		textField.setText(eList.get(0));
//  		
//  		btnSubmit = new JButton("Submit");
//  		btnSubmit.addActionListener(new ActionListener() {
//  			public void actionPerformed(ActionEvent arg0) {
//  				result = textField.getText() ; 
//				setVisible(false);
//				dispose();
//  			}
//  		});
//  		btnSubmit.setBounds(394, 49, 90, 25);
//  		getContentPane().add(btnSubmit);
//  		
//  		JScrollPane scrollPane = new JScrollPane();
//  		scrollPane.setBounds(10, 80, 474, 192);
//  		      	 
//    	  
//    	  int rbCount = eList.size() ;  
//    	  JPanel radioPanel = new JPanel();
//    	  btnGroup = new ButtonGroup();
//          radioPanel.setLayout(new GridLayout(rbCount, 1));
//          
//        //  JTextField field = new JTextField();
//
//          RadioListener listener = new RadioListener(textField);
//          
//    	  JRadioButton rb1 = new JRadioButton(eList.get(0)) ;
//    	  rb1.setSelected(true);
//    	  rb1.addActionListener(listener);
//		  btnGroup.add(rb1); 
//		  radioPanel.add(rb1) ; 
//		  
//    	  for (int i = 1; i < eList.size(); i++) {
//    		  JRadioButton rb = new JRadioButton(eList.get(i)) ;
//    		  rb.addActionListener(listener);
//    		  btnGroup.add(rb);
//    		  radioPanel.add(rb) ;  
//    	  }
//    	  scrollPane.setViewportView(radioPanel);
//    	  cp.add(scrollPane);
//    	  
//  		setVisible(true);		

//	}

//	   private static class RadioListener implements ActionListener{
//
//	        private JTextField textField;
//	        
//	        public RadioListener(JTextField textField){
//	            this.textField = textField;
//	        }
//
//	        @Override
//	        public void actionPerformed(ActionEvent e){
//	            JRadioButton button = (JRadioButton) e.getSource();
//	            textField.setText(button.getText());
//	        }
//	    }
}

