package uk.gov.nationalarchives.droidlet.core.xml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.xml.sax.Attributes;

import uk.gov.nationalarchives.droidlet.core.ByteReader;
import uk.gov.nationalarchives.droidlet.core.xml.InternalSignature.InternalSignatureBuilder;

class InternalSignatureCollection extends SimpleElement
{
	static class InternalSignatureCollectionBuilder extends SimpleElementBuilder
	{
		private final List<InternalSignatureBuilder> internalSignatureBuilders;

		InternalSignatureCollectionBuilder(Attributes attributes)
		{
			super(InternalSignatureCollection.class.getSimpleName(), attributes);
			internalSignatureBuilders = new ArrayList<>();
		}

		@Override
		protected SimpleElementBuilder startChildElementSpecific(String qName, Attributes attributes)
		{
			if (InternalSignature.class.getSimpleName().equals(qName))
			{
				final InternalSignatureBuilder internalSignatureBuilder = new InternalSignatureBuilder(attributes);
				internalSignatureBuilders.add(internalSignatureBuilder);
				return internalSignatureBuilder;
			}

			return null;
		}

		@Override
		public InternalSignatureCollection build()
		{
			return new InternalSignatureCollection(this);
		}
	}

	private final List<InternalSignature> internalSignatures;

	private InternalSignatureCollection(InternalSignatureCollectionBuilder internalSignatureCollectionBuilder)
	{
		final List<InternalSignature> stagingList = new ArrayList<>();
		for (final InternalSignatureBuilder internalSignatureBuilder : internalSignatureCollectionBuilder.internalSignatureBuilders)
			stagingList.add(internalSignatureBuilder.build());
		internalSignatures = Collections.unmodifiableList(stagingList);
	}

	void runFileFormatIdentification(ByteReader byteReader)
	{
		for (final InternalSignature internalSignature : internalSignatures)
			internalSignature.runFileFormatIdentification(byteReader);
	}
}
