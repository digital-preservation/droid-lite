package uk.gov.nationalarchives.droidlet.core.xml;

import org.xml.sax.Attributes;

public class DefaultShift extends SimpleElement
{
	// Unused but needed for backwards compatibility

	public static class DefaultShiftBuilder extends SimpleElementBuilder
	{
		protected DefaultShiftBuilder(Attributes attributes)
		{
			super(DefaultShift.class.getSimpleName(), attributes);
		}
	}
}
