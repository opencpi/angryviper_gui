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

import org.eclipse.sapphire.modeling.annotations.EnumSerialization;
import org.eclipse.sapphire.modeling.annotations.Label;

@Label( standard = "Type" )

public enum MemberPropertyType {

	@Label( standard = "char" )
    @EnumSerialization( primary = "char" )

    TYPE_CHAR,
    
    @Label( standard = "uChar" )
    @EnumSerialization( primary = "uChar" )
    
    TYPE_UCHAR,
    
    @Label( standard = "short" )
    @EnumSerialization( primary = "short" )
    
    TYPE_SHORT,
    
    @Label( standard = "uShort" )
    @EnumSerialization( primary = "uShort" )
    
    TYPE_USHORT,
    
    @Label( standard = "long" )
    @EnumSerialization( primary = "long" )
    
    TYPE_LONG,
    
    @Label( standard = "uLong" )
    @EnumSerialization( primary = "uLong" )
    
    TYPE_ULONG,
    
    @Label( standard = "longLong" )
    @EnumSerialization( primary = "longLong" )
    
    TYPE_LONGLONG,
    
    @Label( standard = "uLongLong" )
    @EnumSerialization( primary = "uLongLong" )
    
    TYPE_ULONGLONG,
    
    @Label( standard = "float" )
    @EnumSerialization( primary = "float" )
    
    TYPE_FLOAT,
    
    @Label( standard = "double" )
    @EnumSerialization( primary = "double" )
    
    TYPE_DOUBLE,
    
    @Label( standard = "bool" )
    @EnumSerialization( primary = "bool" )
    
    TYPE_BOOLEAN,
    
    @Label( standard = "string" )
    @EnumSerialization( primary = "string" )
    
    TYPE_STRING,
    
    @Label( standard = "struct" )
    @EnumSerialization( primary = "struct" )
    
    TYPE_STRUCT,
    
    @Label( standard = "enum" )
    @EnumSerialization( primary = "enum" )
    
    TYPE_ENUM
    
}
