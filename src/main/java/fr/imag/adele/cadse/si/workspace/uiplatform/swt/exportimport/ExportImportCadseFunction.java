package fr.imag.adele.cadse.si.workspace.uiplatform.swt.exportimport;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.bind.JAXBException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;

import adele.util.io.ZipUtil;
import fr.imag.adele.cadse.core.CadseException;
import fr.imag.adele.cadse.core.CadseRuntime;
import java.util.UUID;

import fr.imag.adele.cadse.core.CadseGCST;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.Link;
import fr.imag.adele.cadse.core.LogicalWorkspace;
import fr.imag.adele.cadse.core.ProjectAssociation;
import fr.imag.adele.cadse.core.attribute.IAttributeType;
import fr.imag.adele.cadse.core.impl.CadseCore;
import fr.imag.adele.cadse.core.transaction.LogicalWorkspaceTransaction;
import fr.imag.adele.cadse.core.transaction.delta.ItemDelta;
import fr.imag.adele.cadse.core.transaction.delta.LinkDelta;
import fr.imag.adele.cadse.core.transaction.delta.SetAttributeOperation;

public class ExportImportCadseFunction {

	Set<Link>						outgoinglinks				= new HashSet<Link>();
	Set<ItemType>					requireItemType				= new HashSet<ItemType>();
	Set<CadseRuntime>				requireCadse				= new HashSet<CadseRuntime>();

	Set<IProject>					projects					= new HashSet<IProject>();

	final HashSet<Item>				items						= new HashSet<Item>();

	HashMap<String, UUID>	projectsMap					= new HashMap<String, UUID>();

	HashMap<File, String>			files;

	/** The Constant MELUSINE_DIR. */
	public static final String		MELUSINE_DIR				= ".melusine-dir/";

	/** The Constant MELUSINE_DIR_CADSENAME. */
	public static final String		MELUSINE_DIR_CADSENAME		= ".melusine-dir/cadsename";

	/** The Constant MELUSINE_DIR_CADSENAME_ID. */
	public static final String		MELUSINE_DIR_CADSENAME_ID	= ".melusine-dir/cadsename.id";

	/** The Constant MELUSINE_DIR_CADSENAME_ID. */
	public static final String		REQUIRE_CADSEs				= ".melusine-dir/require-cadses";
	/** The Constant MELUSINE_DIR_CADSENAME_ID. */
	public static final String		REQUIRE_ITEM_TYPEs			= ".melusine-dir/require-its";
	/** The Constant MELUSINE_DIR_CADSENAME_ID. */

	public static final String		PROJECTS					= ".melusine-dir/projects";

