package ir.ui.se.mdserg.e3mp.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import ir.ui.se.mdserg.e3mp.wizards.*;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class MergingHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		
		try {
			MergingWizard wizard = new MergingWizard() ; 
			wizard.setWindowTitle("Merging Process");
			WizardDialog dialog = new WizardDialog(window.getShell(), wizard);

			if (dialog.open() == Window.OK) {
				// Doing something 
				return true ; 
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
//        MatchingWizard wizard = new MatchingWizard();
//        WizardDialog dialog = new WizardDialog(window.getShell(), wizard);
//        dialog.create();
//        dialog.open();
		
//		try {
//			ImportWizard wizard = new ImportWizard();
//			WizardDialog dialog = new WizardDialog(window.getShell(), wizard);
//			
//			if (dialog.open() == Window.OK) {
//				MessageDialog.openInformation(
//						window.getShell(),
//						"E3mp",
//						"Epsilon 3-way Merging Process (E3MP)");
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		try {
//			AndroidSecurityExtractionWizard wizard = new AndroidSecurityExtractionWizard();
//			WizardDialog dialog = new WizardDialog(window.getShell(), wizard);
//			
//			if (dialog.open() == Window.OK) {
//				MessageDialog.openInformation(
//						window.getShell(),
//						"E3mp",
//						"Hello, Welcome to Epsilon 3-way Merging Process (E3MP)");
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
	
		return null;
	}
}
