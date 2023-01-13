package uk.gov.nationalarchives.droidlet.core.xml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.xml.sax.Attributes;

import uk.gov.nationalarchives.droidlet.core.ByteReader;
import uk.gov.nationalarchives.droidlet.core.xml.SubSequence.SubSequenceBuilder;

/**
 * A ByteSequence is a regular-expression-like object that can match a target file against its subsequences.
 *
 * <p>
 * It is composed of a list of {@link SubSequence} objects, all of which must match for the ByteSequence as a whole to match.
 * </p>
 *
 * <p>
 * Subsequences are effectively individual strings of bytes (albeit complex ones, including alternate strings and gaps), separated from each other by wildcard .* operators. If there are no .* operators in a ByteSequence, then there is only one SubSequence.
 * </p>
 */
public class ByteSequence extends SimpleElement
{
	static class Anchor
	{
		static final Anchor BOF = new Anchor("BOFoffset", true);
		static final Anchor EOF = new Anchor("EOFoffset", true);
		static final Anchor VARIABLE = new Anchor("Variable", false);

		private final String referenceIdentifier;
		private final boolean fixed;

		private Anchor(String referenceIdentifier, boolean fixed)
		{
			this.referenceIdentifier = referenceIdentifier;
			this.fixed = fixed;
		}

		static Anchor byReference(String reference)
		{
			if (reference.endsWith(BOF.referenceIdentifier))
				return BOF;
			if (reference.endsWith(EOF.referenceIdentifier))
				return EOF;
			return VARIABLE;
		}
	};

	static class ByteSequenceBuilder extends SimpleElementBuilder
	{
		private final List<SubSequenceBuilder> subSequenceBuilders;

		private final Integer indirectOffsetLocation;
		private final String reference;
		private final Integer indirectOffsetLength;
		private final String endianness;

		public ByteSequenceBuilder(Attributes attributes)
		{
			super(ByteSequence.class.getSimpleName(), attributes);
			subSequenceBuilders = new ArrayList<>();

			indirectOffsetLocation = getNullableIntegerAttributeValue("IndirectOffsetLocation");
			reference = attributes.getValue("Reference");
			indirectOffsetLength = getNullableIntegerAttributeValue("IndirectOffsetLength");
			endianness = attributes.getValue("Endianness");
		}

		@Override
		protected SimpleElementBuilder startChildElementSpecific(String qName, Attributes attributes)
		{
			if (SubSequence.class.getSimpleName().equals(qName))
			{
				final SubSequenceBuilder subSequenceBuilder = new SubSequenceBuilder(attributes);
				subSequenceBuilders.add(subSequenceBuilder);
				return subSequenceBuilder;
			}

			return null;
		}

		@Override
		public ByteSequence build()
		{
			return new ByteSequence(this);
		}
	}

	private final List<SubSequence> subSequences;
	private final boolean hasIndirectOffset;
	private final String reference;
	private final Integer indirectOffsetLocation;
	private final Integer indirectOffsetLength;
	private final boolean bigEndian;
	private final Anchor anchor;

	private ByteSequence(ByteSequenceBuilder byteSequenceBuilder)
	{
		// SubSequences
		final List<SubSequence> stagingSubSequences = new ArrayList<>();
		for (final SubSequenceBuilder subSequenceBuilder : byteSequenceBuilder.subSequenceBuilders)
			stagingSubSequences.add(subSequenceBuilder.build());
		subSequences = Collections.unmodifiableList(stagingSubSequences);

		// Other fields
		reference = byteSequenceBuilder.reference;
		hasIndirectOffset = reference.startsWith("Indirect");
		anchor = Anchor.byReference(reference);

		indirectOffsetLocation = byteSequenceBuilder.indirectOffsetLocation;
		indirectOffsetLength = byteSequenceBuilder.indirectOffsetLength;

		// We assume a signature is big-endian unless we are told to the contrary.
		bigEndian = "Big-endian".equalsIgnoreCase(byteSequenceBuilder.endianness);

		// TODO compile the sequence
	}

