import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class Parser extends DefaultHandler {
	private StringBuilder fileContents;
	private String fileContentsStr;
	private boolean flag;
	
	public Parser() {
		fileContents = new StringBuilder();
		fileContentsStr = "";
	}
	
	public void startDocument() throws SAXException {
	}
	
	public void endDocument() throws SAXException {
		fileContentsStr = fileContents.toString();
	}
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		flag = true;
	}
	
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		flag = false;
	}
	
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		if(flag) {
			String str = new String(ch, start, length);
			if(str!= null && !str.trim().isEmpty()) {
				fileContents.append(str);
			}
		}
	}
	
	public String parseXml(File file) {
		
		try {
			SAXParserFactory spf = SAXParserFactory.newInstance();
			spf.setNamespaceAware(true);
			SAXParser saxParser = spf.newSAXParser();
			XMLReader xmlReader = saxParser.getXMLReader();
			xmlReader.setContentHandler(this);
			xmlReader.parse(file.getAbsolutePath());
			
		} catch(ParserConfigurationException pce) {
			System.err.println("XML file is invalid...");
			pce.printStackTrace();
			return null;
		} catch(SAXException se) {
			System.err.println("XML file is invalid...");
			se.printStackTrace();
			return null;
		} catch(IOException ioe) {
			System.err.println("XML file is invalid....");
			ioe.printStackTrace();
			return null;
		}
		
		return fileContentsStr;
	}

}
