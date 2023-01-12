package uk.gov.nationalarchives.droidlet.core.xml;

import org.xml.sax.Attributes;

public class Extension extends SimpleElement
{

	public static class ExtensionBuilder extends SimpleElementBuilder
	{
		private String value;

		protected ExtensionBuilder(Attributes attributes)
		{
			super(Extension.class.getSimpleName(), attributes);
		}

		@Override
		protected void addTextSpecific(String string)
		{
			value = string;
		}
	}
}
