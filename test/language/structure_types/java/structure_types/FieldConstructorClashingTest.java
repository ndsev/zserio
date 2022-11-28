package structure_types;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import structure_types.field_constructor_clashing.FieldConstructorClashing;
import structure_types.field_constructor_clashing.CompoundRead;
import structure_types.field_constructor_clashing.CompoundPackingRead;
import structure_types.field_constructor_clashing.Field;

import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

// this test is mainly for C++, so just check that it is ok
public class FieldConstructorClashingTest
{
    @Test
    public void writeRead() throws IOException
    {
        final FieldConstructorClashing fieldConstructorClashing = createFieldConstructorClashing();

        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        fieldConstructorClashing.write(writer);

        final BitStreamReader reader = new ByteArrayBitStreamReader(
                writer.toByteArray(), writer.getBitPosition());
        final FieldConstructorClashing readFieldConstructorClashing = new FieldConstructorClashing(reader);
        assertEquals(fieldConstructorClashing, readFieldConstructorClashing);
    }

    private FieldConstructorClashing createFieldConstructorClashing()
    {
        final Field field1 = new Field(FIELD1);
        final Field field2 = new Field(FIELD2);
        final Field field3 = new Field(FIELD3);

        final CompoundRead[] compoundReadArray = { new CompoundRead(field1, field2) };
        final CompoundPackingRead[] compoundPackingReadArray =
                { new CompoundPackingRead(field1, field2, field3) };

        return new FieldConstructorClashing(compoundReadArray, compoundPackingReadArray);
    }

    private static final long FIELD1 = 1;
    private static final long FIELD2 = 9;
    private static final long FIELD3 = 5;
}
