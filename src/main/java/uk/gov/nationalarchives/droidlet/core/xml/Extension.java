package uk.gov.nationalarchives.droidlet.core.xml;

public class Extension extends SimpleElement
{
	// Unused but needed for backwards compatibility

	public static class ExtensionBuilder extends SimpleElementBuilder
	{
		protected ExtensionBuilder()
		{
			super(ExtensionBuilder.class.getSimpleName());
		}
	}
}