	public final void runFileFormatIdentification(final ByteReader byteReader)
	{
		// Don't bother checking further if a match has been found
		if (byteReader.hasBeenMatched())
			return;

		if (anchor == Anchor.EOF)
		{
			for (final SubSequence subSequence : subSequences)
				subSequence.findSequenceFromPosition(byteReader, false, anchor.fixed);
		}

		// TODO

		//		else
		//		{
		//			fixedSubsequence = this.anchoredToBOF;
		//			try
		//			{
		//				byteReader.setFileMarker(getIndirectOffset(byteReader));
		//				for (int subSequenceIndex = 0; matchResult && subSequenceIndex < seq.length; subSequenceIndex++)
		//				{
		//					final SubSequence subseq = seq[subSequenceIndex];
		//					final long currentFilePos = byteReader.getFileMarker();
		//					/*
		//					 * matchResult = subseq.findSequenceFromPosition( currentFilePos, targetFile, maxBytesToScan, fixedSubsequence, false);
		//					 */
		//					matchResult = subseq.findSequenceFromPosition(currentFilePos, byteReader, maxBytesToScan, fixedSubsequence, false);
		//
		//					fixedSubsequence = false;
		//				}
		//			}
		//			catch (final IOException io)
		//			{
		//				//					log.error(String.format("Error processing file: %s. for byte sequence match", targetFile.getFileName()), io);
		//				return false;
		//			}
		//			return matchResult;
		//		}
	}
}

///**
// * Format string definition of a two-char hex byte value.
// */
//private static final String HEX_FORMAT = "%02x";
//
///**
// * Value of end of printable ascii chars.
// */
//private static final int END_PRINTABLE_ASCII_CHARS = 126;
//
///**
// * Value of start of printable ascii chars.
// */
//private static final int START_PRINTABLE_ASCII_CHARS = 32;
//
///**
// * A mask to convert bytes into integers.
// */
//private static final int BYTEMASK = 0xFF;
//
///**
// * The number of possible bytes in a byte.
// */
//private static final int BYTEVALUES = 256;
//
///**
// * A reference string defining whether the byte sequence is anchored to the beginning of the file.
// */
//private static final int QUOTE_CHARACTER_VALUE = 39;
//
///**
// * Error message when exception reading a byte from positionInFile
// */
//private static final String BYTE_READ_ERROR = "An error occurred reading a byte at positionInFile ";
//
//private static final int SORT1 = 1;
//private static final int SORT2 = 2;
//private static final int SORT3 = 3;
//private static final int SORT4 = 4;
//private static final int SORT5 = 5;

