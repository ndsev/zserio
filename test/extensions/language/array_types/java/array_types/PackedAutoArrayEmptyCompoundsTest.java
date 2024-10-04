package array_types;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import zserio.runtime.io.SerializeUtil;

import array_types.packed_auto_array_empty_compounds.EmptyChoice;
import array_types.packed_auto_array_empty_compounds.EmptyStruct;
import array_types.packed_auto_array_empty_compounds.EmptyUnion;
import array_types.packed_auto_array_empty_compounds.Main;
import array_types.packed_auto_array_empty_compounds.PackedAutoArray;

public class PackedAutoArrayEmptyCompoundsTest
{
    @Test
    public void writeReadFile()
    {
        final PackedAutoArray packedAutoArray = new PackedAutoArray(
                new EmptyStruct[] {new EmptyStruct(), new EmptyStruct(), new EmptyStruct()},
                new EmptyUnion[] {new EmptyUnion(), new EmptyUnion(), new EmptyUnion()},
                new EmptyChoice[] {new EmptyChoice(0), new EmptyChoice(0), new EmptyChoice(0)},
                new Main[] {new Main(new EmptyStruct(), new EmptyUnion(), 0, new EmptyChoice(0)),
                        new Main(new EmptyStruct(), new EmptyUnion(), 1, new EmptyChoice(1)),
                        new Main(new EmptyStruct(), new EmptyUnion(), 2, new EmptyChoice(2))});

        SerializeUtil.serializeToFile(packedAutoArray, BLOB_NAME);
        final PackedAutoArray readPackedAutoArray =
                SerializeUtil.deserializeFromFile(PackedAutoArray.class, BLOB_NAME);
        assertEquals(packedAutoArray, readPackedAutoArray);
    }

    private static final String BLOB_NAME = "packed_auto_array_empty_compounds.blob";
}
