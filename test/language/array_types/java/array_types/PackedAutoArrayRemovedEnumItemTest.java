package array_types;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import array_types.packed_auto_array_removed_enum_item.PackedAutoArrayRemovedEnumItem;
import array_types.packed_auto_array_removed_enum_item.Traffic;

import zserio.runtime.ZserioError;
import zserio.runtime.io.BitBuffer;
import zserio.runtime.io.SerializeUtil;

public class PackedAutoArrayRemovedEnumItemTest
{
    @Test
    public void writeReadFile()
    {
        final PackedAutoArrayRemovedEnumItem packedAutoArrayRemovedEnumItem =
                new PackedAutoArrayRemovedEnumItem(new Traffic[] {Traffic.NONE, Traffic.LIGHT, Traffic.MID});

        SerializeUtil.serializeToFile(packedAutoArrayRemovedEnumItem, BLOB_NAME);

        final PackedAutoArrayRemovedEnumItem readPackedAutoArrayRemovedEnumItem =
                SerializeUtil.deserializeFromFile(PackedAutoArrayRemovedEnumItem.class, BLOB_NAME);
        assertEquals(packedAutoArrayRemovedEnumItem, readPackedAutoArrayRemovedEnumItem);
    }

    @Test
    public void writeRemovedException()
    {
        final PackedAutoArrayRemovedEnumItem packedAutoArrayRemovedEnumItem =
                new PackedAutoArrayRemovedEnumItem(new Traffic[] {
                        Traffic.NONE, Traffic.LIGHT, Traffic.MID, Traffic.ZSERIO_REMOVED_HEAVY});
        assertThrows(ZserioError.class, () -> SerializeUtil.serialize(packedAutoArrayRemovedEnumItem));
    }

    private static final String BLOB_NAME = "packed_auto_array_removed_enum_item.blob";
}
