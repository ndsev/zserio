package array_types;

import static org.junit.jupiter.api.Assertions.*;

import static test_utils.AssertionUtils.assertInnerClassNotPresent;
import static test_utils.AssertionUtils.assertInnerClassPresent;
import static test_utils.AssertionUtils.assertMethodNotPresent;
import static test_utils.AssertionUtils.assertMethodPresent;

import org.junit.jupiter.api.Test;

import zserio.runtime.io.SerializeUtil;

import array_types.packing_interface_optimization.MixedColorBitmask;
import array_types.packing_interface_optimization.MixedColorChoice;
import array_types.packing_interface_optimization.MixedColorEnum;
import array_types.packing_interface_optimization.MixedColorStruct;
import array_types.packing_interface_optimization.MixedColorUnion;
import array_types.packing_interface_optimization.PackedColorBitmask;
import array_types.packing_interface_optimization.PackedColorChoice;
import array_types.packing_interface_optimization.PackedColorEnum;
import array_types.packing_interface_optimization.PackedColorStruct;
import array_types.packing_interface_optimization.PackedColorUnion;
import array_types.packing_interface_optimization.PackedColorsHolder;
import array_types.packing_interface_optimization.PackingInterfaceOptimization;
import array_types.packing_interface_optimization.UnpackedColorBitmask;
import array_types.packing_interface_optimization.UnpackedColorChoice;
import array_types.packing_interface_optimization.UnpackedColorEnum;
import array_types.packing_interface_optimization.UnpackedColorStruct;
import array_types.packing_interface_optimization.UnpackedColorUnion;
import array_types.packing_interface_optimization.UnpackedColorsHolder;

public class PackingInterfaceOptimizationTest
{
    @Test
    public void writeReadFile()
    {
        final PackingInterfaceOptimization packingInterfaceOptimization =
                new PackingInterfaceOptimization(createUnpackedColorsHolder(), createPackedColorsHolder());

        SerializeUtil.serializeToFile(packingInterfaceOptimization, BLOB_NAME);
        final PackingInterfaceOptimization readPackingInterfaceOptimization =
                SerializeUtil.deserializeFromFile(PackingInterfaceOptimization.class, BLOB_NAME);
        assertEquals(packingInterfaceOptimization, readPackingInterfaceOptimization);
    }

    @Test
    public void packingInterfaceMethods()
    {
        assertPackingInterfaceMethodsNotPresent(PackingInterfaceOptimization.class);
        assertInnerClassNotPresent(PackingInterfaceOptimization.class, "ZserioPackingContext");
    }

    @Test
    public void unpackedColorsHolderMethods()
    {
        assertPackingInterfaceMethodsNotPresent(UnpackedColorsHolder.class);
        assertInnerClassNotPresent(UnpackedColorsHolder.class, "ZserioPackingContext");
        assertInnerClassPresent(UnpackedColorsHolder.class, "ZserioElementFactory_unpackedColors");
        assertInnerClassPresent(UnpackedColorsHolder.class, "ZserioElementFactory_mixedColors");

        final Class<?> zserioElementFactory_unpackedColors_class =
                getPrivateInnerClass(UnpackedColorsHolder.class, "ZserioElementFactory_unpackedColors");
        assertMethodPresent(zserioElementFactory_unpackedColors_class, "create");
        assertMethodNotPresent(zserioElementFactory_unpackedColors_class, "createPackingContext");
        assertMethodNotPresent(
                zserioElementFactory_unpackedColors_class, "create(zserio.runtime.array.PackingContext");

        final Class<?> zserioElementFactory_mixedColors_class =
                getPrivateInnerClass(UnpackedColorsHolder.class, "ZserioElementFactory_mixedColors");
        assertMethodPresent(zserioElementFactory_mixedColors_class, "create");
        assertMethodPresent(zserioElementFactory_mixedColors_class, "createPackingContext");
        assertMethodPresent(
                zserioElementFactory_mixedColors_class, "create(zserio.runtime.array.PackingContext");
    }

