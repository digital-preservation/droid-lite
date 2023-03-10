package uk.gov.nationalarchives.droidlet.core.xml;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.xml.sax.Attributes;

import uk.gov.nationalarchives.droidlet.core.xml.Extension.ExtensionBuilder;
import uk.gov.nationalarchives.droidlet.core.xml.HasPriorityOverFileFormatID.HasPriorityOverFileFormatIDBuilder;
import uk.gov.nationalarchives.droidlet.core.xml.InternalSignatureID.InternalSignatureIDBuilder;

public class FileFormat extends SimpleElement
{
	public static class FileFormatBuilder extends SimpleElementBuilder
	{
		final List<InternalSignatureIDBuilder> internalSignatureIDBuilders;
		final List<ExtensionBuilder> extensionBuilders;
		final List<HasPriorityOverFileFormatIDBuilder> hasPriorityOverFileFormatIDBuilders;

		final String mimeType;
		final String puid;
		final String version;
		final int id;
		final String name;

		public FileFormatBuilder(Attributes attributes)
		{
			super(FileFormat.class.getSimpleName(), attributes);

			internalSignatureIDBuilders = new ArrayList<>();
			extensionBuilders = new ArrayList<>();
			hasPriorityOverFileFormatIDBuilders = new ArrayList<>();

			mimeType = attributes.getValue("MIMEType");
			puid = attributes.getValue("PUID");
			version = attributes.getValue("Version");
			id = Integer.parseInt(attributes.getValue("ID"));
			name = attributes.getValue("name");
		}

		@Override
		protected SimpleElementBuilder startChildElementSpecific(String qName, Attributes attributes)
		{
			if (InternalSignatureID.class.getSimpleName().equals(qName))
			{
				final InternalSignatureIDBuilder internalSignatureIDBuilder = new InternalSignatureIDBuilder(attributes);
				internalSignatureIDBuilders.add(internalSignatureIDBuilder);
				return internalSignatureIDBuilder;
			}

			if (Extension.class.getSimpleName().equals(qName))
			{
				final ExtensionBuilder extensionBuilder = new ExtensionBuilder(attributes);
				extensionBuilders.add(extensionBuilder);
				return extensionBuilder;
			}

			if (HasPriorityOverFileFormatID.class.getSimpleName().equals(qName))
			{
				final HasPriorityOverFileFormatIDBuilder hasPriorityOverFileFormatIDBuilder = new HasPriorityOverFileFormatIDBuilder(attributes);
				hasPriorityOverFileFormatIDBuilders.add(hasPriorityOverFileFormatIDBuilder);
				return hasPriorityOverFileFormatIDBuilder;
			}

			return null;
		}

		@Override
		public FileFormat build()
		{
			return new FileFormat(this);
		}
	}

	private int identifier;
	private String name;
	private String version;
	private String puid;
	private final List<Integer> internalSigIDs = new ArrayList<Integer>();
	private final List<String> extensions = new ArrayList<String>();
	private final Set<String> extensionLookup = new HashSet<String>();
	private final List<Integer> hasPriorityOver = new ArrayList<Integer>();
	private String mimeType;

	private FileFormat(FileFormatBuilder fileFormatBuilder)
	{
		// TODO
	}

	/**
	 * @param theID
	 *            the id of the internal signature. As a string, as that is how the XML parser sets the value.
	 */
	public final void setInternalSignatureID(final String theID)
	{
		this.internalSigIDs.add(Integer.parseInt(theID));
	}

	/**
	 *
	 * @param id
	 *            The id of the signature to remove.
	 */
	public final void removeInternalSignatureID(int id)
	{
		this.internalSigIDs.remove(id);
	}

	/**
	 * Removes all internal signature ids from a file format.
	 *
	 * @return a copy of the list of signature ids removed from the file format.
	 */
	public final List<Integer> clearSignatures()
	{
		final List<Integer> oldSignatureIDs = new ArrayList<Integer>(internalSigIDs);
		this.internalSigIDs.clear();
		return oldSignatureIDs;
	}

	/**
	 *
	 * @param theExtension
	 *            the file extension.
	 */
	public final void setExtension(final String theExtension)
	{
		this.extensions.add(theExtension);
		this.extensionLookup.add(theExtension.toUpperCase(Locale.ENGLISH));
	}

