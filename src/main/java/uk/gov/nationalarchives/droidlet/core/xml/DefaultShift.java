package uk.gov.nationalarchives.droidlet.core.xml;

import org.xml.sax.Attributes;

public class DefaultShift extends SimpleElement
{
	public static class DefaultShiftBuilder extends SimpleElementBuilder
	{
		private int value;

		protected DefaultShiftBuilder(Attributes attributes)
		{
			super(DefaultShift.class.getSimpleName(), attributes);
		}

		@Override
		protected void addTextSpecific(String string)
		{
			value = Integer.parseInt(string);
		}
	}
}
