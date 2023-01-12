package uk.gov.nationalarchives.droidlet.core.xml;

import org.xml.sax.Attributes;

public class InternalSignatureID extends SimpleElement
{
	public static class InternalSignatureIDBuilder extends SimpleElementBuilder
	{
		private String value;

		protected InternalSignatureIDBuilder(Attributes attributes)
		{
			super(InternalSignatureID.class.getSimpleName(), attributes);
		}

		@Override
		protected void addTextSpecific(String string)
		{
			value = string;
		}
	}
}
