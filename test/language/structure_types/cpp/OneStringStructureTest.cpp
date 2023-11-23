#include <cstring>

#include "gtest/gtest.h"

#include "structure_types/one_string_structure/OneStringStructure.h"

#include "zserio/SerializeUtil.h"
#include "zserio/RebindAlloc.h"

namespace structure_types
{
namespace one_string_structure
{

using allocator_type = OneStringStructure::allocator_type;
using string_type = zserio::string<allocator_type>;

class OneStringStructureTest : public ::testing::Test
{
protected:
    void writeOneStringStructureToByteArray(zserio::BitStreamWriter& writer, const string_type& oneString)
    {
        writer.writeString(oneString);
    }

    static const std::string BLOB_NAME;

    static const size_t EMPTY_ONE_STRING_STRUCTURE_BIT_SIZE;

    static const char* const ONE_STRING;
    static const size_t ONE_STRING_STRUCTURE_BIT_SIZE;

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
};

const std::string OneStringStructureTest::BLOB_NAME = "language/structure_types/one_string_structure.blob";
const char* const OneStringStructureTest::ONE_STRING = "This is a string!";
const size_t OneStringStructureTest::ONE_STRING_STRUCTURE_BIT_SIZE =
        (1 + strlen(OneStringStructureTest::ONE_STRING)) * 8;

TEST_F(OneStringStructureTest, emptyConstructor)
{
    {
        OneStringStructure oneStringStructure;
        ASSERT_EQ("", oneStringStructure.getOneString());
    }
    {
        OneStringStructure oneStringStructure = {};
        ASSERT_EQ("", oneStringStructure.getOneString());
    }
}

TEST_F(OneStringStructureTest, fieldConstructor)
{
    {
        const char* str = "test";
        OneStringStructure oneStringStructure(str);
        ASSERT_EQ(str, oneStringStructure.getOneString());
    }
    {
        string_type movedString(1000, 'a'); // long enough to prevent small string optimization
        const void* ptr = movedString.data();
        OneStringStructure oneStringStructure(std::move(movedString));
        const void* movedPtr= oneStringStructure.getOneString().data();
        ASSERT_EQ(ptr, movedPtr);
    }
    {
        // cannot use just '{}' since ctor would be ambiguous (ambiguity with move/copy ctors)
        OneStringStructure oneStringStructure(string_type{});
        ASSERT_TRUE(oneStringStructure.getOneString().empty());
    }
}

TEST_F(OneStringStructureTest, bitStreamReaderConstructor)
{
    zserio::BitStreamWriter writer(bitBuffer);
    writeOneStringStructureToByteArray(writer, ONE_STRING);
    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());

