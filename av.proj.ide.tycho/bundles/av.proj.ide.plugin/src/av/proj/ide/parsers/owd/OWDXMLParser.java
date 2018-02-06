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

package av.proj.ide.parsers.owd;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class OWDXMLParser extends DefaultHandler {
	private SAXParserFactory parserFactory;
	private SAXParser parser;
	private Worker worker;
	
	
	public OWDXMLParser() {
		this.parserFactory = SAXParserFactory.newInstance();
		try {
			this.parser = this.parserFactory.newSAXParser();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		this.worker = new Worker();
	}
	
	public void parse(InputStream input) {
		try {
			this.worker.clearFields();
			//this.parser.parse(input, this);
			this.parser.parse(input, this);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) {
		 switch (qName.toLowerCase()) {
		 case "RccWorker":
			 String rccname = attributes.getValue("name");
			 this.worker.setName(rccname);
			 break;
		 case "HdlWorker":
			 String hdlname = attributes.getValue("name");
			 this.worker.setName(hdlname);
			 break;
		 default:
			 break;
		 }
	}
	
	public Worker getWorker() {
		return this.worker;
	}
}
