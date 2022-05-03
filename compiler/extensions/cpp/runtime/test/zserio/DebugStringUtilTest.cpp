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

struct DummyObject
{
    template <typename ALLOC>
    IBasicReflectablePtr<ALLOC> reflectable(const ALLOC& allocator)
    {
        class Reflectable : public ReflectableAllocatorHolderBase<ALLOC>
        {
        public:
            explicit Reflectable(const ALLOC& allocator) :
                    ReflectableAllocatorHolderBase<ALLOC>(DUMMY_OBJECT_TYPE_INFO, allocator)
            {}

            virtual IBasicReflectablePtr<ALLOC> getField(StringView name) const override
            {
                if (name == makeStringView("text"))
                {
                    return BasicReflectableFactory<ALLOC>::getString(m_text, ReflectableAllocatorHolderBase<ALLOC>::get_allocator());
                }
                throw CppRuntimeException("Field '") + name + "' doesn't exist in 'DummyNested'!";
            }

            virtual void write(BitStreamWriter&) override
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

TEST(DebugStringUtilTest, toDebugStreamDefault)
{
    std::ostringstream os;
    DummyObject dummyObject;
    toDebugStream(dummyObject, os);
    ASSERT_EQ("{\n    \"text\": \"test\"\n}", os.str());
}

TEST(DebugStringUtilTest, toDebugStreamDefaultWithAlloc)
{
    std::ostringstream os;
    DummyObject dummyObject;
    toDebugStream(dummyObject, os, std::allocator<uint8_t>());
    ASSERT_EQ("{\n    \"text\": \"test\"\n}", os.str());
}

TEST(DebugStringUtilTest, toDebugStreamDefaultWithPolymorphicAlloc)
{
    std::ostringstream os;
    DummyObject dummyObject;
    toDebugStream(dummyObject, os, pmr::PolymorphicAllocator<uint8_t>());
    ASSERT_EQ("{\n    \"text\": \"test\"\n}", os.str());
}

TEST(DebugStringUtilTest, toDebugStreamIndent2)
{
    std::ostringstream os;
    DummyObject dummyObject;
    toDebugStream(dummyObject, os, 2);
    ASSERT_EQ("{\n  \"text\": \"test\"\n}", os.str());
}

TEST(DebugStringUtilTest, toDebugStreamIndent2WithAlloc)
{
    std::ostringstream os;
    DummyObject dummyObject;
    toDebugStream(dummyObject, os, 2, std::allocator<uint8_t>());
    ASSERT_EQ("{\n  \"text\": \"test\"\n}", os.str());
}

TEST(DebugStringUtilTest, toDebugStreamIndent2WithPolymorphicAlloc)
{
    std::ostringstream os;
    DummyObject dummyObject;
    toDebugStream(dummyObject, os, 2, pmr::PolymorphicAllocator<uint8_t>());
    ASSERT_EQ("{\n  \"text\": \"test\"\n}", os.str());
}

TEST(DebugStringUtilTest, toDebugStreamFilter)
{
    std::ostringstream os;
    DummyObject dummyObject;
    toDebugStream(dummyObject, os, DepthWalkFilter(0));
    ASSERT_EQ("{\n}", os.str());
}

TEST(DebugStringUtilTest, toDebugStreamFilterWithAlloc)
{
    std::ostringstream os;
    DummyObject dummyObject;
    toDebugStream(dummyObject, os, DefaultWalkFilter(), std::allocator<uint8_t>());
    ASSERT_EQ("{\n    \"text\": \"test\"\n}", os.str());
}

TEST(DebugStringUtilTest, toDebugStreamFilterWithPolymorphicAlloc)
{
    std::ostringstream os;
    DummyObject dummyObject;
    toDebugStream(dummyObject, os, BasicDefaultWalkFilter<pmr::PolymorphicAllocator<uint8_t>>(),
            pmr::PolymorphicAllocator<uint8_t>());
    ASSERT_EQ("{\n    \"text\": \"test\"\n}", os.str());
}

TEST(DebugStringUtilTest, toDebugStreamIndent2Filter)
{
    std::ostringstream os;
    DummyObject dummyObject;
    toDebugStream(dummyObject, os, 2, DefaultWalkFilter());
    ASSERT_EQ("{\n  \"text\": \"test\"\n}", os.str());
}

TEST(DebugStringUtilTest, toDebugStreamIndent2FilterWithAlloc)
{
    std::ostringstream os;
    DummyObject dummyObject;
    toDebugStream(dummyObject, os, 2, DepthWalkFilter(0), std::allocator<uint8_t>());
    ASSERT_EQ("{\n}", os.str());
}

TEST(DebugStringUtilTest, toDebugStreamIndent2FilterWithPolymorphicAlloc)
{
    std::ostringstream os;
    DummyObject dummyObject;
    toDebugStream(dummyObject, os, 2, BasicDepthWalkFilter<pmr::PolymorphicAllocator<uint8_t>>(0),
            pmr::PolymorphicAllocator<uint8_t>());
    ASSERT_EQ("{\n}", os.str());
}

TEST(DebugStringUtilTest, toDebugStringDefault)
{
    DummyObject dummyObject;
    ASSERT_EQ("{\n    \"text\": \"test\"\n}", toDebugString(dummyObject));
}

TEST(DebugStringUtilTest, toDebugStringDefaultWithAlloc)
{
    DummyObject dummyObject;
    ASSERT_EQ("{\n    \"text\": \"test\"\n}", toDebugString(dummyObject, std::allocator<uint8_t>()));
}

TEST(DebugStringUtilTest, toDebugStringDefaultWithPolymorphicAlloc)
{
    DummyObject dummyObject;
    ASSERT_EQ("{\n    \"text\": \"test\"\n}", toDebugString(dummyObject, pmr::PolymorphicAllocator<uint8_t>()));
}

TEST(DebugStringUtilTest, toDebugStringIndent2)
{
    DummyObject dummyObject;
    ASSERT_EQ("{\n  \"text\": \"test\"\n}", toDebugString(dummyObject, 2));
}

TEST(DebugStringUtilTest, toDebugStringIndent2WithAlloc)
{
    DummyObject dummyObject;
    ASSERT_EQ("{\n  \"text\": \"test\"\n}", toDebugString(dummyObject, 2, std::allocator<uint8_t>()));
}

TEST(DebugStringUtilTest, toDebugStringIndent2WithPolymorphicAlloc)
{
    DummyObject dummyObject;
    ASSERT_EQ("{\n  \"text\": \"test\"\n}",
            toDebugString(dummyObject, 2, pmr::PolymorphicAllocator<uint8_t>()));
}

TEST(DebugStringUtilTest, toDebugStringFilter)
{
    DummyObject dummyObject;
    ASSERT_EQ("{\n    \"text\": \"test\"\n}", toDebugString(dummyObject, DefaultWalkFilter()));
}

TEST(DebugStringUtilTest, toDebugStringFilterWithAlloc)
{
    DummyObject dummyObject;
    ASSERT_EQ("{\n    \"text\": \"test\"\n}",
            toDebugString(dummyObject, DefaultWalkFilter(), std::allocator<uint8_t>()));
}

TEST(DebugStringUtilTest, toDebugStringFilterWithPolymorphicAlloc)
{
    DummyObject dummyObject;
    ASSERT_EQ("{\n    \"text\": \"test\"\n}",
            toDebugString(dummyObject, BasicDefaultWalkFilter<pmr::PolymorphicAllocator<uint8_t>>(),
                    pmr::PolymorphicAllocator<uint8_t>()));
}

TEST(DebugStringUtilTest, toDebugStringIndent2Filter)
{
    DummyObject dummyObject;
    ASSERT_EQ("{\n}", toDebugString(dummyObject, 2, DepthWalkFilter(0)));
}

TEST(DebugStringUtilTest, toDebugStringIndent2FilterWithAlloc)
{
    DummyObject dummyObject;
    ASSERT_EQ("{\n  \"text\": \"test\"\n}",
            toDebugString(dummyObject, 2, DefaultWalkFilter(), std::allocator<uint8_t>()));
}

TEST(DebugStringUtilTest, toDebugStringIndent2FilterWithPolymorphicAlloc)
{
    DummyObject dummyObject;
    ASSERT_EQ("{\n  \"text\": \"test\"\n}",
            toDebugString(dummyObject, 2, BasicDefaultWalkFilter<pmr::PolymorphicAllocator<uint8_t>>(),
                    pmr::PolymorphicAllocator<uint8_t>()));
}

TEST(DebugStringUtilTest, toDebugFileDefault)
{
    DummyObject dummyObject;
    const std::string fileName = "DebugStringUtilTest_default.bin";
    toDebugFile(dummyObject, fileName);

    std::ifstream is(fileName);
    std::stringstream ss;
    ss << is.rdbuf();
    ASSERT_EQ("{\n    \"text\": \"test\"\n}", ss.str());
}

TEST(DebugStringUtilTest, toDebugFileDefaultWithAlloc)
{
    DummyObject dummyObject;
    const std::string fileName = "DebugStringUtilTest_defaultWithAlloc.bin";
    toDebugFile(dummyObject, fileName, std::allocator<uint8_t>());

    std::ifstream is(fileName);
    std::stringstream ss;
    ss << is.rdbuf();
    ASSERT_EQ("{\n    \"text\": \"test\"\n}", ss.str());
}

TEST(DebugStringUtilTest, toDebugFileDefaultWithPolymorphicAlloc)
{
    DummyObject dummyObject;
    const std::string fileName = "DebugStringUtilTest_defaultWithPolymorphicAlloc.bin";
    toDebugFile(dummyObject, fileName, pmr::PolymorphicAllocator<uint8_t>());

    std::ifstream is(fileName);
    std::stringstream ss;
    ss << is.rdbuf();
    ASSERT_EQ("{\n    \"text\": \"test\"\n}", ss.str());
}

TEST(DebugStringUtilTest, toDebugFileIndent2)
{
    DummyObject dummyObject;
    const std::string fileName = "DebugStringUtilTest_indent2.bin";
    toDebugFile(dummyObject, fileName, 2);

    std::ifstream is(fileName);
    std::stringstream ss;
    ss << is.rdbuf();
    ASSERT_EQ("{\n  \"text\": \"test\"\n}", ss.str());
}

TEST(DebugStringUtilTest, toDebugFileIndent2WithAlloc)
{
    DummyObject dummyObject;
    const std::string fileName = "DebugStringUtilTest_indent2WithAlloc.bin";
    toDebugFile(dummyObject, fileName, 2, std::allocator<uint8_t>());

    std::ifstream is(fileName);
    std::stringstream ss;
    ss << is.rdbuf();
    ASSERT_EQ("{\n  \"text\": \"test\"\n}", ss.str());
}

TEST(DebugStringUtilTest, toDebugFileIndent2WithPolymorphicAlloc)
{
    DummyObject dummyObject;
    const std::string fileName = "DebugStringUtilTest_indent2WithPolymorphicAlloc.bin";
    toDebugFile(dummyObject, fileName, 2, pmr::PolymorphicAllocator<uint8_t>());

    std::ifstream is(fileName);
    std::stringstream ss;
    ss << is.rdbuf();
    ASSERT_EQ("{\n  \"text\": \"test\"\n}", ss.str());
}

TEST(DebugStringUtilTest, toDebugFileFilter)
{
    DummyObject dummyObject;
    const std::string fileName = "DebugStringUtilTest_filter.bin";
    toDebugFile(dummyObject, fileName, DefaultWalkFilter());

    std::ifstream is(fileName);
    std::stringstream ss;
    ss << is.rdbuf();
    ASSERT_EQ("{\n    \"text\": \"test\"\n}", ss.str());
}

TEST(DebugStringUtilTest, toDebugFileFilterWithAlloc)
{
    DummyObject dummyObject;
    DefaultWalkFilter defaultWalkFilter;
    const std::string fileName = "DebugStringUtilTest_filterWithAlloc.bin";
    toDebugFile(dummyObject, fileName, defaultWalkFilter, std::allocator<uint8_t>());

    std::ifstream is(fileName);
    std::stringstream ss;
    ss << is.rdbuf();
    ASSERT_EQ("{\n    \"text\": \"test\"\n}", ss.str());
}

TEST(DebugStringUtilTest, toDebugFileFilterWithPolymorphicAlloc)
{
    DummyObject dummyObject;
    BasicDefaultWalkFilter<pmr::PolymorphicAllocator<uint8_t>> defaultWalkFilter;
    const std::string fileName = "DebugStringUtilTest_filterWithPolymorphicAlloc.bin";
    toDebugFile(dummyObject, fileName, defaultWalkFilter, pmr::PolymorphicAllocator<uint8_t>());

    std::ifstream is(fileName);
    std::stringstream ss;
    ss << is.rdbuf();
    ASSERT_EQ("{\n    \"text\": \"test\"\n}", ss.str());
}

TEST(DebugStringUtilTest, toDebugFileIndent2Filter)
{
    DummyObject dummyObject;
    DepthWalkFilter depthWalkFilter(0);
    const std::string fileName = "DebugStringUtilTest_indent2Filter.bin";
    toDebugFile(dummyObject, fileName, 2, depthWalkFilter);

    std::ifstream is(fileName);
    std::stringstream ss;
    ss << is.rdbuf();
    ASSERT_EQ("{\n}", ss.str());
}

TEST(DebugStringUtilTest, toDebugFileIndent2FilterWithAlloc)
{
    DummyObject dummyObject;
    DefaultWalkFilter defaultWalkFilter;
    const std::string fileName = "DebugStringUtilTest_indent2FilterWithAlloc.bin";
    toDebugFile(dummyObject, fileName, 2, defaultWalkFilter, std::allocator<uint8_t>());

    std::ifstream is(fileName);
    std::stringstream ss;
    ss << is.rdbuf();
    ASSERT_EQ("{\n  \"text\": \"test\"\n}", ss.str());
}

TEST(DebugStringUtilTest, toDebugFileIndent2FilterWithPolymorphicAlloc)
{
    DummyObject dummyObject;
    BasicDefaultWalkFilter<pmr::PolymorphicAllocator<uint8_t>> defaultWalkFilter;
    const std::string fileName = "DebugStringUtilTest_indent2FilterWithPolymorphicAlloc.bin";
    toDebugFile(dummyObject, fileName, 2, defaultWalkFilter, pmr::PolymorphicAllocator<uint8_t>());

    std::ifstream is(fileName);
    std::stringstream ss;
    ss << is.rdbuf();
    ASSERT_EQ("{\n  \"text\": \"test\"\n}", ss.str());
}

} // namespace zserio
