package uk.gov.nationalarchives.droidlet.core.xml;

import org.xml.sax.Attributes;

/**
 * A left fragment is all the parts of a byte sequence to the left of an anchoring subsequence. These parts cannot be searched for using the fast BoyerMooreHorspool algorithm so their existence is checked for once an anchoring sequence has been found.
 *
 * <p>
 * This subclass of SideFragment is only required so the XML parser can build an instance of this class and assemble the left fragments together.
 * </p>
 *
 * <p>
 * Left and Right Fragments are otherwise identical in functionality.
 * </p>
 */
public class LeftFragment extends SideFragment
{
	public static class LeftFragmentBuilder extends SideFragmentBuilder
	{
		protected LeftFragmentBuilder(Attributes attributes)
		{
			super(LeftFragment.class.getSimpleName(), attributes);
		}

		@Override
		public LeftFragment build()
		{
			return new LeftFragment(this);
		}
	}

	public LeftFragment(LeftFragmentBuilder leftFragmentBuilder)
	{
		super(leftFragmentBuilder);
	}
}
