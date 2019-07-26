package ir.ui.se.mdserg.e3mp.helper;


import java.io.File;
import java.io.InputStream;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;

import org.eclipse.core.filebuffers.manipulation.ContainerCreator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;

/**
 * @since 3.0
 */
public class ResourceHelper {
	
	private final static IProgressMonitor NULL_MONITOR= new NullProgressMonitor();
	private static final int MAX_RETRY= 5;
	
	public static IProject createProject(String projectName) throws CoreException {
		
		IWorkspaceRoot root= ResourcesPlugin.getWorkspace().getRoot();
		IProject project= root.getProject(projectName);
		if (!project.exists())
			project.create(NULL_MONITOR);
		else
			project.refreshLocal(IResource.DEPTH_INFINITE, null);
		
		if (!project.isOpen())
			project.open(NULL_MONITOR);
		
		return project;
	}
	
	public static void deleteProject(String projectName) throws CoreException {
		IWorkspaceRoot root= ResourcesPlugin.getWorkspace().getRoot();
		IProject project= root.getProject(projectName);
		if (project.exists())
			delete(project);
	}
	
	public static void delete(final IProject project) throws CoreException {
		delete(project, true);
	}
	
	public static void delete(final IProject project, boolean deleteContent) throws CoreException {
		for (int i= 0; i < MAX_RETRY; i++) {
			try {
				project.delete(deleteContent, true, NULL_MONITOR);
				i= MAX_RETRY;
			} catch (CoreException x) {
				
				try {
					Thread.sleep(1000); // sleep a second
				} catch (InterruptedException e) {
				} 
			}
		}
	}
	
	public static IFolder createFolder(String portableFolderPath) throws CoreException {
		ContainerCreator creator= new ContainerCreator(ResourcesPlugin.getWorkspace(), new Path(portableFolderPath));
		IContainer container= creator.createContainer(NULL_MONITOR);
		if (container instanceof IFolder)
			return (IFolder) container;
		return null;
	}

	public static IFile createFile(IFolder folder, String name, String contents) throws CoreException {
		return createFile(folder.getFile(name), name, contents);
	}
	
	public static IFile createFile(IProject project, String name, String contents) throws CoreException {
		return createFile(project.getFile(name), name, contents);
	}
	
	@SuppressWarnings("deprecation")
	private static IFile createFile(IFile file, String name, String contents) throws CoreException {
		if (contents == null)
			contents= "";
		InputStream inputStream= new java.io.StringBufferInputStream(contents);
		file.create(inputStream, true, NULL_MONITOR);
		return file;
	}
	
	public static IFile createLinkedFile(IContainer container, IPath linkPath, File linkedFileTarget) throws CoreException {
		IFile iFile= container.getFile(linkPath);
		iFile.createLink(new Path(linkedFileTarget.getAbsolutePath()), IResource.ALLOW_MISSING_LOCAL, NULL_MONITOR);
		return iFile;
	}
	
	
	
	public static IFolder createLinkedFolder(IContainer container, IPath linkPath, File linkedFolderTarget) throws CoreException {
		IFolder folder= container.getFolder(linkPath);
		folder.createLink(new Path(linkedFolderTarget.getAbsolutePath()), IResource.ALLOW_MISSING_LOCAL, NULL_MONITOR);
		return folder;
	}
	
	
	
}