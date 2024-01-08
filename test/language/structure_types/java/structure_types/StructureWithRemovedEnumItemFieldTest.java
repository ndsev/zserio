package structure_types;

import static org.junit.jupiter.api.Assertions.*;

import static test_utils.AssertionUtils.assertJsonEquals;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import zserio.runtime.DebugStringUtil;
import zserio.runtime.ZserioError;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.SerializeUtil;

import structure_types.structure_with_removed_enum_item_field.Enumeration;
import structure_types.structure_with_removed_enum_item_field.StructureWithRemovedEnumItemField;

public class StructureWithRemovedEnumItemFieldTest
{
    @Test
    public void constructor()
    {
        final StructureWithRemovedEnumItemField structureWithRemovedEnumItemField =
                new StructureWithRemovedEnumItemField(Enumeration.ZSERIO_REMOVED_REMOVED);
        assertEquals(Enumeration.ZSERIO_REMOVED_REMOVED, structureWithRemovedEnumItemField.getEnumeration());
    }

    @Test
    public void bitStreamReaderConstructor() throws IOException
    {
        final BitStreamReader reader = new ByteArrayBitStreamReader(new byte[] {(byte)0});

        final StructureWithRemovedEnumItemField structureWithRemovedEnumItemField =
                new StructureWithRemovedEnumItemField(reader);
        assertEquals(Enumeration.ZSERIO_REMOVED_REMOVED, structureWithRemovedEnumItemField.getEnumeration());
    }

    @Test
    public void setter()
    {
        final StructureWithRemovedEnumItemField structureWithRemovedEnumItemField =
                new StructureWithRemovedEnumItemField();
        structureWithRemovedEnumItemField.setEnumeration(Enumeration.ZSERIO_REMOVED_REMOVED);
        assertEquals(Enumeration.ZSERIO_REMOVED_REMOVED, structureWithRemovedEnumItemField.getEnumeration());
    }

    @Test
    public void writeValid()
    {
        final StructureWithRemovedEnumItemField structureWithRemovedEnumItemField =
                new StructureWithRemovedEnumItemField(Enumeration.VALID);

        assertDoesNotThrow(() -> SerializeUtil.serialize(structureWithRemovedEnumItemField));
    }

    @Test
    public void writeRemovedException()
    {
        final StructureWithRemovedEnumItemField structureWithRemovedEnumItemField =
                new StructureWithRemovedEnumItemField(Enumeration.ZSERIO_REMOVED_REMOVED);

        assertThrows(ZserioError.class, () -> SerializeUtil.serialize(structureWithRemovedEnumItemField));
    }

    @Test
    public void toJsonString()
    {
        final StructureWithRemovedEnumItemField structureWithRemovedEnumItemField =
                new StructureWithRemovedEnumItemField(Enumeration.ZSERIO_REMOVED_REMOVED);
        final String json = DebugStringUtil.toJsonString(structureWithRemovedEnumItemField);
        assertJsonEquals("{\n    \"enumeration\": \"REMOVED\"\n}", json);
    }

    @Test
    public void fromJsonString()
    {
        final Object zserioObject = DebugStringUtil.fromJsonString(
                StructureWithRemovedEnumItemField.class, "{\n    \"enumeration\": \"REMOVED\"\n}");
        assertNotNull(zserioObject);
        assertInstanceOf(StructureWithRemovedEnumItemField.class, zserioObject);
        final StructureWithRemovedEnumItemField structureWithRemovedEnumItemField =
                (StructureWithRemovedEnumItemField)zserioObject;
        assertEquals(Enumeration.ZSERIO_REMOVED_REMOVED, structureWithRemovedEnumItemField.getEnumeration());
    }
};
