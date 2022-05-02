#include "gtest/gtest.h"

#include <sstream>

#include "zserio/DebugStringUtil.h"
#include "zserio/Reflectable.h"

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
    IReflectablePtr reflectable(const std::allocator<uint8_t>& allocator)
    {
        class Reflectable : public ReflectableAllocatorHolderBase<std::allocator<uint8_t>>
        {
        public:
            explicit Reflectable(const std::allocator<uint8_t>& allocator) :
                    ReflectableAllocatorHolderBase<std::allocator<uint8_t>>(DUMMY_OBJECT_TYPE_INFO, allocator)
            {}

            virtual IReflectablePtr getField(StringView name) const override
            {
                if (name == makeStringView("text"))
                {
                    return ReflectableFactory::getString(m_text, get_allocator());
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
            const std::string m_text = "test";
        };

        return std::allocate_shared<Reflectable>(allocator, allocator);
    }
};

const std::string TEST_FILE_NAME = "DebugStringUtilTest.bin";

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

TEST(DebugStringUtilTest, toDebugStreamDefaultFilter)
{
    std::ostringstream os;
    DummyObject dummyObject;
    toDebugStream(dummyObject, os, DepthWalkFilter(0));
    ASSERT_EQ("{\n}", os.str());
}

TEST(DebugStringUtilTest, toDebugStreamDefaultFilterWithAlloc)
{
    std::ostringstream os;
    DummyObject dummyObject;
    toDebugStream(dummyObject, os, DefaultWalkFilter(), std::allocator<uint8_t>());
    ASSERT_EQ("{\n    \"text\": \"test\"\n}", os.str());
}

TEST(DebugStringUtilTest, toDebugStreamIndent2DefaultFilter)
{
    std::ostringstream os;
    DummyObject dummyObject;
    toDebugStream(dummyObject, os, 2, DefaultWalkFilter());
    ASSERT_EQ("{\n  \"text\": \"test\"\n}", os.str());
}

TEST(DebugStringUtilTest, toDebugStreamIndent2DefaultFilterWithAlloc)
{
    std::ostringstream os;
    DummyObject dummyObject;
    toDebugStream(dummyObject, os, 2, DepthWalkFilter(0), std::allocator<uint8_t>());
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

TEST(DebugStringUtilTest, toDebugStringDefaultFilter)
{
    DummyObject dummyObject;
    ASSERT_EQ("{\n    \"text\": \"test\"\n}", toDebugString(dummyObject, DefaultWalkFilter()));
}

TEST(DebugStringUtilTest, toDebugStringDefaultFilterWithAlloc)
{
    DummyObject dummyObject;
    ASSERT_EQ("{\n    \"text\": \"test\"\n}",
            toDebugString(dummyObject, DefaultWalkFilter(), std::allocator<uint8_t>()));
}

TEST(DebugStringUtilTest, toDebugStringIndent2DefaultFilter)
{
    DummyObject dummyObject;
    ASSERT_EQ("{\n}", toDebugString(dummyObject, 2, DepthWalkFilter(0)));
}

TEST(DebugStringUtilTest, toDebugStringIndent2DefaultFilterWithAlloc)
{
    DummyObject dummyObject;
    ASSERT_EQ("{\n  \"text\": \"test\"\n}",
            toDebugString(dummyObject, 2, DefaultWalkFilter(), std::allocator<uint8_t>()));
}

TEST(DebugStringUtilTest, toDebugFileDefault)
{
    DummyObject dummyObject;
    toDebugFile(dummyObject, TEST_FILE_NAME);

    std::ifstream is(TEST_FILE_NAME);
    std::stringstream ss;
    ss << is.rdbuf();
    ASSERT_EQ("{\n    \"text\": \"test\"\n}", ss.str());
}

TEST(DebugStringUtilTest, toDebugFileDefaultWithAlloc)
{
    DummyObject dummyObject;
    toDebugFile(dummyObject, TEST_FILE_NAME, std::allocator<uint8_t>());

    std::ifstream is(TEST_FILE_NAME);
    std::stringstream ss;
    ss << is.rdbuf();
    ASSERT_EQ("{\n    \"text\": \"test\"\n}", ss.str());
}

TEST(DebugStringUtilTest, toDebugFileIndent2)
{
    DummyObject dummyObject;
    toDebugFile(dummyObject, TEST_FILE_NAME, 2);

    std::ifstream is(TEST_FILE_NAME);
    std::stringstream ss;
    ss << is.rdbuf();
    ASSERT_EQ("{\n  \"text\": \"test\"\n}", ss.str());
}

TEST(DebugStringUtilTest, toDebugFileIndent2WithAlloc)
{
    DummyObject dummyObject;
    toDebugFile(dummyObject, TEST_FILE_NAME, 2, std::allocator<uint8_t>());

    std::ifstream is(TEST_FILE_NAME);
    std::stringstream ss;
    ss << is.rdbuf();
    ASSERT_EQ("{\n  \"text\": \"test\"\n}", ss.str());
}

TEST(DebugStringUtilTest, toDebugFileDefaultFilter)
{
    DummyObject dummyObject;
    toDebugFile(dummyObject, TEST_FILE_NAME, DefaultWalkFilter());

    std::ifstream is(TEST_FILE_NAME);
    std::stringstream ss;
    ss << is.rdbuf();
    ASSERT_EQ("{\n    \"text\": \"test\"\n}", ss.str());
}

TEST(DebugStringUtilTest, toDebugFileDefaultFilterWithAlloc)
{
    DummyObject dummyObject;
    DefaultWalkFilter defaultWalkFilter;
    toDebugFile(dummyObject, TEST_FILE_NAME, defaultWalkFilter, std::allocator<uint8_t>());

    std::ifstream is(TEST_FILE_NAME);
    std::stringstream ss;
    ss << is.rdbuf();
    ASSERT_EQ("{\n    \"text\": \"test\"\n}", ss.str());
}

TEST(DebugStringUtilTest, toDebugFileIndent2DefaultFilter)
{
    DummyObject dummyObject;
    DepthWalkFilter depthWalkFilter(0);
    toDebugFile(dummyObject, TEST_FILE_NAME, 2, depthWalkFilter);

    std::ifstream is(TEST_FILE_NAME);
    std::stringstream ss;
    ss << is.rdbuf();
    ASSERT_EQ("{\n}", ss.str());
}

TEST(DebugStringUtilTest, toDebugFileIndent2DefaultFilterWithAlloc)
{
    DummyObject dummyObject;
    DefaultWalkFilter defaultWalkFilter;
    toDebugFile(dummyObject, TEST_FILE_NAME, 2, defaultWalkFilter, std::allocator<uint8_t>());

    std::ifstream is(TEST_FILE_NAME);
    std::stringstream ss;
    ss << is.rdbuf();
    ASSERT_EQ("{\n  \"text\": \"test\"\n}", ss.str());
}

} // namespace zserio
