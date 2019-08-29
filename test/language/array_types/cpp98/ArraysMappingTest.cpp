#include "gtest/gtest.h"

#include "array_types/arrays_mapping/ArraysMapping.h"

namespace array_types
{
namespace arrays_mapping
{

class ArraysMappingTest : public ::testing::Test
{
protected:
    static const size_t fixedArrayLength = 5;
};

TEST_F(ArraysMappingTest, unsignedIntegerArrays)
{
    ArraysMapping arraysMapping;

    arraysMapping.setUint8Array(zserio::UInt8Array(fixedArrayLength));
    arraysMapping.setUint16Array(zserio::UInt16Array(fixedArrayLength));
    arraysMapping.setUint32Array(zserio::UInt32Array(fixedArrayLength));
    arraysMapping.setUint64Array(zserio::UInt64Array(fixedArrayLength));
}

TEST_F(ArraysMappingTest, signedIntegerArrays)
{
    ArraysMapping arraysMapping;

    arraysMapping.setInt8Array(zserio::Int8Array(fixedArrayLength));
    arraysMapping.setInt16Array(zserio::Int16Array(fixedArrayLength));
    arraysMapping.setInt32Array(zserio::Int32Array(fixedArrayLength));
    arraysMapping.setInt64Array(zserio::Int64Array(fixedArrayLength));
}

TEST_F(ArraysMappingTest, unsignedBitfieldArrays)
{
    ArraysMapping arraysMapping;

    arraysMapping.setBitfield8Array(zserio::UInt8Array(fixedArrayLength));
    arraysMapping.setBitfield16Array(zserio::UInt16Array(fixedArrayLength));
    arraysMapping.setBitfield32Array(zserio::UInt32Array(fixedArrayLength));
    arraysMapping.setBitfield63Array(zserio::UInt64Array(fixedArrayLength));
    arraysMapping.setUint8Value(8);
    arraysMapping.setVariableBitfieldLongArray(zserio::UInt64Array(fixedArrayLength));
    arraysMapping.setVariableBitfieldIntArray(zserio::UInt32Array(fixedArrayLength));
    arraysMapping.setVariableBitfieldShortArray(zserio::UInt16Array(fixedArrayLength));
    arraysMapping.setVariableBitfieldByteArray(zserio::UInt8Array(fixedArrayLength));
}

TEST_F(ArraysMappingTest, signedBitfieldArrays)
{
    ArraysMapping arraysMapping;

    arraysMapping.setIntfield8Array(zserio::Int8Array(fixedArrayLength));
    arraysMapping.setIntfield16Array(zserio::Int16Array(fixedArrayLength));
    arraysMapping.setIntfield32Array(zserio::Int32Array(fixedArrayLength));
    arraysMapping.setIntfield64Array(zserio::Int64Array(fixedArrayLength));
    arraysMapping.setUint8Value(8);
    arraysMapping.setVariableIntfieldLongArray(zserio::Int64Array(fixedArrayLength));
    arraysMapping.setVariableIntfieldIntArray(zserio::Int32Array(fixedArrayLength));
    arraysMapping.setVariableIntfieldShortArray(zserio::Int16Array(fixedArrayLength));
    arraysMapping.setVariableIntfieldByteArray(zserio::Int8Array(fixedArrayLength));
}

TEST_F(ArraysMappingTest, float16Array)
{
    ArraysMapping arraysMapping;
    arraysMapping.setFloat16Array(zserio::Float16Array(fixedArrayLength));
    arraysMapping.setFloat32Array(zserio::Float32Array(fixedArrayLength));
    arraysMapping.setFloat64Array(zserio::Float64Array(fixedArrayLength));
}

TEST_F(ArraysMappingTest, variableUnsignedIntegerArrays)
{
    ArraysMapping arraysMapping;

    arraysMapping.setVaruint16Array(zserio::VarUInt16Array(fixedArrayLength));
    arraysMapping.setVaruint32Array(zserio::VarUInt32Array(fixedArrayLength));
    arraysMapping.setVaruint64Array(zserio::VarUInt64Array(fixedArrayLength));
    arraysMapping.setVaruintArray(zserio::VarUIntArray(fixedArrayLength));
}

TEST_F(ArraysMappingTest, variableSignedIntegerArrays)
{
    ArraysMapping arraysMapping;

    arraysMapping.setVarint16Array(zserio::VarInt16Array(fixedArrayLength));
    arraysMapping.setVarint32Array(zserio::VarInt32Array(fixedArrayLength));
    arraysMapping.setVarint64Array(zserio::VarInt64Array(fixedArrayLength));
    arraysMapping.setVarintArray(zserio::VarIntArray(fixedArrayLength));
}

TEST_F(ArraysMappingTest, boolArray)
{
    ArraysMapping arraysMapping;
    arraysMapping.setBoolArray(zserio::BoolArray(fixedArrayLength));
}

TEST_F(ArraysMappingTest, stringArrays)
{
    ArraysMapping arraysMapping;
    arraysMapping.setStringArray(zserio::StringArray(fixedArrayLength));
}

TEST_F(ArraysMappingTest, compoundArray)
{
    ArraysMapping arraysMapping;
    arraysMapping.setCompoundArray(zserio::ObjectArray<TestStructure>(fixedArrayLength));
}

TEST_F(ArraysMappingTest, enumArray)
{
    ArraysMapping arraysMapping;
    arraysMapping.setEnumArray(zserio::ObjectArray<TestEnum>(fixedArrayLength));
}

} // namespace arrays_mapping
} // namespace array_types
