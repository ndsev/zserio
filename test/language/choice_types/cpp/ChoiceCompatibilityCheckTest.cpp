#include "gtest/gtest.h"

#include "choice_types/choice_compatibility_check/ChoiceCompatibilityCheckVersion1.h"
#include "choice_types/choice_compatibility_check/ChoiceCompatibilityCheckVersion2.h"

#include "zserio/SerializeUtil.h"

namespace choice_types
{
namespace choice_compatibility_check
{

using allocator_type = ChoiceCompatibilityCheckVersion1::allocator_type;
template <typename T>
using vector_type = zserio::vector<T, allocator_type>;

namespace
{
    template <typename HOLDER>
    struct VersionTraits
    {};

    template <>
    struct VersionTraits<HolderVersion1>
    {
        using Holder = HolderVersion1;
        using Choice = ChoiceVersion1;
        using Enum = EnumVersion1;
    };

    template <>
    struct VersionTraits<HolderVersion2>
    {
        using Holder = HolderVersion2;
        using Choice = ChoiceVersion2;
        using Enum = EnumVersion2;
    };
} // namespace

class ChoiceCompatibilityCheckTest : public ::testing::Test
{
protected:
    template <typename T, typename ARRAY>
    void fill(T& choiceCompatibilityCheck, const ARRAY& array)
    {
        choiceCompatibilityCheck.setArray(array);
        choiceCompatibilityCheck.setPackedArray(array);
    }

    template <typename T_READ, typename T_WRITE>
    T_READ writeRead(T_WRITE& choiceCompatibilityCheck)
    {
        zserio::BitStreamWriter writer(bitBuffer);
        choiceCompatibilityCheck.write(writer);

        zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
        return T_READ(reader);
    }

    template <typename T_READ, typename T_WRITE>
    T_READ writeReadFile(T_WRITE& choiceCompatibilityCheck, const char* variant)
    {
        const std::string fileName = BLOB_NAME_BASE + variant + ".blob";
        zserio::serializeToFile(choiceCompatibilityCheck, fileName);

        return zserio::deserializeFromFile<T_READ>(fileName);
    }

    template <typename HOLDER, typename ENUM>
    HOLDER createHolder(ENUM selector, uint32_t index)
    {
        using Choice = typename VersionTraits<HOLDER>::Choice;

        Choice choice;
        if (selector == ENUM::COORD_XY)
        {
            choice.initialize({ nullptr, 0, [](void*, size_t) { return ENUM::COORD_XY; } });
            choice.setCoordXY(CoordXY{10 * index, 20 * index});
        }
        else if (selector == ENUM::TEXT)
        {
            choice.initialize({ nullptr, 0, [](void*, size_t) { return ENUM::TEXT; } });
            choice.setText("text" + zserio::toString<allocator_type>(index));
        }

        return HOLDER(selector, std::move(choice));
    }

    template <typename HOLDER>
    vector_type<HOLDER> createArrayVersion1()
    {
        using Enum = typename VersionTraits<HOLDER>::Enum;

        return vector_type<HOLDER>{
            createHolder<HOLDER>(Enum::COORD_XY, 0),
            createHolder<HOLDER>(Enum::TEXT, 1),
            createHolder<HOLDER>(Enum::COORD_XY, 2),
            createHolder<HOLDER>(Enum::TEXT, 3)
        };
    }

    HolderVersion2 createHolderCoordXYZ(uint32_t index)
    {
        ChoiceVersion2 choice;
        choice.initialize({ nullptr, 0, [](void*, size_t) { return EnumVersion2::COORD_XYZ; } });
        choice.setCoordXYZ(CoordXYZ{10 * index, 20 * index, 1.1 * index});

        return HolderVersion2(EnumVersion2::COORD_XYZ, std::move(choice));
    }

    vector_type<HolderVersion2> createArrayVersion2()
    {
        auto array = createArrayVersion1<HolderVersion2>();
        array.push_back(createHolderCoordXYZ(4));
        array.push_back(createHolderCoordXYZ(5));
        array.push_back(createHolderCoordXYZ(6));
        return array;
    }



private:
    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);

