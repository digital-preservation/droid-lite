package uk.gov.nationalarchives.droidlet.core.xml;

public class Sequence extends SimpleElement
{
	public static class SequenceBuilder extends SimpleElementBuilder
	{
		protected SequenceBuilder(String qName)
		{
			super(Sequence.class.getSimpleName());
		}
	}
}
