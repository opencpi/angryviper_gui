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

import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.modeling.xml.StandardXmlValueBindingImpl;
import org.eclipse.sapphire.modeling.xml.XmlElement;
import org.eclipse.sapphire.modeling.xml.XmlPath;

public class NodePresentValueBinding extends StandardXmlValueBindingImpl {
	
	protected String name = "";
	protected String lowerName = "";
	
	@Override
	protected void initBindingMetadata()
    {
		super.initBindingMetadata();
        final Value<?> property = (Value<?>) property();
        this.name = property.name();
        this.lowerName = this.name.toLowerCase();
        this.treatExistanceAsValue = true;
        this.valueWhenPresent = "true";
        this.valueWhenNotPresent = "false";
    }
	
	@Override
    public String read()
    {
		String value = null;
        final XmlElement element = xml( false );
        this.path = new XmlPath(this.lowerName , resource().getXmlNamespaceResolver());
        if( element != null )
        {
            if( this.treatExistanceAsValue )
            {
                final boolean exists = ( element.getChildNode( this.path, false ) != null );
                value = ( exists ? this.valueWhenPresent : this.valueWhenNotPresent );
            }
            else if( this.path == null )
            {
                value = element.getText();
            }
        }
        return value;
    }

    @Override
    public void write( final String value )
    {
    }
}
