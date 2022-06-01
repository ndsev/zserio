#include "math.h"

#include "gtest/gtest.h"

#include "expressions/modulo_operator/ModuloFunction.h"

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
