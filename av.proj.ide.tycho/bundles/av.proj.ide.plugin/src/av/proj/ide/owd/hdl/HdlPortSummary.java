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

import org.eclipse.sapphire.Element;
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

public interface HdlPortSummary extends Element {
	ElementType TYPE = new ElementType( HdlPortSummary.class );
	
	// *** NumberOfOpCodes ***
	@CustomXmlValueBinding( impl = CaseInsenitiveAttributeValueBinding.class ) 
	@Label( standard = "NumberOfOpCodes")
	
	ValueProperty PROP_NUMBER_OF_OP_CODES = new ValueProperty( TYPE, "NumberOfOpCodes");
	
	Value<String> getNumberOfOpCodes();
	void setNumberOfOpCodes( String value );
	
	// *** DatValueGranularity ***
	@CustomXmlValueBinding( impl = CaseInsenitiveAttributeValueBinding.class ) 
	@Label( standard = "DataValueGranularity")
	
	ValueProperty PROP_DATA_VALUE_GRANULARITY = new ValueProperty( TYPE, "DataValueGranularity");
	
	Value<String> getDataValueGranularity();
	void setDataValueGranularity( String value );
	
	// *** ZeroLengthMessages ***
	@Type( base = Boolean.class )
	@CustomXmlValueBinding( impl=BooleanAttributeRemoveIfFalseValueBinding.class )
	@Label( standard = "ZeroLengthMessages" )
		
	ValueProperty PROP_ZERO_LENGTH_MESSAGES = new ValueProperty(TYPE, "ZeroLengthMessages");
		
	Value<Boolean> getZeroLengthMessages();
	void setZeroLengthMessages( String value );
	void setZeroLengthMessages( Boolean value );
	
	// *** MaxMessageValues ***
	@CustomXmlValueBinding( impl = CaseInsenitiveAttributeValueBinding.class ) 
	@Label( standard = "MaxMessageValues")
	
	ValueProperty PROP_MAX_MESSAGE_VALUES = new ValueProperty( TYPE, "MaxMessageValues");
	
	Value<String> getMaxMessageValues();
	void setMaxMessageValues( String value );
	
}
