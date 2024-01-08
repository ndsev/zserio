#include "gtest/gtest.h"
#include "parameterized_types/parameterized_inner_classes_clashing/ElementChildrenInitializer_array.h"
#include "parameterized_types/parameterized_inner_classes_clashing/ElementFactory_array.h"
#include "parameterized_types/parameterized_inner_classes_clashing/ElementInitializer_array.h"
#include "zserio/BitStreamReader.h"
#include "zserio/BitStreamWriter.h"

namespace parameterized_types
{
namespace parameterized_inner_classes_clashing
{

using allocator_type = ElementFactory_array::allocator_type;
template <typename T>
using vector_type = zserio::vector<T, allocator_type>;

class ParameterizedInnerClassesClashingTest : public ::testing::Test
{
protected:
    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
};

TEST_F(ParameterizedInnerClassesClashingTest, writeReadElementFactory)
{
    ElementFactory_array testStructure{100, vector_type<Compound>{{Compound{13}, Compound{42}}}};
    testStructure.initializeChildren();

    zserio::BitStreamWriter writer(bitBuffer);
    testStructure.write(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    ElementFactory_array readTestStructure(reader);
    ASSERT_EQ(testStructure, readTestStructure);
}

TEST_F(ParameterizedInnerClassesClashingTest, writeReadElementInitializer)
{
    ElementInitializer_array testStructure{100, vector_type<Compound>{{Compound{13}, Compound{42}}}};
    testStructure.initializeChildren();

    zserio::BitStreamWriter writer(bitBuffer);
    testStructure.write(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    ElementInitializer_array readTestStructure(reader);
    ASSERT_EQ(testStructure, readTestStructure);
}

TEST_F(ParameterizedInnerClassesClashingTest, writeReadElementChildrenInitializer)
{
    const uint32_t param = 100;
    ElementChildrenInitializer_array testStructure{
            vector_type<Parent>{{Parent{param, Compound{13}}, Parent{param, Compound{42}}}}};
    testStructure.initializeChildren();

    zserio::BitStreamWriter writer(bitBuffer);
    testStructure.write(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    ElementChildrenInitializer_array readTestStructure(reader);
    ASSERT_EQ(testStructure, readTestStructure);
}

} // namespace parameterized_inner_classes_clashing
} // namespace parameterized_types
