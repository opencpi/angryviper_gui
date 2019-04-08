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

package av.proj.ide.services;

import java.util.List;

import javax.xml.namespace.QName;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.modeling.xml.XmlElement;
import org.eclipse.sapphire.modeling.xml.XmlResource;
import org.eclipse.sapphire.services.ValidationService;

public class WorkerSpecValidationService extends ValidationService {
	
	private static String warning = 
"This file uses a deprecated method of referencing the worker spec with xi:include. It is now done by assigning this attribute.";

	@Override
	protected void initValidationService()
    {
		
    }
	
	@Override
	protected Status compute() {
		
		Status stat = Status.createErrorStatus("This OWD does not reference an OCS spec.");
		
		final Value<?> value = context(Element.class).property(context(ValueProperty.class));
		Object obj = value.content();
		if (obj != null) {
			final String name = obj.toString();
			if (name != null) {
				
				if (! name.toLowerCase().endsWith("spec")) {
					stat = Status.createErrorStatus("This spec reference does not appear valid.");
				}
				else {
					stat = Status.createOkStatus();
				}
			}
			return stat;
		}
		else {
			final Element parent = context(Element.class);
			XmlElement parentElement = ( (XmlResource) parent.resource() ).getXmlElement();
			// Look for xi:include for a spec.
			QName inc = new QName("http://www.w3.org/2001/XInclude", "include");
			List<XmlElement> elements = parentElement.getChildElements(inc);
			for(XmlElement element : elements) {
				QName name = element.getQualifiedName();
				String lp = name.getLocalPart();
				if("include".equals(lp)) {
					String ref = element.getAttributeText("href");
					if(ref != null && ref.toLowerCase().endsWith("spec.xml")) {
						return Status.createWarningStatus(warning);
					}
				}
			}
			QName compSpec = new QName("componentspec");
			elements = parentElement.getChildElements(compSpec);
			if(elements.size() == 0) {
				compSpec = new QName("ComponentSpec");
				elements = parentElement.getChildElements(compSpec);
			}
			if(elements.size() == 0) {
				elements = parentElement.getChildElements();
				for(XmlElement element : elements) {
					QName name = element.getQualifiedName();
					String lp = name.getLocalPart().toLowerCase();
					if("componentspec".equals(lp))
						return Status.createOkStatus();
				}
			}
			else {
				return Status.createOkStatus();
			}
			
			return stat;
		}
	}
}