    OneStringStructure oneStringStructure(reader);
    ASSERT_EQ(ONE_STRING, oneStringStructure.getOneString());
}

// we shall test also generated 'default' methods!
TEST_F(OneStringStructureTest, copyConstructor)
{
    const char* str = "test";
    OneStringStructure oneStringStructure(str);
    OneStringStructure oneStringStructureCopy(oneStringStructure);
    ASSERT_EQ(str, oneStringStructure.getOneString());
    ASSERT_EQ(str, oneStringStructureCopy.getOneString());
}

// we shall test also generated 'default' methods!
TEST_F(OneStringStructureTest, copyAssignmentOperator)
{
    const char* str = "test";
    OneStringStructure oneStringStructure(str);
    OneStringStructure oneStringStructureCopy;
    oneStringStructureCopy = oneStringStructure;
    ASSERT_EQ(str, oneStringStructure.getOneString());
    ASSERT_EQ(str, oneStringStructureCopy.getOneString());
}

// we shall test also generated 'default' methods!
TEST_F(OneStringStructureTest, moveConstructor)
{
    OneStringStructure oneStringStructure(string_type(1000, 'a'));
    const void* ptr = oneStringStructure.getOneString().data();
    OneStringStructure movedOneStringStructure(std::move(oneStringStructure));
    const void* movedPtr = movedOneStringStructure.getOneString().data();
    ASSERT_EQ(ptr, movedPtr);
    ASSERT_EQ(string_type(1000, 'a'), movedOneStringStructure.getOneString());
}

// we shall test also generated 'default' methods!
TEST_F(OneStringStructureTest, moveAssignmentOperator)
{
    OneStringStructure oneStringStructure(string_type(1000, 'a'));
    const void* ptr = oneStringStructure.getOneString().data();
    OneStringStructure movedOneStringStructure;
    movedOneStringStructure = std::move(oneStringStructure);
    const void* movedPtr = movedOneStringStructure.getOneString().data();
    ASSERT_EQ(ptr, movedPtr);
    ASSERT_EQ(string_type(1000, 'a'), movedOneStringStructure.getOneString());
}

TEST_F(OneStringStructureTest, propagateAllocatorCopyConstructor)
{
    const char* str = "test";
    OneStringStructure oneStringStructure(str);
    OneStringStructure oneStringStructureCopy(zserio::PropagateAllocator, oneStringStructure,
            OneStringStructure::allocator_type());
    ASSERT_EQ(str, oneStringStructure.getOneString());
    ASSERT_EQ(str, oneStringStructureCopy.getOneString());
}

TEST_F(OneStringStructureTest, getSetOneString)
{
    OneStringStructure oneStringStructure;
    oneStringStructure.setOneString(ONE_STRING);
    ASSERT_EQ(ONE_STRING, oneStringStructure.getOneString());

    string_type movedString(1000, 'a'); // long enough to prevent small string optimization
    const void* ptr = movedString.data();
    oneStringStructure.setOneString(std::move(movedString));
    const void* movedPtr = oneStringStructure.getOneString().data();
    ASSERT_EQ(ptr, movedPtr);

    string_type& value = oneStringStructure.getOneString();
    value = ONE_STRING;
    ASSERT_EQ(ONE_STRING, oneStringStructure.getOneString());
}

TEST_F(OneStringStructureTest, bitSizeOf)
{
    OneStringStructure oneStringStructure;
    oneStringStructure.setOneString(ONE_STRING);
    ASSERT_EQ(ONE_STRING_STRUCTURE_BIT_SIZE, oneStringStructure.bitSizeOf());
}

TEST_F(OneStringStructureTest, initializeOffsets)
{
    OneStringStructure oneStringStructure;
    oneStringStructure.setOneString(ONE_STRING);
    const size_t bitPosition = 1;
    const size_t expectedBitSize = bitPosition + ONE_STRING_STRUCTURE_BIT_SIZE;
    ASSERT_EQ(expectedBitSize, oneStringStructure.initializeOffsets(bitPosition));
}

TEST_F(OneStringStructureTest, operatorEquality)
{
    OneStringStructure oneStringStructure1;
    OneStringStructure oneStringStructure2;
    ASSERT_TRUE(oneStringStructure1 == oneStringStructure2);

    oneStringStructure1.setOneString(ONE_STRING);
    ASSERT_FALSE(oneStringStructure1 == oneStringStructure2);

    oneStringStructure2.setOneString(ONE_STRING);
    ASSERT_TRUE(oneStringStructure1 == oneStringStructure2);
}

TEST_F(OneStringStructureTest, operatorLessThan)
{
    OneStringStructure oneStringStructure1;
    OneStringStructure oneStringStructure2;
    ASSERT_FALSE(oneStringStructure1 < oneStringStructure2);

    oneStringStructure1.setOneString(ONE_STRING);
    ASSERT_FALSE(oneStringStructure1 < oneStringStructure2);
    ASSERT_TRUE(oneStringStructure2 < oneStringStructure1);

    oneStringStructure2.setOneString(ONE_STRING);
    ASSERT_FALSE(oneStringStructure1 < oneStringStructure2);
    ASSERT_FALSE(oneStringStructure2 < oneStringStructure1);

    oneStringStructure1.setOneString("A string");
    ASSERT_TRUE(oneStringStructure1 < oneStringStructure2);
    ASSERT_FALSE(oneStringStructure2 < oneStringStructure1);
}

TEST_F(OneStringStructureTest, hashCode)
{
    OneStringStructure oneStringStructure1;
    OneStringStructure oneStringStructure2;
    ASSERT_EQ(oneStringStructure1.hashCode(), oneStringStructure2.hashCode());

    oneStringStructure1.setOneString(ONE_STRING);
    ASSERT_NE(oneStringStructure1.hashCode(), oneStringStructure2.hashCode());

    // use hardcoded values to check that the hash code is stable
    ASSERT_EQ(1773897624, oneStringStructure1.hashCode());
    ASSERT_EQ(23, oneStringStructure2.hashCode());

    oneStringStructure2.setOneString(ONE_STRING);
    ASSERT_EQ(oneStringStructure1.hashCode(), oneStringStructure2.hashCode());
}

TEST_F(OneStringStructureTest, writeRead)
{
    OneStringStructure oneStringStructure;
    oneStringStructure.setOneString(ONE_STRING);

    zserio::BitStreamWriter writer(bitBuffer);
    oneStringStructure.write(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    OneStringStructure readOneStringStructure(reader);
    ASSERT_EQ(ONE_STRING, readOneStringStructure.getOneString());
    ASSERT_TRUE(oneStringStructure == readOneStringStructure);
}

TEST_F(OneStringStructureTest, writeReadFile)
{
    OneStringStructure oneStringStructure;
    oneStringStructure.setOneString(ONE_STRING);
    zserio::serializeToFile(oneStringStructure, BLOB_NAME);

    OneStringStructure readOneStringStructure = zserio::deserializeFromFile<OneStringStructure>(BLOB_NAME);
    ASSERT_EQ(ONE_STRING, readOneStringStructure.getOneString());
    ASSERT_TRUE(oneStringStructure == readOneStringStructure);
}

} // namespace one_string_structure
} // namespace structure_types
