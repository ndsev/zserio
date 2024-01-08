package with_type_info_code;

import java.math.BigInteger;

import zserio.runtime.io.BitBuffer;

public class WithTypeInfoCodeCreator
{
    public static WithTypeInfoCode createWithTypeInfoCode()
    {
        return createWithTypeInfoCode(true);
    }

    public static WithTypeInfoCode createWithTypeInfoCode(boolean createOptionals)
    {
        final SimpleStruct simpleStruct = createSimpleStruct();
        final TestEnum testEnum = TestEnum._TWO;
        final TS32 ts32 = createTS32();
        final WithTypeInfoCode withTypeInfoCode = new WithTypeInfoCode(simpleStruct,
                createComplexStruct(createOptionals), createParameterizedStruct(simpleStruct),
                createRecursiveStruct(), createRecursiveUnion(), createRecursiveChoice(true, false), testEnum,
                createSimpleChoice(testEnum), ts32, createTemplatedParameterizedStruct_TS32(ts32),
                createExternData(), new BitBuffer[] {createExternData(), createExternData()}, createBytesData(),
                new byte[][] {createBytesData(), createBytesData()}, new long[] {1, 4, 6, 4, 6, 1});

        return withTypeInfoCode;
    }

    private static SimpleStruct createSimpleStruct()
    {
        final SimpleStruct simpleStruct = new SimpleStruct();
        simpleStruct.setFieldOffset(0);
        simpleStruct.setFieldFloat32(4.0f);

        return simpleStruct;
    }

    private static ComplexStruct createComplexStruct(boolean createOptionals)
    {
        final SimpleStruct simpleStruct = createSimpleStruct();
        final SimpleStruct anotherSimpleStruct = createSimpleStruct();
        final BigInteger[] dynamicBitFieldArray = new BigInteger[65536 / 2];
        for (int i = 0; i < dynamicBitFieldArray.length; i++)
            dynamicBitFieldArray[i] = BigInteger.valueOf(2 * i + 1);

        final ComplexStruct complexStruct = new ComplexStruct(simpleStruct, anotherSimpleStruct,
                (createOptionals) ? createSimpleStruct() : null,
                new long[] {3, 0xABCD2, 0xABCD3, 0xABCD4, 0xABCD5}, new byte[] {3, 2, 1},
                (createOptionals) ? new ParameterizedStruct[] {createParameterizedStruct(simpleStruct),
                                            createParameterizedStruct(anotherSimpleStruct)}
                                  : null,
                BigInteger.valueOf(8), dynamicBitFieldArray, (createOptionals) ? TestEnum.ItemThree : null,
                (createOptionals)
                        ? TestBitmask.Values.RED.or(TestBitmask.Values._Green).or(TestBitmask.Values.ColorBlue)
                        : null,
                (createOptionals) ? new BitBuffer(new byte[] {(byte)0xCB, (byte)0xF0}, 12) : null,
                (createOptionals) ? createBytesData() : null,
                new TestEnum[] {TestEnum._TWO, TestEnum.ItemThree},
                new TestBitmask[] {TestBitmask.Values._Green, TestBitmask.Values._Green,
                        TestBitmask.Values._Green, TestBitmask.Values._Green, TestBitmask.Values._Green});

        return complexStruct;
    }

    private static ParameterizedStruct createParameterizedStruct(SimpleStruct simpleStruct)
    {
        final short[] array = new short[(int)simpleStruct.getFieldU32()];
        for (int i = 0; i < array.length; i++)
            array[i] = (short)i;
        final ParameterizedStruct parameterizedStruct = new ParameterizedStruct(simpleStruct, array);

        return parameterizedStruct;
    }

    private static RecursiveStruct createRecursiveStruct()
    {
        final RecursiveStruct recursiveStruct = new RecursiveStruct(0xDEAD1,
                new RecursiveStruct(0xDEAD2, null, new RecursiveStruct[0]),
                new RecursiveStruct[] {new RecursiveStruct(0xDEAD3, null, new RecursiveStruct[0]),
                        new RecursiveStruct(0xDEAD4, null, new RecursiveStruct[0])});

        return recursiveStruct;
    }

    private static RecursiveUnion createRecursiveUnion()
    {
        final RecursiveUnion recursiveUnionFieldU32 = new RecursiveUnion();
        recursiveUnionFieldU32.setFieldU32(0xDEAD);
        final RecursiveUnion[] recursive = new RecursiveUnion[] {recursiveUnionFieldU32};
        final RecursiveUnion recursiveUnion = new RecursiveUnion();
        recursiveUnion.setRecursive(recursive);

        return recursiveUnion;
    }

    private static RecursiveChoice createRecursiveChoice(boolean param1, boolean param2)
    {
        final RecursiveChoice recursiveChoice = new RecursiveChoice(param1, param2);
        if (param1)
        {
            final RecursiveChoice recursiveChoiceFalse = createRecursiveChoice(param2, false);
            final RecursiveChoice[] recursive = new RecursiveChoice[] {recursiveChoiceFalse};
            recursiveChoice.setRecursive(recursive);
        }
        else
        {
            recursiveChoice.setFieldU32(0xDEAD);
        }

        return recursiveChoice;
    }

    private static SimpleUnion createSimpleUnion()
    {
        final SimpleUnion simpleUnion = new SimpleUnion();
        simpleUnion.setTestBitmask(TestBitmask.Values._Green);

        return simpleUnion;
    }

    private static SimpleChoice createSimpleChoice(TestEnum testEnum)
    {
        final SimpleChoice simpleChoice = new SimpleChoice(testEnum);
        if (testEnum == TestEnum._TWO)
            simpleChoice.setFieldTwo(createSimpleUnion());
        else
            simpleChoice.setFieldDefault("text");

        return simpleChoice;
    }

    private static TS32 createTS32()
    {
        final TS32 ts32 = new TS32(0xDEAD);

        return ts32;
    }

    private static TemplatedParameterizedStruct_TS32 createTemplatedParameterizedStruct_TS32(TS32 ts32)
    {
        final long[] array = new long[(int)ts32.getField()];
        for (int i = array.length; i > 0; --i)
            array[array.length - i] = i;
        final TemplatedParameterizedStruct_TS32 templatedParameterizedStruct_TS32 =
                new TemplatedParameterizedStruct_TS32(ts32, array);

        return templatedParameterizedStruct_TS32;
    }

    private static BitBuffer createExternData()
    {
        return new BitBuffer(new byte[] {(byte)0xCA, (byte)0xFE}, 15);
    }

    private static byte[] createBytesData()
    {
        return new byte[] {(byte)0xAB, (byte)0xCD};
    }
}