	public void exportItems(IProgressMonitor pmo, File file, String exportNameFile, boolean tstamp, Item... rootItems)
			throws FileNotFoundException, IOException {
		CadseCore.getCadseDomain().beginOperation("Export cadse");
		try {

			// String qname = CadseDefinitionManager.getUniqueName(cadsedef);
			File pf = new File(file, exportNameFile + "-cadse.zip");
			if (tstamp) {
				Date d = new Date(System.currentTimeMillis());
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HHmm");
				System.out.println(formatter.format(d));
				pf = new File(file, exportNameFile + "-cadse-" + formatter.format(d) + ".zip");
			}
			ZipOutputStream outputStream = new ZipOutputStream(new FileOutputStream(pf));

			files = new HashMap<File, String>();

			pmo.beginTask("export cadse items ", 3);

			File wsFile = ResourcesPlugin.getWorkspace().getRoot().getLocation().toFile();
			File melusineDir = new File(wsFile, ".cadse");
			for (Item rootItem : rootItems) {
				pmo.setTaskName(rootItem.getName());
				getPersistanceFileAll(melusineDir, rootItem);
			}

			includesContents(pmo, projects, files);
			pmo.worked(1);
			pmo.setTaskName("zip entries...");
			ZipUtil.zip(files, outputStream);

			pmo.worked(2);

			ArrayList<Object> requireCadseIds = new ArrayList<Object>();
			for (CadseRuntime cr : requireCadse) {
				if (items.contains(cr)) {
					continue;
				}

				requireCadseIds.add(cr.getId());
				requireCadseIds.add(cr.getName());
				requireCadseIds.add(cr.getQualifiedName());
				requireCadseIds.add(cr.getVersion());
			}

			// format  UUID, name, qname, int version
			ZipUtil.addEntryZip(outputStream, new ByteArrayInputStream(toByteArray(requireCadseIds.toArray())), REQUIRE_CADSEs,
					-1);

			ArrayList<Object> requireItemIds = new ArrayList<Object>();
			for (ItemType cr : requireItemType) {
				if (items.contains(cr)) {
					continue;
				}
				requireItemIds.add(cr.getId());
				requireItemIds.add(cr.getName());
				requireItemIds.add(cr.getQualifiedName());
				requireItemIds.add(cr.getVersion());
			}

			// format  UUID, name, qname, int version
			ZipUtil.addEntryZip(outputStream, new ByteArrayInputStream(toByteArray(requireItemIds.toArray())),
					REQUIRE_ITEM_TYPEs, -1);

			ZipUtil.addEntryZip(outputStream, new ByteArrayInputStream(toByteArray(projectsMap)), PROJECTS, -1);

			pmo.worked(3);
			outputStream.close();
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			CadseCore.getCadseDomain().endOperation();
		}
	}

	private byte[] toByteArray(Object v) throws IOException {
		ByteArrayOutputStream cd = new ByteArrayOutputStream();
		ObjectOutputStream outObj = new ObjectOutputStream(cd);

		outObj.writeObject(v);
		outObj.flush();
		outObj.close();

		return cd.toByteArray();
	}

	private void includesContents(IProgressMonitor pmo, Set<IProject> projects, HashMap<File, String> files) {
		for (IProject p : projects) {
			File eclipseProjectFile = p.getLocation().toFile();
			files.put(eclipseProjectFile, p.getName()+"/");
		}
	}

	/**
	 * Gets the persistance file all.
	 * 
	 * @param melusineDir
	 *            the melusine dir
	 * @param item
	 *            the item
	 * @param files
	 *            the files
	 * @param items
	 *            the items
	 * 
	 * @return the persistance file all
	 */
	void getPersistanceFileAll(File melusineDir, Item item) {

		if (items.contains(item)) {
			System.err.println("entry duplicate " + item.getId() + " " + item.getQualifiedName());
			return;
		}

		items.add(item);
		ItemType it = item.getType();
		if (it != null) {
			if (!requireItemType.contains(it)) {
				requireItemType.add(it);
				CadseRuntime cr = it.getCadse();
				if (cr != null) {
					requireCadse.add(cr);
				}
			}
		}
		IProject r = item.getMainMappingContent(IProject.class);
		if (r != null) {
			projects.add(r);
			projectsMap.put(r.getName(), item.getId());
		}

		File xmlfile = new File(melusineDir, item.getId().toString() + ".ser");
		files.put(xmlfile, MELUSINE_DIR);
		xmlfile = new File(melusineDir, item.getId().toString() + ".xml");
		if (xmlfile.exists()) {
			files.put(xmlfile, MELUSINE_DIR);
		}

		List<? extends Link> links = item.getOutgoingLinks();
		for (Link link : links) {
			if (!link.getLinkType().isPart()) {
				if (!items.contains(link.getDestination())) {
					outgoinglinks.add(link);
				}

				continue;
			}
			if (!link.isLinkResolved()) {
				outgoinglinks.add(link);
				continue;
			}
			getPersistanceFileAll(melusineDir, link.getDestination());
		}
	}

	public Set<Link> getOutgoinglinks() {
		return outgoinglinks;
	}

	public Set<ItemType> getRequireItemType() {
		return requireItemType;
	}

	public Set<CadseRuntime> getRequireCadse() {
		return requireCadse;
	}

