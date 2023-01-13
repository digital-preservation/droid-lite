package uk.gov.nationalarchives.droidlet.core.xml;

import org.xml.sax.Attributes;

import uk.gov.nationalarchives.droidlet.core.ByteReader;
import uk.gov.nationalarchives.droidlet.core.xml.FileFormatCollection.FileFormatCollectionBuilder;
import uk.gov.nationalarchives.droidlet.core.xml.InternalSignatureCollection.InternalSignatureCollectionBuilder;

public class FFSignatureFile extends SimpleElement
{
	public static class FFSignatureFileBuilder extends SimpleElementBuilder
	{
		private InternalSignatureCollectionBuilder internalSignatureCollectionBuilder;
		private FileFormatCollectionBuilder fileFormatCollectionBuilder;

		public FFSignatureFileBuilder(Attributes attributes)
		{
			super(FFSignatureFileBuilder.class.getSimpleName(), attributes);
		}

		@Override
		protected SimpleElementBuilder startChildElementSpecific(String qName, Attributes attributes)
		{
			if (InternalSignatureCollection.class.getSimpleName().equals(qName))
			{
				internalSignatureCollectionBuilder = new InternalSignatureCollectionBuilder(attributes);
				return internalSignatureCollectionBuilder;
			}

			if (FileFormatCollection.class.getSimpleName().equals(qName))
			{
				fileFormatCollectionBuilder = new FileFormatCollectionBuilder(attributes);
				return fileFormatCollectionBuilder;
			}

			return null;
		}

		@Override
		public FFSignatureFile build()
		{
			return new FFSignatureFile(this);
		}
	}

	private final InternalSignatureCollection internalSignatureCollection;
	private final FileFormatCollection formatCollection;

	private FFSignatureFile(FFSignatureFileBuilder builder)
	{
		this.internalSignatureCollection = builder.internalSignatureCollectionBuilder.build();
		this.formatCollection = builder.fileFormatCollectionBuilder.build();
	}

	public final void runFileFormatIdentification(ByteReader byteReader)
	{
		internalSignatureCollection.runFileFormatIdentification(byteReader);

		// TODO find out why the file format collection is not consulted
	}
}

//	private Map<String, List<FileFormat>> tentativeFormats = new HashMap<String, List<FileFormat>>();
//	private Map<String, List<FileFormat>> formatsForExtension = new HashMap<String, List<FileFormat>>();

