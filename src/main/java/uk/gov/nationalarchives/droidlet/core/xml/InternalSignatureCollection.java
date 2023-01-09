package uk.gov.nationalarchives.droidlet.core.xml;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;

import uk.gov.nationalarchives.droidlet.core.exception.UnexpectedXmlStructureException;
import uk.gov.nationalarchives.droidlet.core.xml.InternalSignature.InternalSignatureBuilder;

//import uk.gov.nationalarchives.droid.core.signature.ByteReader;
//import uk.gov.nationalarchives.droid.core.signature.xml.SimpleElement;

public class InternalSignatureCollection extends SimpleElement
{
	public static class InternalSignatureCollectionBuilder extends SimpleElementBuilder
	{
		private final List<InternalSignatureBuilder> internalSignatureBuilders;

		public InternalSignatureCollectionBuilder(Attributes attributes)
		{
			super(InternalSignatureCollection.class.getSimpleName());
			internalSignatureBuilders = new ArrayList<>();
		}

		@Override
		protected SimpleElementBuilder startChildElementSpecific(String qName, Attributes attributes)
		{
			if (InternalSignature.class.getSimpleName().equals(qName))
			{
				final InternalSignatureBuilder internalSignatureBuilder = new InternalSignatureBuilder(attributes);
				internalSignatureBuilders.add(internalSignatureBuilder);
				return internalSignatureBuilder;
			}
			throw new UnexpectedXmlStructureException();
		}
	}

	//	// BNO there is one instance of this for the entire profile - not each
	//	// request
	//	private static final int DEFAULT_COLLECTION_SIZE = 10;
	//
	//	private List<InternalSignature> internalSignatures = new ArrayList<InternalSignature>(DEFAULT_COLLECTION_SIZE);
	//	private Map<Integer, InternalSignature> sigsByID = new HashMap<Integer, InternalSignature>();
	//
	//	/**
	//	 * Runs all the signatures against the target file, adding a hit for each of
	//	 * them, if any of them match.
	//	 * 
	//	 * @param targetFile
	//	 *            The file to match the signatures against.
	//	 * @param maxBytesToScan
	//	 *            The maximum bytes to scan.
	//	 * @return A list of the internal signatures which matched.
	//	 */
	//	public List<InternalSignature> getMatchingSignatures(ByteReader targetFile, long maxBytesToScan)
	//	{
	//		final List<InternalSignature> result = new ArrayList<InternalSignature>();
	//		if (targetFile.getNumBytes() == 0)
	//			return result;
	//
	//		for (InternalSignature internalSignature : internalSignatures)
	//			if (internalSignature.matches(targetFile, maxBytesToScan))
	//				result.add(internalSignature);
	//		return result;
	//	}
	//
	//	/**
	//	 * Prepares the internal signatures in the collection for use.
	//	 */
	//	public void prepareForUse()
	//	{
	//		// BNO: Called once when initialising the profile.
	//		for (Iterator<InternalSignature> sigIterator = internalSignatures.iterator(); sigIterator.hasNext();)
	//		{
	//			InternalSignature sig = sigIterator.next();
	//			sig.prepareForUse();
	//			if (sig.isInvalidSignature())
	//			{
	//				sigsByID.remove(sig.getID());
	//				getLog().warn(getInvalidSignatureWarningMessage(sig));
	//				sigIterator.remove();
	//			}
	//		}
	//	}
	//
	//	private String getInvalidSignatureWarningMessage(InternalSignature sig)
	//	{
	//		return String.format("Removing invalid signature [id:%d]. " + "Matches formats: %s", sig.getID(), sig.getFileFormatDescriptions());
	//	}
	//
	//	/* setters */
	//	/**
	//	 * @param iSig
	//	 *            the signature to add.
	//	 */
	//	public final void addInternalSignature(final InternalSignature iSig)
	//	{
	//		internalSignatures.add(iSig);
	//		sigsByID.put(iSig.getID(), iSig);
	//	}
	//
	//	/**
	//	 * 
	//	 * @param iSig
	//	 *            The signature to remove.
	//	 */
	//	public final void removeInternalSignature(final InternalSignature iSig)
	//	{
	//		internalSignatures.remove(iSig);
	//		sigsByID.remove(iSig.getID());
	//	}
	//
	//	/**
	//	 * 
	//	 * @param signatureID
	//	 *            The id of the signature to get
	//	 * @return The signature with the given id, or null if the signature does
	//	 *         not exist.
	//	 */
	//	public final InternalSignature getInternalSignature(int signatureID)
	//	{
	//		return sigsByID.get(signatureID);
	//	}
	//
	//	/**
	//	 * 
	//	 * @param iSigs
	//	 *            The list of signatures to add.
	//	 */
	//	public final void setInternalSignatures(final List<InternalSignature> iSigs)
	//	{
	//		internalSignatures.clear();
	//		sigsByID.clear();
	//		for (InternalSignature signature : iSigs)
	//		{
	//			addInternalSignature(signature);
	//		}
	//	}
	//
	//	/* getters */
	//	/**
	//	 * A list of internal signatures in the collection.
	//	 * 
	//	 * @return A list of internal signatures in the collection.
	//	 */
	//	public final List<InternalSignature> getInternalSignatures()
	//	{
	//		return internalSignatures;
	//	}
	//
	//	/**
	//	 * Sorts the signatures in an order which maximises performance.
	//	 * 
	//	 * @param compareWith
	//	 *            the internal signature comparator to compare with.
	//	 */
	//	public void sortSignatures(final Comparator<InternalSignature> compareWith)
	//	{
	//		Collections.sort(internalSignatures, compareWith);
	//	}

}
