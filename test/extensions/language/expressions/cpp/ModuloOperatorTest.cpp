#include "expressions/modulo_operator/ModuloFunction.h"
#include "gtest/gtest.h"

namespace expressions
{
namespace modulo_operator
{

TEST(ModuloOperatorTest, isValueDivBy4)
{
    const ModuloFunction moduloFunction;
    ASSERT_TRUE(moduloFunction.funcIsValueDivBy4());
}

} // namespace modulo_operator
} // namespace expressions
