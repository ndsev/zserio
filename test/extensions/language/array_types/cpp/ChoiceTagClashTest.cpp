#include "choice_tag_clash/ChoiceTagClash.h"
#include "gtest/gtest.h"
#include "zserio/SerializeUtil.h"

namespace choice_tag_clash
{

using allocator_type = ChoiceTagClash::allocator_type;
template <typename T>
using vector_type = zserio::vector<T, allocator_type>;
using BitBuffer = zserio::BasicBitBuffer<zserio::RebindAlloc<allocator_type, uint8_t>>;

class ChoiceTagClashTest : public ::testing::Test
{
protected:
    ChoiceTagClash createChoiceTagClash()
    {
        ChoiceTagClash choiceTagClash;
        fillChoices(choiceTagClash.getChoices());
        fillUnions(choiceTagClash.getUnions());
        return choiceTagClash;
    }

    static void fillChoices(vector_type<TestChoice>& choices)
    {
        for (size_t i = 0; i < NUM_CHOICES; ++i)
        {
            choices.emplace_back();
            if (i % 2 == 0)
            {
                choices.back().setChoiceTag(static_cast<uint32_t>(i));
            }
            else
            {
                choices.back().setStringField("text " + zserio::toString<allocator_type>(i));
            }
        }
    }

    static void fillUnions(vector_type<TestUnion>& unions)
    {
        for (size_t i = 0; i < NUM_UNIONS; ++i)
        {
            unions.emplace_back();
            if (i % 2 == 0)
            {
                unions.back().setChoiceTag(static_cast<uint32_t>(i));
            }
            else
            {
                unions.back().setStringField("text " + zserio::toString<allocator_type>(i));
            }
        }
    }

private:
    static constexpr size_t NUM_CHOICES = 10;
    static constexpr size_t NUM_UNIONS = 13;
};

constexpr size_t ChoiceTagClashTest::NUM_CHOICES;
constexpr size_t ChoiceTagClashTest::NUM_UNIONS;

TEST_F(ChoiceTagClashTest, writeRead)
{
    ChoiceTagClash choiceTagClash = createChoiceTagClash();

    BitBuffer bitBuffer = zserio::serialize(choiceTagClash);
    ChoiceTagClash readChoiceTagClash = zserio::deserialize<ChoiceTagClash>(bitBuffer);

    ASSERT_EQ(choiceTagClash, readChoiceTagClash);
}

} // namespace choice_tag_clash
