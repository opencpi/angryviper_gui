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
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.LongString;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlListBinding;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlValueBinding;

import av.proj.ide.custom.bindings.list.MultiCaseXmlListBinding;
import av.proj.ide.custom.bindings.value.CaseInsenitiveAttributeValueBinding;

public interface Argument extends av.proj.ide.common.PropertyAttributes {
	ElementType TYPE = new ElementType(Argument.class);

	// *** Description ***
	@CustomXmlValueBinding( impl=CaseInsenitiveAttributeValueBinding.class )
	@Label(standard = "Description")
	@LongString
	
	ValueProperty PROP_DESCRIPTION = new ValueProperty(TYPE, "Description");

	Value<String> getDescription();
	void setDescription(String value);

	// *** StringLength ***
	// This is a property attribute. It is separated out because it is 
	// not required in an Argument description but is in OCS Property.
	// 
	@CustomXmlValueBinding(impl = CaseInsenitiveAttributeValueBinding.class)
	@Label(standard = "StringLength")

	ValueProperty PROP_STRING_LENGTH = new ValueProperty(TYPE, "StringLength");

	Value<String> getStringLength();
	void setStringLength(String value);
	
	// *** Members ***
	@Type ( base = Argument.class )
	@CustomXmlListBinding(impl = MultiCaseXmlListBinding.class )
	@Label( standard = "Members" )
		
	ListProperty PROP_MEMBERS = new ListProperty( TYPE, "Members" );
	    
	ElementList<Argument> getMembers();
}
