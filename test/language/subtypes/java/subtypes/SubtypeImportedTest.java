package subtypes;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

import subtypes.subtype_imported.SubtypeImported;
import subtypes.subtype_imported.pkg.SubTest;

public class SubtypeImportedTest
{
    @Test
    public void readWriteBase() throws IOException
    {
        final SubtypeImported subtypeImported = new SubtypeImported(new subtypes.subtype_imported.Test(42));

        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        subtypeImported.write(writer);

        final BitStreamReader reader =
                new ByteArrayBitStreamReader(writer.toByteArray(), writer.getBitPosition());
        SubtypeImported readSubtypeImported = new SubtypeImported(reader);
        assertTrue(subtypeImported.equals(readSubtypeImported));
    }

    @Test
    public void readWriteSubtype() throws IOException
    {
        final SubtypeImported subtypeImported =
                new SubtypeImported(new subtypes.subtype_imported.pkg.SubTest(42));

        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        subtypeImported.write(writer);

        final BitStreamReader reader =
                new ByteArrayBitStreamReader(writer.toByteArray(), writer.getBitPosition());
        SubtypeImported readSubtypeImported = new SubtypeImported(reader);
        assertTrue(subtypeImported.equals(readSubtypeImported));
    }
}
