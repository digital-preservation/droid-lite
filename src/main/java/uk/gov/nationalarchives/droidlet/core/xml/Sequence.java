package uk.gov.nationalarchives.droidlet.core.xml;

import org.xml.sax.Attributes;

public class Sequence extends SimpleElement
{
	public static class SequenceBuilder extends SimpleElementBuilder
	{
		protected SequenceBuilder(Attributes attributes)
		{
			super(Sequence.class.getSimpleName(), attributes);
		}
	}
}
