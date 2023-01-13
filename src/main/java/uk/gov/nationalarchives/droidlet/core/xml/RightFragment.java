package uk.gov.nationalarchives.droidlet.core.xml;

import org.xml.sax.Attributes;

/**
 * A right fragment is all the parts of a byte sequence to the right of an anchoring subsequence. These parts cannot be searched for using the fast BoyerMooreHorspool algorithm so their existence is checked for once an anchoring sequence has been found.
 *
 * <p>
 * This subclass of SideFragment is only required so the XML parser can build an instance of this class and assemble the right fragments together.
 * </p>
 *
 * <p>
 * Left and Right Fragments are otherwise identical in functionality.
 * </p>
 */
public class RightFragment extends SideFragment
{
	public static class RightFragmentBuilder extends SideFragmentBuilder
	{
		protected RightFragmentBuilder(Attributes attributes)
		{
			super(RightFragment.class.getSimpleName(), attributes);
		}

		@Override
		public RightFragment build()
		{
			return new RightFragment(this);
		}
	}

	public RightFragment(RightFragmentBuilder rightFragmentBuilder)
	{
		super(rightFragmentBuilder);
	}
}
