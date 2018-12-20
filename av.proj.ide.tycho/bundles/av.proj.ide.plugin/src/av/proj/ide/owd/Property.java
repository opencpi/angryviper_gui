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

package av.proj.ide.owd;

import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlValueBinding;

import av.proj.ide.custom.bindings.value.BooleanAttributeRemoveIfFalseValueBinding;

public interface Property extends av.proj.ide.ocs.Property {
	ElementType TYPE = new ElementType( Property.class );
	
	
	// *** Padding ***
	@Type(base = Boolean.class)
	@CustomXmlValueBinding(impl = BooleanAttributeRemoveIfFalseValueBinding.class )
	@Label(standard = "Padding")

	ValueProperty PROP_PADDING = new ValueProperty(TYPE, "Padding");

	Value<Boolean> getPadding();
	void setPadding(String value);
	void setPadding(Boolean value);
		
	// *** ReadSync ***
	@CustomXmlValueBinding( impl=BooleanAttributeRemoveIfFalseValueBinding.class )
	@Type( base = Boolean.class )
	@Label( standard = "ReadSync" )
	
	ValueProperty PROP_READ_SYNC = new ValueProperty(TYPE, "ReadSync");

	Value<Boolean> getReadSync();
	void setReadSync( String value );
	void setReadSync( Boolean value ); 
	
	// *** WriteSync ***
	@Type( base = Boolean.class )
	@CustomXmlValueBinding( impl=BooleanAttributeRemoveIfFalseValueBinding.class )
	@Label( standard = "WriteSync" )
		
	ValueProperty PROP_WRITE_SYNC = new ValueProperty(TYPE, "WriteSync");

	Value<Boolean> getWriteSync();
	void setWriteSync( String value );
	void setWriteSync( Boolean value ); 
	
	// *** ReadError ***
	@Type( base = Boolean.class )
	@CustomXmlValueBinding( impl=BooleanAttributeRemoveIfFalseValueBinding.class )
	@Label( standard = "ReadError" )
	
	ValueProperty PROP_READ_ERROR = new ValueProperty(TYPE, "ReadError");

	Value<Boolean> getReadError();
	void setReadError( String value );
	void setReadError( Boolean value ); 
	
	// *** WriteError ***
	@Type( base = Boolean.class )
	@CustomXmlValueBinding( impl=BooleanAttributeRemoveIfFalseValueBinding.class )
	@Label( standard = "WriteError" )
		
	ValueProperty PROP_WRITE_ERROR = new ValueProperty(TYPE, "WriteError");

	Value<Boolean> getWriteError();
	void setWriteError( String value );
	void setWriteError( Boolean value );
	
	// *** Members ***
//	@Type ( base = Member.class )
//	@CustomXmlListBinding(impl = OPSMemberXmlListBinding.class)
//	@Label( standard = "Members" )
//		
//	ListProperty PROP_MEMBERS = new ListProperty( TYPE, "Members" );
//	    
//	ElementList<Member> getMembers();
}
