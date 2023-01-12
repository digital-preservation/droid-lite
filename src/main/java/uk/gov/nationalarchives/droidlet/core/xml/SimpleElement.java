package uk.gov.nationalarchives.droidlet.core.xml;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.xml.sax.Attributes;

import uk.gov.nationalarchives.droidlet.core.exception.UnexpectedBuildException;
import uk.gov.nationalarchives.droidlet.core.exception.UnexpectedXmlStructureException;

/**
 * Holds the details of an XML element loaded from the PRONOM file
 */
public abstract class SimpleElement
{
	public static abstract class SimpleElementBuilder
	{
		// REMOVE: debugging
		final static Map<String, Set<String>> debugMap = new HashMap<>();

		static void register(String tag, Attributes attributes)
		{
			Set<String> set = debugMap.get(tag);

			// Check the key for the tag exists
			if (set == null)
			{
				set = new HashSet<>();
				debugMap.put(tag, set);
			}

			for (int index = 0; index < attributes.getLength(); index++)
				set.add(attributes.getQName(index));
		}

		public static void outputAttributes()
		{
			final StringBuilder result = new StringBuilder();
			for (final String tag : debugMap.keySet())
			{
				result.append("\n<");
				result.append(tag).append(" ");
				final Set<String> set = debugMap.get(tag);
				set.forEach((value) -> result.append(value + " "));
				result.append(">");
			}
			System.out.println(result);
		}

		private SimpleElementBuilder currentChildBuilder = null;
		private final String qName;
		private final Attributes attributes;

		protected SimpleElementBuilder(String qName, Attributes attributes)
		{
			this.qName = qName;
			this.attributes = attributes;
		}

		/**
		 * Called by the sax parser then recursively through the child builders
		 */
		public final void startElement(String qName, Attributes attributes)
		{
			register(qName, attributes);

			// If there is a current child then we just pass the message down to the child
			if (currentChildBuilder != null)
			{
				currentChildBuilder.startElement(qName, attributes);
				return;
			}

			//			System.out.println("<" + qName + ">" + attributes.getValue("ID"));

			// There is no child so we give subclasses a chance to handle it
			currentChildBuilder = startChildElementSpecific(qName, attributes);

			// Check that the subclass was expecting the child tag
			if (currentChildBuilder == null)
				throw new UnexpectedXmlStructureException("Unexpected child tag " + this.qName + "->" + qName);
		}

		/**
		 * Called by the sax parser then recursively through the child builders
		 */
		public final void endElement(String qName)
		{
			if (currentChildBuilder == null)
				return;
			if (currentChildBuilder.qName.equals(qName))
				currentChildBuilder = null;
			else
				currentChildBuilder.endElement(qName);
		}

		/**
		 * Called by the sax parser
		 */
		public final void addText(String string)
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

		/**
		 * Called by the owner once all XML loading has finished
		 */
		public SimpleElement build()
		{
			throw new UnexpectedBuildException("Unhandled child tag " + this.qName + "->" + qName);
		}

		/**
		 * Every subclass which expects to contain child tags should override this method
		 */
		protected SimpleElementBuilder startChildElementSpecific(String qName, Attributes attributes)
		{
			throw new UnexpectedXmlStructureException("Unhandled child tag " + this.qName + "->" + qName);
		}

		/**
		 * Every subclass which
		 */
		protected void addTextSpecific(String string)
		{
			// Do nothing
		}

		protected Integer getNullableIntegerAttributeValue(String attributeName)
		{
			if (attributes.getValue(attributeName) == null)
				return null;
			return Integer.valueOf(attributes.getValue(attributeName));
		}

		protected Byte singleByteAttribute(String string)
		{
			final String byteRawValue = attributes.getValue("Byte");
			if (byteRawValue == null)
				return null;

			final int intValue = Integer.parseInt(byteRawValue, 16);
			return (byte) intValue;
		}
	}
}