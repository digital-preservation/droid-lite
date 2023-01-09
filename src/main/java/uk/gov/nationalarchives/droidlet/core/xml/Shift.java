package uk.gov.nationalarchives.droidlet.core.xml;

import org.xml.sax.Attributes;

/**
 * The current system calculates its own shifts, so this class does nothing
 * except preserve backwards compatibility with the DROID XML parser, which will
 * attempt to create Shift elements.
 */
public class Shift extends SimpleElement
{
	public static class ShiftBuilder extends SimpleElementBuilder
	{
		protected ShiftBuilder(Attributes attributes)
		{
			super(Shift.class.getSimpleName());
		}
	}
}
