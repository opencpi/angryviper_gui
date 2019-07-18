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

package av.proj.ide.hplat;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Enablement;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.ReadOnly;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlValueBinding;

import av.proj.ide.custom.bindings.value.CaseInsenitiveAttributeValueBinding;

public interface SDP extends Element {
	ElementType TYPE = new ElementType(SDP.class);

	// *** Master ***
	@Type(base = Boolean.class)
	@ReadOnly
	@CustomXmlValueBinding(impl = CaseInsenitiveAttributeValueBinding.class)
	@Enablement( expr="false")
	@Label(standard = "Master")

	ValueProperty PROP_MASTER = new ValueProperty(TYPE, "Master");
	Value<Boolean> getMaster();
	
	// Name
	@CustomXmlValueBinding(impl = CaseInsenitiveAttributeValueBinding.class)
	@Label(standard = "name")
	//@Enablement( expr="false")

	ValueProperty PROP_NAME = new ValueProperty(TYPE, "Name");
	
	Value<String> getName();
	void setName(String value);

	
	// Count (optional)
	@Type(base = Integer.class)
	@CustomXmlValueBinding(impl = CaseInsenitiveAttributeValueBinding.class)
	@Label(standard = "count")
	
	ValueProperty PROP_COUNT = new ValueProperty(TYPE, "Count");
	
	Value<Integer> getCount();
	void setCount(String value);
	void setCount(Integer value);
}
