package uk.gov.nationalarchives.droidlet.core.xml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.xml.sax.Attributes;

import uk.gov.nationalarchives.droidlet.core.xml.SubSequence.SubSequenceBuilder;

//import net.byteseek.compiler.CompileException;
//import net.byteseek.io.reader.WindowReader;
//import uk.gov.nationalarchives.droid.core.signature.ByteReader;
//import uk.gov.nationalarchives.droid.core.signature.compiler.ByteSequenceAnchor;
//import uk.gov.nationalarchives.droid.core.signature.compiler.ByteSequenceCompiler;

/**
 * A ByteSequence is a regular-expression like object that can match a target file against itself, but operating over bytes rather than text.
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
	/**
	 * Format string definition of a two-char hex byte value.
	 */
	private static final String HEX_FORMAT = "%02x";

	/**
	 * Value of end of printable ascii chars.
	 */
	private static final int END_PRINTABLE_ASCII_CHARS = 126;

	/**
	 * Value of start of printable ascii chars.
	 */
	private static final int START_PRINTABLE_ASCII_CHARS = 32;

	/**
	 * A mask to convert bytes into integers.
	 */
	private static final int BYTEMASK = 0xFF;

	/**
	 * The number of possible bytes in a byte.
	 */
	private static final int BYTEVALUES = 256;

	/**
	 * A reference string defining whether the byte sequence is anchored to the beginning of the file.
	 */
	private static final String BOF_OFFSET = "BOFoffset";

	/**
	 * A reference string defining whether the byte sequence is anchored to the end of the file.
	 */
	private static final String EOF_OFFSET = "EOFoffset";

	/**
	 * The value of the quote character.
	 */
	private static final int QUOTE_CHARACTER_VALUE = 39;

	/**
	 * Error message when exception reading a byte from positionInFile
	 */
	private static final String BYTE_READ_ERROR = "An error occurred reading a byte at positionInFile ";

	private static final int SORT1 = 1;
	private static final int SORT2 = 2;
	private static final int SORT3 = 3;
	private static final int SORT4 = 4;
	private static final int SORT5 = 5;

	public static class ByteSequenceBuilder extends SimpleElementBuilder
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
	private final SubSequence[] sequences = new SubSequence[0];
	private final String reference = "Variable";

	// Assume a signature is big-endian unless we are told to the contrary.
	private final boolean bigEndian = true;

	private boolean hasIndirectOffset;
	private boolean anchoredToBOF;
	private boolean anchoredToEOF;
	private boolean reverseOrder;
	private boolean isFixedStart;
	private int indirectOffsetLength;
	private int indirectOffsetLocation;
	private int sortOrder;

	private boolean isInvalidByteSequence;

	/**
	 * The signature sequence in PRONOM or container compatible syntax. It won't be set for most ByteSequences, but can be set from the signature XML using a Sequence attribute on a ByteSequence element.
	 */
	private final String sequence = "";

	// Probably not needed after the use of the builder pattern
	//	/**
	//	 * Whether the bytesequence is prepared for use or not. The flag intends to only call this once, but container signature compile on demand from multiple threads.
	//	 * This causes the ByteSequence to be prepared for use multiple times even with this flag set.
	//	 */
	//
	//	// TODO: investigate threading issues around ByteSequence compilation and preparation in container signatures.
	//	private boolean preparedForUse;

	private ByteSequence(ByteSequenceBuilder byteSequenceBuilder)
	{
		final List<SubSequence> stagingSubSequences = new ArrayList<>();
		for (final SubSequenceBuilder subSequenceBuilder : byteSequenceBuilder.subSequenceBuilders)
			stagingSubSequences.add(subSequenceBuilder.build());
		subSequences = Collections.unmodifiableList(stagingSubSequences);
	}

	//	/**
	//	 *
	//	 * @return Whether the byte sequence is anchored to the beginning of a file.
	//	 */
	//	public final boolean isAnchoredToBOF()
	//	{
	//		return anchoredToBOF;
	//	}
	//
	//	/**
	//	 *
	//	 * @return Whether the byte sequence is anchored to the end of a file.
	//	 */
	//	public final boolean isAnchoredToEOF()
	//	{
	//		return anchoredToEOF;
	//	}
	//
	//	/**
	//	 *
	//	 * @return The sort order of this byte sequence.
	//	 */
	//	public final int getSortOrder()
	//	{
	//		return sortOrder;
	//	}
	//
	//	/**
	//	 *
	//	 * @return A string defining the anchoring status of the byte sequence.
	//	 */
	//	public final String getReference()
	//	{
	//		return reference;
	//	}
	//
	//	/**
	//	 *
	//	 * @param sseq
	//	 *            The subsequence to add to the byte sequence.
	//	 */
	//	public final void addSubSequence(final SubSequence sseq)
	//	{
	//		subSequences.add(sseq);
	//	}
	//
	//	/**
	//	 *
	//	 * @return The number of subsequence in the byte sequence.
	//	 */
	//	public final int getNumberOfSubSequences()
	//	{
	//		return subSequences.size();
	//	}
	//
	//	/**
	//	 * Return a defensive copy of any subsequences in this ByteSequence.
	//	 *
	//	 * @return a defensive copy of any subsequences in this ByteSequence.
	//	 */
	//	public List<SubSequence> getSubSequences()
	//	{
	//		return new ArrayList<SubSequence>(subSequences);
	//	}
	//
	//	/**
	//	 * If a reference attribute doesn't exist, this method may never be called.
	//	 * Be careful with any general setup done in this method. Defaults should
	//	 * already exist which are true if the reference is not set.
	//	 *
	//	 * @param theRef
	//	 *            A string defining the anchoring status of the byte sequence.
	//	 */
	//	public final void setReference(final String theRef)
	//	{
	//		this.hasIndirectOffset = theRef.startsWith("Indirect");
	//		this.anchoredToEOF = theRef.endsWith(EOF_OFFSET);
	//		this.anchoredToBOF = theRef.endsWith(BOF_OFFSET);
	//		this.isFixedStart = anchoredToEOF || anchoredToBOF;
	//		// Only pure EOFoffsets are reversed (not IndirectEOFOffsets),
	//		// IndirectEOFOffsets are really anchored to the BOF (but we find the
	//		// BOF offset relative to the EOF).
	//		this.reverseOrder = theRef.equalsIgnoreCase(EOF_OFFSET);
	//		// this.searchDirection = this.reverseOrder? -1 : 1;
	//		this.reference = theRef;
	//		preparedForUse = false;
	//		isInvalidByteSequence = false; // changed something - don't know if it's
	//		                               // invalid anymore.
	//	}
	//
	//	/**
	//	 * Sets a signature sequence value directly on a ByteSequence object. This
	//	 * is a full PRONOM or DROID container compatible signature.
	//	 *
	//	 * @param sequence
	//	 *            A PRONOM or container signature sequence
	//	 */
	//	public final void setSequence(final String sequence)
	//	{
	//		this.sequence = sequence;
	//		preparedForUse = false;
	//		isInvalidByteSequence = false; // changed something - don't know if it's
	//		                               // invalid anymore.
	//	}
	//
	//	/**
	//	 * Returns a signature sequence set on this ByteSequence object.
	//	 *
	//	 * @return a signature sequence set on this ByteSequence object.
	//	 */
	//	public String getSequence()
	//	{
	//		return sequence;
	//	}
	//
	//	/**
	//	 * This is only used when calculating indirect offsets. There are currently
	//	 * no signatures that use this at the time of writing.
	//	 *
	//	 * @param endianness
	//	 *            The endianess of the byte sequence.
	//	 */
	//	public final void setEndianness(final String endianness)
	//	{
	//		this.bigEndian = !"Little-endian".equals(endianness);
	//	}
	//
	//	/**
	//	 *
	//	 * @param indirectOffsetLength
	//	 *            The length of the indirect offset.
	//	 */
	//	public final void setIndirectOffsetLength(final String indirectOffsetLength)
	//	{
	//		this.indirectOffsetLength = Integer.parseInt(indirectOffsetLength);
	//	}
	//
	//	/**
	//	 *
	//	 * @param indirectOffsetLocation
	//	 *            The location of the indirect offset.
	//	 */
	//	public final void setIndirectOffsetLocation(final String indirectOffsetLocation)
	//	{
	//		this.indirectOffsetLocation = Integer.parseInt(indirectOffsetLocation);
	//	}
	//
	//	@Override
	//	public final void setAttributeValue(final String name, final String value)
	//	{
	//		if ("Reference".equals(name))
	//		{
	//			setReference(value);
	//		}
	//		else
	//			if ("Endianness".equals(name))
	//			{
	//				setEndianness(value);
	//			}
	//			else
	//				if ("IndirectOffsetLength".equals(name))
	//				{
	//					setIndirectOffsetLength(value);
	//				}
	//				else
	//					if ("IndirectOffsetLocation".equals(name))
	//					{
	//						setIndirectOffsetLocation(value);
	//					}
	//					else
	//						if ("Sequence".equals(name))
	//						{
	//							setSequence(value);
	//						}
	//						else
	//						{
	//							unknownAttributeWarning(name, this.getElementName());
	//						}
	//	}
	//
	//	private SubSequence getSubSequence(final int theIndex)
	//	{
	//		return subSequences.get(theIndex);
	//	}
	//
	//	/**
	//	 * Prepares bytes sequences and sub-sequences for use. MUST be called before
	//	 * using the byte sequence to match.
	//	 */
	//	public final void prepareForUse()
	//	{
	//		if (!preparedForUse)
	//		{
	//			// If we have a sequence attribute set, compile it:
	//			if (!sequence.isEmpty())
	//			{
	//				compileSequence();
	//			}
	//
	//			// Set the other properties of the ByteSequence needed to search.
	//			if (!isInvalidByteSequence)
	//			{
	//				setSortOrder();
	//				prepareSequenceFragments();
	//			}
	//		}
	//		preparedForUse = true;
	//	}
	//
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
	//	/**
	//	 *
	//	 * @return Whether the byteSequence is invalid or not.
	//	 */
	//	public boolean isInvalidByteSequence()
	//	{
	//		return isInvalidByteSequence;
	//	}
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
	//	/**
	//	 * checks whether the binary file specified by targetFile matches with this
	//	 * byte sequence.
	//	 *
	//	 * @param targetFile
	//	 *            The binary file to be identified
	//	 * @param maxBytesToScan
	//	 *            the maximum number of bytes to scan from the start or the end
	//	 *            of a file, or a negative number meaning a full file scan is
	//	 *            possible.
	//	 * @return boolean
	//	 */
	//	public final boolean matches(final ByteReader targetFile, final long maxBytesToScan)
	//	{
	//		boolean matchResult = true;
	//
	//		// Use a local reference to the sequence list for better performance:
	//		final SubSequence[] seq = this.sequences;
	//		boolean fixedSubsequence;
	//		if (reverseOrder)
	//		{
	//			fixedSubsequence = this.anchoredToEOF;
	//			final long fileSize = targetFile.getNumBytes() - 1L;
	//			targetFile.setFileMarker(fileSize);
	//			for (int subSequenceIndex = seq.length - 1; matchResult && subSequenceIndex >= 0; subSequenceIndex--)
	//			{
	//				final SubSequence subseq = seq[subSequenceIndex];
	//				final long currentFilePos = targetFile.getFileMarker();
	//				/*
	//				 * matchResult = subseq.findSequenceFromPosition(
	//				 * currentFilePos, targetFile, maxBytesToScan, false,
	//				 * fixedSubsequence);
	//				 */
	//				matchResult = subseq.findSequenceFromPosition(currentFilePos, targetFile, maxBytesToScan, false, fixedSubsequence);
	//
	//				fixedSubsequence = false;
	//			}
	//		}
	//		else
	//		{
	//			fixedSubsequence = this.anchoredToBOF;
	//			try
	//			{
	//				final long offset = getIndirectOffset(targetFile);
	//				targetFile.setFileMarker(offset);
	//				for (int subSequenceIndex = 0; matchResult && subSequenceIndex < seq.length; subSequenceIndex++)
	//				{
	//					final SubSequence subseq = seq[subSequenceIndex];
	//					final long currentFilePos = targetFile.getFileMarker();
	//					/*
	//					 * matchResult = subseq.findSequenceFromPosition(
	//					 * currentFilePos, targetFile, maxBytesToScan,
	//					 * fixedSubsequence, false);
	//					 */
	//					matchResult = subseq.findSequenceFromPosition(currentFilePos, targetFile, maxBytesToScan, fixedSubsequence, false);
	//
	//					fixedSubsequence = false;
	//				}
	//			}
	//			catch (IOException io)
	//			{
	//				log.error(String.format("Error processing file: %s. for byte sequence match", targetFile.getFileName()), io);
	//				return false;
	//			}
	//		}
	//		return matchResult;
	//	}
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
	//	// CHECKSTYLE:OFF - cyclomatic complexity too high.
	//	public final static String bytesToString(final boolean prettyPrint, byte[] bytes)
	//	{
	//		// CHECKSTYLE:ON
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
}
