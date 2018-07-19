#include "zserio/StringHolder.h"

#include "gtest/gtest.h"

namespace zserio
{

TEST(StringHolderTest, EmptyContructor)
{
    const StringHolder stringHolder;
    const char* emptyPointer = stringHolder.get();
    EXPECT_EQ(NULL, emptyPointer);
}

TEST(StringHolderTest, StringObjectConstructor)
{
    const std::string stringObject("Test1");
    const StringHolder stringHolder1(stringObject);
    EXPECT_EQ(stringObject, std::string(stringHolder1.get()));

    const char* constPointer = "Test2";
    const StringHolder stringHolder2(constPointer);
    EXPECT_EQ(constPointer, std::string(stringHolder2.get()));
}

TEST(StringHolderTest, StringHolderConstructor)
{
    const StringHolder stringHolder1("Test1");
    EXPECT_EQ(std::string("Test1"), std::string(stringHolder1.get()));

    const char constString[] = "Test2";
    const StringHolder stringHolder2(constString);
    EXPECT_EQ(std::string("Test2"), std::string(stringHolder2.get()));

    // The following should give the compilation error (constructor is private)
    //char nonConstString[] = "Test3";
    //const StringHolder stringHolder3(nonConstString);
}

TEST(StringHolderTest, CopyConstructor)
{
    const std::string stringObject("Test1");
    const StringHolder stringHolder1(stringObject);
    const StringHolder stringHolder1Copy(stringHolder1);
    EXPECT_EQ(stringHolder1, stringHolder1Copy);

    const StringHolder stringHolder2("Test2");
    const StringHolder stringHolder2Copy(stringHolder2);
    EXPECT_EQ(stringHolder2, stringHolder2Copy);
}

TEST(StringHolderTest, AssignmentOperator)
{
    const std::string stringObject("Test1");
    const StringHolder stringHolder1(stringObject);
    const StringHolder stringHolder1Copy = stringHolder1;
    EXPECT_EQ(stringHolder1, stringHolder1Copy);

    const StringHolder stringHolder2("Test2");
    const StringHolder stringHolder2Copy = stringHolder2;
    EXPECT_EQ(stringHolder2, stringHolder2Copy);
}

TEST(StringHolderTest, Get)
{
    const std::string stringObject("Test1");
    const StringHolder stringHolder1(stringObject);
    EXPECT_EQ(stringObject, std::string(stringHolder1.get()));

    const StringHolder stringHolder2("Test2");
    EXPECT_EQ("Test2", std::string(stringHolder2.get()));
}

TEST(StringHolderTest, EqualOperator)
{
    const std::string stringObject1("Test1");
    const StringHolder stringHolder1(stringObject1);

    const StringHolder stringHolder2(stringObject1);
    const StringHolder stringHolder3("Test1");
    EXPECT_TRUE(stringHolder1 == stringHolder2);
    EXPECT_TRUE(stringHolder1 == stringHolder3);

    const std::string stringObject4("Test2");
    const StringHolder stringHolder4(stringObject4);
    const StringHolder stringHolder5("Test2");
    EXPECT_FALSE(stringHolder1 == stringHolder4);
    EXPECT_FALSE(stringHolder1 == stringHolder5);
}

TEST(StringHolderTest, StringEqualOperator)
{
    const std::string stringObject1("Test1");
    const StringHolder stringHolder1(stringObject1);
    EXPECT_TRUE(stringHolder1 == std::string("Test1"));
    EXPECT_FALSE(stringHolder1 == std::string("Test2"));

    const StringHolder stringHolder2("Test1");
    EXPECT_TRUE(stringHolder1 == std::string("Test1"));
    EXPECT_FALSE(stringHolder1 == std::string("Test2"));
}

TEST(StringHolderTest, ConstCharPointerEqualOperator)
{
    const std::string stringObject1("Test1");
    const StringHolder stringHolder1(stringObject1);
    EXPECT_TRUE(stringHolder1 == "Test1");
    EXPECT_FALSE(stringHolder1 == "Test2");

    const StringHolder stringHolder2("Test1");
    EXPECT_TRUE(stringHolder1 == "Test1");
    EXPECT_FALSE(stringHolder1 == "Test2");
}

} // namespace zserio
