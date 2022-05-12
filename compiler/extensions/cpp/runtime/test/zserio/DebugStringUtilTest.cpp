#include "gtest/gtest.h"

#include <sstream>

#include "zserio/DebugStringUtil.h"
#include "zserio/Reflectable.h"
#include "zserio/pmr/PolymorphicAllocator.h"

namespace zserio
{

namespace
{

const std::array<FieldInfo, 1> FIELDS{
    FieldInfo{
        "text"_sv, BuiltinTypeInfo::getString(),
        {}, {}, {}, {}, false, {}, {}, false, {}, false, false
    }
};

const StructTypeInfo DUMMY_OBJECT_TYPE_INFO{
    "Dummy"_sv, {}, {}, FIELDS, {}, {}
};

template <typename ALLOC = std::allocator<uint8_t>>
struct DummyObject
{
    IBasicReflectableConstPtr<ALLOC> reflectable(const ALLOC& allocator = ALLOC()) const
    {
        class Reflectable : public ReflectableAllocatorHolderBase<ALLOC>
        {
        public:
            explicit Reflectable(const ALLOC& allocator) :
                    ReflectableAllocatorHolderBase<ALLOC>(DUMMY_OBJECT_TYPE_INFO, allocator)
            {}

            virtual IBasicReflectableConstPtr<ALLOC> getField(StringView name) const override
            {
                if (name == makeStringView("text"))
                {
                    return BasicReflectableFactory<ALLOC>::getString(m_text,
                            ReflectableAllocatorHolderBase<ALLOC>::get_allocator());
                }
                throw CppRuntimeException("Field '") + name + "' doesn't exist in 'DummyNested'!";
            }

            virtual void write(BitStreamWriter&) const override
            {
            }

            virtual size_t bitSizeOf(size_t) const override
            {
                return 0;
            }

        private:
            const string<ALLOC> m_text = "test";
        };

        return std::allocate_shared<Reflectable>(allocator, allocator);
    }
};

const std::string TEST_NAME = "DebugStringUtilTest";

} // namespace

TEST(DebugStringUtilTest, toJsonStreamDefault)
{
    std::ostringstream os;
    const DummyObject<> dummyObject;
    toJsonStream(dummyObject, os);
    ASSERT_EQ("{\n    \"text\": \"test\"\n}", os.str());
}

TEST(DebugStringUtilTest, toJsonStreamDefaultWithAlloc)
{
    std::ostringstream os;
    const DummyObject<> dummyObject;
    toJsonStream(dummyObject, os, std::allocator<uint8_t>());
    ASSERT_EQ("{\n    \"text\": \"test\"\n}", os.str());
}

TEST(DebugStringUtilTest, toJsonStreamDefaultWithPolymorphicAlloc)
{
    std::ostringstream os;
    const DummyObject<pmr::PolymorphicAllocator<uint8_t>> dummyObject;
    toJsonStream(dummyObject, os, pmr::PolymorphicAllocator<uint8_t>());
    ASSERT_EQ("{\n    \"text\": \"test\"\n}", os.str());
}

TEST(DebugStringUtilTest, toJsonStreamIndent2)
{
    std::ostringstream os;
    const DummyObject<> dummyObject;
    toJsonStream(dummyObject, os, 2);
    ASSERT_EQ("{\n  \"text\": \"test\"\n}", os.str());
}

TEST(DebugStringUtilTest, toJsonStreamIndent2WithAlloc)
{
    std::ostringstream os;
    const DummyObject<> dummyObject;
    toJsonStream(dummyObject, os, 2, std::allocator<uint8_t>());
    ASSERT_EQ("{\n  \"text\": \"test\"\n}", os.str());
}

TEST(DebugStringUtilTest, toJsonStreamIndent2WithPolymorphicAlloc)
{
    std::ostringstream os;
    const DummyObject<pmr::PolymorphicAllocator<uint8_t>> dummyObject;
    toJsonStream(dummyObject, os, 2, pmr::PolymorphicAllocator<uint8_t>());
    ASSERT_EQ("{\n  \"text\": \"test\"\n}", os.str());
}

TEST(DebugStringUtilTest, toJsonStreamFilter)
{
    std::ostringstream os;
    const DummyObject<> dummyObject;
    toJsonStream(dummyObject, os, DepthWalkFilter(0));
    ASSERT_EQ("{\n}", os.str());
}

TEST(DebugStringUtilTest, toJsonStreamFilterWithAlloc)
{
    std::ostringstream os;
    const DummyObject<> dummyObject;
    toJsonStream(dummyObject, os, DefaultWalkFilter(), std::allocator<uint8_t>());
    ASSERT_EQ("{\n    \"text\": \"test\"\n}", os.str());
}

TEST(DebugStringUtilTest, toJsonStreamFilterWithPolymorphicAlloc)
{
    std::ostringstream os;
    const DummyObject<pmr::PolymorphicAllocator<uint8_t>> dummyObject;
    toJsonStream(dummyObject, os, BasicDefaultWalkFilter<pmr::PolymorphicAllocator<uint8_t>>(),
            pmr::PolymorphicAllocator<uint8_t>());
    ASSERT_EQ("{\n    \"text\": \"test\"\n}", os.str());
}

TEST(DebugStringUtilTest, toJsonStreamIndent2Filter)
{
    std::ostringstream os;
    const DummyObject<> dummyObject;
    toJsonStream(dummyObject, os, 2, DefaultWalkFilter());
    ASSERT_EQ("{\n  \"text\": \"test\"\n}", os.str());
}

TEST(DebugStringUtilTest, toJsonStreamIndent2FilterWithAlloc)
{
    std::ostringstream os;
    const DummyObject<> dummyObject;
    toJsonStream(dummyObject, os, 2, DepthWalkFilter(0), std::allocator<uint8_t>());
    ASSERT_EQ("{\n}", os.str());
}

TEST(DebugStringUtilTest, toJsonStreamIndent2FilterWithPolymorphicAlloc)
{
    std::ostringstream os;
    const DummyObject<pmr::PolymorphicAllocator<uint8_t>> dummyObject;
    toJsonStream(dummyObject, os, 2, BasicDepthWalkFilter<pmr::PolymorphicAllocator<uint8_t>>(0),
            pmr::PolymorphicAllocator<uint8_t>());
    ASSERT_EQ("{\n}", os.str());
}

TEST(DebugStringUtilTest, toJsonStringDefault)
{
    const DummyObject<> dummyObject;
    ASSERT_EQ("{\n    \"text\": \"test\"\n}", toJsonString(dummyObject));
}

TEST(DebugStringUtilTest, toJsonStringDefaultWithAlloc)
{
    const DummyObject<> dummyObject;
    ASSERT_EQ("{\n    \"text\": \"test\"\n}", toJsonString(dummyObject, std::allocator<uint8_t>()));
}

TEST(DebugStringUtilTest, toJsonStringDefaultWithPolymorphicAlloc)
{
    const DummyObject<pmr::PolymorphicAllocator<uint8_t>> dummyObject;
    ASSERT_EQ("{\n    \"text\": \"test\"\n}", toJsonString(dummyObject, pmr::PolymorphicAllocator<uint8_t>()));
}

TEST(DebugStringUtilTest, toJsonStringIndent2)
{
    const DummyObject<> dummyObject;
    ASSERT_EQ("{\n  \"text\": \"test\"\n}", toJsonString(dummyObject, 2));
}

TEST(DebugStringUtilTest, toJsonStringIndent2WithAlloc)
{
    const DummyObject<> dummyObject;
    ASSERT_EQ("{\n  \"text\": \"test\"\n}", toJsonString(dummyObject, 2, std::allocator<uint8_t>()));
}

TEST(DebugStringUtilTest, toJsonStringIndent2WithPolymorphicAlloc)
{
    const DummyObject<pmr::PolymorphicAllocator<uint8_t>> dummyObject;
    ASSERT_EQ("{\n  \"text\": \"test\"\n}",
            toJsonString(dummyObject, 2, pmr::PolymorphicAllocator<uint8_t>()));
}

TEST(DebugStringUtilTest, toJsonStringFilter)
{
    const DummyObject<> dummyObject;
    ASSERT_EQ("{\n    \"text\": \"test\"\n}", toJsonString(dummyObject, DefaultWalkFilter()));
}

TEST(DebugStringUtilTest, toJsonStringFilterWithAlloc)
{
    const DummyObject<> dummyObject;
    ASSERT_EQ("{\n    \"text\": \"test\"\n}",
            toJsonString(dummyObject, DefaultWalkFilter(), std::allocator<uint8_t>()));
}

TEST(DebugStringUtilTest, toJsonStringFilterWithPolymorphicAlloc)
{
    const DummyObject<pmr::PolymorphicAllocator<uint8_t>> dummyObject;
    ASSERT_EQ("{\n    \"text\": \"test\"\n}",
            toJsonString(dummyObject, BasicDefaultWalkFilter<pmr::PolymorphicAllocator<uint8_t>>(),
                    pmr::PolymorphicAllocator<uint8_t>()));
}

TEST(DebugStringUtilTest, toJsonStringIndent2Filter)
{
    const DummyObject<> dummyObject;
    ASSERT_EQ("{\n}", toJsonString(dummyObject, 2, DepthWalkFilter(0)));
}

TEST(DebugStringUtilTest, toJsonStringIndent2FilterWithAlloc)
{
    const DummyObject<> dummyObject;
    ASSERT_EQ("{\n  \"text\": \"test\"\n}",
            toJsonString(dummyObject, 2, DefaultWalkFilter(), std::allocator<uint8_t>()));
}

TEST(DebugStringUtilTest, toJsonStringIndent2FilterWithPolymorphicAlloc)
{
    const DummyObject<pmr::PolymorphicAllocator<uint8_t>> dummyObject;
    ASSERT_EQ("{\n  \"text\": \"test\"\n}",
            toJsonString(dummyObject, 2, BasicDefaultWalkFilter<pmr::PolymorphicAllocator<uint8_t>>(),
                    pmr::PolymorphicAllocator<uint8_t>()));
}

TEST(DebugStringUtilTest, toJsonFileDefault)
{
    const DummyObject<> dummyObject;
    const std::string fileName = "DebugStringUtilTest_default.json";
    toJsonFile(dummyObject, fileName);

    std::ifstream is(fileName);
    std::stringstream ss;
    ss << is.rdbuf();
    ASSERT_EQ("{\n    \"text\": \"test\"\n}", ss.str());
}

TEST(DebugStringUtilTest, toJsonFileDefaultWithAlloc)
{
    const DummyObject<> dummyObject;
    const std::string fileName = "DebugStringUtilTest_defaultWithAlloc.json";
    toJsonFile(dummyObject, fileName, std::allocator<uint8_t>());

    std::ifstream is(fileName);
    std::stringstream ss;
    ss << is.rdbuf();
    ASSERT_EQ("{\n    \"text\": \"test\"\n}", ss.str());
}

TEST(DebugStringUtilTest, toJsonFileDefaultWithPolymorphicAlloc)
{
    const DummyObject<pmr::PolymorphicAllocator<uint8_t>> dummyObject;
    const std::string fileName = "DebugStringUtilTest_defaultWithPolymorphicAlloc.json";
    toJsonFile(dummyObject, fileName, pmr::PolymorphicAllocator<uint8_t>());

    std::ifstream is(fileName);
    std::stringstream ss;
    ss << is.rdbuf();
    ASSERT_EQ("{\n    \"text\": \"test\"\n}", ss.str());
}

TEST(DebugStringUtilTest, toJsonFileIndent2)
{
    const DummyObject<> dummyObject;
    const std::string fileName = "DebugStringUtilTest_indent2.json";
    toJsonFile(dummyObject, fileName, 2);

    std::ifstream is(fileName);
    std::stringstream ss;
    ss << is.rdbuf();
    ASSERT_EQ("{\n  \"text\": \"test\"\n}", ss.str());
}

TEST(DebugStringUtilTest, toJsonFileIndent2WithAlloc)
{
    const DummyObject<> dummyObject;
    const std::string fileName = "DebugStringUtilTest_indent2WithAlloc.json";
    toJsonFile(dummyObject, fileName, 2, std::allocator<uint8_t>());

    std::ifstream is(fileName);
    std::stringstream ss;
    ss << is.rdbuf();
    ASSERT_EQ("{\n  \"text\": \"test\"\n}", ss.str());
}

TEST(DebugStringUtilTest, toJsonFileIndent2WithPolymorphicAlloc)
{
    const DummyObject<pmr::PolymorphicAllocator<uint8_t>> dummyObject;
    const std::string fileName = "DebugStringUtilTest_indent2WithPolymorphicAlloc.json";
    toJsonFile(dummyObject, fileName, 2, pmr::PolymorphicAllocator<uint8_t>());

    std::ifstream is(fileName);
    std::stringstream ss;
    ss << is.rdbuf();
    ASSERT_EQ("{\n  \"text\": \"test\"\n}", ss.str());
}

TEST(DebugStringUtilTest, toJsonFileFilter)
{
    const DummyObject<> dummyObject;
    const std::string fileName = "DebugStringUtilTest_filter.json";
    toJsonFile(dummyObject, fileName, DefaultWalkFilter());

    std::ifstream is(fileName);
    std::stringstream ss;
    ss << is.rdbuf();
    ASSERT_EQ("{\n    \"text\": \"test\"\n}", ss.str());
}

TEST(DebugStringUtilTest, toJsonFileFilterWithAlloc)
{
    const DummyObject<> dummyObject;
    DefaultWalkFilter defaultWalkFilter;
    const std::string fileName = "DebugStringUtilTest_filterWithAlloc.json";
    toJsonFile(dummyObject, fileName, defaultWalkFilter, std::allocator<uint8_t>());

    std::ifstream is(fileName);
    std::stringstream ss;
    ss << is.rdbuf();
    ASSERT_EQ("{\n    \"text\": \"test\"\n}", ss.str());
}

TEST(DebugStringUtilTest, toJsonFileFilterWithPolymorphicAlloc)
{
    const DummyObject<pmr::PolymorphicAllocator<uint8_t>> dummyObject;
    BasicDefaultWalkFilter<pmr::PolymorphicAllocator<uint8_t>> defaultWalkFilter;
    const std::string fileName = "DebugStringUtilTest_filterWithPolymorphicAlloc.json";
    toJsonFile(dummyObject, fileName, defaultWalkFilter, pmr::PolymorphicAllocator<uint8_t>());

    std::ifstream is(fileName);
    std::stringstream ss;
    ss << is.rdbuf();
    ASSERT_EQ("{\n    \"text\": \"test\"\n}", ss.str());
}

TEST(DebugStringUtilTest, toJsonFileIndent2Filter)
{
    const DummyObject<> dummyObject;
    DepthWalkFilter depthWalkFilter(0);
    const std::string fileName = "DebugStringUtilTest_indent2Filter.json";
    toJsonFile(dummyObject, fileName, 2, depthWalkFilter);

    std::ifstream is(fileName);
    std::stringstream ss;
    ss << is.rdbuf();
    ASSERT_EQ("{\n}", ss.str());
}

TEST(DebugStringUtilTest, toJsonFileIndent2FilterWithAlloc)
{
    const DummyObject<> dummyObject;
    DefaultWalkFilter defaultWalkFilter;
    const std::string fileName = "DebugStringUtilTest_indent2FilterWithAlloc.json";
    toJsonFile(dummyObject, fileName, 2, defaultWalkFilter, std::allocator<uint8_t>());

    std::ifstream is(fileName);
    std::stringstream ss;
    ss << is.rdbuf();
    ASSERT_EQ("{\n  \"text\": \"test\"\n}", ss.str());
}

TEST(DebugStringUtilTest, toJsonFileIndent2FilterWithPolymorphicAlloc)
{
    const DummyObject<pmr::PolymorphicAllocator<uint8_t>> dummyObject;
    BasicDefaultWalkFilter<pmr::PolymorphicAllocator<uint8_t>> defaultWalkFilter;
    const std::string fileName = "DebugStringUtilTest_indent2FilterWithPolymorphicAlloc.json";
    toJsonFile(dummyObject, fileName, 2, defaultWalkFilter, pmr::PolymorphicAllocator<uint8_t>());

    std::ifstream is(fileName);
    std::stringstream ss;
    ss << is.rdbuf();
    ASSERT_EQ("{\n  \"text\": \"test\"\n}", ss.str());
}

} // namespace zserio
