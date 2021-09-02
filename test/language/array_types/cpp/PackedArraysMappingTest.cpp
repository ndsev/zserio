#include "gtest/gtest.h"

#include "array_types/packed_arrays_mapping/PackedArraysMapping.h"

#include "zserio/RebindAlloc.h"

namespace array_types
{
namespace packed_arrays_mapping
{

using allocator_type = PackedArraysMapping::allocator_type;
using string_type = zserio::string<zserio::RebindAlloc<allocator_type, char>>;
template <typename T>
using vector_type = std::vector<T, zserio::RebindAlloc<allocator_type, T>>;

using BitBuffer = zserio::BasicBitBuffer<zserio::RebindAlloc<allocator_type, uint8_t>>;

class PackedArraysMappingTest : public ::testing::Test
{
protected:
    static const size_t fixedArrayLength = 5;
};

TEST_F(PackedArraysMappingTest, unsignedIntegerArrays)
{
    PackedArraysMapping packedArraysMapping;

    packedArraysMapping.setUint8Array(vector_type<uint8_t>(fixedArrayLength));
    packedArraysMapping.setUint16Array(vector_type<uint16_t>(fixedArrayLength));
    packedArraysMapping.setUint32Array(vector_type<uint32_t>(fixedArrayLength));
    packedArraysMapping.setUint64Array(vector_type<uint64_t>(fixedArrayLength));
}

TEST_F(PackedArraysMappingTest, signedIntegerArrays)
{
    PackedArraysMapping packedArraysMapping;

    packedArraysMapping.setInt8Array(vector_type<int8_t>(fixedArrayLength));
    packedArraysMapping.setInt16Array(vector_type<int16_t>(fixedArrayLength));
    packedArraysMapping.setInt32Array(vector_type<int32_t>(fixedArrayLength));
    packedArraysMapping.setInt64Array(vector_type<int64_t>(fixedArrayLength));
}

TEST_F(PackedArraysMappingTest, unsignedBitfieldArrays)
{
    PackedArraysMapping packedArraysMapping;

    packedArraysMapping.setBitfield8Array(vector_type<uint8_t>(fixedArrayLength));
    packedArraysMapping.setBitfield16Array(vector_type<uint16_t>(fixedArrayLength));
    packedArraysMapping.setBitfield32Array(vector_type<uint32_t>(fixedArrayLength));
    packedArraysMapping.setBitfield63Array(vector_type<uint64_t>(fixedArrayLength));
    packedArraysMapping.setUint8Value(8);
    packedArraysMapping.setVariableBitfieldLongArray(vector_type<uint64_t>(fixedArrayLength));
}

TEST_F(PackedArraysMappingTest, signedBitfieldArrays)
{
    PackedArraysMapping packedArraysMapping;

    packedArraysMapping.setIntfield8Array(vector_type<int8_t>(fixedArrayLength));
    packedArraysMapping.setIntfield16Array(vector_type<int16_t>(fixedArrayLength));
    packedArraysMapping.setIntfield32Array(vector_type<int32_t>(fixedArrayLength));
    packedArraysMapping.setIntfield64Array(vector_type<int64_t>(fixedArrayLength));
    packedArraysMapping.setUint8Value(8);
    packedArraysMapping.setVariableIntfieldLongArray(vector_type<int64_t>(fixedArrayLength));
}

TEST_F(PackedArraysMappingTest, variableUnsignedIntegerArrays)
{
    PackedArraysMapping packedArraysMapping;

    packedArraysMapping.setVaruint16Array(vector_type<uint16_t>(fixedArrayLength));
    packedArraysMapping.setVaruint32Array(vector_type<uint32_t>(fixedArrayLength));
    packedArraysMapping.setVaruint64Array(vector_type<uint64_t>(fixedArrayLength));
    packedArraysMapping.setVaruintArray(vector_type<uint64_t>(fixedArrayLength));
    packedArraysMapping.setVarsizeArray(vector_type<uint32_t>(fixedArrayLength));
}

TEST_F(PackedArraysMappingTest, variableSignedIntegerArrays)
{
    PackedArraysMapping packedArraysMapping;

    packedArraysMapping.setVarint16Array(vector_type<int16_t>(fixedArrayLength));
    packedArraysMapping.setVarint32Array(vector_type<int32_t>(fixedArrayLength));
    packedArraysMapping.setVarint64Array(vector_type<int64_t>(fixedArrayLength));
    packedArraysMapping.setVarintArray(vector_type<int64_t>(fixedArrayLength));
}

TEST_F(PackedArraysMappingTest, compoundArray)
{
    PackedArraysMapping packedArraysMapping;
    packedArraysMapping.setCompoundArray(vector_type<TestStructure>(fixedArrayLength));
}

TEST_F(PackedArraysMappingTest, enumArray)
{
    PackedArraysMapping packedArraysMapping;
    packedArraysMapping.setEnumArray(vector_type<TestEnum>(fixedArrayLength));
}

TEST_F(PackedArraysMappingTest, bitmaskArray)
{
    PackedArraysMapping packedArraysMapping;
    packedArraysMapping.setBitmaskArray(vector_type<TestBitmask>(fixedArrayLength));
}

} // namespace packed_arrays_mapping
} // namespace array_types
