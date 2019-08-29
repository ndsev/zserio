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

    arraysMapping.setUint8Array(std::vector<uint8_t>(fixedArrayLength));
    arraysMapping.setUint16Array(std::vector<uint16_t>(fixedArrayLength));
    arraysMapping.setUint32Array(std::vector<uint32_t>(fixedArrayLength));
    arraysMapping.setUint64Array(std::vector<uint64_t>(fixedArrayLength));
}

TEST_F(ArraysMappingTest, signedIntegerArrays)
{
    ArraysMapping arraysMapping;

    arraysMapping.setInt8Array(std::vector<int8_t>(fixedArrayLength));
    arraysMapping.setInt16Array(std::vector<int16_t>(fixedArrayLength));
    arraysMapping.setInt32Array(std::vector<int32_t>(fixedArrayLength));
    arraysMapping.setInt64Array(std::vector<int64_t>(fixedArrayLength));
}

TEST_F(ArraysMappingTest, unsignedBitfieldArrays)
{
    ArraysMapping arraysMapping;

    arraysMapping.setBitfield8Array(std::vector<uint8_t>(fixedArrayLength));
    arraysMapping.setBitfield16Array(std::vector<uint16_t>(fixedArrayLength));
    arraysMapping.setBitfield32Array(std::vector<uint32_t>(fixedArrayLength));
    arraysMapping.setBitfield63Array(std::vector<uint64_t>(fixedArrayLength));
    arraysMapping.setUint8Value(8);
    arraysMapping.setVariableBitfieldLongArray(std::vector<uint64_t>(fixedArrayLength));
    arraysMapping.setVariableBitfieldIntArray(std::vector<uint32_t>(fixedArrayLength));
    arraysMapping.setVariableBitfieldShortArray(std::vector<uint16_t>(fixedArrayLength));
    arraysMapping.setVariableBitfieldByteArray(std::vector<uint8_t>(fixedArrayLength));
}

TEST_F(ArraysMappingTest, signedBitfieldArrays)
{
    ArraysMapping arraysMapping;

    arraysMapping.setIntfield8Array(std::vector<int8_t>(fixedArrayLength));
    arraysMapping.setIntfield16Array(std::vector<int16_t>(fixedArrayLength));
    arraysMapping.setIntfield32Array(std::vector<int32_t>(fixedArrayLength));
    arraysMapping.setIntfield64Array(std::vector<int64_t>(fixedArrayLength));
    arraysMapping.setUint8Value(8);
    arraysMapping.setVariableIntfieldLongArray(std::vector<int64_t>(fixedArrayLength));
    arraysMapping.setVariableIntfieldIntArray(std::vector<int32_t>(fixedArrayLength));
    arraysMapping.setVariableIntfieldShortArray(std::vector<int16_t>(fixedArrayLength));
    arraysMapping.setVariableIntfieldByteArray(std::vector<int8_t>(fixedArrayLength));
}

TEST_F(ArraysMappingTest, float16Array)
{
    ArraysMapping arraysMapping;
    arraysMapping.setFloat16Array(std::vector<float>(fixedArrayLength));
    arraysMapping.setFloat32Array(std::vector<float>(fixedArrayLength));
    arraysMapping.setFloat64Array(std::vector<double>(fixedArrayLength));
}

TEST_F(ArraysMappingTest, variableUnsignedIntegerArrays)
{
    ArraysMapping arraysMapping;

    arraysMapping.setVaruint16Array(std::vector<uint16_t>(fixedArrayLength));
    arraysMapping.setVaruint32Array(std::vector<uint32_t>(fixedArrayLength));
    arraysMapping.setVaruint64Array(std::vector<uint64_t>(fixedArrayLength));
    arraysMapping.setVaruintArray(std::vector<uint64_t>(fixedArrayLength));
}

TEST_F(ArraysMappingTest, variableSignedIntegerArrays)
{
    ArraysMapping arraysMapping;

    arraysMapping.setVarint16Array(std::vector<int16_t>(fixedArrayLength));
    arraysMapping.setVarint32Array(std::vector<int32_t>(fixedArrayLength));
    arraysMapping.setVarint64Array(std::vector<int64_t>(fixedArrayLength));
    arraysMapping.setVarintArray(std::vector<int64_t>(fixedArrayLength));
}

TEST_F(ArraysMappingTest, boolArray)
{
    ArraysMapping arraysMapping;
    arraysMapping.setBoolArray(std::vector<bool>(fixedArrayLength));
}

TEST_F(ArraysMappingTest, stringArrays)
{
    ArraysMapping arraysMapping;
    arraysMapping.setStringArray(std::vector<std::string>(fixedArrayLength));
}

TEST_F(ArraysMappingTest, compoundArray)
{
    ArraysMapping arraysMapping;
    arraysMapping.setCompoundArray(std::vector<TestStructure>(fixedArrayLength));
}

TEST_F(ArraysMappingTest, enumArray)
{
    ArraysMapping arraysMapping;
    arraysMapping.setEnumArray(std::vector<TestEnum>(fixedArrayLength));
}

} // namespace arrays_mapping
} // namespace array_types
