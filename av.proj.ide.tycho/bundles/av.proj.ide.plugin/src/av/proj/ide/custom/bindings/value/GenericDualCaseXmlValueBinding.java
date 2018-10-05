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

package av.proj.ide.custom.bindings.value;

import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.modeling.xml.StandardXmlValueBindingImpl;
import org.eclipse.sapphire.modeling.xml.XmlElement;
import org.eclipse.sapphire.modeling.xml.XmlPath;

/***
 * The was developed to support tag attributes.  It might work for tags as well.
 * Since the XML files used by open cpi is not standardized xml binding need to deal
 * with multiple styles of case. This class is intended to work with single syllable
 * words.  There is another class that deals with multiple syllable words.
  */
public class GenericDualCaseXmlValueBinding extends StandardXmlValueBindingImpl {
	protected String name = "";
	protected String lowerName = "";
	
	@Override
	protected void initBindingMetadata()
    {
		this.removeNodeOnSetIfNull=true;
		super.initBindingMetadata();
        final Value<?> property = (Value<?>) property();
        this.name = "@"+property.name();
        this.lowerName = this.name.toLowerCase();
    }
	
    @Override
    public String read()
    {
        //String value = super.read();
    	// Note - doing the initial parent read caused state problems
    	// and this class was not updating instance properties in larger
    	// application graphs.
    	
        String value = null;
        final XmlElement element = xml( false );
        if (element == null)
        	return null;

        if(this.path != null) {
            value = element.getChildNodeText( this.path );
        }
        if(value == null || value.isEmpty()) {
            this.path = new XmlPath(this.name , resource().getXmlNamespaceResolver());
            value = element.getChildNodeText( this.path );
        }
        if(value == null || value.isEmpty()) {
            this.path = new XmlPath(this.lowerName , resource().getXmlNamespaceResolver());
            value = element.getChildNodeText( this.path );
        }
        if(value == null || value.isEmpty()) {
        	// This attribute doesn't exist yet.  If it is added let write define
        	// default attribute case (done with path);
            this.path = null;
        }
        return value;
    }

    @Override
    public void write( final String value )
    {
    	if(this.path == null) {
    		// If this is a new instance of this attribute, default it to property name.
    		this.path = new XmlPath(this.name , resource().getXmlNamespaceResolver());
    	}
    	super.write(value);
    }
}
