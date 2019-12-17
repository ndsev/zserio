#include "gtest/gtest.h"

#include "templates/struct_template_clash/InstantiationNameClash.h"

namespace templates
{
namespace struct_template_clash
{

TEST(StructTemplateClashTest, readWrite)
{
    TestStruct_uint32 testStruct_uint32;
    testStruct_uint32.setValue(42);

    A_B a_b;
    a_b.setValue(1);
    C c;
    c.setValue(true);
    Template_A_B_C_7FE93D34 T1;
    T1.setValue1(a_b);
    T1.setValue2(c);
    testStruct_uint32.setT1(T1);

    A a;
    a.setValue(1);
    B_C b_c;
    b_c.setValue("string");
    Template_A_B_C_5EB4E3FC T2;
    T2.setValue1(a);
    T2.setValue2(b_c);
    testStruct_uint32.setT2(T2);

    InstantiationNameClash instantiationNameClash;
    instantiationNameClash.setTest(testStruct_uint32);

    zserio::BitStreamWriter writer;
    instantiationNameClash.write(writer);
    size_t bufferSize = 0;
    const uint8_t* buffer = writer.getWriteBuffer(bufferSize);

    zserio::BitStreamReader reader(buffer, bufferSize);
    InstantiationNameClash readInstantiationNameClash(reader);

    ASSERT_TRUE(instantiationNameClash == readInstantiationNameClash);
}

} // namespace struct_template_clash
} // namespace templates
