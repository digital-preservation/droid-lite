package uk.gov.nationalarchives.droidlet.core.exception;

public class UnexpectedBuildException extends RuntimeException
{
	private static final long serialVersionUID = -2039903643752094055L;

	public UnexpectedBuildException(String message)
	{
		super(message);
	}

}
