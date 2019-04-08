
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
package av.proj.ide.properties;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlListBinding;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlRootBinding;

import av.proj.ide.custom.bindings.list.MultiCaseXmlListBinding;
import av.proj.ide.owd.Property;

@CustomXmlRootBinding (value = PropertiesRootXmlBinding.class)
public interface Properties extends Element {

	ElementType TYPE = new ElementType(Properties.class);
	
	
	//*** Property List ***	
	@Type( base = Property.class )
	@CustomXmlListBinding(impl = MultiCaseXmlListBinding.class)
	@Label(standard = "Properties")
	
	ListProperty PROP_PROPERTIES = new ListProperty(TYPE, "Properties");
	
	ElementList<Property> getProperties();
	
}