//	/**
//	 * Informs the signature file that we should remove any internal binary signatures for a given puid.
//	 *
//	 * For example, if there are container signatures for a puid, we should call this method, to ensure that we don't run the binary signatures as well as the container signatures.
//	 *
//	 * This will remove any internal signatures that exist for that puid, and adjust the tentative extension maps.
//	 *
//	 * Note that internal signatures can be mapped to more than one file format, and file formats can have more than one signature attached to them.
//	 *
//	 * @param puid
//	 *            The puid.
//	 */
//	public final void puidHasOverridingSignatures(String puid)
//	{
//		final FileFormat format = getFileFormat(puid);
//		if (format != null)
//		{
//			// 1. remove all the internal signature ids from the file format:
//			final List<Integer> removedSignatureIDs = format.clearSignatures();
//
//			// 2. For each signature removed from the file format,
//			// also remove the file format from the signature:
//			for (final Integer id : removedSignatureIDs)
//			{
//				final InternalSignature signature = internalSignatureCollection.getInternalSignature(id);
//				if (signature != null)
//				{
//					signature.removeFileFormat(format);
//					// 3. If the signature no longer points at any
//					// file formats, remove the signature entirely:
//					if (signature.getNumFileFormats() == 0)
//					{
//						internalSignatureCollection.removeInternalSignature(signature);
//					}
//				}
//			}
//
//			// 4. The file format no longer has any internal signatures.
//			// It is possible that it never had any, and was a
//			// tentative format. We can't tell at this point,
//			// as this method may have been called before.
//			// However, it is definitely not a tentative format now,
//			// as it has an overriding signature (probably a container
//			// signature at the time of writing for DROID 6).
//			// Therefore, remove it from the tentative extensions lists,
//			// if it ever existed there.
//			for (final String extension : format.getExtensions())
//			{
//				final List<FileFormat> tentativeFormatsForExtension = tentativeFormats.get(extension);
//				if (tentativeFormatsForExtension != null)
//				{
//					tentativeFormatsForExtension.remove(format);
//					// 5. If there are no more file formats defined
//					// for this extension, remove the entry entirely
//					// so it is consistent with how it was built.
//					if (tentativeFormatsForExtension.size() == 0)
//					{
//						tentativeFormats.remove(extension);
//					}
//				}
//			}
//		}
//	}
//
//	/*
//	 * Ensures that each internal signature does whatever it needs to do to
//	 * ensure its own best performance.
//	 */
//	private void prepareInternalSignatures()
//	{
//		// BNO: Called when profile initialised
//		this.setAllSignatureFileFormats();
//		this.internalSignatureCollection.prepareForUse();
//		internalSignatureCollection.sortSignatures(new InternalSignatureComparator());
//		buildFileExtensions();
//	}
//
//	private void debugWriteOutInternalSignatures()
//	{
//		try
//		{
//			// debug: write out signatures
//			final Path outputFile = Paths.get(System.getProperty("user.home"), "DROID4 Signature Sequences.csv");
//			if (Files.exists(outputFile))
//			{
//				Files.delete(outputFile);
//			}
//			Files.createFile(outputFile);
//
//			try (final Writer writer = Files.newBufferedWriter(outputFile, UTF_8))
//			{
//				final int stop = this.getNumInternalSignatures();
//				for (int signatureIndex = 0; signatureIndex < stop; signatureIndex++)
//				{
//					final InternalSignature sig = getInternalSignature(signatureIndex);
//					sig.debugWriteOutSignatureSequences(writer);
//				}
//			}
//			catch (IOException ex)
//			{
//				getLog().error(ex.getMessage());
//			}
//
//		}
//		catch (IOException ex)
//		{
//			getLog().error(ex.getMessage());
//		}
//	}
//
//	/**
//	 * Points all internal signatures to the fileFormat objects they identify.
//	 */
//	private void setAllSignatureFileFormats()
//	{
//		final int numFormats = this.getNumFileFormats();
//		for (int iFormat = 0; iFormat < numFormats; iFormat++)
//		{ // loop through file formats
//			final int numFormatInternalSignatures = this.getFileFormat(iFormat).getNumInternalSignatures();
//			for (int iFileSig = 0; iFileSig < numFormatInternalSignatures; iFileSig++)
//			{ // loop through internal
//			  // signatures for each file
//			  // format
//				final int iFileSigID = this.getFileFormat(iFormat).getInternalSignatureID(iFileSig);
//				// loop through all internal signatures to find one with a
//				// matching ID
//				final int numTotalInternalSignatures = this.getNumInternalSignatures();
//				for (int iIntSig = 0; iIntSig < numTotalInternalSignatures; iIntSig++)
//				{
//					if (this.getInternalSignature(iIntSig).getID() == iFileSigID)
//					{
//						this.getInternalSignature(iIntSig).addFileFormat(this.getFileFormat(iFormat));
//						break;
//					}
//				}
//			}
//		}
//	}
//
//	// Builds a mapped list of file formats with no binary signatures, indexed
//	// against their file extensions.
//	private void buildFileExtensions()
//	{
//		final int numFileFormats = this.getNumFileFormats();
//		for (int iFormat = 0; iFormat < numFileFormats; iFormat++)
//		{
//			final FileFormat theFormat = this.getFileFormat(iFormat);
//			if (theFormat.getNumInternalSignatures() == 0)
//			{
//				addTentativeFormat(theFormat);
//			}
//			addExtensions(theFormat);
//		}
//	}
//
//	/**
//	 * Maps a format against its extensions, if it doesn't have any other
//	 * signature defined for it.
//	 *
//	 * The original meaning of a Tentative format in earlier versions of DROID
//	 * was precisely a format which only had file extensions defined, and no
//	 * other signatures.
//	 *
//	 * @param tentativeFormat
//	 */
//	private void addTentativeFormat(final FileFormat tentativeFormat)
//	{
//		final int numExtensions = tentativeFormat.getNumExtensions();
//		for (int iExtension = 0; iExtension < numExtensions; iExtension++)
//		{
//			final String extension = tentativeFormat.getExtension(iExtension).toUpperCase();
//			List<FileFormat> formatList = tentativeFormats.get(extension);
//			if (formatList == null)
//			{
//				formatList = new ArrayList<FileFormat>();
//				tentativeFormats.put(extension, formatList);
//			}
//			formatList.add(tentativeFormat);
//		}
//	}
//
//	/**
//	 * Maps a format against all the extensions it defines.
//	 *
//	 * @param format
//	 *            The format to add its extensions for.
//	 */
//	private void addExtensions(final FileFormat format)
//	{
//		final int numExtensions = format.getNumExtensions();
//		for (int iExtension = 0; iExtension < numExtensions; iExtension++)
//		{
//			final String extension = format.getExtension(iExtension).toUpperCase();
//			List<FileFormat> formatList = formatsForExtension.get(extension);
//			if (formatList == null)
//			{
//				formatList = new ArrayList<FileFormat>();
//				formatsForExtension.put(extension, formatList);
//			}
//			formatList.add(format);
//		}
//	}
//
