package fr.imag.adele.cadse.si.workspace.uiplatform.swt.exportimport;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.JAXBException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;

import fr.imag.adele.cadse.core.CadseException;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.impl.ExportImportCadseFunction;

public class ExportImportCadseFunctionUI extends ExportImportCadseFunction {

	Set<IProject>					projects					= new HashSet<IProject>();
	private IProgressMonitor pmo;

	protected void includesContents(HashMap<File, String> files) {
		for (IProject p : projects) {
			File eclipseProjectFile = p.getLocation().toFile();
			files.put(eclipseProjectFile, p.getName()+"/");
		}
	}
	
	protected void computeContentFromItem(Item item) {
		IProject r = item.getMainMappingContent(IProject.class);
		if (r != null) {
			projects.add(r);
			projectsMap.put(r.getName(), item.getId());
		}
	}
	
	public Set<IProject> getProjects() {
		return projects;
	}

	public File exportItems(IProgressMonitor pmo, File directory, String exportNameFile,
			boolean tstamp, Item[] rootItems) throws FileNotFoundException, IOException {
		this.pmo = pmo;
		return super.exportItems(directory, exportNameFile, "-cadse", tstamp, rootItems);
	}

	public void importCadseItems(IProgressMonitor pmo, File file) throws MalformedURLException, IOException, JAXBException, CadseException, ClassNotFoundException {
		this.pmo = pmo;
		super.importCadseItems(file);
	}
	
	@Override
	protected void beginTask(String name, int totalWork) {
		if (pmo != null)
			pmo.beginTask(name, totalWork);
	}
	@Override
	protected void worked(int work) {
		if (pmo != null)
			pmo.worked(work);
	}
	
	@Override
	protected void setTaskName(String name) {
		if (pmo != null) {
			pmo.setTaskName(name);
		}
	}
}
