package package_name_conflict;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import zserio.runtime.io.ByteArrayBitStreamWriter;
import zserio.runtime.io.ByteArrayBitStreamReader;

public class PackageNameConflictTest
{
    @Test
    public void packageNameConflict() throws Exception
    {
        // just test that PackageNameConflict uses correct Blob
        PackageNameConflict packageNameConflict = new PackageNameConflict(new Blob(13));

        ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        packageNameConflict.write(writer);

        ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(writer.toByteArray());
        PackageNameConflict readPackageNameConflict = new PackageNameConflict(reader);

        assertEquals(13, packageNameConflict.getBlob().getValue());
        assertEquals(packageNameConflict.getBlob().getValue(), readPackageNameConflict.getBlob().getValue());
    }

    @Test
    public void packageNameConflictInner() throws Exception
    {
        // just test that PackageNameConflictInner uses correct Blob
        PackageNameConflictInner packageNameConflictInner = new PackageNameConflictInner(
                new package_name_conflict.package_name_conflict.Blob("test"));

        ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        packageNameConflictInner.write(writer);

        ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(writer.toByteArray());
        PackageNameConflictInner readPackageNameConflictInner = new PackageNameConflictInner(reader);

        assertEquals("test", packageNameConflictInner.getBlob().getValue());
        assertEquals(packageNameConflictInner.getBlob().getValue(),
                readPackageNameConflictInner.getBlob().getValue());
    }
};
