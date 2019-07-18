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

package av.proj.ide.owd.hdl;

import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.modeling.xml.StandardXmlElementBindingImpl;

/***
 * The was developed to support tag attributes.  It might work for tags as well.
 * Since the XML files used by open cpi is not standardized xml binding need to deal
 * with multiple styles of case. This class is intended to work with single syllable
 * words.  There is another class that deals with multiple syllable words.
  */
public class CaseInsensitiveElementBinding extends  StandardXmlElementBindingImpl {

	
//	@Override
//	protected void initNames() {
//        this.name = propertyName;
//        this.lowerName = this.name.toLowerCase();
//        char c[] = this.name.toCharArray();
//        c[1] = Character.toLowerCase(c[0]);
//        // If it is a multiple word camel cased this will take a shot at it.
//        this.camelName = new String(c);
//		
//	}
	
    @Override
    protected Object readUnderlyingObject()
    {
        //final XmlElement parent = parent( false );
        Object readit = super.readUnderlyingObject();
        return readit;
    }
    
    @Override
    protected Object createUnderlyingObject( final ElementType type )
    {
        //final XmlElement parent = parent( false );
        Object writeit = super.createUnderlyingObject(type);
        return writeit;
    }
    
}
