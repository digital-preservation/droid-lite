package uk.gov.nationalarchives.droidlet.core.xml;

import org.xml.sax.Attributes;

public class InternalSignatureID extends SimpleElement
{
	public static class InternalSignatureIDBuilder extends SimpleElementBuilder
	{
		protected InternalSignatureIDBuilder(Attributes attributes)
		{
			super(InternalSignatureIDBuilder.class.getSimpleName(), attributes);
		}
	}
}
