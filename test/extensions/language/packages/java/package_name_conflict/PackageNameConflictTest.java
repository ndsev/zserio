package package_name_conflict;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

public class PackageNameConflictTest
{
    @Test
    public void packageNameConflictLocal() throws Exception
    {
        // just test that PackageNameConflictLocal uses correct Blob
        final PackageNameConflictLocal packageNameConflictLocal = new PackageNameConflictLocal(new Blob(13));

        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        packageNameConflictLocal.write(writer);

        final ByteArrayBitStreamReader reader =
                new ByteArrayBitStreamReader(writer.toByteArray(), writer.getBitPosition());
        final PackageNameConflictLocal readPackageNameConflictLocal = new PackageNameConflictLocal(reader);

        assertEquals(13, packageNameConflictLocal.getBlob().getValue());
        assertEquals(packageNameConflictLocal.getBlob().getValue(),
                readPackageNameConflictLocal.getBlob().getValue());
    }

    @Test
    public void packageNameConflictImported() throws Exception
    {
        // just test that PackageNameConflictImported uses correct Blob
        final PackageNameConflictImported packageNameConflictImported =
                new PackageNameConflictImported(new package_name_conflict.package_name_conflict.Blob("test"));

        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        packageNameConflictImported.write(writer);

        final ByteArrayBitStreamReader reader =
                new ByteArrayBitStreamReader(writer.toByteArray(), writer.getBitPosition());
        final PackageNameConflictImported readPackageNameConflictImported =
                new PackageNameConflictImported(reader);

        assertEquals("test", packageNameConflictImported.getBlob().getValue());
        assertEquals(packageNameConflictImported.getBlob().getValue(),
                readPackageNameConflictImported.getBlob().getValue());
    }
};
