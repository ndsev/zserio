#include "gtest/gtest.h"
#include "union_types/union_compatibility_check/UnionCompatibilityCheckVersion1.h"
#include "union_types/union_compatibility_check/UnionCompatibilityCheckVersion2.h"
#include "zserio/SerializeUtil.h"

namespace union_types
{
namespace union_compatibility_check
{

using allocator_type = UnionCompatibilityCheckVersion1::allocator_type;
template <typename T>
using vector_type = zserio::vector<T, allocator_type>;

class UnionCompatibilityCheckTest : public ::testing::Test
{
protected:
    template <typename T, typename ARRAY>
    void fill(T& unionCompatibilityCheck, const ARRAY& array)
    {
        unionCompatibilityCheck.setArray(array);
        unionCompatibilityCheck.setPackedArray(array);
    }

    template <typename T_READ, typename T_WRITE>
    T_READ writeRead(T_WRITE& unionCompatibilityCheck)
    {
        zserio::BitStreamWriter writer(bitBuffer);
        unionCompatibilityCheck.write(writer);

        zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
        return T_READ(reader);
    }

    template <typename T_READ, typename T_WRITE>
    T_READ writeReadFile(T_WRITE& unionCompatibilityCheck, const char* variant)
    {
        const std::string fileName = BLOB_NAME_BASE + variant + ".blob";
        zserio::serializeToFile(unionCompatibilityCheck, fileName);

        return zserio::deserializeFromFile<T_READ>(fileName);
    }

    template <typename UNION>
    UNION createUnion(uint32_t index)
    {
        UNION unionType;
        if (index % 2 == 0)
        {
            unionType.setCoordXY(CoordXY{10 * index, 20 * index});
        }
        else
        {
            unionType.setText("text" + zserio::toString<allocator_type>(index));
        }

        return unionType;
    }

    template <typename UNION>
    vector_type<UNION> createArrayVersion1()
    {
        return vector_type<UNION>{
                createUnion<UNION>(0), createUnion<UNION>(1), createUnion<UNION>(2), createUnion<UNION>(3)};
    }

    UnionVersion2 createUnionXYZ(uint32_t index)
    {
        UnionVersion2 unionType;
        unionType.setCoordXYZ(CoordXYZ{10 * index, 20 * index, 1.1 * index});
        return unionType;
    }

    vector_type<UnionVersion2> createArrayVersion2()
    {
        auto array = createArrayVersion1<UnionVersion2>();
        array.push_back(createUnionXYZ(4));
        array.push_back(createUnionXYZ(5));
        array.push_back(createUnionXYZ(6));
        return array;
    }

private:
    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);

