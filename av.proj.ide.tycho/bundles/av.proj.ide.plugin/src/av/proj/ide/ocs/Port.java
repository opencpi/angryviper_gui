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

import org.eclipse.sapphire.Element;
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
import av.proj.ide.services.ProtocolPossibleValuesService;

public interface Port extends Element {
	ElementType TYPE = new ElementType(Port.class);

	// *** Name ***
	@CustomXmlValueBinding( impl=CaseInsenitiveAttributeValueBinding.class )
	@Label(standard = "Name")
	@Required
	
	ValueProperty PROP_NAME = new ValueProperty(TYPE, "Name");

	Value<String> getName();
	void setName(String value);
	
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
	
	// *** NumberOfOpCodes ***
	@CustomXmlValueBinding(impl = CaseInsenitiveAttributeValueBinding.class)
	@Label(standard = "NumberOfOpCodes")

	ValueProperty PROP_NUMBER_OF_OP_CODES = new ValueProperty(TYPE, "NumberOfOpCodes");

	Value<String> getNumberOfOpCodes();
	void setNumberOfOpCodes(String value);

	// *** DataValueWidth ***
	@CustomXmlValueBinding(impl = CaseInsenitiveAttributeValueBinding.class)
	@Label(standard = "DataValueWidth")

	ValueProperty PROP_DATA_VALUE_WIDTH = new ValueProperty(TYPE, "DataValueWidth");

	Value<String> getDataValueWidth();
	void setDataValueWidth(String value);

	// *** DatValueGranularity ***
	@CustomXmlValueBinding(impl = CaseInsenitiveAttributeValueBinding.class)
	@Label(standard = "DataValueGranularity")

	ValueProperty PROP_DATA_VALUE_GRANULARITY = new ValueProperty(TYPE, "DataValueGranularity");

	Value<String> getDataValueGranularity();
	void setDataValueGranularity(String value);

	// *** ZeroLengthMessages ***
	@Type(base = Boolean.class)
	@CustomXmlValueBinding(impl = BooleanAttributeRemoveIfFalseValueBinding.class)
	@Label(standard = "ZeroLengthMessages")

	ValueProperty PROP_ZERO_LENGTH_MESSAGES = new ValueProperty(TYPE, "ZeroLengthMessages");

	Value<Boolean> getZeroLengthMessages();
	void setZeroLengthMessages(String value);
	void setZeroLengthMessages(Boolean value);

	// *** MaxMessageValues ***
	@CustomXmlValueBinding(impl = CaseInsenitiveAttributeValueBinding.class)
	@Label(standard = "MaxMessageValues")

	ValueProperty PROP_MAX_MESSAGE_VALUES = new ValueProperty(TYPE, "MaxMessageValues");

	Value<String> getMaxMessageValues();
	void setMaxMessageValues(String value);

	// *** VariableMessageLength ***
	@Type(base = Boolean.class)
	@CustomXmlValueBinding(impl = BooleanAttributeRemoveIfFalseValueBinding.class)
	@Label(standard = "VariableMessageLength")

	ValueProperty PROP_VARIABLE_MESSAGE_LENGTH = new ValueProperty(TYPE, "VariableMessageLength");

	Value<Boolean> getVariableMessageLength();
	void setVariableMessageLength(String value);
	void setVariableMessageLength(Boolean value);

	// *** DiverseDataSizes ***
	@Type(base = Boolean.class)
	@CustomXmlValueBinding(impl = BooleanAttributeRemoveIfFalseValueBinding.class)
	@Label(standard = "DiverseDataSizes")

	ValueProperty PROP_DIVERSE_DATA_SIZES = new ValueProperty(TYPE, "DiverseDataSizes");

	Value<Boolean> getDiverseDataSizes();
	void setDiverseDataSizes(String value);
	void setDiverseDataSizes(Boolean value);

	// *** UnBounded ***
	@Type(base = Boolean.class)
	@CustomXmlValueBinding(impl = BooleanAttributeRemoveIfFalseValueBinding.class)
	@Label(standard = "UnBounded")

	ValueProperty PROP_UN_BOUNDED = new ValueProperty(TYPE, "UnBounded");

	Value<Boolean> getUnBounded();
	void setUnBounded(String value);
	void setUnBounded(Boolean value);
	
}
