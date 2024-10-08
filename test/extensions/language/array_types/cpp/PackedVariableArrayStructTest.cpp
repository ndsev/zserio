#include "array_types/packed_variable_array_struct/PackedVariableArray.h"
#include "gtest/gtest.h"
#include "zserio/SerializeUtil.h"

namespace array_types
{
namespace packed_variable_array_struct
{

using allocator_type = PackedVariableArray::allocator_type;
template <typename T>
using vector_type = zserio::vector<T, allocator_type>;

using BitBuffer = zserio::BasicBitBuffer<zserio::RebindAlloc<allocator_type, uint8_t>>;

class PackedVariableArrayStructTest : public ::testing::Test
{
protected:
    void fillPackedVariableArray(PackedVariableArray& packedVariableArray, uint32_t numElements)
    {
        vector_type<TestStructure> testStructureArray;
        fillTestStructureArray(testStructureArray, numElements);

        packedVariableArray.setNumElements(numElements);
        packedVariableArray.setTestUnpackedArray(TestUnpackedArray{testStructureArray});
        packedVariableArray.setTestPackedArray(TestPackedArray{testStructureArray});

        packedVariableArray.initializeChildren();
        packedVariableArray.initializeOffsets();
    }

    void fillTestStructureArray(vector_type<TestStructure>& testStructureArray, uint32_t numElements)
    {
        testStructureArray.reserve(numElements);
        for (uint32_t i = 0; i < numElements; ++i)
        {
            testStructureArray.push_back(createTestStructure(i));
        }
    }

    TestStructure createTestStructure(uint32_t index)
    {
        TestStructure testStructure;
        testStructure.setId(index);
        testStructure.setName("name" + zserio::toString<allocator_type>(index));
        testStructure.setData(BitBuffer{vector_type<uint8_t>{{0xCD, 0xC0}}, 10});
        testStructure.setBytesData(vector_type<uint8_t>{{0xCD, 0xC0}});
        testStructure.setTestChoice(createTestChoice(index));
        testStructure.setTestUnion(createTestUnion(index));
        testStructure.setTestEnum(index % 2 == 0 ? TestEnum::DARK_RED : TestEnum::DARK_GREEN);
        testStructure.setTestBitmask(index % 2 == 0 ? TestBitmask::Values::READ : TestBitmask::Values::CREATE);
        if (index % 2 == 0)
        {
            testStructure.setTestOptional(static_cast<uint16_t>(index));
        }
        testStructure.setTestDynamicBitfield(index % 3);
        vector_type<uint64_t> values;
        for (uint64_t value = 1; value < 18; value += 3)
        {
            values.push_back(value);
        }
        testStructure.setNumValues(static_cast<uint32_t>(values.size()));
        testStructure.setUnpackedValues(values);
        testStructure.setPackedValues(values);
        vector_type<Empty> empties(values.size());
        testStructure.setPackedEmpties(empties);

        return testStructure;
    }

    TestChoice createTestChoice(uint32_t index)
    {
        TestChoice testChoice;
        if (index == 0 || index == 2 || index == 4)
        {
            testChoice.setValue16(static_cast<uint16_t>(index));
        }
        else if (index == 5)
        {
            testChoice.setArray32(vector_type<uint32_t>{index * 2, index * 2 + 1});
        }
        else
        {
            testChoice.setValue32(Value32{index * 2});
        }

        return testChoice;
    }

    TestUnion createTestUnion(uint32_t index)
    {
        TestUnion testUnion;
        if (index % 2 == 0)
        {
            testUnion.setValue16(static_cast<uint16_t>(index));
        }
        else if (index == 5)
        {
            testUnion.setArray32(vector_type<uint32_t>{index * 2, index * 2 + 1});
        }
        else
        {
            testUnion.setValue32(Value32{index * 2});
        }

        return testUnion;
    }

    void checkBitSizeOf(uint32_t numElements)
    {
        PackedVariableArray packedVariableArray;
        fillPackedVariableArray(packedVariableArray, numElements);

        const double unpackedBitSize =
                static_cast<double>(packedVariableArray.getTestUnpackedArray().bitSizeOf());
        const double packedBitSize = static_cast<double>(packedVariableArray.getTestPackedArray().bitSizeOf());
        const double minCompressionRatio = 0.622;
        ASSERT_GT(unpackedBitSize * minCompressionRatio, packedBitSize)
                << "Unpacked array has " << std::to_string(unpackedBitSize) << " bits, "
                << "packed array has " << std::to_string(packedBitSize) << " bits, "
                << "compression ratio is " << std::to_string(packedBitSize / unpackedBitSize * 100) << "%!";
    }