    static const std::string BLOB_NAME_BASE;
};

const std::string UnionCompatibilityCheckTest::BLOB_NAME_BASE =
        "language/union_types/union_compatibility_check_";

TEST_F(UnionCompatibilityCheckTest, writeVersion1ReadVersion1)
{
    UnionCompatibilityCheckVersion1 unionCompatibilityCheckVersion1;
    fill(unionCompatibilityCheckVersion1, createArrayVersion1<UnionVersion1>());

    auto readUnionCompatibilityCheckVersion1 =
            writeRead<UnionCompatibilityCheckVersion1>(unionCompatibilityCheckVersion1);
    ASSERT_EQ(unionCompatibilityCheckVersion1, readUnionCompatibilityCheckVersion1);
}

TEST_F(UnionCompatibilityCheckTest, writeVersion1ReadVersion1File)
{
    UnionCompatibilityCheckVersion1 unionCompatibilityCheckVersion1;
    fill(unionCompatibilityCheckVersion1, createArrayVersion1<UnionVersion1>());

    auto readUnionCompatibilityCheckVersion1 = writeReadFile<UnionCompatibilityCheckVersion1>(
            unionCompatibilityCheckVersion1, "version1_version1");
    ASSERT_EQ(unionCompatibilityCheckVersion1, readUnionCompatibilityCheckVersion1);
}

TEST_F(UnionCompatibilityCheckTest, writeVersion1ReadVersion2)
{
    UnionCompatibilityCheckVersion1 unionCompatibilityCheckVersion1;
    fill(unionCompatibilityCheckVersion1, createArrayVersion1<UnionVersion1>());

    auto readUnionCompatibilityCheckVersion2 =
            writeRead<UnionCompatibilityCheckVersion2>(unionCompatibilityCheckVersion1);
    const auto expectedArrayVersion2 = createArrayVersion1<UnionVersion2>();
    ASSERT_EQ(expectedArrayVersion2, readUnionCompatibilityCheckVersion2.getArray());
    ASSERT_EQ(expectedArrayVersion2, readUnionCompatibilityCheckVersion2.getPackedArray());
}

TEST_F(UnionCompatibilityCheckTest, writeVersion1ReadVersion2File)
{
    UnionCompatibilityCheckVersion1 unionCompatibilityCheckVersion1;
    fill(unionCompatibilityCheckVersion1, createArrayVersion1<UnionVersion1>());

    auto readUnionCompatibilityCheckVersion2 = writeReadFile<UnionCompatibilityCheckVersion2>(
            unionCompatibilityCheckVersion1, "version1_version2");
    const auto expectedArrayVersion2 = createArrayVersion1<UnionVersion2>();
    ASSERT_EQ(expectedArrayVersion2, readUnionCompatibilityCheckVersion2.getArray());
    ASSERT_EQ(expectedArrayVersion2, readUnionCompatibilityCheckVersion2.getPackedArray());
}

TEST_F(UnionCompatibilityCheckTest, writeVersion2ReadVersion1)
{
    UnionCompatibilityCheckVersion2 unionCompatibilityCheckVersion2;
    fill(unionCompatibilityCheckVersion2, createArrayVersion1<UnionVersion2>());

    auto readUnionCompatibilityCheckVersion1 =
            writeRead<UnionCompatibilityCheckVersion1>(unionCompatibilityCheckVersion2);
    const auto expectedArrayVersion1 = createArrayVersion1<UnionVersion1>();
    ASSERT_EQ(expectedArrayVersion1, readUnionCompatibilityCheckVersion1.getArray());
    ASSERT_EQ(expectedArrayVersion1, readUnionCompatibilityCheckVersion1.getPackedArray());
}

TEST_F(UnionCompatibilityCheckTest, writeVersion2ReadVersion1File)
{
    UnionCompatibilityCheckVersion2 unionCompatibilityCheckVersion2;
    fill(unionCompatibilityCheckVersion2, createArrayVersion1<UnionVersion2>());

    auto readUnionCompatibilityCheckVersion1 = writeReadFile<UnionCompatibilityCheckVersion1>(
            unionCompatibilityCheckVersion2, "version2_version1");
    const auto expectedArrayVersion1 = createArrayVersion1<UnionVersion1>();
    ASSERT_EQ(expectedArrayVersion1, readUnionCompatibilityCheckVersion1.getArray());
    ASSERT_EQ(expectedArrayVersion1, readUnionCompatibilityCheckVersion1.getPackedArray());
}

TEST_F(UnionCompatibilityCheckTest, writeVersion2ReadVersion2)
{
    UnionCompatibilityCheckVersion2 unionCompatibilityCheckVersion2;
    fill(unionCompatibilityCheckVersion2, createArrayVersion2());

    auto readUnionCompatibilityCheckVersion2 =
            writeRead<UnionCompatibilityCheckVersion2>(unionCompatibilityCheckVersion2);
    ASSERT_EQ(unionCompatibilityCheckVersion2, readUnionCompatibilityCheckVersion2);
}

TEST_F(UnionCompatibilityCheckTest, writeVersion2ReadVersion2File)
{
    UnionCompatibilityCheckVersion2 unionCompatibilityCheckVersion2;
    fill(unionCompatibilityCheckVersion2, createArrayVersion2());

    auto readUnionCompatibilityCheckVersion2 = writeReadFile<UnionCompatibilityCheckVersion2>(
            unionCompatibilityCheckVersion2, "version2_version2");
    ASSERT_EQ(unionCompatibilityCheckVersion2, readUnionCompatibilityCheckVersion2);
}

} // namespace union_compatibility_check
} // namespace union_types
