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

package av.proj.ide.testeditor;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Enablement;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Whitespace;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlValueBinding;

import av.proj.ide.custom.bindings.value.CaseInsenitiveAttributeValueBinding;

public interface CoreTestAttributes extends Element
{
	ElementType TYPE = new ElementType(CoreTestAttributes.class);

	@CustomXmlValueBinding(impl = CaseInsenitiveAttributeValueBinding.class)
	@Label(standard = "Time Out")
    @Enablement( expr = "${ Duration  == null }" )
	
	ValueProperty PROP_TIMEOUT = new ValueProperty(TYPE, "Timeout");
	
	Value<String> getTimeOut();
	void setTimeOut(String value);
	
	// Messages In File
	@CustomXmlValueBinding(impl = CaseInsenitiveAttributeValueBinding.class)
	@Label(standard = "Duration")
    @Enablement( expr = "${ Timeout  == null }" )
	
	ValueProperty PROP_DURATION = new ValueProperty(TYPE, "Duration");
	
	Value<String> getDuration();
	void setDuration(String value);

	// ***  ***
	@CustomXmlValueBinding(impl = CaseInsenitiveAttributeValueBinding.class)
	@Label(standard = "Only Workers")
	@Whitespace( trim = true, collapse = true )
    @Enablement( expr = "${ ExcludeWorkers  == null }" )
	
	ValueProperty PROP_ONLY_WORKERS = new ValueProperty(TYPE, "OnlyWorkers");

	Value<String> getOnlyWorkers();
	void setOnlyWorkers(String value);

	@CustomXmlValueBinding(impl = CaseInsenitiveAttributeValueBinding.class)
	@Label(standard = "Exclude Workers")
	@Whitespace( trim = true, collapse = true )
	@Enablement( expr = "${ OnlyWorkers  == null }" )
	
	ValueProperty PROP_EXCLUDE_WORKERS = new ValueProperty(TYPE, "ExcludeWorkers");

	Value<String> getExcludeWorkers();
	void setExcludeWorkers(String value);

	@CustomXmlValueBinding(impl = CaseInsenitiveAttributeValueBinding.class)
	@Label(standard = "Only Platforms")
	@Whitespace( trim = true, collapse = true )
	@Enablement( expr = "${ ExcludePlatforms  == null }" )
	
	ValueProperty PROP_ONLY_PLATFORMS = new ValueProperty(TYPE, "OnlyPlatforms");

	Value<String> getOnlyPlatforms();
	void setOnlyPlatforms(String value);

	@CustomXmlValueBinding(impl = CaseInsenitiveAttributeValueBinding.class)
	@Label(standard = "Exclude Platforms")
	@Whitespace( trim = true, collapse = true )
	@Enablement( expr = "${ OnlyPlatforms  == null }" )
	
	ValueProperty PROP_EXCLUDE_PLATFORMS = new ValueProperty(TYPE, "ExcludePlatforms");

	Value<String> getExcludePlatforms();
	void setExcludePlatforms(String value);

	
}