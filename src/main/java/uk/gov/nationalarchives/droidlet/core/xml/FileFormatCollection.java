package uk.gov.nationalarchives.droidlet.core.xml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.xml.sax.Attributes;

import uk.gov.nationalarchives.droidlet.core.xml.FileFormat.FileFormatBuilder;

public class FileFormatCollection extends SimpleElement
{
	public static class FileFormatCollectionBuilder extends SimpleElementBuilder
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

	//	private Map<String, FileFormat> puidFormats = new HashMap<String, FileFormat>();
	//
	//	/* setters */
	//
	//	/**
	//	 * @param format
	//	 *            A file format to add to the collection.
	//	 */
	//	public final void addFileFormat(final FileFormat format)
	//	{
	//		formats.add(format);
	//		puidFormats.put(format.getPUID(), format);
	//	}
	//
	//	/**
	//	 *
	//	 * @param formatList
	//	 *            A list of file formats to set for the collection.
	//	 */
	//	public final void setFileFormats(final List<FileFormat> formatList)
	//	{
	//		formats.clear();
	//		puidFormats.clear();
	//		for (FileFormat format : formatList)
	//		{
	//			addFileFormat(format);
	//		}
	//	}
	//
	//	/* getters */
	//
	//	/**
	//	 * @return The list of file formats held by this collection.
	//	 */
	//	public final List<FileFormat> getFileFormats()
	//	{
	//		return formats;
	//	}
	//
	//	/**
	//	 *
	//	 * @param puid
	//	 *            The puid
	//	 * @return A file format for that puid.
	//	 */
	//	public FileFormat getFormatForPUID(final String puid)
	//	{
	//		return puidFormats.get(puid);
	//	}

}