    void checkWriteRead(uint32_t numElements)
    {
        PackedVariableArray packedVariableArray;
        fillPackedVariableArray(packedVariableArray, numElements);

        zserio::BitStreamWriter writer(bitBuffer);
        packedVariableArray.write(writer);

        ASSERT_EQ(packedVariableArray.bitSizeOf(), writer.getBitPosition());
        ASSERT_EQ(packedVariableArray.initializeOffsets(), writer.getBitPosition());

        zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
        PackedVariableArray readPackedVariableArray(reader);
        ASSERT_EQ(packedVariableArray, readPackedVariableArray);
    }

    void checkWriteReadFile(uint32_t numElements)
    {
        PackedVariableArray packedVariableArray;
        fillPackedVariableArray(packedVariableArray, numElements);

        const std::string fileName = BLOB_NAME_BASE + std::to_string(numElements) + ".blob";
        zserio::serializeToFile(packedVariableArray, fileName);

        ASSERT_EQ(packedVariableArray.bitSizeOf(), packedVariableArray.initializeOffsets());

        auto readPackedVariableArray = zserio::deserializeFromFile<PackedVariableArray>(fileName);
        ASSERT_EQ(packedVariableArray, readPackedVariableArray);
    }

    static const std::string BLOB_NAME_BASE;
    static const uint32_t VARIABLE_ARRAY_LENGTH1;
    static const uint32_t VARIABLE_ARRAY_LENGTH2;
    static const uint32_t VARIABLE_ARRAY_LENGTH3;
    static const uint32_t VARIABLE_ARRAY_LENGTH4;
    zserio::BitBuffer bitBuffer = zserio::BitBuffer(70 * 1024 * 8);
};

const std::string PackedVariableArrayStructTest::BLOB_NAME_BASE =
        "language/array_types/packed_variable_array_struct_";

const uint32_t PackedVariableArrayStructTest::VARIABLE_ARRAY_LENGTH1 = 25;
const uint32_t PackedVariableArrayStructTest::VARIABLE_ARRAY_LENGTH2 = 50;
const uint32_t PackedVariableArrayStructTest::VARIABLE_ARRAY_LENGTH3 = 100;
const uint32_t PackedVariableArrayStructTest::VARIABLE_ARRAY_LENGTH4 = 1000;

TEST_F(PackedVariableArrayStructTest, bitSizeOfLength1)
{
    checkBitSizeOf(VARIABLE_ARRAY_LENGTH1);
}

TEST_F(PackedVariableArrayStructTest, bitSizeOfLength2)
{
    checkBitSizeOf(VARIABLE_ARRAY_LENGTH2);
}

TEST_F(PackedVariableArrayStructTest, bitSizeOfLength3)
{
    checkBitSizeOf(VARIABLE_ARRAY_LENGTH3);
}

TEST_F(PackedVariableArrayStructTest, bitSizeOfLength4)
{
    checkBitSizeOf(VARIABLE_ARRAY_LENGTH4);
}

TEST_F(PackedVariableArrayStructTest, writeReadLength1)
{
    checkWriteRead(VARIABLE_ARRAY_LENGTH1);
}

TEST_F(PackedVariableArrayStructTest, writeReadLength2)
{
    checkWriteRead(VARIABLE_ARRAY_LENGTH2);
}

TEST_F(PackedVariableArrayStructTest, writeReadLength3)
{
    checkWriteRead(VARIABLE_ARRAY_LENGTH3);
}

TEST_F(PackedVariableArrayStructTest, writeReadLength4)
{
    checkWriteRead(VARIABLE_ARRAY_LENGTH4);
}

TEST_F(PackedVariableArrayStructTest, writeReadFileLength1)
{
    checkWriteReadFile(VARIABLE_ARRAY_LENGTH1);
}

TEST_F(PackedVariableArrayStructTest, writeReadFileLength2)
{
    checkWriteReadFile(VARIABLE_ARRAY_LENGTH2);
}

TEST_F(PackedVariableArrayStructTest, writeReadFileLength3)
{
    checkWriteReadFile(VARIABLE_ARRAY_LENGTH3);
}

TEST_F(PackedVariableArrayStructTest, writeReadFileLength4)
{
    checkWriteReadFile(VARIABLE_ARRAY_LENGTH4);
}

} // namespace packed_variable_array_struct
} // namespace array_types
