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

package av.proj.ide.owd.hdl;

import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlRootBinding;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlValueBinding;

import av.proj.ide.custom.bindings.root.ProtocolRootXmlBinding;
import av.proj.ide.custom.bindings.value.BooleanAttributeRemoveIfFalseValueBinding;
import av.proj.ide.custom.bindings.value.CaseInsenitiveAttributeValueBinding;

@CustomXmlRootBinding( value = ProtocolRootXmlBinding.class )

public interface HdlDataPortSpecific extends HdlPortSummary {
	ElementType TYPE = new ElementType( HdlDataPortSpecific.class );

	// *** DataWidth *** 
	@CustomXmlValueBinding( impl=CaseInsenitiveAttributeValueBinding.class )
	@Label(standard = "DataWidth") 
		
	ValueProperty PROP_DATA_WIDTH = new ValueProperty(TYPE, "DataWidth");

	Value<String> getDataWidth();
	void setDataWidth(String value);

	// *** DataValueSize ***
	@CustomXmlValueBinding( impl=CaseInsenitiveAttributeValueBinding.class )
	@Label(standard = "DataValueSize")
		
	ValueProperty PROP_DATA_VALUE_SIZE = new ValueProperty(TYPE, "DataValueSize");

	Value<String> getDataValueSize();
	void setDataValueSize(String value);
	
	// *** PreciseBurst ***
	@Type( base = Boolean.class )
	@CustomXmlValueBinding( impl=BooleanAttributeRemoveIfFalseValueBinding.class )
	@Label( standard = "PreciseBurst" )
			
	ValueProperty PROP_PRECISE_BURST = new ValueProperty(TYPE, "PreciseBurst");
			
	Value<Boolean> getPreciseBurst();
	void setPreciseBurst( String value );
	void setPreciseBurst( Boolean value );
	
	// *** Abortable ***
	@Type( base = Boolean.class )
	@CustomXmlValueBinding( impl=BooleanAttributeRemoveIfFalseValueBinding.class )
	@Label( standard = "Abortable" )
				
	ValueProperty PROP_ABORTABLE = new ValueProperty(TYPE, "Abortable");
				
	Value<Boolean> getAbortable();
	void setAbortable( String value );
	void setAbortable( Boolean value );

}
