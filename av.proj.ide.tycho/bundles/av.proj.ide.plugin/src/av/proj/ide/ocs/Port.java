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

package av.proj.ide.ocs;

import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlValueBinding;

import av.proj.ide.custom.bindings.value.BooleanAttributeRemoveIfFalseValueBinding;
import av.proj.ide.custom.bindings.value.CaseInsenitiveAttributeValueBinding;
import av.proj.ide.ops.ProtocolSummary;
import av.proj.ide.services.ProtocolPossibleValuesService;

public interface Port extends ProtocolSummary {
	ElementType TYPE = new ElementType(Port.class);

	// *** Name ***(required for name attribute of Protocol in OPS Editor)
	@CustomXmlValueBinding( impl = CaseInsenitiveAttributeValueBinding.class ) 
	@Label( standard = "Name")
	@Required
	
	ValueProperty PROP_NAME = new ValueProperty( TYPE, "Name");
	
	Value<String> getName();
	void setName( String value );
	
	// *** Producer ***
	@Type ( base = Boolean.class )
	@CustomXmlValueBinding( impl = BooleanAttributeRemoveIfFalseValueBinding.class )
	@Label( standard = "Producer" )

	ValueProperty PROP_PRODUCER = new ValueProperty( TYPE, "Producer" );
		
	Value<Boolean> getProducer();
	void setProducer( String value );
	void setProducer(Boolean value);

	// *** Protocol ***
	@CustomXmlValueBinding( impl=CaseInsenitiveAttributeValueBinding.class )
	@Label(standard = "Protocol")
	@Service(impl=ProtocolPossibleValuesService.class)
	
	ValueProperty PROP_PROTOCOL = new ValueProperty(TYPE, "Protocol");

	Value<String> getProtocol();
	void setProtocol(String value);
	

	// *** Optional ***
	@Type( base = Boolean.class )
	@CustomXmlValueBinding( impl=BooleanAttributeRemoveIfFalseValueBinding.class )
	@Label(standard = "Optional")
	
	ValueProperty PROP_OPTIONAL = new ValueProperty(TYPE, "Optional");

	Value<Boolean> getOptional();
	void setOptional(String value);
	void setOptional(Boolean value);
	
	// These are in protocol summary
	// *** Name ***
	// *** NumberOfOpCodes ***
	// *** DataValueGranularity ***
	// *** ZeroLengthMessages ***
	// *** MaxMessageValues ***
	// *** VariableMessageLength ***
	// *** DiverseDataSizes ***
	// *** UnBounded ***
	// *** DefaultBufferSize ***

	// ProtocolSummary
	// Missing:
	//  - MinMessageValues
	//  - BufferSize
}
