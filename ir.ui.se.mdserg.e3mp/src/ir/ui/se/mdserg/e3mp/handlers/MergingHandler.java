package ir.ui.se.mdserg.e3mp.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

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
			wizard.setWindowTitle("Three-way Merging Part");
			WizardDialog dialog = new WizardDialog(window.getShell(), wizard);

			if (dialog.open() == Window.OK) {
				// Doing something 
				return true ; 
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}		
	
		return null;
	}
}