	public Set<IProject> getProjects() {
		return projects;
	}

	public HashSet<Item> getItemsHash() {
		return items;
	}

	/**
	 * Read cadse uuid.
	 * 
	 * @param f
	 *            the root directory
	 * 
	 * @return the compact uuid
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws ClassNotFoundException
	 */
	public Object readObject(File pf, String key) throws IOException, ClassNotFoundException {
		if (!pf.exists())
			return null;
		File data = new File(pf, key);
		if (!data.exists())
			return null;
		ObjectInputStream isr = new ObjectInputStream(new FileInputStream(data));
		try {
			Object o = isr.readObject();
			return o;
		} finally {
			isr.close();
		}
	}

	public Item importCadseItems(IProgressMonitor pmo, File file) throws IOException, MalformedURLException,
			JAXBException, CadseException, ClassNotFoundException {
		CadseCore.getCadseDomain().beginOperation("Import cadse");
		File pf = null;
		try {
			File dir = ResourcesPlugin.getWorkspace().getRoot().getLocation().toFile();
			pf = createTempDirectory(dir);
			pf.mkdirs();

			ZipUtil.unzipFile(file, pf);
			String cadse = readCadseFolder(pf);
			if (cadse != null) {
				pf.renameTo(new File(dir, cadse));
				pf = new File(dir, cadse);
			}
			
			File melusineDir = new File(pf, ".melusine-dir");
			File[] filesserxml = melusineDir.listFiles();
			Collection<URL> itemdescription = new ArrayList<URL>();
			for (File fser : filesserxml) {
				if (fser.getName().endsWith(".ser")) {
					itemdescription.add(fser.toURI().toURL());
				}
			}
			
			LogicalWorkspace lw = CadseCore.getLogicalWorkspace();
			if (cadse == null) {
				Object[] requireCadseIds = (Object[]) readObject(pf,
						REQUIRE_CADSEs);
				for (int i = 0; i < requireCadseIds.length;) {
					UUID id = (UUID) requireCadseIds[i++];
					String name = (String) requireCadseIds[i++];
					String qname = (String) requireCadseIds[i++];
					Integer version = (Integer) requireCadseIds[i++];
					Item cr = lw.getItem(id);
					if (cr == null) {
						throw new CadseException(
								"Missing cadse {0} version {1} ({2}, {3})",
								name, version, qname, id);
					}
					if (!(cr instanceof CadseRuntime)) {
						throw new CadseException(
								"Item {0} version {1} is not a cadse ! ({2}, {3})",
								name, version, qname, id);
					}
				}
				Object[] requireItemTypeIds = (Object[]) readObject(pf,
						REQUIRE_ITEM_TYPEs);
				for (int i = 0; i < requireItemTypeIds.length;) {
					UUID id = (UUID) requireItemTypeIds[i++];
					String name = (String) requireItemTypeIds[i++];
					String qname = (String) requireItemTypeIds[i++];
					Integer version = (Integer) requireItemTypeIds[i++];
					Item cr = lw.getItem(id);
					if (cr == null) {
						throw new CadseException(
								"Missing item type {0} version {1} ({2}, {3})",
								name, version, qname, id);
					}
					if (!(cr instanceof ItemType)) {
						throw new CadseException(
								"Item {0} version {1} is not an item type ! ({2}, {3})",
								name, version, qname, id);
					}
				}
			}
			Collection<ProjectAssociation> projectAssociationSet = new ArrayList<ProjectAssociation>();
			projectsMap = (HashMap<String, UUID>) readObject(pf, PROJECTS);
			if (projectsMap != null) {
				for (Map.Entry<String, UUID> e : projectsMap.entrySet()) {
					ProjectAssociation pa = new ProjectAssociation(
							e.getValue(), e.getKey());
					projectAssociationSet.add(pa);
					File destProject = new File(dir, e.getKey());
					if (destProject.exists()) {
						destProject.delete();
					}
						
					new File(pf, e.getKey()).renameTo(destProject);
				}
			} else {
				UUID uuid = readCadseUUIDFolder(pf);
				ProjectAssociation pa = new ProjectAssociation(uuid, cadse);
				projectAssociationSet.add(pa);
			}
			LogicalWorkspaceTransaction transaction = lw.createTransaction();
			transaction.loadItems(itemdescription);
			
			migrate(transaction);
			UUID uuid = readCadseUUIDFolder(pf);
			ItemDelta cadseDef = transaction.getItem(uuid);
			transaction.commit(false, true, false, projectAssociationSet);
			checkAction(transaction);
			return cadseDef.getBaseItem();			
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} finally {
			if (pf != null)
				pf.delete();
			CadseCore.getCadseDomain().endOperation();
		}
	}
	
