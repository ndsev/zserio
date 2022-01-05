package identifiers;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import javax.imageio.stream.FileImageOutputStream;

import identifiers.structure_name_clashing_with_java.StructureNameClashingWithJava;
import identifiers.structure_name_clashing_with_java.Byte;
import identifiers.structure_name_clashing_with_java.Short;
import identifiers.structure_name_clashing_with_java.Integer;
import identifiers.structure_name_clashing_with_java.Long;
import identifiers.structure_name_clashing_with_java.BigInteger;
import identifiers.structure_name_clashing_with_java.Float;
import identifiers.structure_name_clashing_with_java.Double;
import identifiers.structure_name_clashing_with_java.String;

import zserio.runtime.ZserioError;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.BitStreamWriter;
import zserio.runtime.io.FileBitStreamReader;
import zserio.runtime.io.FileBitStreamWriter;

public class StructureNameClashingWithJavaTest
{
    @Test
    public void emptyConstructor()
    {
        final StructureNameClashingWithJava structureNameClashingWithJava =
                new StructureNameClashingWithJava();
        assertEquals(null, structureNameClashingWithJava.getByteField());
        assertEquals(null, structureNameClashingWithJava.getShortField());
        assertEquals(null, structureNameClashingWithJava.getIntegerField());
        assertEquals(null, structureNameClashingWithJava.getLongField());
        assertEquals(null, structureNameClashingWithJava.getBigIntegerField());
        assertEquals(null, structureNameClashingWithJava.getFloatField());
        assertEquals(null, structureNameClashingWithJava.getDoubleField());
        assertEquals(null, structureNameClashingWithJava.getStringField());
    }

    @Test
    public void bitSizeOf()
    {
        final StructureNameClashingWithJava structureNameClashingWithJava =
                new StructureNameClashingWithJava(
                        new Byte(java.lang.Byte.valueOf((byte)0)),
                        new Short(java.lang.Short.valueOf((short)0)),
                        new Integer(java.lang.Integer.valueOf(0)),
                        new Long(java.lang.Long.valueOf((long)0)),
                        new BigInteger(java.math.BigInteger.ZERO),
                        new Float(java.lang.Float.valueOf(0.0f)),
                        new Double(java.lang.Double.valueOf(0.0)),
                        new String("")
                );
        assertEquals(BIT_SIZE, structureNameClashingWithJava.bitSizeOf());
    }

    private static final int BIT_SIZE =
            8 * 1 + // all auto optionals
            8 + // Byte
            16 + // Short
            32 + // Integer
            64 + // Long
            64 + // BigInteger
            32 + // Float
            64 + // Double
            8; // String '\0'
}
