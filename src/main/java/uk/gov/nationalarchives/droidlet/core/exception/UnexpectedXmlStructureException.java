package uk.gov.nationalarchives.droidlet.core.exception;

public class UnexpectedXmlStructureException extends RuntimeException
{
	private static final long serialVersionUID = 8581651620853422701L;

	public UnexpectedXmlStructureException(String message)
	{
		super(message);
	}
}