    @Test
    public void packedColorsHolderMethods()
    {
        assertPackingInterfaceMethodsNotPresent(PackedColorsHolder.class);
        assertInnerClassNotPresent(PackedColorsHolder.class, "ZserioPackingContext");
        assertInnerClassPresent(PackedColorsHolder.class, "ZserioElementFactory_mixedColors");
        assertInnerClassPresent(PackedColorsHolder.class, "ZserioElementFactory_packedColors");

        final Class<?> zserioElementFactory_mixedColors_class =
                getPrivateInnerClass(PackedColorsHolder.class, "ZserioElementFactory_mixedColors");
        assertMethodPresent(zserioElementFactory_mixedColors_class, "create");
        assertMethodPresent(zserioElementFactory_mixedColors_class, "createPackingContext");
        assertMethodPresent(
                zserioElementFactory_mixedColors_class, "create(zserio.runtime.array.PackingContext");

        final Class<?> zserioElementFactory_packedColors_class =
                getPrivateInnerClass(PackedColorsHolder.class, "ZserioElementFactory_packedColors");
        assertMethodPresent(zserioElementFactory_packedColors_class, "create");
        assertMethodPresent(zserioElementFactory_packedColors_class, "createPackingContext");
        assertMethodPresent(
                zserioElementFactory_packedColors_class, "create(zserio.runtime.array.PackingContext");
    }

    @Test
    public void unpackedColorStructMethods()
    {
        assertPackingInterfaceMethodsNotPresent(UnpackedColorStruct.class);
        assertInnerClassNotPresent(UnpackedColorStruct.class, "ZserioPackingContext");
    }

    @Test
    public void unpackedColorChoiceMethods()
    {
        assertPackingInterfaceMethodsNotPresent(UnpackedColorChoice.class);
        assertInnerClassNotPresent(UnpackedColorChoice.class, "ZserioPackingContext");
    }

    @Test
    public void unpackedColorUnionMethods()
    {
        assertPackingInterfaceMethodsNotPresent(UnpackedColorUnion.class);
        assertInnerClassNotPresent(UnpackedColorUnion.class, "ZserioPackingContext");
    }

    @Test
    public void unpackedColorEnumMethods()
    {
        assertMethodNotPresent(UnpackedColorEnum.class, "readEnum(zserio.runtime.array.PackingContext");
        assertMethodNotPresent(UnpackedColorEnum.class, "initPackingContext");
        assertMethodNotPresent(UnpackedColorEnum.class, "bitSizeOf(zserio.runtime.array.PackingContext");
        assertMethodNotPresent(
                UnpackedColorEnum.class, "initializeOffsets(zserio.runtime.array.PackingContext");
        assertMethodNotPresent(UnpackedColorEnum.class, "write(zserio.runtime.array.PackingContext");
    }

    @Test
    public void unpackedColorBitmaskMethods()
    {
        assertMethodNotPresent(
                UnpackedColorBitmask.class, "UnpackedColorBitmask(zserio.runtime.array.PackingContext");
        assertMethodNotPresent(UnpackedColorBitmask.class, "initPackingContext");
        assertMethodNotPresent(UnpackedColorBitmask.class, "bitSizeOf(zserio.runtime.array.PackingContext");
        assertMethodNotPresent(
                UnpackedColorBitmask.class, "initializeOffsets(zserio.runtime.array.PackingContext");
        assertMethodNotPresent(UnpackedColorBitmask.class, "write(zserio.runtime.array.PackingContext");
    }

    @Test
    public void mixedColorStructMethods()
    {
        assertPackingInterfaceMethodsPresent(MixedColorStruct.class);
        assertInnerClassPresent(MixedColorStruct.class, "ZserioPackingContext");
    }

    @Test
    public void mixedColorChoiceMethods()
    {
        assertPackingInterfaceMethodsPresent(MixedColorChoice.class);
        assertInnerClassPresent(MixedColorChoice.class, "ZserioPackingContext");
    }

    @Test
    public void mixedColorUnionMethods()
    {
        assertPackingInterfaceMethodsPresent(MixedColorUnion.class);
        assertInnerClassPresent(MixedColorUnion.class, "ZserioPackingContext");
    }

    @Test
    public void mixedColorEnumMethods()
    {
        assertMethodPresent(MixedColorEnum.class, "readEnum(zserio.runtime.array.PackingContext");
        assertMethodPresent(MixedColorEnum.class, "initPackingContext");
        assertMethodPresent(MixedColorEnum.class, "bitSizeOf(zserio.runtime.array.PackingContext");
        assertMethodPresent(MixedColorEnum.class, "initializeOffsets(zserio.runtime.array.PackingContext");
        assertMethodPresent(MixedColorEnum.class, "write(zserio.runtime.array.PackingContext");
    }

