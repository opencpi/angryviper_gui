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

package av.proj.ide.avps.xml;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.ReadOnly;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;

public interface Hdl extends Element {
	ElementType TYPE = new ElementType(Hdl.class);

	
	interface Assembly extends Element {
		ElementType TYPE = new ElementType(Assembly.class);
		@Label(standard="Name")
		@XmlBinding(path="@name")
		@ReadOnly
		ValueProperty PROP_NAME = new ValueProperty(TYPE, "Name");
		Value<String> getName();
		void setName(String value);
	}
	
	@Type(base = Assembly.class)
    @XmlListBinding( path = "assemblies", mappings = @XmlListBinding.Mapping( element = "assembly", type = Assembly.class ) )
	@ReadOnly
    
	ListProperty PROP_ASSEMBLIES = new ListProperty(TYPE, "Assemblies");
	
    ElementList<Assembly>  getAssemblies();
	
	
	// *** Library Elements ***
	@Label(standard="Libraries")
	@Type(base=Library.class)
    @XmlListBinding( path = "libraries", mappings = @XmlListBinding.Mapping( element = "library", type = Library.class ) )
	
	ListProperty PROP_LIBRARIES = new ListProperty(TYPE, "Libraries");

	ElementList<Library> getLibraries();
	
	
	// *** Primitive Elements ***
	
	interface Primitive extends Element {
		ElementType TYPE = new ElementType(Primitive.class);
		@Label(standard="Name")
		@XmlBinding(path="@name")
		@ReadOnly
		ValueProperty PROP_NAME = new ValueProperty(TYPE, "Name");
		Value<String> getName();
		void setName(String value);
	}	

    @Type(base = Primitive.class)
    @XmlListBinding( path = "primitives", mappings = @XmlListBinding.Mapping( element = "primitive", type = Primitive.class ) )
	@ReadOnly
    
	ListProperty PROP_PRIMITIVES = new ListProperty(TYPE,"Primitives");
    
    ElementList<Primitive>  getPrimitives();
    
	interface Platform extends Element {
		ElementType TYPE = new ElementType(Platform.class);
		@Label(standard="Name")
		@XmlBinding(path="@name")
		@ReadOnly
		ValueProperty PROP_NAME = new ValueProperty(TYPE, "Name");
		Value<String> getName();
		void setName(String value);
	}	

    @Type(base = Platform.class)
    @XmlListBinding( path = "platforms", mappings = @XmlListBinding.Mapping( element = "platform", type = Platform.class ) )
	@ReadOnly
    
	ListProperty PROP_PLATFORMS = new ListProperty(TYPE,"Platforms");
    
    ElementList<Platform>  getPlatforms();
    
	
	
}
