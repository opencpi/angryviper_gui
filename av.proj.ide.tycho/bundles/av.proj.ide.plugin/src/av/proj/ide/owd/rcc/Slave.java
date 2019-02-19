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

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlValueBinding;

import av.proj.ide.custom.bindings.value.CaseInsenitiveAttributeValueBinding;

/***
 * This Slave interface has two properties defined for adding additional
 * slave(s) from the RCC OWD Editor.
 **/
public interface Slave extends Element{
	
    ElementType TYPE = new ElementType(Slave.class);
   
    //*** Worker ***
    @CustomXmlValueBinding(impl = CaseInsenitiveAttributeValueBinding.class)
    @Label(standard = "Worker")
    
    ValueProperty PROP_WORKER = new ValueProperty(TYPE, "Worker");
    
    Value<String> getWorker();
    void setWorker(String value);
    
    //*** Name ***
    @CustomXmlValueBinding(impl = CaseInsenitiveAttributeValueBinding.class)
    @Label(standard = "Name (optional)")
    
    ValueProperty PROP_NAME = new ValueProperty(TYPE, "Name");
    
    Value<String> getName();
    void setName(String value);
    
}
