package uk.gov.nationalarchives.droidlet.core.xml;

import org.xml.sax.Attributes;

public class Extension extends SimpleElement
{
	// Unused but needed for backwards compatibility

	public static class ExtensionBuilder extends SimpleElementBuilder
	{
		protected ExtensionBuilder(Attributes attributes)
		{
			super(ExtensionBuilder.class.getSimpleName(), attributes);
		}
	}
}
