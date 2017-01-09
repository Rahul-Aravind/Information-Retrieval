import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class XMLContentParser extends DefaultHandler {
	private String titleName;
	private boolean flag;
	private File file;
	
	public XMLContentParser(File file) {
		this.titleName = "";
		this.flag = false;
		this.file = file;
	}
	
	public void startDocument() throws SAXException {
	}
	
	public void endDocument() throws SAXException {
	}
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if(qName.equalsIgnoreCase("TITLE"))
			flag = true;
	}
	
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
	}
	
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		if(flag) {
			titleName = new String(ch, start, length);
			flag = false;
		}
	}
	
	public String getTitleName() {
		
		try {
			SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
			saxParserFactory.setNamespaceAware(true);
			SAXParser saxParser = saxParserFactory.newSAXParser();
			XMLReader xmlReader = saxParser.getXMLReader();
			xmlReader.setContentHandler((ContentHandler) this);
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
		
		return titleName;
	}

}
