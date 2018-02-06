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

package av.proj.ide.parsers.ocs;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class OCSXMLParser extends DefaultHandler {
	private Property prop = null;
	private SAXParserFactory parserFactory;
	private SAXParser parser;
	private ComponentSpec spec;
	private Port port = null;
	private Protocol protocol = null;
	private Argument argument = null;
	private Operation operation = null;
	
	public OCSXMLParser() {
		this.parserFactory = SAXParserFactory.newInstance();
		try {
			this.parser = this.parserFactory.newSAXParser();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		this.spec = new ComponentSpec();
	}
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) {
		 switch (qName.toLowerCase()) {
		 	// Create a new Property
		 	case "property": {
		 		this.prop = new Property();
		 		String Name = attributes.getValue("name");
		 		if (Name != null) {
		 			this.prop.setName(Name);
		 		}
		 		String Type = attributes.getValue("type");
		 		if (Type != null) {
		 			this.prop.setType(Type);
		 		}
		 		String StringLength = attributes.getValue("stringlength");
		 		if (StringLength != null) {
		 			this.prop.setStringLength(StringLength);
		 		}
		 		String Enums = attributes.getValue("enums");
		 		ArrayList<String> enums = new ArrayList<String>();
		 		if (Enums != null) {
		 			// Remove the end [] and split at commas
		 			String[] elements = Enums.substring(1, Enums.length()-1).split(",");
		 			for (String e : elements) {
		 				enums.add(e.trim());
		 			}
		 			this.prop.setEnums(enums);
		 		}
		 		String ArrayLength = attributes.getValue("arraylength");
		 		if (ArrayLength != null) {
		 			this.prop.setArrayLength(ArrayLength);
		 		}
		 		String SequenceLength = attributes.getValue("sequencelength");
		 		if (SequenceLength != null) {
		 			this.prop.setSequenceLength(SequenceLength);
		 		}
		 		String ArrayDimensions = attributes.getValue("arraydimensions");
		 		if (ArrayDimensions != null) {
		 			this.prop.setArrayDimensions(ArrayDimensions);
		 		}
		 		String Default = attributes.getValue("default");
		 		if (Default != null) {
		 			this.prop.setDefault(Default);
		 		}
		 		break; 
		 	}
		 	// Create a new ComponentSpec
		 	case "componentspec": {
		 		String name = attributes.getValue("name");
		 		this.spec.setName(name);
		 		break;
		 	}
		 	// Create a new Port
		 	case "port": {
		 		this.port = new Port();
		 		String name = attributes.getValue("name");
		 		if (name != null) {
		 			this.port.setName(name);
		 		}
		 		String producer = attributes.getValue("producer");
		 		if (producer != null) {
		 			this.port.setProducer(producer);
		 		}
		 		String protocol = attributes.getValue("protocol");
		 		if (protocol != null) {
		 			this.port.setProtocol(protocol);
		 		}
		 		break;
		 	}
		 	case "protocol": {
		 		this.protocol = new Protocol();
		 		break;
		 	}
		 	case "operation": {
		 		this.operation = new Operation();
		 		String name = attributes.getValue("name");
		 		if (name != null) {
		 			this.operation.setName(name);
		 		}
		 		String twoway = attributes.getValue("twoway");
		 		if (name != null) {
		 			this.operation.setTwoway(twoway);
		 		}
		 		break;
		 	}
		 	case "argument": {
		 		this.argument = new Argument();
		 		String Name = attributes.getValue("name");
		 		if (Name != null) {
		 			this.argument.setName(Name);
		 		}
		 		String Type = attributes.getValue("type");
		 		if (Type != null) {
		 			this.argument.setType(Type);
		 		}
		 		String StringLength = attributes.getValue("stringlength");
		 		if (StringLength != null) {
		 			this.argument.setStringLength(StringLength);
		 		}
		 		String Enums = attributes.getValue("enums");
		 		if (Enums != null) {
		 			this.argument.setEnums(Enums);
		 		}
		 		String ArrayLength = attributes.getValue("arraylength");
		 		if (ArrayLength != null) {
		 			this.argument.setArrayLength(ArrayLength);
		 		}
		 		String SequenceLength = attributes.getValue("sequencelength");
		 		if (SequenceLength != null) {
		 			this.argument.setSequenceLength(SequenceLength);
		 		}
		 		String ArrayDimensions = attributes.getValue("arraydimensions");
		 		if (ArrayDimensions != null) {
		 			this.argument.setArrayDimensions(ArrayDimensions);
		 		}
		 		String Default = attributes.getValue("default");
		 		if (Default != null) {
		 			this.argument.setDefault(Default);
		 		}
		 		break; 
		 	}
		 }
	}
	
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		switch(qName.toLowerCase()) {
		case "property":
			if (this.prop != null) {
				this.spec.addProperty(prop);
				break;
			}
		case "port":
			if (this.port != null) {
				this.spec.addPort(this.port);
			}
		case "protocol":
			if (this.protocol != null) {
				this.port.addProtocol(this.protocol);
			}
		case "operation":
			if (this.operation != null) {
				this.protocol.addOperation(this.operation);
			}
		case "argument":
			if (this.argument != null) {
				this.operation.addArgument(this.argument);
			}
		}	
	}
	
	public void parse(InputStream input) {
		try {
			this.spec.clearFields();
			//this.parser.parse(input, this);
			this.parser.parse(input, this);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public ComponentSpec getComponentSpec() {
		return this.spec;
	}
}