    static const std::string BLOB_NAME_BASE;
};

const std::string ChoiceCompatibilityCheckTest::BLOB_NAME_BASE =
        "language/choice_types/choice_compatibility_check_";

TEST_F(ChoiceCompatibilityCheckTest, writeVersion1ReadVersion1)
{
    ChoiceCompatibilityCheckVersion1 choiceCompatibilityCheckVersion1;
    fill(choiceCompatibilityCheckVersion1, createArrayVersion1<HolderVersion1>());

    auto readChoiceCompatibilityCheckVersion1 = writeRead<ChoiceCompatibilityCheckVersion1>(
            choiceCompatibilityCheckVersion1);
    ASSERT_EQ(choiceCompatibilityCheckVersion1, readChoiceCompatibilityCheckVersion1);
}

TEST_F(ChoiceCompatibilityCheckTest, writeVersion1ReadVersion2)
{
    ChoiceCompatibilityCheckVersion1 choiceCompatibilityCheckVersion1;
    fill(choiceCompatibilityCheckVersion1, createArrayVersion1<HolderVersion1>());

    auto readChoiceCompatibilityCheckVersion2 = writeRead<ChoiceCompatibilityCheckVersion2>(
            choiceCompatibilityCheckVersion1);
    const auto expectedArrayVersion2 = createArrayVersion1<HolderVersion2>();
    ASSERT_EQ(expectedArrayVersion2, readChoiceCompatibilityCheckVersion2.getArray());
    ASSERT_EQ(expectedArrayVersion2, readChoiceCompatibilityCheckVersion2.getPackedArray());
}

TEST_F(ChoiceCompatibilityCheckTest, writeVersion2ReadVersion1)
{
    ChoiceCompatibilityCheckVersion2 choiceCompatibilityCheckVersion2;
    fill(choiceCompatibilityCheckVersion2, createArrayVersion1<HolderVersion2>());

    auto readChoiceCompatibilityCheckVersion1 = writeRead<ChoiceCompatibilityCheckVersion1>(
            choiceCompatibilityCheckVersion2);
    const auto expectedArrayVersion1 = createArrayVersion1<HolderVersion1>();
    ASSERT_EQ(expectedArrayVersion1, readChoiceCompatibilityCheckVersion1.getArray());
    ASSERT_EQ(expectedArrayVersion1, readChoiceCompatibilityCheckVersion1.getPackedArray());
}

TEST_F(ChoiceCompatibilityCheckTest, writeVersion2ReadVersion2)
{
    ChoiceCompatibilityCheckVersion2 choiceCompatibilityCheckVersion2;
    fill(choiceCompatibilityCheckVersion2, createArrayVersion2());

    auto readChoiceCompatibilityCheckVersion2 = writeRead<ChoiceCompatibilityCheckVersion2>(
            choiceCompatibilityCheckVersion2);
    ASSERT_EQ(choiceCompatibilityCheckVersion2, readChoiceCompatibilityCheckVersion2);
}

TEST_F(ChoiceCompatibilityCheckTest, writeVersion1ReadVersion1File)
{
    ChoiceCompatibilityCheckVersion1 choiceCompatibilityCheckVersion1;
    fill(choiceCompatibilityCheckVersion1, createArrayVersion1<HolderVersion1>());

    auto readChoiceCompatibilityCheckVersion1 = writeReadFile<ChoiceCompatibilityCheckVersion1>(
            choiceCompatibilityCheckVersion1, "version1_version1");
    ASSERT_EQ(choiceCompatibilityCheckVersion1, readChoiceCompatibilityCheckVersion1);
}

TEST_F(ChoiceCompatibilityCheckTest, writeVersion1ReadVersion2File)
{
    ChoiceCompatibilityCheckVersion1 choiceCompatibilityCheckVersion1;
    fill(choiceCompatibilityCheckVersion1, createArrayVersion1<HolderVersion1>());

    auto readChoiceCompatibilityCheckVersion2 = writeReadFile<ChoiceCompatibilityCheckVersion2>(
            choiceCompatibilityCheckVersion1, "version1_version2");
    const auto expectedArrayVersion2 = createArrayVersion1<HolderVersion2>();
    ASSERT_EQ(expectedArrayVersion2, readChoiceCompatibilityCheckVersion2.getArray());
    ASSERT_EQ(expectedArrayVersion2, readChoiceCompatibilityCheckVersion2.getPackedArray());
}

TEST_F(ChoiceCompatibilityCheckTest, writeVersion2ReadVersion1File)
{
    ChoiceCompatibilityCheckVersion2 choiceCompatibilityCheckVersion2;
    fill(choiceCompatibilityCheckVersion2, createArrayVersion1<HolderVersion2>());

    auto readChoiceCompatibilityCheckVersion1 = writeReadFile<ChoiceCompatibilityCheckVersion1>(
            choiceCompatibilityCheckVersion2, "version2_version1");
    const auto expectedArrayVersion1 = createArrayVersion1<HolderVersion1>();
    ASSERT_EQ(expectedArrayVersion1, readChoiceCompatibilityCheckVersion1.getArray());
    ASSERT_EQ(expectedArrayVersion1, readChoiceCompatibilityCheckVersion1.getPackedArray());
}

TEST_F(ChoiceCompatibilityCheckTest, writeVersion2ReadVersion2File)
{
    ChoiceCompatibilityCheckVersion2 choiceCompatibilityCheckVersion2;
    fill(choiceCompatibilityCheckVersion2, createArrayVersion2());

    auto readChoiceCompatibilityCheckVersion2 = writeReadFile<ChoiceCompatibilityCheckVersion2>(
            choiceCompatibilityCheckVersion2, "version2_version2");
    ASSERT_EQ(choiceCompatibilityCheckVersion2, readChoiceCompatibilityCheckVersion2);
}

} // namespace choice_compatibility_check
} // namespace choice_types
