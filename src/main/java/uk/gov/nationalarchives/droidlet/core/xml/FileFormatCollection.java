package uk.gov.nationalarchives.droidlet.core.xml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.xml.sax.Attributes;

import uk.gov.nationalarchives.droidlet.core.xml.FileFormat.FileFormatBuilder;

class FileFormatCollection extends SimpleElement
{
	static class FileFormatCollectionBuilder extends SimpleElementBuilder
	{
		private final List<FileFormatBuilder> fileFormatBuilders;

		protected FileFormatCollectionBuilder(Attributes attributes)
		{
			super(FileFormatCollection.class.getSimpleName(), attributes);
			fileFormatBuilders = new ArrayList<>();
		}

		@Override
		protected SimpleElementBuilder startChildElementSpecific(String qName, Attributes attributes)
		{
			if (FileFormat.class.getSimpleName().equals(qName))
			{
				final FileFormatBuilder fileFormatBuilder = new FileFormatBuilder(attributes);
				fileFormatBuilders.add(fileFormatBuilder);
				return fileFormatBuilder;
			}

			return null;
		}

		@Override
		public FileFormatCollection build()
		{
			return new FileFormatCollection(this);
		}
	}

	private final List<FileFormat> formats;

	private FileFormatCollection(FileFormatCollectionBuilder fileFormatCollectionBuilder)
	{
		final List<FileFormat> fileFormatsStaging = new ArrayList<>();
		for (final FileFormatBuilder fileFormatBuilder : fileFormatCollectionBuilder.fileFormatBuilders)
			fileFormatsStaging.add(fileFormatBuilder.build());
		formats = Collections.unmodifiableList(fileFormatsStaging);
	}
}
