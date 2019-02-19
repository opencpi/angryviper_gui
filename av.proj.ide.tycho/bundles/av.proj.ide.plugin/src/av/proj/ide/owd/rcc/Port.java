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

package av.proj.ide.owd.rcc;

import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Validation;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlValueBinding;

import av.proj.ide.custom.bindings.value.CaseInsenitiveAttributeValueBinding;
import av.proj.ide.ops.ProtocolSummary;

public interface Port extends ProtocolSummary {
	ElementType TYPE = new ElementType( Port.class );

	/***
	 * Current implementation is just to give the warning before the user
	 * provide any input.  Once we have the service to get the name(s) of the 
	 * worker(s) in a given library then this part should develop to use that service.
	 **/
	
	// *** Name ***(required for name attribute of Protocol in OPS Editor)
	@Validation( rule = "${Name.Size > 0}", 
	             message = "Must match the name attribute of a Port or DataInterfaceSpec element of the ComponentSpec",
			    severity = Status.Severity.WARNING)
	@CustomXmlValueBinding( impl = CaseInsenitiveAttributeValueBinding.class ) 
	@Label( standard = "Name")
	
	ValueProperty PROP_NAME = new ValueProperty( TYPE, "Name");
	
	Value<String> getName();
	void setName( String value );
	
	
	
	// *** MinBufferCount *** 
	@CustomXmlValueBinding( impl=CaseInsenitiveAttributeValueBinding.class )
	@Label(standard = "MinBufferCount")	
	@Validation(   rule = "${ MinBufferCount == null || MinBufferCount > 0 }",
		        message = "MinBufferCount must be a positive integer",
		       severity = Status.Severity.WARNING)
	
	ValueProperty PROP_MIN_BUFFER_COUNT = new ValueProperty(TYPE, "MinBufferCount");

	Value<String> getMinBufferCount();
	void setMinBufferCount(String value);
	
}
