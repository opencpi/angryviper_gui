/*
 * This file is protected by Copyright. Please refer to the COPYRIGHT file
 * distributed with this source distribution.
 *
 * This file is part of OpenCPI <http://www.opencpi.org>
 *
 * OpenCPI is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * OpenCPI is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package av.proj.ide.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class SpecTraverseUtility {
	
	private List<String> files;
	private String projectPackage;
	private boolean findPrefix;
	private HashSet<String> invalidSpecPath;
	
	public SpecTraverseUtility(boolean findPrefix) {
		this.files = new ArrayList<String>();
		this.projectPackage = "";
		this.findPrefix = findPrefix;
		invalidSpecPath = new HashSet<String>();
		invalidSpecPath.add("applications");
		invalidSpecPath.add("doc");
		invalidSpecPath.add("exports");
		invalidSpecPath.add("imports");
		invalidSpecPath.add("scripts");
		invalidSpecPath.add("gen");
		invalidSpecPath.add("lib");
		invalidSpecPath.add("adapters");
		invalidSpecPath.add("assemblies");
		invalidSpecPath.add("platforms");
		invalidSpecPath.add("primitives");
	}
	
	protected boolean itsNotOneOfLike(String dirName) {
		boolean itsNotLike = false;
		if( dirName.endsWith(".rcc") 
			|| dirName.endsWith(".hdl") || dirName.endsWith(".hdl")
			|| dirName.endsWith(".test")) {
			itsNotLike = true;
		}
		return itsNotLike;
	}
	
	public void traverseForSpecs(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i=0; children != null && i < children.length; ++i) {
				if(! invalidSpecPath.contains(children[i]) || itsNotOneOfLike(children[i]))
					traverseForSpecs(new File(dir, children[i]));
			}
		} else if (dir.isFile() && !Files.isSymbolicLink(dir.toPath())) {
			String name = dir.getName();
			String prefix = "";
			if (findPrefix) {
				prefix = this.projectPackage;
				String packageName = getPackageName(dir.getParentFile().getParentFile());
				if (!packageName.equals("") && !packageName.equals("local")) {
					// Found package specified in Makefile
					prefix = packageName + ".";
				} else {
					// No package specified in Makefile
					// Use projectPackage
					//   if local don't use library
					//   otherwise use the library name if its not components
					if (!this.projectPackage.equals("local")) {
						packageName = dir.getParentFile().getParentFile().getName();
						if (!packageName.equals("components")) {
							prefix += packageName+".";
						}
					}
				}
			}
			
			if (name.endsWith("-spec.xml")) {
				this.files.add(prefix+name.replace("-spec.xml", ""));
			} else if (name.endsWith("_spec.xml")) {
				this.files.add(prefix+name.replace("_spec.xml", ""));
			}
		}
	}
	
	
	public void traverseForProts(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i=0; children != null && i < children.length; ++i) {
				if(! invalidSpecPath.contains(children[i]) || itsNotOneOfLike(children[i]))
						traverseForProts(new File(dir, children[i]));
			}
		} else if (dir.isFile() && !Files.isSymbolicLink(dir.toPath())) {
			String name = dir.getName();
			if (name.endsWith("-prot.xml") ||
					name.endsWith("_prot.xml") ||
					name.endsWith("-protocol.xml") ||
					name.endsWith("_protocol.xml")) {
				this.files.add(name.replace(".xml", ""));
			}
		}
	}
	
	private String getPackageName(File file) {
		String packageName = "";
		if (file.isDirectory()) {
			String[] children = file.list();
			if (children != null) {
				for (String s : children) {
					if (s.equals("Makefile")) {
						File makefile = new File(file, s);
						String line = null;
						FileReader fileReader = null;
						BufferedReader bufferedReader = null;
						try {
							fileReader = new FileReader(makefile);
							bufferedReader = new BufferedReader(fileReader);
							while((line = bufferedReader.readLine()) != null) {
								if (line.startsWith("Package=")) {
									packageName = line.replace("Package=", "");
									break;
								}
							}
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						} finally {
							try {
								if (fileReader != null) {
									fileReader.close();
								}
								if (bufferedReader != null) {
									bufferedReader.close();
								}
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		}
		return packageName;
	}
	
	public void clear() {
		this.files.clear();
	}
	
	public List<String> getFiles() {
		return this.files;
	}
	
	public void setProjectPackage(String value) {
		if (value.equals("")) {
			this.projectPackage = value;
		} else {
			this.projectPackage = value+".";
		}
	}
	
	public void setFindPrefix(boolean value) {
		this.findPrefix = value;
	}
}