	/**
	 * 
	 * @param cadse
	 *            the name of the cadse : can be null, it read from
	 * @param input
	 * @return
	 * @throws IOException
	 * @throws CadseException
	 */
	static public Item importCadse(String cadse, InputStream input) throws IOException, CadseException {
		CadseCore.getCadseDomain().beginOperation("Import cadse");
		try {
			File pf;
			File dir = ResourcesPlugin.getWorkspace().getRoot().getLocation().toFile();
			if (cadse != null)
				pf = new File(dir, cadse);
			else
				pf = createTempDirectory(dir);

			ZipUtil.unzip(input, pf);
			UUID uuid = readCadseUUIDFolder(pf);
			if (cadse == null) {
				cadse = readCadseFolder(pf);
				pf.renameTo(new File(dir, cadse));
				pf = new File(dir, cadse);
			}

			File melusineDir = new File(pf, ".melusine-dir");
			File[] filesserxml = melusineDir.listFiles();
			Collection<URL> itemdescription = new ArrayList<URL>();
			for (File fser : filesserxml) {
				if (fser.getName().endsWith(".ser")) {
					itemdescription.add(fser.toURI().toURL());
				}
			}
			Collection<ProjectAssociation> projectAssociationSet = new ArrayList<ProjectAssociation>();
			ProjectAssociation pa = new ProjectAssociation(uuid, cadse);
			projectAssociationSet.add(pa);
			LogicalWorkspaceTransaction transaction = CadseCore.getLogicalWorkspace().createTransaction();

			transaction.loadItems(itemdescription);
			migrate(transaction);
			ItemDelta cadseDef = transaction.getItem(uuid);
			transaction.commit(false, true, false, projectAssociationSet);
			checkAction(transaction);
			return cadseDef.getBaseItem();
		} finally {
			CadseCore.getCadseDomain().endOperation();
		}
	}
	
	/**
	 * Read cadse.
	 * 
	 * @param f
	 *            the f
	 * 
	 * @return the string
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws JAXBException
	 *             the JAXB exception
	 */
	static public String readCadse(File f) throws IOException, JAXBException {
		JarFile jis = new JarFile(f);
		ZipEntry entry = jis.getEntry(ExportImportCadseFunction.MELUSINE_DIR_CADSENAME);
		if (entry == null) {
			entry = jis.getEntry("/" + ExportImportCadseFunction.MELUSINE_DIR_CADSENAME);
			if (entry == null) {
				return null;
			}
		}
		InputStream imput = jis.getInputStream(entry);
		BufferedReader isr = new BufferedReader(new InputStreamReader(imput));
		return isr.readLine();
	}

	static public String readCadseFolder(File f) throws IOException {
		File cadseNameFile = new File(f, ExportImportCadseFunction.MELUSINE_DIR_CADSENAME);
		if (!cadseNameFile.exists()) {
			return null;
		}
		InputStream imput = new FileInputStream(cadseNameFile);
		BufferedReader isr = new BufferedReader(new InputStreamReader(imput));
		return isr.readLine();
	}
	
