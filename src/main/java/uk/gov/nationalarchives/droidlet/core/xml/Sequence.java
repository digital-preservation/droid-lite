package uk.gov.nationalarchives.droidlet.core.xml;

import org.xml.sax.Attributes;

public class Sequence extends SimpleElement
{
	public static class SequenceBuilder extends SimpleElementBuilder
	{
		byte[] data;

		protected SequenceBuilder(Attributes attributes)
		{
			super(Sequence.class.getSimpleName(), attributes);
		}

		@Override
		protected void addTextSpecific(String string)
		{
			final int length = string.length();
			data = new byte[length / 2];
			for (int index = 0; index < length; index += 2)
				data[index / 2] = (byte) ((Character.digit(string.charAt(index), 16) << 4) + Character.digit(string.charAt(index + 1), 16));
		}
	}
}