	/**
	 *
	 * @param theID
	 *            The signature the file format takes priority over.
	 */
	public final void setHasPriorityOverFileFormatID(final String theID)
	{
		this.hasPriorityOver.add(Integer.parseInt(theID));
	}

	/**
	 *
	 * @param mimeType
	 *            The mime type of the file format.
	 */
	public final void setMimeType(final String mimeType)
	{
		this.mimeType = mimeType;
	}

	//	@Override
	//	public final void setAttributeValue(final String theName, final String theValue)
	//	{
	//		if ("ID".equals(theName))
	//		{
	//			this.identifier = Integer.parseInt(theValue);
	//		}
	//		else
	//			if ("Name".equals(theName))
	//			{
	//				this.name = theValue;
	//			}
	//			else
	//				if ("Version".equals(theName))
	//				{
	//					this.version = theValue;
	//				}
	//				else
	//					if ("PUID".equals(theName))
	//					{
	//						this.puid = theValue;
	//					}
	//					else
	//						if ("MIMEType".equals(theName))
	//						{
	//							this.mimeType = theValue;
	//						}
	//						else
	//						{
	//							unknownAttributeWarning(name, this.getElementName());
	//						}
	//	}

	/* getters */

	/**
	 * @return the number of internal signatures.
	 */
	public final int getNumInternalSignatures()
	{
		return this.internalSigIDs.size();
	}

	/**
	 *
	 * @return the number of file extensions.
	 */
	public final int getNumExtensions()
	{
		return this.extensions.size();
	}

	/**
	 *
	 * @return the number of file formats this format has priority over.
	 */
	public final int getNumHasPriorityOver()
	{
		return this.hasPriorityOver.size();
	}

	/**
	 *
	 * @return The list of format ids this format has priority over.
	 */
	public List<Integer> getFormatIdsHasPriorityOver()
	{
		return this.hasPriorityOver;
	}

	/**
	 *
	 * @param theIndex
	 *            The index of the internal signature to get the id of.
	 * @return the id of the internal signature.
	 */
	public final int getInternalSignatureID(final int theIndex)
	{
		return this.internalSigIDs.get(theIndex);
	}

	/**
	 *
	 * @return The mime type of the file format.
	 */
	public final String getMimeType()
	{
		return this.mimeType == null ? "" : this.mimeType;
	}

	/**
	 *
	 * @param theIndex
	 *            The index of the file extension
	 * @return the file extension.
	 */
	public final String getExtension(final int theIndex)
	{
		return this.extensions.get(theIndex);
	}

	/**
	 *
	 * @return A list of extensions defined against this file format.
	 */
	public final List<String> getExtensions()
	{
		return extensions;
	}

	/**
	 *
	 * @param theIndex
	 *            The index of the format this format takes priority over.
	 * @return The id of the file format which this format takes priority over.
	 */
	public final int getHasPriorityOver(final int theIndex)
	{
		return this.hasPriorityOver.get(theIndex);
	}

	/**
	 *
	 * @return The id of this file format.
	 */
	public final int getID()
	{
		return identifier;
	}

	/**
	 *
	 * @return the name of this file format.
	 */
	public final String getName()
	{
		return name;
	}

	/**
	 *
	 * @return The version of this file format.
	 */
	public final String getVersion()
	{
		return version;
	}

	/**
	 *
	 * @return the puid of this file format.
	 */
	public final String getPUID()
	{
		return puid;
	}

	/**
	 * Indicates whether the file extension given is listed against this file format.
	 *
	 * @param theExtension
	 *            file extension
	 * @return whether this file format has a matching extension.
	 *
	 */
	public final boolean hasMatchingExtension(final String theExtension)
	{
		return extensionLookup.contains(theExtension.toUpperCase(Locale.ENGLISH));
	}

	/**
	 * Indicates whether the file extension given should result in a mismatch warning.
	 *
	 * If there are no extensions listed for this file format, there should be no mismatch warning, whatever the given extension.
	 *
	 * Otherwise, a warning should be issued if the extension is not listed against this file format.
	 *
	 * @param theExtension
	 *            The file extension to check.
	 * @return Whether the file extension given should result in a mismatch warning.
	 */
	public final boolean hasExtensionMismatch(final String theExtension)
	{
		return extensions.size() == 0 ? false : !hasMatchingExtension(theExtension);
	}

}