    @Test
    public void mixedColorBitmaskMethods()
    {
        assertMethodPresent(MixedColorBitmask.class, "MixedColorBitmask(zserio.runtime.array.PackingContext");
        assertMethodPresent(MixedColorBitmask.class, "initPackingContext");
        assertMethodPresent(MixedColorBitmask.class, "bitSizeOf(zserio.runtime.array.PackingContext");
        assertMethodPresent(MixedColorBitmask.class, "initializeOffsets(zserio.runtime.array.PackingContext");
        assertMethodPresent(MixedColorBitmask.class, "write(zserio.runtime.array.PackingContext");
    }

    @Test
    public void packedColorStructMethods()
    {
        assertPackingInterfaceMethodsPresent(PackedColorStruct.class);
        assertInnerClassPresent(PackedColorStruct.class, "ZserioPackingContext");
    }

    @Test
    public void packedColorChoiceMethods()
    {
        assertPackingInterfaceMethodsPresent(PackedColorChoice.class);
        assertInnerClassPresent(PackedColorChoice.class, "ZserioPackingContext");
    }

    @Test
    public void packedColorUnionMethods()
    {
        assertPackingInterfaceMethodsPresent(PackedColorUnion.class);
        assertInnerClassPresent(PackedColorUnion.class, "ZserioPackingContext");
    }

    @Test
    public void packedColorEnumMethods()
    {
        assertMethodPresent(PackedColorEnum.class, "readEnum(zserio.runtime.array.PackingContext");
        assertMethodPresent(PackedColorEnum.class, "initPackingContext");
        assertMethodPresent(PackedColorEnum.class, "bitSizeOf(zserio.runtime.array.PackingContext");
        assertMethodPresent(PackedColorEnum.class, "initializeOffsets(zserio.runtime.array.PackingContext");
        assertMethodPresent(PackedColorEnum.class, "write(zserio.runtime.array.PackingContext");
    }

    @Test
    public void packedColorBitmaskMethods()
    {
        assertMethodPresent(PackedColorBitmask.class, "PackedColorBitmask(zserio.runtime.array.PackingContext");
        assertMethodPresent(PackedColorBitmask.class, "initPackingContext");
        assertMethodPresent(PackedColorBitmask.class, "bitSizeOf(zserio.runtime.array.PackingContext");
        assertMethodPresent(PackedColorBitmask.class, "initializeOffsets(zserio.runtime.array.PackingContext");
        assertMethodPresent(PackedColorBitmask.class, "write(zserio.runtime.array.PackingContext");
    }

    private void assertPackingInterfaceMethodsNotPresent(Class<?> userType)
    {
        assertMethodNotPresent(userType, userType.getSimpleName() + "(zserio.runtime.array.PackingContext");
        assertMethodNotPresent(userType, "initPackingContext");
        assertMethodNotPresent(userType, "bitSizeOf(zserio.runtime.array.PackingContext");
        assertMethodNotPresent(userType, "initializeOffsets(zserio.runtime.array.PackingContext");
        assertMethodNotPresent(userType, "read(zserio.runtime.array.PackingContext");
        assertMethodNotPresent(userType, "write(zserio.runtime.array.PackingContext");
    }

    private void assertPackingInterfaceMethodsPresent(Class<?> userType)
    {
        assertMethodPresent(userType, userType.getSimpleName() + "(zserio.runtime.array.PackingContext");
        assertMethodPresent(userType, "initPackingContext");
        assertMethodPresent(userType, "bitSizeOf(zserio.runtime.array.PackingContext");
        assertMethodPresent(userType, "initializeOffsets(zserio.runtime.array.PackingContext");
        assertMethodPresent(userType, "read(zserio.runtime.array.PackingContext");
        assertMethodPresent(userType, "write(zserio.runtime.array.PackingContext");
    }

    private Class<?> getPrivateInnerClass(Class<?> userType, String innerClassName)
    {
        for (Class<?> innerClass : userType.getDeclaredClasses())
        {
            if (innerClass.getSimpleName().equals(innerClassName))
                return innerClass;
        }

        return null;
    }

