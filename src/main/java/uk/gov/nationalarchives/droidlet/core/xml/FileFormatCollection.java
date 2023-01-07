package uk.gov.nationalarchives.droidlet.core.xml;

import org.xml.sax.Attributes;

public class FileFormatCollection extends SimpleElement
{
	public static class FileFormatCollectionBuilder extends SimpleElementBuilder
	{
		protected FileFormatCollectionBuilder(Attributes attributes)
		{
			super("FileFormatCollection");
		}
	}

	//	private List<FileFormat> formats = new ArrayList<FileFormat>();
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