//	/**
//	 * Compiles the ByteSequence object from the PRONOM syntax defined in the
//	 * sequence attribute.
//	 */
//	private void compileSequence()
//	{
//		try
//		{
//			if (!subSequences.isEmpty())
//			{
//				log.warn("A sequence is defined - ByteSequence is clearing any sub-objects (probably from XML parsing) before compiling: " + sequence);
//				subSequences.clear();
//			}
//			ByteSequenceCompiler.COMPILER.compile(this, sequence, getAnchor());
//		}
//		catch (CompileException e)
//		{
//			log.warn("Compilation error in signature for sequence: " + sequence + "\n" + e.getMessage(), e);
//			isInvalidByteSequence = true;
//		}
//	}
//
//	private ByteSequenceAnchor getAnchor()
//	{
//		ByteSequenceAnchor anchor = ByteSequenceAnchor.VariableOffset;
//		if (reference.endsWith(BOF_OFFSET))
//		{
//			anchor = ByteSequenceAnchor.BOFOffset;
//		}
//		else
//			if (reference.endsWith(EOF_OFFSET))
//			{
//				anchor = ByteSequenceAnchor.EOFOffset;
//			}
//		return anchor;
//	}
//
//	/**
//	 * Defines the sort order which a parent collection applies to sort this
//	 * ByteSequence relative to others.
//	 */
//	private void setSortOrder()
//	{
//		final int noOfSubSequences = subSequences.size();
//		if (anchoredToBOF)
//		{
//			if (noOfSubSequences == 1)
//			{
//				sortOrder = SORT1;
//			}
//			else
//			{
//				sortOrder = SORT2;
//			}
//		}
//		else
//			if (anchoredToEOF)
//			{
//				if (noOfSubSequences == 1)
//				{
//					sortOrder = SORT3;
//				}
//				else
//				{
//					sortOrder = SORT4;
//				}
//			}
//			else
//			{
//				sortOrder = SORT5;
//			}
//	}
//
//	/**
//	 * Run prepareSeqFragments on all subSequences within all ByteSequences
//	 * within all internalSignatures.
//	 */
//	private void prepareSequenceFragments()
//	{
//		// Determine first subsequence index which will be scanned when matching
//		// files:
//		final int firstSequenceToMatch = reverseOrder ? subSequences.size() - 1 : 0;
//
//		// For each subsequence:
//		final int stop = subSequences.size();
//		for (int subSequenceIndex = 0; subSequenceIndex < stop; subSequenceIndex++)
//		{
//			// Determine if the subsequence will scan the entire file, or be
//			// limited by a max offset:
//			final boolean fullFileScan = !(subSequenceIndex == firstSequenceToMatch && isFixedStart);
//			SubSequence subSequence = getSubSequence(subSequenceIndex);
//			subSequence.prepareForUse(reverseOrder, fullFileScan);
//			if (subSequence.isInvalidSubSequence())
//			{
//				isInvalidByteSequence = true;
//				break;
//			}
//		}
//		sequences = subSequences.toArray(sequences);
//	}
//
//
//	/**
//	 * Interpret the bytes in a file as an offset.
//	 * <p/>
//	 * The next <code>indirectOffsetLength()</code> bytes after
//	 * <code>indirectOffsetLocation()</code> are interpreted as an offset
//	 * according to the endianness of the byte sequence.
//	 *
//	 * @param targetFile
//	 * @return the offset
//	 */
//	private int getIndirectOffset(final ByteReader targetFile) throws IOException
//	{
//		int offset = 0;
//		if (this.hasIndirectOffset)
//		{
//			long power = 1;
//			long offsetLocation = indirectOffsetLocation;
//			final int offsetLength = indirectOffsetLength;
//			if (this.anchoredToEOF)
//			{
//				offsetLocation = targetFile.getNumBytes() - offsetLocation - 1;
//			}
//
//			final WindowReader reader = targetFile.getWindowReader();
//
//			// In the case of indirect BOF or indirect EOF bytesequences,
//			// We need to read the file to get the offset.
//			if (bigEndian)
//			{
//				for (int byteIndex = offsetLength - 1; byteIndex > -1; byteIndex--)
//				{
//					final int byteValue = reader.readByte(offsetLocation + byteIndex);
//					if (byteValue >= 0)
//					{
//						offset += power * byteValue;
//						power *= BYTEVALUES;
//					}
//					else
//					{
//						throw new IOException(BYTE_READ_ERROR + (offsetLocation + byteIndex));
//					}
//				}
//			}
//			else
//			{
//				for (int byteIndex = 0; byteIndex < offsetLength; byteIndex++)
//				{
//					final int byteValue = reader.readByte(offsetLocation + byteIndex);
//					if (byteValue >= 0)
//					{
//						offset += power * byteValue;
//						power *= BYTEVALUES;
//					}
//					else
//					{
//						throw new IOException(BYTE_READ_ERROR + (offsetLocation + byteIndex));
//					}
//					/*
//					 * final Byte fileByte = reader.readByte(offsetLocation +
//					 * byteIndex); int byteValue = fileByte.intValue();
//					 * byteValue = (byteValue >= 0) ? byteValue : byteValue +
//					 * BYTEVALUES; offset += power * byteValue; power *=
//					 * BYTEVALUES;
//					 */
//				}
//			}
//		}
//		return offset;
//	}
//
//
//	/**
//	 *
//	 * @param prettyPrint
//	 *            whether to pretty print the regular expression.
//	 * @return A regular expression representation of the byte sequence.
//	 */
//	public final String toRegularExpression(final boolean prettyPrint)
//	{
//		StringBuffer regularExpression = new StringBuffer();
//		final int numSequences = subSequences.size();
//		for (int subSequenceIndex = 0; subSequenceIndex < numSequences; subSequenceIndex++)
//		{
//			final SubSequence subseq = subSequences.get(subSequenceIndex);
//			final int minSeqOffset = subseq.getMinSeqOffset();
//			final int maxSeqOffset = subseq.getMaxSeqOffset();
//			final String subSeqExpression = subseq.toRegularExpression(prettyPrint);
//
//			// Append any bounded gaps at the start (or end if going backwards)
//			// of the sequence:
//			ByteSequence.appendBoundedGapExpression(prettyPrint, reverseOrder, regularExpression, subSeqExpression, minSeqOffset, subSequenceIndex == 0 ? maxSeqOffset : -1);
//		}
//
//		return regularExpression.toString().trim();
//	}
//
//	/**
//	 *
//	 * @param prettyPrint
//	 *            whether to pretty print the regular expression.
//	 * @param bytes
//	 *            a byte array of bytes to get an expression for.
//	 * @return A string containing the regular expression.
//	 */
//	public final static String bytesToString(final boolean prettyPrint, byte[] bytes)
//	{
//		StringBuffer hexString = new StringBuffer();
//		boolean inString = false;
//		final int byteLength = bytes.length;
//		for (int byteIndex = 0; byteIndex < byteLength; byteIndex++)
//		{
//			int byteValue = BYTEMASK & bytes[byteIndex];
//			if (prettyPrint && byteValue >= START_PRINTABLE_ASCII_CHARS && byteValue <= END_PRINTABLE_ASCII_CHARS && byteValue != QUOTE_CHARACTER_VALUE)
//			{
//				final String formatString = inString ? "%c" : " '%c";
//				hexString.append(String.format(formatString, (char) byteValue));
//				inString = true;
//			}
//			else
//			{
//				final String formatString = prettyPrint ? inString ? "' %02x" : HEX_FORMAT : HEX_FORMAT;
//				hexString.append(String.format(formatString, byteValue));
//				inString = false;
//			}
//		}
//		if (prettyPrint && inString)
//		{
//			hexString.append("' ");
//		}
//		return hexString.toString();
//	}
//
//	/**
//	 *
//	 * @param prettyPrint
//	 *            whether to pretty print the regular expression.
//	 * @param byteValue
//	 *            The byte to get an expression for.
//	 * @return The regular expression for a single byte.
//	 */
//	public static final String byteValueToString(final boolean prettyPrint, final int byteValue)
//	{
//		byte theByte = (byte) (BYTEMASK & byteValue);
//		byte[] singleByte = new byte[1];
//		singleByte[0] = theByte;
//		return bytesToString(prettyPrint, singleByte);
//	}
//
//	/**
//	 * Append a bounded gap to a string buffer.
//	 *
//	 * @param prettyPrint
//	 *            whether to pretty print the regular expression.
//	 * @param buffer
//	 *            The buffer to append to.
//	 * @param minGap
//	 *            the minimum gap
//	 * @param maxGap
//	 *            the maximum gap.
//	 */
//	public static void appendBoundedGap(final boolean prettyPrint, final StringBuffer buffer, final int minGap, final int maxGap)
//	{
//		if (maxGap < 0)
//		{
//			if (minGap > 0)
//			{
//				final String formatString = prettyPrint ? "  .{%d,*}  " : ".{%d,*}";
//				buffer.append(String.format(formatString, minGap));
//			}
//			else
//			{
//				buffer.append(prettyPrint ? "  .*  " : ".*");
//			}
//		}
//		else
//			if (minGap > 0 || maxGap > 0)
//			{
//				if (minGap == maxGap)
//				{ // defined offset
//					final String formatString = prettyPrint ? " .{%d} " : ".{%d}";
//					buffer.append(String.format(formatString, minGap));
//				}
//				else
//				{
//					final String formatString = prettyPrint ? " .{%d,%d} " : ".{%d,%d}";
//					buffer.append(String.format(formatString, minGap, maxGap));
//				}
//			}
//	}
//
//	/**
//	 * Append an expression to a string buffer with a bounded gap range on the
//	 * correct side of it.
//	 *
//	 * @param prettyPrint
//	 *            whether to pretty print the regular expression.
//	 * @param expressionFirst
//	 *            is the expression before or after the gap.
//	 * @param buffer
//	 *            The buffer to append to.
//	 * @param expression
//	 *            The expression.
//	 * @param min
//	 *            the minimum gap
//	 * @param max
//	 *            the maximum gap.
//	 */
//	public static void appendBoundedGapExpression(final boolean prettyPrint, final boolean expressionFirst, final StringBuffer buffer, final String expression, final int min, final int max)
//	{
//		if (expressionFirst)
//		{
//			buffer.append(expression);
//			appendBoundedGap(prettyPrint, buffer, min, max);
//		}
//		else
//		{
//			appendBoundedGap(prettyPrint, buffer, min, max);
//			buffer.append(expression);
//		}
//	}
