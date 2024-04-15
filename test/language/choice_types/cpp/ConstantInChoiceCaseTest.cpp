#include "choice_types/constant_in_choice_case/ConstantInChoiceCase.h"
#include "choice_types/constant_in_choice_case/UINT8_CONST.h"
#include "gtest/gtest.h"
#include "zserio/SerializeUtil.h"

namespace choice_types
{
namespace constant_in_choice_case
{

TEST(ConstantInChoiceCaseTest, writeRead)
{
    ConstantInChoiceCase constantInChoiceCase;
    constantInChoiceCase.setConstCase(42);

    auto bitBuffer = zserio::serialize(constantInChoiceCase, UINT8_CONST);

    ConstantInChoiceCase readConstantInChoiceCase =
            zserio::deserialize<ConstantInChoiceCase>(bitBuffer, UINT8_CONST);
    ASSERT_EQ(constantInChoiceCase, readConstantInChoiceCase);
}

} // namespace constant_in_choice_case
} // namespace choice_types
