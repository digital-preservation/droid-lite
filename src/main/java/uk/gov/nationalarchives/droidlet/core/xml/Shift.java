package uk.gov.nationalarchives.droidlet.core.xml;

import org.xml.sax.Attributes;

public class Shift extends SimpleElement
{
	public static class ShiftBuilder extends SimpleElementBuilder
	{
		private final Byte byteIdentifier;
		private int value;

		protected ShiftBuilder(Attributes attributes)
		{
			super(Shift.class.getSimpleName(), attributes);
			byteIdentifier = singleByteAttribute("Byte");
		}

		@Override
		protected void addTextSpecific(String string)
		{
			value = Integer.parseInt(string);
		}
	}
}
