package uk.gov.nationalarchives.droidlet.core.xml;

import org.xml.sax.Attributes;

//import net.byteseek.compiler.CompileException;
//import net.byteseek.compiler.matcher.SequenceMatcherCompiler;
//import net.byteseek.io.reader.WindowReader;
//import net.byteseek.matcher.MatchResult;
//import net.byteseek.matcher.sequence.SequenceMatcher;
//import net.byteseek.searcher.Searcher;
//import net.byteseek.searcher.bytes.ByteMatcherSearcher;
//import net.byteseek.searcher.sequence.horspool.HorspoolFinalFlagSearcher;
//import uk.gov.nationalarchives.droid.core.signature.xml.SimpleElement;

/**
 * A SideFragment is any fragment of a subsequence which cannot be searched for using the BoyerHooreHorspool algorithm. Typically, this means parts of the subsequence with gaps {n-m}, and alternatives (A|B|C).
 *
 * <p>
 * A subsequence is defined by the longest anchoring sequence which can be searched for, with any side fragments to the left and right of it checked for after the anchoring sequence is found.
 * </p>
 *
 */
public abstract class SideFragment extends SimpleElement
{
	private static final String FRAGMENT_PARSE_ERROR = "The signature fragment [%s] could not be parsed. The error returned was [%s]";

	public abstract static class SideFragmentBuilder extends SimpleElementBuilder
	{
		final int maxOffset;
		final int minOffset;
		final int position;

		protected SideFragmentBuilder(String qName, Attributes attributes)
		{
			super(qName, attributes);
			maxOffset = Integer.parseInt(attributes.getValue("MaxOffset"));
			minOffset = Integer.parseInt(attributes.getValue("MinOffset"));
			position = Integer.parseInt(attributes.getValue("Position"));
		}
	}

	//	private static final SequenceMatcherCompiler EXPRESSION_COMPILER = new SequenceMatcherCompiler();

	private int myPosition;
	private int myMinOffset;
	private int myMaxOffset;
	//	private SequenceMatcher matcher;
	//	private Searcher searcher;
	private boolean isInvalidFragment;

