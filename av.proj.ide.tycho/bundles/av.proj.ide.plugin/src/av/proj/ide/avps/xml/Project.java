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
import org.eclipse.sapphire.ImpliedElementProperty;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.ReadOnly;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;


@XmlBinding( path="project" )

public interface Project extends Element {
	ElementType TYPE = new ElementType(Project.class);
	
	// *** Name ***
	@Label(standard = "Name")
	@XmlBinding(path="@name")
	@ReadOnly
	
	ValueProperty PROP_NAME = new ValueProperty(TYPE, "Name");
	
	Value<String> getName();
	void setName(String value);
	
	// *** Applications ***
	
	interface Application extends Element {
		ElementType TYPE = new ElementType(Application.class);
		// *** Name ***
		@Label(standard="Name")
		@XmlBinding(path="@name")
		@ReadOnly
		
		ValueProperty PROP_NAME = new ValueProperty(TYPE, "Name");
		
		Value<String> getName();
		
	}
	@Label(standard="Applications")
	@Type(base = Application.class)
    @XmlListBinding( path = "applications", mappings = @XmlListBinding.Mapping( element = "application", type = Application.class ) )
    
    ListProperty PROP_APPLICATIONS = new ListProperty( TYPE, "Applications" );
    
    ElementList<Application> getApplications();

    
	// *** Components ***
    
	@Label(standard="Components")
	@Type(base = Components.class)
	@XmlBinding(path="components")
	@ReadOnly
	
	ImpliedElementProperty PROP_COMPONENTS = new ImpliedElementProperty(TYPE,"Components");
	
	Components getComponents();
	
	// *** HDL  ***
	
	@Type(base=Hdl.class)
	@XmlBinding(path="hdl")
	@ReadOnly
	
	ImpliedElementProperty PROP_HDL = new ImpliedElementProperty(TYPE,"Hdl");
	
	Hdl getHdl();
	
    
	
}
