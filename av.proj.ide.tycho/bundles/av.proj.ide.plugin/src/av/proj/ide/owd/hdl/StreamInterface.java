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
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlValueBinding;

import av.proj.ide.custom.bindings.value.BooleanAttributeRemoveIfFalseValueBinding;
import av.proj.ide.custom.bindings.value.GenericDualCaseXmlValueBinding;
import av.proj.ide.custom.bindings.value.GenericMultiwordXmlValueBinding;

public interface StreamInterface extends Element {
	ElementType TYPE = new ElementType( StreamInterface.class );
	
	// *** Name *** 
	@CustomXmlValueBinding( impl=GenericDualCaseXmlValueBinding.class )
	@Label(standard = "Name")
	@Required 
	
	ValueProperty PROP_NAME = new ValueProperty(TYPE, "Name");

	Value<String> getName();
	void setName(String value);
		
	// *** DataWidth *** 
	@CustomXmlValueBinding( impl=GenericMultiwordXmlValueBinding.class )
	@Label(standard = "DataWidth")
		
	ValueProperty PROP_DATA_WIDTH = new ValueProperty(TYPE, "DataWidth");

	Value<String> getDataWidth();
	void setDataWidth(String value);
	
	// *** PreciseBurst ***
	@Type( base = Boolean.class )
	@CustomXmlValueBinding( impl=GenericMultiwordXmlValueBinding.class )
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
	
	// *** Pattern *** 
	@CustomXmlValueBinding( impl=GenericDualCaseXmlValueBinding.class )
	@Label(standard = "Pattern")
			
	ValueProperty PROP_PATTERN = new ValueProperty(TYPE, "Pattern");

	Value<String> getPattern();
	void setPattern(String value);
	
	// *** Clock *** 
	@CustomXmlValueBinding( impl=GenericDualCaseXmlValueBinding.class )
	@Label(standard = "Clock")
			
	ValueProperty PROP_CLOCK = new ValueProperty(TYPE, "Clock");

	Value<String> getClock();
	void setClock(String value);
	
	// *** MyClock ***
	@Type( base = Boolean.class )
	@CustomXmlValueBinding( impl=GenericMultiwordXmlValueBinding.class )
	@Label( standard = "MyClock" )
		
	ValueProperty PROP_MY_CLOCK = new ValueProperty(TYPE, "MyClock");
		
	Value<Boolean> getMyClock();
	void setMyClock( String value );
	void setMyClock( Boolean value );
	
	// *** NumberOfOpCodes ***
	@CustomXmlValueBinding( impl=GenericMultiwordXmlValueBinding.class )
	@Label(standard = "NumberOfOpCodes")
			
	ValueProperty PROP_NUMBER_OF_OP_CODES = new ValueProperty(TYPE, "NumberOfOpCodes");

	Value<String> getNumberOfOpCodes();
	void setNumberOfOpCodes(String value);
	
	// *** DataValueWidth ***
	@CustomXmlValueBinding( impl=GenericMultiwordXmlValueBinding.class )
	@Label(standard = "DataValueWidth")
			
	ValueProperty PROP_DATA_VALUE_WIDTH = new ValueProperty(TYPE, "DataValueWidth");

	Value<String> getDataValueWidth();
	void setDataValueWidth(String value);
	
	// *** DataValueGranularity ***
	@CustomXmlValueBinding( impl=GenericMultiwordXmlValueBinding.class )
	@Label(standard = "DataValueGranularity")
			
	ValueProperty PROP_DATA_VALUE_GRANULARITY = new ValueProperty(TYPE, "DataValueGranularity");

	Value<String> getDataValueGranularity();
	void setDataValueGranularity(String value);
	
	// *** ZeroLengthMessages ***
	@Type( base = Boolean.class )
	@CustomXmlValueBinding( impl=GenericMultiwordXmlValueBinding.class )
	@Label( standard = "ZeroLengthMessages" )
		
	ValueProperty PROP_ZERO_LENGTH_MESSAGES = new ValueProperty(TYPE, "ZeroLengthMessages");
		
	Value<Boolean> getZeroLengthMessages();
	void setZeroLengthMessages( String value );
	void setZeroLengthMessages( Boolean value );
	
	// *** MaxMessageValues ***
	@CustomXmlValueBinding(impl = GenericMultiwordXmlValueBinding.class)
	@Label(standard = "MaxMessageValues")

	ValueProperty PROP_MAX_MESSAGE_VALUES = new ValueProperty(TYPE, "MaxMessageValues");
	Value<String> getMaxMessageValues();
	void setMaxMessageValues(String value);

	// *** VariableMessageLength ***
	@Type(base = Boolean.class)
	@CustomXmlValueBinding(impl = GenericMultiwordXmlValueBinding.class)
	@Label(standard = "VariableMessageLength")

	ValueProperty PROP_VARIABLE_MESSAGE_LENGTH = new ValueProperty(TYPE, "VariableMessageLength");

	Value<Boolean> getVariableMessageLength();
	void setVariableMessageLength(String value);
	void setVariableMessageLength(Boolean value);

	// *** DiverseDataSizes ***
	@Type(base = Boolean.class)
	@CustomXmlValueBinding(impl = GenericMultiwordXmlValueBinding.class)
	@Label(standard = "DiverseDataSizes")

	ValueProperty PROP_DIVERSE_DATA_SIZES = new ValueProperty(TYPE, "DiverseDataSizes");

	Value<Boolean> getDiverseDataSizes();
	void setDiverseDataSizes(String value);
	void setDiverseDataSizes(Boolean value);

	// *** UnBounded ***
	@Type(base = Boolean.class)
	@CustomXmlValueBinding(impl = GenericMultiwordXmlValueBinding.class)
	@Label(standard = "UnBounded")

	ValueProperty PROP_UN_BOUNDED = new ValueProperty(TYPE, "UnBounded");

	Value<Boolean> getUnBounded();
	void setUnBounded(String value);
	void setUnBounded(Boolean value);
}