	/**
	 * Read cadse uuid.
	 * 
	 * @param f
	 *            the f
	 * 
	 * @return the compact uuid
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws JAXBException
	 *             the JAXB exception
	 */
	public UUID readCadseUUID(File f) throws IOException, JAXBException {
		JarFile jis = new JarFile(f);
		ZipEntry entry = jis.getEntry(ExportImportCadseFunction.MELUSINE_DIR_CADSENAME_ID);
		if (entry == null) {
			entry = jis.getEntry("/" + ExportImportCadseFunction.MELUSINE_DIR_CADSENAME_ID);
			if (entry == null) {
				return null;
			}
		}
		InputStream imput = jis.getInputStream(entry);
		BufferedReader isr = new BufferedReader(new InputStreamReader(imput));
		return UUID.fromString(isr.readLine());
	}

	/**
	 * Read cadse uuid.
	 * 
	 * @param f
	 *            the f
	 * 
	 * @return the compact uuid
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws JAXBException
	 *             the JAXB exception
	 */
	static public UUID readCadseUUIDFolder(File f) throws IOException {
		File uuid = new File(f, ExportImportCadseFunction.MELUSINE_DIR_CADSENAME_ID);

		if (!uuid.exists()) {
			return null;
		}
		InputStream imput = new FileInputStream(uuid);
		BufferedReader isr = new BufferedReader(new InputStreamReader(imput));
		return UUID.fromString(isr.readLine());
	}

	static private void migrate(LogicalWorkspaceTransaction transaction) throws CadseException {
		Collection<ItemDelta> operations = transaction.getItemOperations();
		for (ItemDelta itemDelta : operations) {

			if (itemDelta.getType() == null) {
				if (itemDelta.getBaseItem() != null) {
					itemDelta.setType(itemDelta.getBaseItem().getType());
				} else
					System.out.println("type has no type " + itemDelta);
			}

			if (!itemDelta.isLoaded())
				continue;

//			SetAttributeOperation uuid_att = itemDelta.getSetAttributeOperation("UUID_ATTRIBUTE");
//			if (uuid_att != null) {
//				if (itemDelta.getType() == null) {
//					System.out.println("Import error type is null");
//				} else if (itemDelta.getType() == CadseGCST.CADSE_DEFINITION) {
//					itemDelta.setAttribute(CadseGCST.CADSE_DEFINITION_at_ID_RUNTIME_, uuid_att.getCurrentValue());
//				} else if (itemDelta.isInstanceOf(CadseGCST.PAGE)) {
//					itemDelta.setAttribute(CadseGCST.PAGE_at_ID_RUNTIME_, uuid_att.getCurrentValue());
//				} else if (itemDelta.isInstanceOf(CadseGCST.ATTRIBUTE)) {
//					itemDelta.setAttribute(CadseGCST.ATTRIBUTE_at_ID_RUNTIME_, uuid_att.getCurrentValue());
//				} else if (itemDelta.isInstanceOf(CadseGCST.TYPE_DEFINITION)) {
//					itemDelta.setAttribute(CadseGCST.TYPE_DEFINITION_at_ID_RUNTIME_, uuid_att.getCurrentValue());
//				} else {
//					System.out.println("Cannot set UUID_ATTRIBUTE for type " + itemDelta.getType().getName());
//				}
//				// remove old valeur
//				itemDelta.setAttribute("UUID_ATTRIBUTE", null);
//			}
			if (itemDelta.getType() == CadseGCST.LINK_TYPE) {
				if (itemDelta.getName().startsWith("#invert_part")) {
					itemDelta.delete(false);
					for (Link l : itemDelta.getIncomingLinks()) {
						l.delete();
					}
				}
				LinkDelta l = itemDelta.getOutgoingLink(CadseGCST.LINK_TYPE_lt_INVERSE_LINK);
				if (l != null && l.getDestination().getName().startsWith("#invert_part")) {
					l.delete();
				}
			}
			SetAttributeOperation committed_date_value = itemDelta
					.getSetAttributeOperation(CadseGCST.ITEM_at_COMMITTED_DATE_);
			if (committed_date_value != null) {
				if (committed_date_value.getCurrentValue() instanceof Date) {
					Date d = (Date) committed_date_value.getCurrentValue();
					itemDelta.setAttribute(CadseGCST.ITEM_at_COMMITTED_DATE_, d.getTime());
				}
			}
		}

		for (ItemDelta itemDelta : operations) {
			if (!itemDelta.isLoaded())
				continue;
			for (LinkDelta l : itemDelta.getOutgoingLinkOperations()) {
				if (l.getLinkTypeName().startsWith("#parent:") || l.getLinkTypeName().startsWith("#invert_part")) {
					if (itemDelta.getPartParent() == null) {
						itemDelta.setParent(l.getDestination(), null);
					}
					if (itemDelta.getOutgoingLink(CadseGCST.ITEM_lt_PARENT) == null) {
						itemDelta.createLink(CadseGCST.ITEM_lt_PARENT, l.getDestination());
					}
					l.delete();
				} else if (l.getDestination().getName().contains("#invert_part_")) {
					l.delete();
				}
				if (l.getLinkType() != null && l.getLinkType().isPart() && l.getDestination().getPartParent() == null) {
					l.getDestination().setParent(l.getSource(), l.getLinkType());
				}
			}
			for (LinkDelta l : itemDelta.getOutgoingLinkOperations(CadseGCST.ITEM_lt_MODIFIED_ATTRIBUTES)) {
				if (!l.isLoaded())
					continue;
				if (l.getDestination().getType() == null) {
					IAttributeType<?> att = l.getSource().getLocalAttributeType(l.getDestinationName());
					
					if (att != null) {
						LinkDelta latt = itemDelta.getOutgoingLink(CadseGCST.ITEM_lt_MODIFIED_ATTRIBUTES, att.getId());
						if (latt != null) {
							l.delete();
						} else
							l.changeDestination(att);
					} else
						l.delete();
				}
			}
		}
		for (ItemDelta itemDelta : operations) {
			if (!itemDelta.isLoaded())
				continue;
			if (itemDelta.getPartParent() == null && itemDelta.getType() != null && itemDelta.getType().isPartType()) {
				System.out.println("Error cannot found parent for " + itemDelta.getQualifiedName());
			}
		}
	}

