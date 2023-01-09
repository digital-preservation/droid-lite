package uk.gov.nationalarchives.droidlet.core.xml;

import org.xml.sax.Attributes;

import uk.gov.nationalarchives.droidlet.core.exception.UnexpectedXmlStructureException;

/**
 * Holds the details an XML element loaded from the PRONOM file
 */
public abstract class SimpleElement
{
	public static abstract class SimpleElementBuilder
	{
		private SimpleElementBuilder currentChildBuilder = null;
		private final String qName;
		private final Attributes attributes;

		protected SimpleElementBuilder(String qName, Attributes attributes)
		{
			this.qName = qName;
			this.attributes = attributes;
		}

		public void startElement(String qName, Attributes attributes)
		{
			// If there is a current child then we just pass the message down to the child
			if (currentChildBuilder != null)
			{
				currentChildBuilder.startElement(qName, attributes);
				return;
			}

			System.out.println("<" + qName + ">" + attributes.getValue("ID"));

			// There is no child so we give subclasses a chance to handle it
			currentChildBuilder = startChildElementSpecific(qName, attributes);
		}

		public void endElement(String qName)
		{
			if (currentChildBuilder == null)
				return;
			if (currentChildBuilder.qName.equals(qName))
				currentChildBuilder = null;
			else
				currentChildBuilder.endElement(qName);
		}

		public void addText(String string)
		{
			// If there is a current child then we just pass the message down to the child
			if (currentChildBuilder != null)
			{
				currentChildBuilder.addText(string);
				return;
			}

			// There is no child so we need to handle this
			addTextSpecific(string);
		}

		protected SimpleElementBuilder startChildElementSpecific(String qName, Attributes attributes)
		{
			throw new UnexpectedXmlStructureException("Unhandled child tag " + this.qName + "->" + qName);
		}

		protected void addTextSpecific(String string)
		{
			// Do nothing
		}

		protected int getIntAttributeValue(String attributeName)
		{
			final String stringValue = attributes.getValue(attributeName);
			if (stringValue == null)
				throw new UnexpectedXmlStructureException("Missing integer attribute " + attributeName);
			try
			{
				return Integer.parseInt(stringValue);
			}
			catch (final NumberFormatException exception)
			{
				throw new UnexpectedXmlStructureException("Error parsing integer value for " + stringValue);
			}
		}
	}
}