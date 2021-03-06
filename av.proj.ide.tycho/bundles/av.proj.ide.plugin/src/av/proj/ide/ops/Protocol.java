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

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlListBinding;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlRootBinding;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlValueBinding;

import av.proj.ide.custom.bindings.list.OPSOperationXmlListBinding;
import av.proj.ide.custom.bindings.root.ProtocolRootXmlBinding;
import av.proj.ide.custom.bindings.value.GenericMultiwordXmlValueBinding;
import av.proj.ide.custom.bindings.value.GenericDualCaseXmlValueBinding;

@CustomXmlRootBinding( value = ProtocolRootXmlBinding.class )

public interface Protocol extends Element {
	ElementType TYPE = new ElementType( Protocol.class );

	// *** Name ***
	@CustomXmlValueBinding( impl = GenericDualCaseXmlValueBinding.class ) 
	@Label( standard = "Name")
	
	ValueProperty PROP_NAME = new ValueProperty( TYPE, "Name");
	
	Value<String> getName();
	void setName( String value );
	
	// *** NumberOfOpCodes ***
	@CustomXmlValueBinding( impl = GenericMultiwordXmlValueBinding.class ) 
	@Label( standard = "NumberOfOpCodes")
	
	ValueProperty PROP_NUMBER_OF_OP_CODES = new ValueProperty( TYPE, "NumberOfOpCodes");
	
	Value<String> getNumberOfOpCodes();
	void setNumberOfOpCodes( String value );
	
	// *** DataValueWidth ***
	@CustomXmlValueBinding( impl = GenericMultiwordXmlValueBinding.class ) 
	@Label( standard = "DataValueWidth")
	
	ValueProperty PROP_DATA_VALUE_WIDTH = new ValueProperty( TYPE, "DataValueWidth");
	
	Value<String> getDataValueWidth();
	void setDataValueWidth( String value );
	
	// *** DatValueGranularity ***
	@CustomXmlValueBinding( impl = GenericMultiwordXmlValueBinding.class ) 
	@Label( standard = "DataValueGranularity")
	
	ValueProperty PROP_DATA_VALUE_GRANULARITY = new ValueProperty( TYPE, "DataValueGranularity");
	
	Value<String> getDataValueGranularity();
	void setDataValueGranularity( String value );
	
	// *** ZeroLengthMessages ***
	@Type( base = Boolean.class )
	@CustomXmlValueBinding( impl=GenericMultiwordXmlValueBinding.class )
	@Label( standard = "ZeroLengthMessages" )
		
	ValueProperty PROP_ZERO_LENGTH_MESSAGES = new ValueProperty(TYPE, "ZeroLengthMessages");
		
	Value<Boolean> getZeroLengthMessages();
	void setZeroLengthMessages( String value );
	void setZeroLengthMessages( Boolean value );
	
	// *** MaxMessageValues ***
	@CustomXmlValueBinding( impl = GenericMultiwordXmlValueBinding.class ) 
	@Label( standard = "MaxMessageValues")
	
	ValueProperty PROP_MAX_MESSAGE_VALUES = new ValueProperty( TYPE, "MaxMessageValues");
	
	Value<String> getMaxMessageValues();
	void setMaxMessageValues( String value );
	
	// *** VariableMessageLength ***
	@Type( base = Boolean.class )
	@CustomXmlValueBinding( impl=GenericMultiwordXmlValueBinding.class )
	@Label( standard = "VariableMessageLength" )
		
	ValueProperty PROP_VARIABLE_MESSAGE_LENGTH = new ValueProperty(TYPE, "VariableMessageLength");
		
	Value<Boolean> getVariableMessageLength();
	void setVariableMessageLength( String value );
	void setVariableMessageLength( Boolean value );
	
	// *** DiverseDataSizes ***
	@Type( base = Boolean.class )
	@CustomXmlValueBinding( impl=GenericMultiwordXmlValueBinding.class )
	@Label( standard = "DiverseDataSizes" )
		
	ValueProperty PROP_DIVERSE_DATA_SIZES = new ValueProperty(TYPE, "DiverseDataSizes");
		
	Value<Boolean> getDiverseDataSizes();
	void setDiverseDataSizes( String value );
	void setDiverseDataSizes( Boolean value );
	
	// *** UnBounded ***
	@Type( base = Boolean.class )
	@CustomXmlValueBinding( impl=GenericMultiwordXmlValueBinding.class )
	@Label( standard = "UnBounded" )
		
	ValueProperty PROP_UN_BOUNDED = new ValueProperty(TYPE, "UnBounded");
		
	Value<Boolean> getUnBounded();
	void setUnBounded( String value );
	void setUnBounded( Boolean value );
	
	// *** Operations ***
	@Type( base = Operation.class )
	//@XmlListBinding( mappings = { @XmlListBinding.Mapping( element = "Operation", type = Operation.class ), @XmlListBinding.Mapping( element = "operation", type = OperationLower.class ) } )
	@CustomXmlListBinding(impl = OPSOperationXmlListBinding.class )
	@Label( standard = "Operations" )
		
	ListProperty PROP_OPERATIONS = new ListProperty( TYPE, "Operations" );
	
	ElementList<Operation> getOperations();
}