	static private void checkAction(LogicalWorkspaceTransaction transaction) {
		Collection<ItemDelta> operations = transaction.getItemOperations();
		LogicalWorkspace lw = CadseCore.getLogicalWorkspace();
		for (ItemDelta itemDelta : operations) {
			Item gI = lw.getItem(itemDelta.getId());
			if (gI == null) {
				System.err.println("Cannot found commited item " + itemDelta);
				continue;
			}
			Item parent = gI.getPartParent();
			if (parent == null && itemDelta.getPartParent() != null) {
				System.err.println("Parent not setted " + itemDelta + " -> " + itemDelta.getPartParent());
			} else {
				if (parent != null && itemDelta.getPartParent() != null) {
					if (!parent.getId().equals(itemDelta.getPartParent().getId())) {
						System.err.println("Parent not same " + itemDelta + " -> " + itemDelta.getPartParent() + "<>"
								+ parent);
					}
				}
			}

		}

	}

	

	/**
	 * 
	 * @param dir
	 *            can be null : see
	 *            {@link File#createTempFile(String, String, File)}.
	 * @return a tempory folder
	 * @throws IOException
	 */

	public static File createTempDirectory(File dir) throws IOException {
		final File temp;

		temp = File.createTempFile("temp", Long.toString(System.nanoTime()), dir);

		if (!(temp.delete())) {
			throw new IOException("Could not delete temp file: " + temp.getAbsolutePath());
		}

		if (!(temp.mkdir())) {
			throw new IOException("Could not create temp directory: " + temp.getAbsolutePath());
		}

		return (temp);
	}


}
