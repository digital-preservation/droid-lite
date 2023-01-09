package uk.gov.nationalarchives.droidlet.core.xml;

import org.xml.sax.Attributes;

public class HasPriorityOverFileFormatID extends SimpleElement
{
	public static class HasPriorityOverFileFormatIDBuilder extends SimpleElementBuilder
	{
		protected HasPriorityOverFileFormatIDBuilder(Attributes attributes)
		{
			super(HasPriorityOverFileFormatID.class.getSimpleName(), attributes);
		}
	}
}
