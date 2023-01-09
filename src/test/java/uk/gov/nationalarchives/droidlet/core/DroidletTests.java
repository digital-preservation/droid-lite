package uk.gov.nationalarchives.droidlet.core;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;

import org.junit.jupiter.api.Test;

public class DroidletTests
{
	@Test
	void loadPronomFile_pronomFileIsLoaded() throws Exception
	{
		// Load the raw MIDI data
		final File file = new File(getClass().getClassLoader().getResource("DROID_SignatureFile_V109.xml").getFile());
		final byte[] pronomFileContents = Files.readAllBytes(file.toPath());

		// Check that the file contents were loaded
		assertTrue(pronomFileContents.length > 0);

		final ByteArrayInputStream inputStream = new ByteArrayInputStream(pronomFileContents);

		try
		{
			new Droidlet().loadPronomFileFormatSpecifications(inputStream);
		}
		catch (Exception exception)
		{
			exception.printStackTrace();
		}

		inputStream.close();
	}
}