	//
	//
	//	/**
	//	 * @param matcher
	//	 *            THe SequenceMatcher which the SideFragment will match.
	//	 * @param minOffset
	//	 *            The minimum offset to begin looking for this fragment
	//	 * @param maxOffset
	//	 *            The maximum offset to begin looking for this fragment
	//	 * @param position
	//	 *            the position in file of this fragment
	//	 */
	//	public SideFragment(SequenceMatcher matcher, int minOffset, int maxOffset, int position)
	//	{
	//		this.matcher = matcher;
	//		this.myMinOffset = minOffset;
	//		this.myMaxOffset = maxOffset;
	//		this.myPosition = position;
	//		buildSearcher();
	//	}
	//
	//	/**
	//	 * @param toCopy
	//	 *            sideFragment to copy
	//	 */
	//	public SideFragment(final SideFragment toCopy)
	//	{
	//		this.matcher = toCopy.matcher;
	//		this.myMinOffset = toCopy.myMinOffset;
	//		this.myMaxOffset = toCopy.myMaxOffset;
	//		this.myPosition = toCopy.myPosition;
	//		buildSearcher();
	//	}
	//
	//	/* setters */
	//	/**
	//	 * @param thePosition
	//	 *            the positionInFile of the fragment in the list of
	//	 *            SideFragments held to the left or right of a subsequence.
	//	 *
	//	 *            Individual fragments can have the same positionInFile as each
	//	 *            other - this is how alternatives are represented - as
	//	 *            different fragments with the same positionInFile.
	//	 */
	//	public final void setPosition(final int thePosition)
	//	{
	//		this.myPosition = thePosition;
	//	}
	//
	//	/**
	//	 * A minimum offset is the amount of bytes to skip before looking for this
	//	 * fragment.
	//	 *
	//	 * @param theMinOffset
	//	 *            The minimum offset to begin looking for this fragment.
	//	 */
	//	public final void setMinOffset(final int theMinOffset)
	//	{
	//		this.myMinOffset = theMinOffset;
	//		// ensure the maximum is never less than then minimum.
	//		if (this.myMaxOffset < this.myMinOffset)
	//		{
	//			this.myMaxOffset = theMinOffset;
	//		}
	//	}
	//
	//	/**
	//	 * A maximum offset is the largest amount of bytes to look in for this
	//	 * fragment. If the maximum offset is greater than the minimum offset, then
	//	 * a range of bytes will be searched for this fragment.
	//	 *
	//	 * @param theMaxOffset
	//	 *            The maximum offset to begin lookiing for this fragment.
	//	 */
	//	public final void setMaxOffset(final int theMaxOffset)
	//	{
	//		this.myMaxOffset = theMaxOffset;
	//		// ensure the minimum is never greater than the maximum.
	//		if (this.myMinOffset > this.myMaxOffset)
	//		{
	//			this.myMinOffset = theMaxOffset;
	//		}
	//	}
	//
	//	/**
	//	 *
	//	 * @param expression
	//	 *            The regular expression defining the fragment.
	//	 */
	//	public final void setFragment(final String expression)
	//	{
	//		try
	//		{
	//			final String transformed = FragmentRewriter.rewriteFragment(expression);
	//			matcher = EXPRESSION_COMPILER.compile(transformed);
	//			buildSearcher();
	//		}
	//		catch (CompileException ex)
	//		{
	//			final String warning = String.format(FRAGMENT_PARSE_ERROR, expression, ex.getMessage());
	//			isInvalidFragment = true;
	//			getLog().warn(warning);
	//		}
	//	}
	//
	//	private void buildSearcher()
	//	{
	//		if (matcher.length() == 1)
	//		{
	//			searcher = new ByteMatcherSearcher(matcher.getMatcherForPosition(0));
	//		}
	//		else
	//		{
	//			searcher = new HorspoolFinalFlagSearcher(matcher);
	//		}
	//	}
	//
	//	/**
	//	 * Returns the SequenceMatcher object which matches the fragment.
	//	 *
	//	 * @return the SequenceMatcher object which matches the fragment.
	//	 */
	//	public final SequenceMatcher getMatcher()
	//	{
	//		return matcher; // SequenceMatchers are immutable, so can just return it
	//		                // directly without fear.
	//	}
	//
	//	/**
	//	 *
	//	 * @return Whether the fragment managed to be assembled correctly.
	//	 */
	//	public boolean isInvalidFragment()
	//	{
	//		return isInvalidFragment;
	//	}
	//
	//	@Override
	//	public final void setAttributeValue(final String name, final String value)
	//	{
	//		if ("Position".equals(name))
	//		{
	//			setPosition(Integer.parseInt(value));
	//		}
	//		else
	//			if ("MinOffset".equals(name))
	//			{
	//				setMinOffset(Integer.parseInt(value));
	//			}
	//			else
	//				if ("MaxOffset".equals(name))
	//				{
	//					setMaxOffset(Integer.parseInt(value));
	//				}
	//				else
	//				{
	//					unknownAttributeWarning(name, this.getElementName());
	//				}
	//	}
	//
	//	/* getters */
	//	/**
	//	 *
	//	 * @return the positionInFile of this fragment.
	//	 */
	//	public final int getPosition()
	//	{
	//		return myPosition;
	//	}
	//
	//	/**
	//	 * A minimum offset is the amount of bytes to skip before looking for this
	//	 * fragment.
	//	 *
	//	 * @return The minimum offset to begin looking for this fragment.
	//	 */
	//	public final int getMinOffset()
	//	{
	//		return myMinOffset;
	//	}
	//
	//	/**
	//	 * A maximum offset is the largest amount of bytes to look in for this
	//	 * fragment. If the maximum offset is greater than the minimum offset, then
	//	 * a range of bytes will be searched for this fragment.
	//	 *
	//	 * @return The maximum offset to look for this fragment.
	//	 */
	//	public final int getMaxOffset()
	//	{
	//		return myMaxOffset;
	//	}
	//
	//	/**
	//	 *
	//	 * @return The number of bytes matched by this fragment.
	//	 */
	//	public final int getNumBytes()
	//	{
	//		return matcher == null ? 0 : matcher.length();
	//	}
	//
	//	/**
	//	 * Set the sideFragment sequence. This will have been stored in the text
	//	 * attribute by the setText method. Then transforms the input string into a
	//	 * list of matching objects.
	//	 */
	//	@Override
	//	public final void completeElementContent()
	//	{
	//		setFragment(this.getText());
	//	}
	//
	//	/**
	//	 * Matches the fragment against the positionInFile in the ByteReader given.
	//	 *
	//	 * @param bytes
	//	 *            The byte reader to match the bytes with.
	//	 * @param matchFrom
	//	 *            The positionInFile to match from.
	//	 * @return Whether the fragment matches at the positionInFile given.
	//	 * @throws IOException
	//	 *             If a problem occurs reading the underlying file or stream
	//	 */
	//	public final boolean matchesBytes(final WindowReader bytes, final long matchFrom) throws IOException
	//	{
	//		return matcher.matches(bytes, matchFrom);
	//	}
	//
	//	/**
	//	 * Finds the fragment looking forwards from 'from' up to 'to'.
	//	 *
	//	 * @param bytes
	//	 *            a Byteseek WindowReader object
	//	 * @param from
	//	 *            the positionInFile within bytes from which to start searching
	//	 * @param to
	//	 *            the positionInFile within bytes at which to end searching
	//	 * @return A list of match results
	//	 * @throws IOException
	//	 *             If a problem occurs reading the underlying file or stream
	//	 */
	//	public final List<MatchResult> findFragmentForwards(final WindowReader bytes, final long from, final long to) throws IOException
	//	{
	//		return searcher.searchForwards(bytes, from, to);
	//	}
	//
	//	/**
	//	 * Finds the fragment looking backwards from 'from' back to 'to'.
	//	 *
	//	 * @param bytes
	//	 *            a Byteseek WindowReader object
	//	 * @param from
	//	 *            the positionInFile within bytes from which to start searching
	//	 * @param to
	//	 *            the positionInFile within bytes at which to end searching
	//	 * @return A list of match results
	//	 * @throws IOException
	//	 *             If a problem occurs reading the underlying file or stream
	//	 */
	//	public final List<MatchResult> findBackwards(final WindowReader bytes, final long from, final long to) throws IOException
	//	{
	//		return searcher.searchBackwards(bytes, from, to);
	//	}
	//
	//	/**
	//	 * Returns a regular expression representation of this fragment.
	//	 *
	//	 * @param prettyPrint
	//	 *            whether to pretty print the regular expression.
	//	 * @return a regular expression defining this fragment, but minus any
	//	 *         offsets defined here (handled by the parent subsequence).
	//	 */
	//	public final String toRegularExpression(final boolean prettyPrint)
	//	{
	//		return matcher == null ? "" : matcher.toRegularExpression(prettyPrint);
	//	}
	//
	//	@Override
	//	public String toString()
	//	{
	//		return getClass().getSimpleName() + '[' + toRegularExpression(true) + ']';
	//	}
	//
	//	@Override
	//	protected Object clone() throws CloneNotSupportedException
	//	{
	//		return super.clone();
	//	}
	//
	//	/**
	//	 * Creates a clone of the side fragment on whihc it is invoked.
	//	 *
	//	 * @return A deep clone of the target SideFragment
	//	 */
	//	public SideFragment copy()
	//	{
	//		SideFragment copy = null;
	//		try
	//		{
	//			copy = (SideFragment) this.clone();
	//		}
	//		catch (CloneNotSupportedException e)
	//		{
	//			System.out.println("Error cloning fragment " + this.toString() + ":" + e.getMessage());
	//		}
	//		return copy;
	//	}
}
