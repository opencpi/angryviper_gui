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

public interface Library extends Element {
	ElementType TYPE = new ElementType(Library.class);
	
	// *** Name ***
	@Label(standard="Name")
	@XmlBinding(path="@name")
	@ReadOnly
	
	ValueProperty PROP_NAME = new ValueProperty(TYPE, "Name");
	
	Value<String> getName();
	void setName(String value);
	
	@Type(base = Spec.class)
    @XmlListBinding( path = "specs", mappings = @XmlListBinding.Mapping( element = "spec", type = Spec.class ) )
    
    ListProperty PROP_SPECS = new ListProperty( TYPE, "Specs" );
    
    ElementList<Spec> getSpecs();
    
	@Type(base = Worker.class)
    @XmlListBinding( path = "workers", mappings = @XmlListBinding.Mapping( element = "worker", type = Worker.class ) )
    
    ListProperty PROP_WORKERS = new ListProperty( TYPE, "Workers" );
    
    ElementList<Worker> getWorkers();
	
	@Type(base = Test.class)
    @XmlListBinding( path = "tests", mappings = @XmlListBinding.Mapping( element = "test", type = Test.class ) )
    
    ListProperty PROP_TESTS = new ListProperty( TYPE, "Tests" );
    
    ElementList<Test> getTests();
	
}
