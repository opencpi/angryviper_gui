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

package av.proj.ide.ops;

import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlListBinding;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlRootBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlNamespace;

import av.proj.ide.custom.bindings.list.MultiCaseXmlListBinding;
import av.proj.ide.custom.bindings.root.ProtocolRootXmlBinding;

@CustomXmlRootBinding( value = ProtocolRootXmlBinding.class )

@XmlNamespace( uri = "http://www.w3.org/2001/XInclude", prefix = "xi" )

public interface Protocol extends ProtocolSummary {
	ElementType TYPE = new ElementType( Protocol.class );
	
	// *** Operations ***
	@Type( base = Operation.class )
	@CustomXmlListBinding(impl = MultiCaseXmlListBinding.class )
	@Label( standard = "Operations" )
		
	ListProperty PROP_OPERATIONS = new ListProperty( TYPE, "Operations" );
	
	ElementList<Operation> getOperations();
	
	@Type( base = Include.class )
    @XmlListBinding( mappings = @XmlListBinding.Mapping(element = "xi:include", type = Include.class ) )
	@Label( standard = "Operation File Includes" )
	
	ListProperty PROP_INCLUDES = new ListProperty( TYPE, "Includes" );
	ElementList<Include> geIncludes();

}
