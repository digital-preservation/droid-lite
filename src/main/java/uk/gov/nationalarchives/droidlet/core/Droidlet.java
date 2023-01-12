package uk.gov.nationalarchives.droidlet.core;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import uk.gov.nationalarchives.droidlet.core.xml.FFSignatureFile;
import uk.gov.nationalarchives.droidlet.core.xml.FFSignatureFile.FFSignatureFileBuilder;
import uk.gov.nationalarchives.droidlet.core.xml.SimpleElement.SimpleElementBuilder;

public class Droidlet
{
	private FFSignatureFile ffSignatureFile;

	void loadPronomFileFormatSpecifications(InputStream xmlInputStream) throws SAXException, IOException, ParserConfigurationException
	{
		class SaxHandler extends DefaultHandler
		{
			private FFSignatureFileBuilder rootBuilder;

			@Override
			public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
			{
				// This is a special case - the opening of the top level tag
				if (rootBuilder == null)
				{
					rootBuilder = new FFSignatureFileBuilder(attributes);
					return;
				}

				rootBuilder.startElement(qName, attributes);
			}

			@Override
			public void endElement(String uri, String localName, String qName) throws SAXException
			{
				rootBuilder.endElement(qName);
			}

			@Override
			public void characters(char[] characters, int start, int length) throws SAXException
			{
				final StringBuilder stringBuilder = new StringBuilder();
				stringBuilder.append(characters, start, length);
				rootBuilder.addText(stringBuilder.toString());
			}
		}

		// Trigger the parse phase
		final SaxHandler saxHandler = new SaxHandler();
		SAXParserFactory.newInstance().newSAXParser().parse(xmlInputStream, saxHandler);

		// Trigger the build phase
		ffSignatureFile = saxHandler.rootBuilder.build();

		xmlInputStream.close();

		SimpleElementBuilder.outputAttributes();
	}
}