    private UnpackedColorStruct[] createUnpackedColors()
    {
        final UnpackedColorChoice unpackedColorChoice1 = new UnpackedColorChoice(true);
        unpackedColorChoice1.setColorName("yellow");
        final UnpackedColorChoice unpackedColorChoice2 = new UnpackedColorChoice(false);
        final UnpackedColorUnion unpackedColorUnion2 = new UnpackedColorUnion();
        unpackedColorUnion2.setColorBitmask(
                UnpackedColorBitmask.Values.GREEN.or(UnpackedColorBitmask.Values.RED));
        unpackedColorChoice2.setColorUnion(unpackedColorUnion2);
        final UnpackedColorChoice unpackedColorChoice3 = new UnpackedColorChoice(false);
        final UnpackedColorUnion unpackedColorUnion3 = new UnpackedColorUnion();
        unpackedColorUnion3.setColorEnum(UnpackedColorEnum.BLUE);
        unpackedColorChoice3.setColorUnion(unpackedColorUnion3);
        return new UnpackedColorStruct[] {
                new UnpackedColorStruct(unpackedColorChoice1.getSelector(), unpackedColorChoice1),
                new UnpackedColorStruct(unpackedColorChoice2.getSelector(), unpackedColorChoice2),
                new UnpackedColorStruct(unpackedColorChoice3.getSelector(), unpackedColorChoice3)};
    }

    private MixedColorStruct[] createMixedColors()
    {
        final MixedColorChoice mixedColorChoice1 = new MixedColorChoice(true);
        mixedColorChoice1.setColorName("purple");
        final MixedColorChoice mixedColorChoice2 = new MixedColorChoice(false);
        final MixedColorUnion mixedColorUnion2 = new MixedColorUnion();
        mixedColorUnion2.setColorBitmask(MixedColorBitmask.Values.BLUE.or(MixedColorBitmask.Values.GREEN));
        mixedColorChoice2.setColorUnion(mixedColorUnion2);
        final MixedColorChoice mixedColorChoice3 = new MixedColorChoice(false);
        final MixedColorUnion mixedColorUnion3 = new MixedColorUnion();
        mixedColorUnion3.setColorEnum(MixedColorEnum.RED);
        mixedColorChoice3.setColorUnion(mixedColorUnion3);
        return new MixedColorStruct[] {new MixedColorStruct(mixedColorChoice1.getSelector(), mixedColorChoice1),
                new MixedColorStruct(mixedColorChoice2.getSelector(), mixedColorChoice2),
                new MixedColorStruct(mixedColorChoice3.getSelector(), mixedColorChoice3)};
    }

    private PackedColorStruct[] createPackedColors()
    {
        final PackedColorChoice packedColorChoice1 = new PackedColorChoice(true);
        packedColorChoice1.setColorName("grey");
        final PackedColorChoice packedColorChoice2 = new PackedColorChoice(false);
        final PackedColorUnion packedColorUnion2 = new PackedColorUnion();
        packedColorUnion2.setColorBitmask(PackedColorBitmask.Values.BLUE.or(PackedColorBitmask.Values.RED));
        packedColorChoice2.setColorUnion(packedColorUnion2);
        final PackedColorChoice packedColorChoice3 = new PackedColorChoice(false);
        final PackedColorUnion packedColorUnion3 = new PackedColorUnion();
        packedColorUnion3.setColorEnum(PackedColorEnum.GREEN);
        packedColorChoice3.setColorUnion(packedColorUnion3);
        return new PackedColorStruct[] {
                new PackedColorStruct(packedColorChoice1.getSelector(), packedColorChoice1),
                new PackedColorStruct(packedColorChoice2.getSelector(), packedColorChoice2),
                new PackedColorStruct(packedColorChoice3.getSelector(), packedColorChoice3)};
    }

    private UnpackedColorsHolder createUnpackedColorsHolder()
    {
        return new UnpackedColorsHolder(createUnpackedColors(), createMixedColors());
    }

    private PackedColorsHolder createPackedColorsHolder()
    {
        return new PackedColorsHolder(createMixedColors(), createPackedColors());
    }

    private static final String BLOB_NAME = "packing_interface_optimization.blob";
}
