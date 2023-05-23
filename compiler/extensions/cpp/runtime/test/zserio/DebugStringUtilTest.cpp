#include <sstream>
#include <array>

#include "gtest/gtest.h"

#include "zserio/CppRuntimeException.h"
#include "zserio/StringView.h"
#include "zserio/DebugStringUtil.h"
#include "zserio/Reflectable.h"
#include "zserio/pmr/PolymorphicAllocator.h"

#include "test_object/std_allocator/DebugStringObject.h"
#include "test_object/std_allocator/DebugStringParamObject.h"
#include "test_object/polymorphic_allocator/DebugStringObject.h"
#include "test_object/polymorphic_allocator/DebugStringParamObject.h"

using StdDebugStringObject = test_object::std_allocator::DebugStringObject;
using StdDebugStringParamObject = test_object::std_allocator::DebugStringParamObject;
using PmrDebugStringObject = test_object::polymorphic_allocator::DebugStringObject;
using PmrDebugStringParamObject = test_object::polymorphic_allocator::DebugStringParamObject;

using std_alloc = std::allocator<uint8_t>;
using pmr_alloc = zserio::pmr::PropagatingPolymorphicAllocator<uint8_t>;

namespace zserio
{

TEST(DebugStringUtilTest, toJsonStreamDefault)
{
    std::ostringstream os;
    StdDebugStringObject debugStringObject;
    toJsonStream(debugStringObject, os);
    ASSERT_EQ("{\n    \"text\": \"test\"\n}", os.str());
}

TEST(DebugStringUtilTest, toJsonStreamDefaultWithAlloc)
{
    std::ostringstream os;
    const StdDebugStringObject debugStringObject;
    toJsonStream(debugStringObject, os, std_alloc());
    ASSERT_EQ("{\n    \"text\": \"test\"\n}", os.str());
}

TEST(DebugStringUtilTest, toJsonStreamDefaultWithPolymorphicAlloc)
{
    std::ostringstream os;
    const PmrDebugStringObject debugStringObject;
    toJsonStream(debugStringObject, os, pmr_alloc());
    ASSERT_EQ("{\n    \"text\": \"test\"\n}", os.str());
}

TEST(DebugStringUtilTest, toJsonStreamIndent2)
{
    std::ostringstream os;
    const StdDebugStringObject debugStringObject;
    toJsonStream(debugStringObject, os, 2);
    ASSERT_EQ("{\n  \"text\": \"test\"\n}", os.str());
}

TEST(DebugStringUtilTest, toJsonStreamIndent2WithAlloc)
{
    std::ostringstream os;
    const StdDebugStringObject debugStringObject;
    toJsonStream(debugStringObject, os, 2, std_alloc());
    ASSERT_EQ("{\n  \"text\": \"test\"\n}", os.str());
}

TEST(DebugStringUtilTest, toJsonStreamIndent2WithPolymorphicAlloc)
{
    std::ostringstream os;
    const PmrDebugStringObject debugStringObject;
    toJsonStream(debugStringObject, os, 2, pmr_alloc());
    ASSERT_EQ("{\n  \"text\": \"test\"\n}", os.str());
}

TEST(DebugStringUtilTest, toJsonStreamFilter)
{
    std::ostringstream os;
    const StdDebugStringObject debugStringObject;
    toJsonStream(debugStringObject, os, DepthWalkFilter(0));
    ASSERT_EQ("{\n}", os.str());
}

TEST(DebugStringUtilTest, toJsonStreamFilterWithAlloc)
{
    std::ostringstream os;
    const StdDebugStringObject debugStringObject;
    toJsonStream(debugStringObject, os, DefaultWalkFilter(), std_alloc());
    ASSERT_EQ("{\n    \"text\": \"test\"\n}", os.str());
}

TEST(DebugStringUtilTest, toJsonStreamFilterWithPolymorphicAlloc)
{
    std::ostringstream os;
    const PmrDebugStringObject debugStringObject;
    toJsonStream(debugStringObject, os, BasicDefaultWalkFilter<pmr_alloc>(),
            pmr_alloc());
    ASSERT_EQ("{\n    \"text\": \"test\"\n}", os.str());
}

TEST(DebugStringUtilTest, toJsonStreamIndent2Filter)
{
    std::ostringstream os;
    const StdDebugStringObject debugStringObject;
    toJsonStream(debugStringObject, os, 2, DefaultWalkFilter());
    ASSERT_EQ("{\n  \"text\": \"test\"\n}", os.str());
}

TEST(DebugStringUtilTest, toJsonStreamIndent2FilterWithAlloc)
{
    std::ostringstream os;
    const StdDebugStringObject debugStringObject;
    toJsonStream(debugStringObject, os, 2, DepthWalkFilter(0), std_alloc());
    ASSERT_EQ("{\n}", os.str());
}

TEST(DebugStringUtilTest, toJsonStreamIndent2FilterWithPolymorphicAlloc)
{
    std::ostringstream os;
    const PmrDebugStringObject debugStringObject;
    toJsonStream(debugStringObject, os, 2, BasicDepthWalkFilter<pmr_alloc>(0),
            pmr_alloc());
    ASSERT_EQ("{\n}", os.str());
}

TEST(DebugStringUtilTest, toJsonStringDefault)
{
    const StdDebugStringObject debugStringObject;
    ASSERT_EQ("{\n    \"text\": \"test\"\n}", toJsonString(debugStringObject));
}

TEST(DebugStringUtilTest, toJsonStringDefaultWithAlloc)
{
    const StdDebugStringObject debugStringObject;
    ASSERT_EQ("{\n    \"text\": \"test\"\n}", toJsonString(debugStringObject, std_alloc()));
}

TEST(DebugStringUtilTest, toJsonStringDefaultWithPolymorphicAlloc)
{
    const PmrDebugStringObject debugStringObject;
    ASSERT_EQ("{\n    \"text\": \"test\"\n}", toJsonString(debugStringObject, pmr_alloc()));
}

TEST(DebugStringUtilTest, toJsonStringIndent2)
{
    const StdDebugStringObject debugStringObject;
    ASSERT_EQ("{\n  \"text\": \"test\"\n}", toJsonString(debugStringObject, 2));
}

TEST(DebugStringUtilTest, toJsonStringIndent2WithAlloc)
{
    const StdDebugStringObject debugStringObject;
    ASSERT_EQ("{\n  \"text\": \"test\"\n}", toJsonString(debugStringObject, 2, std_alloc()));
}

TEST(DebugStringUtilTest, toJsonStringIndent2WithPolymorphicAlloc)
{
    const PmrDebugStringObject debugStringObject;
    ASSERT_EQ("{\n  \"text\": \"test\"\n}",
            toJsonString(debugStringObject, 2, pmr_alloc()));
}

TEST(DebugStringUtilTest, toJsonStringFilter)
{
    const StdDebugStringObject debugStringObject;
    ASSERT_EQ("{\n    \"text\": \"test\"\n}", toJsonString(debugStringObject, DefaultWalkFilter()));
}

TEST(DebugStringUtilTest, toJsonStringFilterWithAlloc)
{
    const StdDebugStringObject debugStringObject;
    ASSERT_EQ("{\n    \"text\": \"test\"\n}",
            toJsonString(debugStringObject, DefaultWalkFilter(), std_alloc()));
}

TEST(DebugStringUtilTest, toJsonStringFilterWithPolymorphicAlloc)
{
    const PmrDebugStringObject debugStringObject;
    ASSERT_EQ("{\n    \"text\": \"test\"\n}",
            toJsonString(debugStringObject, BasicDefaultWalkFilter<pmr_alloc>(),
                    pmr_alloc()));
}

TEST(DebugStringUtilTest, toJsonStringIndent2Filter)
{
    const StdDebugStringObject debugStringObject;
    ASSERT_EQ("{\n}", toJsonString(debugStringObject, 2, DepthWalkFilter(0)));
}

TEST(DebugStringUtilTest, toJsonStringIndent2FilterWithAlloc)
{
    const StdDebugStringObject debugStringObject;
    ASSERT_EQ("{\n  \"text\": \"test\"\n}",
            toJsonString(debugStringObject, 2, DefaultWalkFilter(), std_alloc()));
}

TEST(DebugStringUtilTest, toJsonStringIndent2FilterWithPolymorphicAlloc)
{
    const PmrDebugStringObject debugStringObject;
    ASSERT_EQ("{\n  \"text\": \"test\"\n}",
            toJsonString(debugStringObject, 2, BasicDefaultWalkFilter<pmr_alloc>(),
                    pmr_alloc()));
}

TEST(DebugStringUtilTest, toJsonFileDefault)
{
    const StdDebugStringObject debugStringObject;
    const std::string fileName = "DebugStringUtilTest_toJsonFileDefault.json";
    toJsonFile(debugStringObject, fileName);
    ASSERT_THROW(toJsonFile(debugStringObject, ""), CppRuntimeException);

    std::ifstream is(fileName.c_str());
    std::stringstream ss;
    ss << is.rdbuf();
    ASSERT_EQ("{\n    \"text\": \"test\"\n}", ss.str());
}

TEST(DebugStringUtilTest, toJsonFileDefaultWithAlloc)
{
    const StdDebugStringObject debugStringObject;
    const std::string fileName = "DebugStringUtilTest_toJsonFileDefaultWithAlloc.json";
    toJsonFile(debugStringObject, fileName, std_alloc());

    std::ifstream is(fileName.c_str());
    std::stringstream ss;
    ss << is.rdbuf();
    ASSERT_EQ("{\n    \"text\": \"test\"\n}", ss.str());
}

TEST(DebugStringUtilTest, toJsonFileDefaultWithPolymorphicAlloc)
{
    const PmrDebugStringObject debugStringObject;
    const string<pmr_alloc> fileName = "DebugStringUtilTest_toJsonFileDefaultWithPolymorphicAlloc.json";
    toJsonFile(debugStringObject, fileName, pmr_alloc());

    std::ifstream is(fileName.c_str());
    std::stringstream ss;
    ss << is.rdbuf();
    ASSERT_EQ("{\n    \"text\": \"test\"\n}", ss.str());
}

TEST(DebugStringUtilTest, toJsonFileIndent2)
{
    const StdDebugStringObject debugStringObject;
    const std::string fileName = "DebugStringUtilTest_toJsonFileIndent2.json";
    toJsonFile(debugStringObject, fileName, 2);

    std::ifstream is(fileName.c_str());
    std::stringstream ss;
    ss << is.rdbuf();
    ASSERT_EQ("{\n  \"text\": \"test\"\n}", ss.str());
}

TEST(DebugStringUtilTest, toJsonFileIndent2WithAlloc)
{
    const StdDebugStringObject debugStringObject;
    const std::string fileName = "DebugStringUtilTest_toJsonFileIndent2WithAlloc.json";
    toJsonFile(debugStringObject, fileName, 2, std_alloc());

    std::ifstream is(fileName.c_str());
    std::stringstream ss;
    ss << is.rdbuf();
    ASSERT_EQ("{\n  \"text\": \"test\"\n}", ss.str());
}

TEST(DebugStringUtilTest, toJsonFileIndent2WithPolymorphicAlloc)
{
    const PmrDebugStringObject debugStringObject;
    const string<pmr_alloc> fileName = "DebugStringUtilTest_toJsonFileIndent2WithPolymorphicAlloc.json";
    toJsonFile(debugStringObject, fileName, 2, pmr_alloc());

    std::ifstream is(fileName.c_str());
    std::stringstream ss;
    ss << is.rdbuf();
    ASSERT_EQ("{\n  \"text\": \"test\"\n}", ss.str());
}

TEST(DebugStringUtilTest, toJsonFileFilter)
{
    const StdDebugStringObject debugStringObject;
    const std::string fileName = "DebugStringUtilTest_toJsonFileFilter.json";
    toJsonFile(debugStringObject, fileName, DefaultWalkFilter());

    std::ifstream is(fileName.c_str());
    std::stringstream ss;
    ss << is.rdbuf();
    ASSERT_EQ("{\n    \"text\": \"test\"\n}", ss.str());
}

TEST(DebugStringUtilTest, toJsonFileFilterWithAlloc)
{
    const StdDebugStringObject debugStringObject;
    DefaultWalkFilter defaultWalkFilter;
    const std::string fileName = "DebugStringUtilTest_toJsonFileFilterWithAlloc.json";
    toJsonFile(debugStringObject, fileName, defaultWalkFilter, std_alloc());

    std::ifstream is(fileName.c_str());
    std::stringstream ss;
    ss << is.rdbuf();
    ASSERT_EQ("{\n    \"text\": \"test\"\n}", ss.str());
}

TEST(DebugStringUtilTest, toJsonFileFilterWithPolymorphicAlloc)
{
    const PmrDebugStringObject debugStringObject;
    BasicDefaultWalkFilter<pmr_alloc> defaultWalkFilter;
    const string<pmr_alloc> fileName = "DebugStringUtilTest_toJsonFileFilterWithPolymorphicAlloc.json";
    toJsonFile(debugStringObject, fileName, defaultWalkFilter, pmr_alloc());

    std::ifstream is(fileName.c_str());
    std::stringstream ss;
    ss << is.rdbuf();
    ASSERT_EQ("{\n    \"text\": \"test\"\n}", ss.str());
}

TEST(DebugStringUtilTest, toJsonFileIndent2Filter)
{
    const StdDebugStringObject debugStringObject;
    DepthWalkFilter depthWalkFilter(0);
    const std::string fileName = "DebugStringUtilTest_toJsonFileIndent2Filter.json";
    toJsonFile(debugStringObject, fileName, 2, depthWalkFilter);

    std::ifstream is(fileName.c_str());
    std::stringstream ss;
    ss << is.rdbuf();
    ASSERT_EQ("{\n}", ss.str());
}

TEST(DebugStringUtilTest, toJsonFileIndent2FilterWithAlloc)
{
    const StdDebugStringObject debugStringObject;
    DefaultWalkFilter defaultWalkFilter;
    const std::string fileName = "DebugStringUtilTest_toJsonFileIndent2FilterWithAlloc.json";
    toJsonFile(debugStringObject, fileName, 2, defaultWalkFilter, std_alloc());

    std::ifstream is(fileName.c_str());
    std::stringstream ss;
    ss << is.rdbuf();
    ASSERT_EQ("{\n  \"text\": \"test\"\n}", ss.str());
}

TEST(DebugStringUtilTest, toJsonFileIndent2FilterWithPolymorphicAlloc)
{
    const PmrDebugStringObject debugStringObject;
    BasicDefaultWalkFilter<pmr_alloc> defaultWalkFilter;
    const string<pmr_alloc> fileName = "DebugStringUtilTest_toJsonFileIndent2FilterWithPolymorphicAlloc.json";
    toJsonFile(debugStringObject, fileName, 2, defaultWalkFilter, pmr_alloc());

    std::ifstream is(fileName.c_str());
    std::stringstream ss;
    ss << is.rdbuf();
    ASSERT_EQ("{\n  \"text\": \"test\"\n}", ss.str());
}

TEST(DebugStringUtilTest, fromJsonStreamTypeInfo)
{
    std::istringstream ss("{\n  \"text\": \"something\"\n}");
    IReflectablePtr reflectable = fromJsonStream(StdDebugStringObject::typeInfo(), ss);
    ASSERT_TRUE(reflectable);

    ASSERT_EQ("something"_sv, reflectable->getField("text")->getStringView());
}

TEST(DebugStringUtilTest, fromJsonStreamParameterizedTypeInfo)
{
    std::istringstream ss("{\n  \"text\": \"something\"\n}");
    IReflectablePtr reflectable = fromJsonStream(StdDebugStringParamObject::typeInfo(), ss);
    ASSERT_TRUE(reflectable);

    ASSERT_THROW(reflectable->getParameter("param"), CppRuntimeException);
    reflectable->initialize(vector<AnyHolder<>>{AnyHolder<>{10}});

    ASSERT_EQ(10, reflectable->getParameter("param")->getInt32());
    ASSERT_EQ("something"_sv, reflectable->getField("text")->getStringView());
}

TEST(DebugStringUtilTest, fromJsonStreamTypeInfoWithAlloc)
{
    std::istringstream ss("{\n  \"text\": \"something\"\n}");
    IReflectablePtr reflectable = fromJsonStream(StdDebugStringObject::typeInfo(), ss, std_alloc());
    ASSERT_TRUE(reflectable);

    ASSERT_EQ("something"_sv, reflectable->getField("text")->getStringView());
}

TEST(DebugStringUtilTest, fromJsonStreamTypeInfoWithPolymorphicAllocDefault)
{
    std::istringstream ss("{\n  \"text\": \"something\"\n}");
    IBasicReflectablePtr<pmr_alloc> reflectable =
            fromJsonStream(PmrDebugStringObject::typeInfo(), ss);
    ASSERT_TRUE(reflectable);

    ASSERT_EQ("something"_sv, reflectable->getField("text")->getStringView());
}

TEST(DebugStringUtilTest, fromJsonStreamTypeInfoWithPolymorphicAlloc)
{
    std::istringstream ss("{\n  \"text\": \"something\"\n}");
    IBasicReflectablePtr<pmr_alloc> reflectable =
            fromJsonStream(PmrDebugStringObject::typeInfo(), ss, pmr_alloc());
    ASSERT_TRUE(reflectable);

    ASSERT_EQ("something"_sv, reflectable->getField("text")->getStringView());
}

TEST(DebugStringUtilTest, fromJsonStreamType)
{
    std::istringstream ss("{\n  \"text\": \"something\"\n}");
    StdDebugStringObject debugStringObject = fromJsonStream<StdDebugStringObject>(ss);

    ASSERT_EQ("something", debugStringObject.getText());
}

TEST(DebugStringUtilTest, fromJsonStreamParameterizedType)
{
    std::istringstream ss("{\n  \"text\": \"something\"\n}");
    StdDebugStringParamObject debugStringParamObject = fromJsonStream<StdDebugStringParamObject>(ss);

    ASSERT_THROW(debugStringParamObject.getParam(), CppRuntimeException);

    ASSERT_EQ("something", debugStringParamObject.getText());
}

TEST(DebugStringUtilTest, fromJsonStreamTypeWithAlloc)
{
    std::istringstream ss("{\n  \"text\": \"something\"\n}");
    StdDebugStringObject debugStringObject = fromJsonStream<StdDebugStringObject>(ss, std_alloc());

    ASSERT_EQ("something", debugStringObject.getText());
}

TEST(DebugStringUtilTest, fromJsonStreamTypeWithPolymorphicAllocDefault)
{
    std::istringstream ss("{\n  \"text\": \"something\"\n}");
    PmrDebugStringObject debugStringObject = fromJsonStream<PmrDebugStringObject>(ss);

    ASSERT_EQ("something", debugStringObject.getText());
}

TEST(DebugStringUtilTest, fromJsonStreamTypeWithPolymorphicAlloc)
{
    std::istringstream ss("{\n  \"text\": \"something\"\n}");
    PmrDebugStringObject debugStringObject = fromJsonStream<PmrDebugStringObject>(ss, pmr_alloc());

    ASSERT_EQ("something", debugStringObject.getText());
}

TEST(DebugStringUtilTest, fromJsonStringTypeInfo)
{
    std::string jsonString("{\n  \"text\": \"something\"\n}");
    IReflectablePtr reflectable = fromJsonString(StdDebugStringObject::typeInfo(), jsonString);
    ASSERT_TRUE(reflectable);

    ASSERT_EQ("something"_sv, reflectable->getField("text")->getStringView());
}

TEST(DebugStringUtilTest, fromJsonStringParameterizedTypeInfo)
{
    std::string jsonString("{\n  \"text\": \"something\"\n}");
    IReflectablePtr reflectable = fromJsonString(StdDebugStringParamObject::typeInfo(), jsonString);
    ASSERT_TRUE(reflectable);

    ASSERT_THROW(reflectable->getParameter("param"), CppRuntimeException);
    reflectable->initialize(vector<AnyHolder<>>{AnyHolder<>{10}});

    ASSERT_EQ(10, reflectable->getParameter("param")->getInt32());

    ASSERT_EQ("something"_sv, reflectable->getField("text")->getStringView());
}

TEST(DebugStringUtilTest, fromJsonStringTypeInfoWithAlloc)
{
    std::string jsonString("{\n  \"text\": \"something\"\n}");
    IReflectablePtr reflectable =
            fromJsonString(StdDebugStringObject::typeInfo(), jsonString, std_alloc());
    ASSERT_TRUE(reflectable);

    ASSERT_EQ("something"_sv, reflectable->getField("text")->getStringView());
}

TEST(DebugStringUtilTest, fromJsonStringTypeInfoWithPolymorphicAllocDefault)
{
    string<pmr_alloc> jsonString("{\n  \"text\": \"something\"\n}");
    IBasicReflectablePtr<pmr_alloc> reflectable =
            fromJsonString(PmrDebugStringObject::typeInfo(), jsonString);
    ASSERT_TRUE(reflectable);

    ASSERT_EQ("something"_sv, reflectable->getField("text")->getStringView());
}

TEST(DebugStringUtilTest, fromJsonStringTypeInfoWithPolymorphicAlloc)
{
    string<pmr_alloc> jsonString("{\n  \"text\": \"something\"\n}");
    IBasicReflectablePtr<pmr_alloc> reflectable =
            fromJsonString(PmrDebugStringObject::typeInfo(), jsonString, pmr_alloc());
    ASSERT_TRUE(reflectable);

    ASSERT_EQ("something"_sv, reflectable->getField("text")->getStringView());
}

TEST(DebugStringUtilTest, fromJsonStringType)
{
    std::string jsonString("{\n  \"text\": \"something\"\n}");
    StdDebugStringObject debugStringObject = fromJsonString<StdDebugStringObject>(jsonString);

    ASSERT_EQ("something", debugStringObject.getText());
}

TEST(DebugStringUtilTest, fromJsonStringParameterizedType)
{
    std::string jsonString("{\n  \"text\": \"something\"\n}");
    StdDebugStringParamObject debugStringParamObject =
            fromJsonString<StdDebugStringParamObject>(jsonString);

    ASSERT_THROW(debugStringParamObject.getParam(), CppRuntimeException);

    ASSERT_EQ("something", debugStringParamObject.getText());
}

TEST(DebugStringUtilTest, fromJsonStringTypeWithAlloc)
{
    std::string jsonString("{\n  \"text\": \"something\"\n}");
    StdDebugStringObject debugStringObject = fromJsonString<StdDebugStringObject>(jsonString, std_alloc());

    ASSERT_EQ("something", debugStringObject.getText());
}

TEST(DebugStringUtilTest, fromJsonStringTypeWithPolymorphicAllocDefault)
{
    string<pmr_alloc> jsonString("{\n  \"text\": \"something\"\n}");
    PmrDebugStringObject debugStringObject = fromJsonString<PmrDebugStringObject>(jsonString);

    ASSERT_EQ("something", debugStringObject.getText());
}

TEST(DebugStringUtilTest, fromJsonStringTypeWithPolymorphicAlloc)
{
    string<pmr_alloc> jsonString("{\n  \"text\": \"something\"\n}");
    PmrDebugStringObject debugStringObject = fromJsonString<PmrDebugStringObject>(jsonString, pmr_alloc());

    ASSERT_EQ("something", debugStringObject.getText());
}

TEST(DebugStringUtilTest, fromJsonFileTypeInfo)
{
    const char* fileName = "DebugStringUtilTest_fromJsonFileTypeInfo.json";
    {
        std::ofstream os(fileName, std::ofstream::out | std::ofstream::trunc);
        os << "{\n  \"text\": \"something\"\n}";
    }

    IReflectablePtr reflectable = fromJsonFile(StdDebugStringObject::typeInfo(), fileName);
    ASSERT_TRUE(reflectable);

    ASSERT_EQ("something"_sv, reflectable->getField("text")->getStringView());

    ASSERT_THROW(fromJsonFile(StdDebugStringObject::typeInfo(), ""), CppRuntimeException);
}

TEST(DebugStringUtilTest, fromJsonFileParameterizedTypeInfo)
{
    const char* fileName = "DebugStringUtilTest_fromJsonFileParameterizedTypeInfo.json";
    {
        std::ofstream os(fileName, std::ofstream::out | std::ofstream::trunc);
        os << "{\n  \"text\": \"something\"\n}";
    }

    IReflectablePtr reflectable = fromJsonFile(StdDebugStringParamObject::typeInfo(), fileName);
    ASSERT_TRUE(reflectable);

    ASSERT_THROW(reflectable->getParameter("param"), CppRuntimeException);
    reflectable->initialize(vector<AnyHolder<>>{AnyHolder<>{10}});

    ASSERT_EQ(10, reflectable->getParameter("param")->getInt32());

    ASSERT_EQ("something"_sv, reflectable->getField("text")->getStringView());
}

TEST(DebugStringUtilTest, fromJsonFileTypeInfoWithAlloc)
{
    const char* fileName = "DebugStringUtilTest_fromJsonFileTypeInfoWithAlloc.json";
    {
        std::ofstream os(fileName, std::ofstream::out | std::ofstream::trunc);
        os << "{\n  \"text\": \"something\"\n}";
    }

    IReflectablePtr reflectable = fromJsonFile(StdDebugStringObject::typeInfo(), fileName, std_alloc());
    ASSERT_TRUE(reflectable);

    ASSERT_EQ("something"_sv, reflectable->getField("text")->getStringView());
}

TEST(DebugStringUtilTest, fromJsonFileTypeInfoWithPolymorphicAllocDefault)
{
    const char* fileName = "DebugStringUtilTest_fromJsonFileTypeInfoWithPolymorphicAllocDefault.json";
    {
        std::ofstream os(fileName, std::ofstream::out | std::ofstream::trunc);
        os << "{\n  \"text\": \"something\"\n}";
    }

    IBasicReflectablePtr<pmr_alloc> reflectable =
            fromJsonFile(PmrDebugStringObject::typeInfo(), fileName);
    ASSERT_TRUE(reflectable);

    ASSERT_EQ("something"_sv, reflectable->getField("text")->getStringView());
}

TEST(DebugStringUtilTest, fromJsonFileTypeInfoWithPolymorphicAlloc)
{
    const char* fileName = "DebugStringUtilTest_fromJsonFileTypeInfoWithPolymorphicAlloc.json";
    {
        std::ofstream os(fileName, std::ofstream::out | std::ofstream::trunc);
        os << "{\n  \"text\": \"something\"\n}";
    }

    IBasicReflectablePtr<pmr_alloc> reflectable =
            fromJsonFile(PmrDebugStringObject::typeInfo(), fileName, pmr_alloc());
    ASSERT_TRUE(reflectable);

    ASSERT_EQ("something"_sv, reflectable->getField("text")->getStringView());
}

TEST(DebugStringUtilTest, fromJsonFileParameterizedTypeInfoWithPolymorphicAlloc)
{
    const char* fileName = "DebugStringUtilTest_fromJsonFileParameterizedTypeInfoWithPolymorphicAlloc.json";
    {
        std::ofstream os(fileName, std::ofstream::out | std::ofstream::trunc);
        os << "{\n  \"text\": \"something\"\n}";
    }

    IBasicReflectablePtr<pmr_alloc> reflectable = fromJsonFile(
            PmrDebugStringParamObject::typeInfo(), fileName, pmr_alloc());
    ASSERT_TRUE(reflectable);

    ASSERT_THROW(reflectable->getParameter("param"), CppRuntimeException);
    reflectable->initialize(vector<AnyHolder<pmr_alloc>, pmr_alloc>{AnyHolder<pmr_alloc>{10}});

    ASSERT_EQ(10, reflectable->getParameter("param")->getInt32());

    ASSERT_EQ("something"_sv, reflectable->getField("text")->getStringView());
}

TEST(DebugStringUtilTest, fromJsonFileType)
{
    const char* fileName = "DebugStringUtilTest_fromJsonFileType.json";
    {
        std::ofstream os(fileName, std::ofstream::out | std::ofstream::trunc);
        os << "{\n  \"text\": \"something\"\n}";
    }

    StdDebugStringObject debugStringObject = fromJsonFile<StdDebugStringObject>(fileName);

    ASSERT_EQ("something", debugStringObject.getText());
}

TEST(DebugStringUtilTest, fromJsonFileParameterizedType)
{
    const char* fileName = "DebugStringUtilTest_fromJsonFileParameterizedType.json";
    {
        std::ofstream os(fileName, std::ofstream::out | std::ofstream::trunc);
        os << "{\n  \"text\": \"something\"\n}";
    }

    StdDebugStringParamObject debugStringParamObject = fromJsonFile<StdDebugStringParamObject>(fileName);

    ASSERT_THROW(debugStringParamObject.getParam(), CppRuntimeException);

    ASSERT_EQ("something", debugStringParamObject.getText());
}

TEST(DebugStringUtilTest, fromJsonFileTypeWithAlloc)
{
    const char* fileName = "DebugStringUtilTest_fromJsonFileTypeWithAlloc.json";
    {
        std::ofstream os(fileName, std::ofstream::out | std::ofstream::trunc);
        os << "{\n  \"text\": \"something\"\n}";
    }

    StdDebugStringObject debugStringObject = fromJsonFile<StdDebugStringObject>(fileName, std_alloc());

    ASSERT_EQ("something", debugStringObject.getText());
}

TEST(DebugStringUtilTest, fromJsonFileTypeWithPolymorphicAllocDefault)
{
    const char* fileName = "DebugStringUtilTest_fromJsonFileTypeWithPolymorphicAllocDefault.json";
    {
        std::ofstream os(fileName, std::ofstream::out | std::ofstream::trunc);
        os << "{\n  \"text\": \"something\"\n}";
    }

    PmrDebugStringObject debugStringObject = fromJsonFile<PmrDebugStringObject>(fileName);

    ASSERT_EQ("something", debugStringObject.getText());
}

TEST(DebugStringUtilTest, fromJsonFileTypeWithPolymorphicAlloc)
{
    const char* fileName = "DebugStringUtilTest_fromJsonFileTypeWithPolymorphicAlloc.json";
    {
        std::ofstream os(fileName, std::ofstream::out | std::ofstream::trunc);
        os << "{\n  \"text\": \"something\"\n}";
    }

    PmrDebugStringObject debugStringObject = fromJsonFile<PmrDebugStringObject>(fileName, pmr_alloc());

    ASSERT_EQ("something", debugStringObject.getText());
}

TEST(DebugStringUtilTest, fromJsonFileParameterizedTypeWithPolymorphicAlloc)
{
    const char* fileName = "DebugStringUtilTest_fromJsonFileParameteriezedTypeWithPolymorphicAlloc.json";
    {
        std::ofstream os(fileName, std::ofstream::out | std::ofstream::trunc);
        os << "{\n  \"text\": \"something\"\n}";
    }

    PmrDebugStringParamObject debugStringParamObject =
            fromJsonFile<PmrDebugStringParamObject>(fileName, pmr_alloc());

    ASSERT_THROW(debugStringParamObject.getParam(), CppRuntimeException);

    ASSERT_EQ("something", debugStringParamObject.getText());
}

} // namespace zserio
