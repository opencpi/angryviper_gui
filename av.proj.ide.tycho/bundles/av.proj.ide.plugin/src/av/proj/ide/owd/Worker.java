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

package av.proj.ide.owd;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Validation;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.modeling.annotations.Enablement;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlListBinding;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlValueBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlNamespace;

import av.proj.ide.custom.bindings.list.MultiCaseXmlListBinding;
import av.proj.ide.custom.bindings.list.OWDPropertyXmlListBinding;
import av.proj.ide.custom.bindings.value.CaseInsenitiveAttributeValueBinding;
import av.proj.ide.ops.Include;
import av.proj.ide.services.WorkerSpecValidationService;

@XmlNamespace( uri = "http://www.w3.org/2001/XInclude", prefix = "xi" )

public interface Worker extends Element {
	ElementType TYPE = new ElementType( Worker.class );
	
	// *** Name ***
	/***
	 * Used cases: 
	 *           1) used in Advanced Attributes of c++ language worker slave attribute text box entry
	 *           2) used in Advanced Attributes of vhdl language worker slave attribute text box entry
	 */
	@CustomXmlValueBinding( impl = CaseInsenitiveAttributeValueBinding.class )
	@Label(standard = "Name")
	@Validation(rule     = "${Name == null || Name.Size > 2 }",
			    message  = "Must match the name of a worker in the same library",
			    severity = Status.Severity.WARNING)
	ValueProperty PROP_NAME = new ValueProperty(TYPE, "Name");

	Value<String> getName();
	void setName(String value);
	
	/***
	 * Beginning Of OWD Top Level Attributes 
	 **/
	
	// *** Spec ***
	@CustomXmlValueBinding( impl = CaseInsenitiveAttributeValueBinding.class )
	@Label(standard = "Spec")
	@Service(impl=WorkerSpecValidationService.class)
	
	ValueProperty PROP_SPEC = new ValueProperty(TYPE, "Spec");

	Value<String> getSpec();
	void setSpec(String value);
	
	// *** Language ***
	@CustomXmlValueBinding( impl = CaseInsenitiveAttributeValueBinding.class )
	@Label(standard = "Language")
	@Enablement(expr = "false")
	ValueProperty PROP_LANGUAGE = new ValueProperty(TYPE, "Language");

	Value<String> getLanguage();
	void setLanguage(String value);

	//*** Only ***
	@CustomXmlValueBinding( impl = CaseInsenitiveAttributeValueBinding.class )
	@Label(standard = "Only")
		
	ValueProperty PROP_ONLY = new ValueProperty(TYPE, "Only");

	Value<String> getOnly();
	void setOnly(String value);
	
	//***  OnlyPlatforms ***
	@CustomXmlValueBinding( impl = CaseInsenitiveAttributeValueBinding.class )
	@Label(standard = "OnlyPlatforms")
			
	ValueProperty PROP_ONLY_PLATFORMS = new ValueProperty(TYPE, "OnlyPlatforms");

	Value<String> getOnlyPlatforms();
	void setOnlyPlatforms(String value);
	
	//*** OnlyTargets ***
	@CustomXmlValueBinding( impl = CaseInsenitiveAttributeValueBinding.class )
	@Label(standard = "OnlyTargets")
		
	ValueProperty PROP_ONLY_TARGETS = new ValueProperty(TYPE, "OnlyTargets");

	Value<String> getOnlyTargets();
	void setOnlyTargets(String value);
	
	//*** Exclude ***
	@CustomXmlValueBinding( impl = CaseInsenitiveAttributeValueBinding.class )
	@Label(standard = "Exclude")
		
	ValueProperty PROP_EXCLUDE = new ValueProperty(TYPE, "Exclude");

	Value<String> getExclude();
	void setExclude(String value);
	
	//*** ExcludePlatforms ***
    @CustomXmlValueBinding( impl = CaseInsenitiveAttributeValueBinding.class )
	@Label(standard = "ExcludePlatforms")
		
	ValueProperty PROP_EXCLUDE_PLATFORMS = new ValueProperty(TYPE, "ExcludePlatforms");

	Value<String> getExcludePlatforms();
	void setExcludePlatforms(String value);
	
	//*** ExcludeTargets ***
	@CustomXmlValueBinding( impl = CaseInsenitiveAttributeValueBinding.class )
	@Label(standard = "ExcludeTargets")
		
	ValueProperty PROP_EXCLUDE_TARGETS = new ValueProperty(TYPE, "ExcludeTargets");

	Value<String> getExcludeTargets();
	void setExcludeTargets(String value);
	
	//*** Libraries ***
	@CustomXmlValueBinding( impl = CaseInsenitiveAttributeValueBinding.class )
	@Label(standard = "Libraries")
		
	ValueProperty PROP_LIBRARIES = new ValueProperty(TYPE, "Libraries");

	Value<String> getLibraries();
	void setLibraries(String value);
	
	//*** SourceFiles ***
	@CustomXmlValueBinding( impl = CaseInsenitiveAttributeValueBinding.class )
	@Label(standard = "SourceFiles")
		
	ValueProperty PROP_SOURCE_FILES = new ValueProperty(TYPE, "SourceFiles");

	Value<String> getSourceFiles();
	void setSourceFiles(String value);
	
	/***
	 * END of OWD Top Level Attributes 
	 **/
	
	// *** Endian ***
	@Type( base = Endian.class)
	@CustomXmlValueBinding( impl = CaseInsenitiveAttributeValueBinding.class )
	@Label(standard = "Endian")
		
	ValueProperty PROP_ENDIAN = new ValueProperty( TYPE, "Endian" );
	   
	Value<Endian> getEndian();
	void setEndian( String value );
	void setEndian( Endian value ); 
	
	@Type( base = Include.class )
    @XmlListBinding( mappings = @XmlListBinding.Mapping(element = "xi:include", type = Include.class ) )
	@Label( standard = "Property File Includes" )
	
	ListProperty PROP_INCLUDES = new ListProperty( TYPE, "Includes" );
	ElementList<Include> geIncludes();

	// *** Properties ***
	@Type( base = Property.class )
	//@XmlListBinding( mappings = { @XmlListBinding.Mapping( element = "Property", type = Property.class ), @XmlListBinding.Mapping( element = "property", type = PropertyLower.class ) } )
	@CustomXmlListBinding(impl = OWDPropertyXmlListBinding.class)
	@Label( standard = "Properties" )
		
	ListProperty PROP_PROPERTIES = new ListProperty( TYPE, "Properties" );
		
	ElementList<Property> getProperties();
	
	// *** SpecProperties ***
	@Type( base = SpecProperty.class )
	//@XmlListBinding( mappings = { @XmlListBinding.Mapping( element = "SpecProperty", type = SpecProperty.class ), @XmlListBinding.Mapping( element = "specproperty", type = SpecPropertyLower.class ) } )
	@CustomXmlListBinding(impl = MultiCaseXmlListBinding.class)
	@Label( standard = "SpecProperties" )
			
	ListProperty PROP_SPEC_PROPERTIES = new ListProperty( TYPE, "SpecProperties" );
			
	ElementList<av.proj.ide.hplat.SpecProperty> getSpecProperties();
}
