#include "gtest/gtest.h"

#include "array_types/arrays_mapping/ArraysMapping.h"

#include "zserio/RebindAlloc.h"

namespace array_types
{
namespace arrays_mapping
{

using allocator_type = ArraysMapping::allocator_type;
using string_type = zserio::string<zserio::RebindAlloc<allocator_type, char>>;
template <typename T>
using vector_type = std::vector<T, zserio::RebindAlloc<allocator_type, T>>;

using BitBuffer = zserio::BasicBitBuffer<zserio::RebindAlloc<allocator_type, uint8_t>>;

class ArraysMappingTest : public ::testing::Test
{
protected:
    static const size_t fixedArrayLength = 5;
};

TEST_F(ArraysMappingTest, unsignedIntegerArrays)
{
    ArraysMapping arraysMapping;

    arraysMapping.setUint8Array(vector_type<uint8_t>(fixedArrayLength));
    arraysMapping.setUint16Array(vector_type<uint16_t>(fixedArrayLength));
    arraysMapping.setUint32Array(vector_type<uint32_t>(fixedArrayLength));
    arraysMapping.setUint64Array(vector_type<uint64_t>(fixedArrayLength));
}

TEST_F(ArraysMappingTest, signedIntegerArrays)
{
    ArraysMapping arraysMapping;

    arraysMapping.setInt8Array(vector_type<int8_t>(fixedArrayLength));
    arraysMapping.setInt16Array(vector_type<int16_t>(fixedArrayLength));
    arraysMapping.setInt32Array(vector_type<int32_t>(fixedArrayLength));
    arraysMapping.setInt64Array(vector_type<int64_t>(fixedArrayLength));
}

TEST_F(ArraysMappingTest, unsignedBitfieldArrays)
{
    ArraysMapping arraysMapping;

    arraysMapping.setBitfield8Array(vector_type<uint8_t>(fixedArrayLength));
    arraysMapping.setBitfield16Array(vector_type<uint16_t>(fixedArrayLength));
    arraysMapping.setBitfield32Array(vector_type<uint32_t>(fixedArrayLength));
    arraysMapping.setBitfield63Array(vector_type<uint64_t>(fixedArrayLength));
    arraysMapping.setUint8Value(8);
    arraysMapping.setVariableBitfieldLongArray(vector_type<uint64_t>(fixedArrayLength));
    arraysMapping.setVariableBitfieldIntArray(vector_type<uint32_t>(fixedArrayLength));
    arraysMapping.setVariableBitfieldShortArray(vector_type<uint16_t>(fixedArrayLength));
    arraysMapping.setVariableBitfieldByteArray(vector_type<uint8_t>(fixedArrayLength));
}

TEST_F(ArraysMappingTest, signedBitfieldArrays)
{
    ArraysMapping arraysMapping;

    arraysMapping.setIntfield8Array(vector_type<int8_t>(fixedArrayLength));
    arraysMapping.setIntfield16Array(vector_type<int16_t>(fixedArrayLength));
    arraysMapping.setIntfield32Array(vector_type<int32_t>(fixedArrayLength));
    arraysMapping.setIntfield64Array(vector_type<int64_t>(fixedArrayLength));
    arraysMapping.setUint8Value(8);
    arraysMapping.setVariableIntfieldLongArray(vector_type<int64_t>(fixedArrayLength));
    arraysMapping.setVariableIntfieldIntArray(vector_type<int32_t>(fixedArrayLength));
    arraysMapping.setVariableIntfieldShortArray(vector_type<int16_t>(fixedArrayLength));
    arraysMapping.setVariableIntfieldByteArray(vector_type<int8_t>(fixedArrayLength));
}

TEST_F(ArraysMappingTest, float16Array)
{
    ArraysMapping arraysMapping;
    arraysMapping.setFloat16Array(vector_type<float>(fixedArrayLength));
    arraysMapping.setFloat32Array(vector_type<float>(fixedArrayLength));
    arraysMapping.setFloat64Array(vector_type<double>(fixedArrayLength));
}

TEST_F(ArraysMappingTest, variableUnsignedIntegerArrays)
{
    ArraysMapping arraysMapping;

    arraysMapping.setVaruint16Array(vector_type<uint16_t>(fixedArrayLength));
    arraysMapping.setVaruint32Array(vector_type<uint32_t>(fixedArrayLength));
    arraysMapping.setVaruint64Array(vector_type<uint64_t>(fixedArrayLength));
    arraysMapping.setVaruintArray(vector_type<uint64_t>(fixedArrayLength));
    arraysMapping.setVarsizeArray(vector_type<uint32_t>(fixedArrayLength));
}

TEST_F(ArraysMappingTest, variableSignedIntegerArrays)
{
    ArraysMapping arraysMapping;

    arraysMapping.setVarint16Array(vector_type<int16_t>(fixedArrayLength));
    arraysMapping.setVarint32Array(vector_type<int32_t>(fixedArrayLength));
    arraysMapping.setVarint64Array(vector_type<int64_t>(fixedArrayLength));
    arraysMapping.setVarintArray(vector_type<int64_t>(fixedArrayLength));
}

TEST_F(ArraysMappingTest, boolArray)
{
    ArraysMapping arraysMapping;
    arraysMapping.setBoolArray(vector_type<bool>(fixedArrayLength));
}

TEST_F(ArraysMappingTest, stringArray)
{
    ArraysMapping arraysMapping;
    arraysMapping.setStringArray(vector_type<string_type>(fixedArrayLength));
}

TEST_F(ArraysMappingTest, externArray)
{
    ArraysMapping arraysMapping;
    arraysMapping.setExternArray(vector_type<BitBuffer>(fixedArrayLength));
}

TEST_F(ArraysMappingTest, compoundArray)
{
    ArraysMapping arraysMapping;
    arraysMapping.setCompoundArray(vector_type<TestStructure>(fixedArrayLength));
}

TEST_F(ArraysMappingTest, enumArray)
{
    ArraysMapping arraysMapping;
    arraysMapping.setEnumArray(vector_type<TestEnum>(fixedArrayLength));
}

TEST_F(ArraysMappingTest, bitmaskArray)
{
    ArraysMapping arraysMapping;
    arraysMapping.setBitmaskArray(vector_type<TestBitmask>(fixedArrayLength));
}

} // namespace arrays_mapping
} // namespace array_types
