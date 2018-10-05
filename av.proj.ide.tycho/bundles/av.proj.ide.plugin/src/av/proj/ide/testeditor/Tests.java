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
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Enablement;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.annotations.Whitespace;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlListBinding;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlRootBinding;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlValueBinding;

import av.proj.ide.custom.bindings.list.SimpleDualCaseXmlListBinding;
import av.proj.ide.custom.bindings.root.GenericMultiCaseRootBinding;
import av.proj.ide.custom.bindings.value.GenericDualCaseXmlValueBinding;
import av.proj.ide.custom.bindings.value.GenericMultiwordXmlValueBinding;
import av.proj.ide.services.NameValidationService;

@CustomXmlRootBinding( value = GenericMultiCaseRootBinding.class )
public interface Tests extends Element
{
	ElementType TYPE = new ElementType(Tests.class);

	// *** spec attribute***
	@CustomXmlValueBinding(impl = GenericDualCaseXmlValueBinding.class)
	@Label(standard = "Component Spec")
	@Service(impl=NameValidationService.class)
	ValueProperty PROP_SPEC = new ValueProperty(TYPE, "Spec");

	Value<String> getSpec();
	void setSpec(String value);

	
	@Type( base = Boolean.class )
	@CustomXmlValueBinding(impl=GenericMultiwordXmlValueBinding.class)
	@Label( standard = "HDL File IO" )

	ValueProperty PROP_USE_HDL_FILE_IO = new ValueProperty(TYPE, "UseHDLFileIo");
	Value<Boolean> getUseHDLFileIo();
	void setUseHDLFileIo(String value);
	void setUseHDLFileIo(Boolean value);
	
	
	@CustomXmlValueBinding(impl = GenericDualCaseXmlValueBinding.class)
	@Label(standard = "Time Out")
    @Enablement( expr = "${ Duration  == null }" )
	
	ValueProperty PROP_TIMEOUT = new ValueProperty(TYPE, "Timeout");
	
	Value<String> getTimeOut();
	void setTimeOut(String value);
	
	// Messages In File
	@CustomXmlValueBinding(impl = GenericDualCaseXmlValueBinding.class)
	@Label(standard = "Duration")
    @Enablement( expr = "${ Timeout  == null }" )
	
	ValueProperty PROP_DURATION = new ValueProperty(TYPE, "Duration");
	
	Value<String> getDuration();
	void setDuration(String value);

	// ***  ***
	@CustomXmlValueBinding(impl = GenericMultiwordXmlValueBinding.class)
	@Label(standard = "Only Workers")
	@Whitespace( trim = true, collapse = true )
	
	ValueProperty PROP_ONLY_WORKERS = new ValueProperty(TYPE, "OnlyWorkers");

	Value<String> getOnlyWorkers();
	void setOnlyWorkers(String value);

	@CustomXmlValueBinding(impl = GenericMultiwordXmlValueBinding.class)
	@Label(standard = "Exclude Workers")
	@Whitespace( trim = true, collapse = true )
	
	ValueProperty PROP_EXCLUDE_WORKERS = new ValueProperty(TYPE, "ExcludeWorkers");

	Value<String> getExcludeWorkers();
	void setExcludeWorkers(String value);

	@CustomXmlValueBinding(impl = GenericMultiwordXmlValueBinding.class)
	@Label(standard = "Only Platforms")
	@Whitespace( trim = true, collapse = true )
	
	ValueProperty PROP_ONLY_PLATFORMS = new ValueProperty(TYPE, "OnlyPlatforms");

	Value<String> getOnlyPlatforms();
	void setOnlyPlatforms(String value);

	@CustomXmlValueBinding(impl = GenericMultiwordXmlValueBinding.class)
	@Label(standard = "Exclude Platforms")
	@Whitespace( trim = true, collapse = true )
	
	ValueProperty PROP_EXCLUDE_PLATFORMS = new ValueProperty(TYPE, "ExcludePlatforms");

	Value<String> getExcludePlatforms();
	void setExcludePlatforms(String value);
	
	//  Elements
	

	// *** Test Cases ***
	@Type( base = Case.class )
	@CustomXmlListBinding(impl = SimpleDualCaseXmlListBinding.class)
			
	ListProperty PROP_CASES = new ListProperty( TYPE, "Cases" );
			
	ElementList<Case> getCases();

	
  	// *** Test Properties ***
	@Type( base = Property.class )
	@CustomXmlListBinding(impl = SimpleDualCaseXmlListBinding.class)
	@Label( standard = "Test Properties" )


	ListProperty PROP_PROPERTIES = new ListProperty( TYPE, "Properties" );
			
	ElementList<Property> getProperties();


	// *** Test Inputs ***
	@Type( base = Input.class )
	@CustomXmlListBinding(impl = SimpleDualCaseXmlListBinding.class)
	@Label( standard = "Test Inputs" )
			
	ListProperty PROP_INPUTS = new ListProperty( TYPE, "Inputs" );
			
	ElementList<Input> getInputs();


	// *** Test Outputs ***
	@Type( base = Output.class )
	@CustomXmlListBinding(impl = SimpleDualCaseXmlListBinding.class)
	@Label( standard = "Test Outputs" )
			
	ListProperty PROP_OUTPUTS = new ListProperty( TYPE, "Outputs" );
			
	ElementList<Input> getOutputs();

	
}