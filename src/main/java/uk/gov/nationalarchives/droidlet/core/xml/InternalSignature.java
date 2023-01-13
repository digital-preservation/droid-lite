package uk.gov.nationalarchives.droidlet.core.xml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.xml.sax.Attributes;

import uk.gov.nationalarchives.droidlet.core.ByteReader;
import uk.gov.nationalarchives.droidlet.core.xml.ByteSequence.ByteSequenceBuilder;
import uk.gov.nationalarchives.droidlet.core.xml.FileFormat.FileFormatBuilder;

/**
 * A binary signature which can match one or more file formats.
 *
 * It is composed of a list of {@link ByteSequence} objects, which are like regular expressions that can match bytes.
 *
 * A signature runs each ByteSequence against a target file, until one of them doesn't match, or all of them do.
 *
 * It orders the ByteSequence objects to try to ensure optimal matching behaviour - e.g. the ones anchored to the start of the file are done before ones at the end of the file.
 */
class InternalSignature extends SimpleElement
{
	static class InternalSignatureBuilder extends SimpleElementBuilder
	{
		private final List<FileFormatBuilder> fileFormatBuilders;
		private final List<ByteSequenceBuilder> byteSequenceBuilders;

		private final int id;

		protected InternalSignatureBuilder(Attributes attributes)
		{
			super(InternalSignature.class.getSimpleName(), attributes);
			fileFormatBuilders = new ArrayList<>();
			byteSequenceBuilders = new ArrayList<>();
			id = Integer.parseInt(attributes.getValue("ID"));
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

			if (ByteSequence.class.getSimpleName().equals(qName))
			{
				final ByteSequenceBuilder byteSequenceBuilder = new ByteSequenceBuilder(attributes);
				byteSequenceBuilders.add(byteSequenceBuilder);
				return byteSequenceBuilder;
			}

			return null;
		}

		@Override
		public InternalSignature build()
		{
			return new InternalSignature(this);
		}
	}

	private final List<ByteSequence> byteSequences;
	private final int intSigID;

	public InternalSignature(InternalSignatureBuilder internalSignatureBuilder)
	{
		// Byte sequences
		final List<ByteSequence> stagingByteSequences = new ArrayList<>();
		for (final ByteSequenceBuilder byteSequenceBuilder : internalSignatureBuilder.byteSequenceBuilders)
			stagingByteSequences.add(byteSequenceBuilder.build());
		byteSequences = Collections.unmodifiableList(stagingByteSequences);

		// ID
		intSigID = internalSignatureBuilder.id;
	}

	void runFileFormatIdentification(ByteReader byteReader)
	{
		for (final ByteSequence byteSequence : byteSequences)
			byteSequence.runFileFormatIdentification(byteReader);
	}
}

//
//	private boolean prepareByteSequences()
//	{
//		final int endIndex = byteSequences.size();
//		boolean hasOnlyVarSequences = true;
//		for (int byteSequenceIndex = 0; byteSequenceIndex < endIndex; byteSequenceIndex++)
//		{
//			ByteSequence byteSequence = byteSequences.get(byteSequenceIndex);
//			byteSequence.prepareForUse();
//			if (byteSequence.isInvalidByteSequence())
//			{
//				return true;
//			}
//			if (byteSequence.isAnchoredToBOF() || byteSequence.isAnchoredToEOF())
//			{
//				hasOnlyVarSequences = false;
//			}
//		}
//		if (hasOnlyVarSequences)
//		{
//			getLog().debug(getPerformanceWarningMessage());
//		}
//		// must call reorderByteSequences after first preparing byte sequences
//		// for use
//		// as it relies on their sort orders defined when preparing them for
//		// use.
//		reorderByteSequences();
//		calculateSignatureSortOrder();
//		return false;
//	}
//
//	// TODO: this doesn't work when the signature is part of a container
//	// signature.
//	// Or rather, it works but has no format information, since we don't bind
//	// file formats to binary signatures used internally in container
//	// signatures.
//	// Therefore, you get a warning about the sig, but no information about the
//	// format it relates to.
//	private String getPerformanceWarningMessage()
//	{
//
//		String formatDescriptions = getFileFormatDescriptions();
//		return String.format("Signature [id:%d] will always scan up to maximum bytes.  ", intSigID) + ((formatDescriptions.length() > 0) ? String.format("Matches formats: %s", formatDescriptions) : "");
//	}
//
//	/**
//	 *
//	 * @return a string describing the file formats matched by this signature.
//	 */
//	public String getFileFormatDescriptions()
//	{
//		StringBuilder builder = new StringBuilder();
//		for (FileFormat format : fileFormatList)
//		{
//			String formatInfo = String.format(" [Name:%s] [PUID:%s]  ", format.getName(), format.getPUID());
//			builder.append(formatInfo);
//		}
//		return builder.toString();
//	}
//
//	/*
//	 * Reset the bytesequences after reordering (to ensure BOF and EOF sequences
//	 * are checked first
//	 *
//	 * @param byteSequences sequence
//	 */
//	private void reorderByteSequences()
//	{
//		Collections.sort(byteSequences, new ByteSequenceComparator());
//	}
//
//
//	/**
//	 * Indicates whether the internal signature matches the target file.
//	 *
//	 * @param targetFile
//	 *            the binary file to be identified
//	 * @param maxBytesToScan
//	 *            how many bytes should be scanned from the beginning or end of
//	 *            each file. If negative, scanning is unlimited.
//	 * @return Whether the signature matches the target file or not.
//	 */
//	public final boolean matches(final ByteReader targetFile, final long maxBytesToScan)
//	{
//		boolean matchResult = true;
//		// BNO Byte sequences within the current signature
//		final List<ByteSequence> sequences = byteSequences;
//		final int numseqs = sequences.size();
//		// check each byte sequence in turn - stop as soon as one is found to be
//		// non-compliant
//		for (int sequenceIndex = 0; matchResult && sequenceIndex < numseqs; sequenceIndex++)
//		{
//			matchResult = sequences.get(sequenceIndex).matches(targetFile, maxBytesToScan);
//		}
//		return matchResult;
//	}
