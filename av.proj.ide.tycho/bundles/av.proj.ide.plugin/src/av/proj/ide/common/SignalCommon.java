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

package av.proj.ide.common;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.Derived;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/***
 * Signals as specified make for difficult XML.  Instead of giving a signal a
 * direction and a name, an attribute names the direction.  The current UI impl
 * uses the direction as a control element to set up the rest of the form.
 */
public interface SignalCommon extends Element {
	ElementType TYPE = new ElementType(SignalCommon.class);

	// *** compliant signal width attribute***
	// This is the only common attribute for a signal
	@XmlBinding(path = "@width")
	@Label(standard = "width")
	ValueProperty PROP_WIDTH = new ValueProperty(TYPE, "Width");
	
	Value<String> getWidth();
	void setWidth(String value);
	
	// *** Non Compliant name attribute derived ***
	@Type( base = String.class )
	@Label(standard = "name")
	@Derived(text="${ input != null ? input : (output != null ? output : (bidirectional != null ? bidirectional : ( inout != null ? inout : '<signal>' )))}")
	ValueProperty PROP_NAME = new ValueProperty(TYPE, "Name");
	Value<String> getName();

	// *** Non Compliant direction attribute ***
	// Provides more of a control element for the editor.
	// Since Sapphire property editors are tightly tied to a 
	// property this becomes a signal attribute.
	
    @Type( base = SignalDirection.class )
    @Label( standard = "Signal Direction" )
    @DefaultValue( text = "NOTSET" )
	@XmlBinding(path = "extension")
    
    ValueProperty PROP_DIRECTION = new ValueProperty( TYPE, "Direction" );
    
    Value<SignalDirection> getDirection();
    void setDirection( String value );
    void setDirection( SignalDirection value );
	
}
