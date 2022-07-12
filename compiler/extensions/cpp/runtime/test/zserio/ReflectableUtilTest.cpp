#include <cmath>

#include "gtest/gtest.h"

#include "zserio/ReflectableUtil.h"
#include "zserio/Reflectable.h"
#include "zserio/ZserioTreeCreatorTestObject.h"

namespace zserio
{

TEST(ReflectableUtilTest, unequalTypeInfo)
{
    ASSERT_FALSE(ReflectableUtil<>::equal(ReflectableFactory::getBool(false),
            ReflectableFactory::getUInt8(13)));
    ASSERT_FALSE(ReflectableUtil<>::equal(ReflectableFactory::getInt8(0),
            ReflectableFactory::getInt16(0)));
    ASSERT_FALSE(ReflectableUtil<>::equal(ReflectableFactory::getInt8(0),
            ReflectableFactory::getUInt8(0)));
    ASSERT_FALSE(ReflectableUtil<>::equal(ReflectableFactory::getUInt64(0),
            ReflectableFactory::getVarSize(0)));
    ASSERT_FALSE(ReflectableUtil<>::equal(ReflectableFactory::getInt8(0),
            ReflectableFactory::getFloat32(0.0)));
    ASSERT_FALSE(ReflectableUtil<>::equal(ReflectableFactory::getFloat16(0.0f),
            ReflectableFactory::getFloat32(0.0f)));
    ASSERT_FALSE(ReflectableUtil<>::equal(ReflectableFactory::getString(""),
            ReflectableFactory::getInt8(0)));
    ASSERT_FALSE(ReflectableUtil<>::equal(ReflectableFactory::getBitBuffer(BitBuffer()),
            ReflectableFactory::getString("")));
}

TEST(ReflectableUtilTest, equalsNullValues)
{
    ASSERT_TRUE(ReflectableUtil<>::equal(nullptr, nullptr));
    ASSERT_FALSE(ReflectableUtil<>::equal(ReflectableFactory::getBool(false), nullptr));
    ASSERT_FALSE(ReflectableUtil<>::equal(nullptr, ReflectableFactory::getInt8(0)));
}

TEST(ReflectableUtilTest, equalBools)
{
    ASSERT_TRUE(ReflectableUtil<>::equal(ReflectableFactory::getBool(true), ReflectableFactory::getBool(true)));
    ASSERT_FALSE(ReflectableUtil<>::equal(ReflectableFactory::getBool(false),
            ReflectableFactory::getBool(true)));
    ASSERT_FALSE(ReflectableUtil<>::equal(ReflectableFactory::getBool(true),
            ReflectableFactory::getBool(false)));
}

TEST(ReflectableUtilTest, equalSingedIntegrals)
{
    ASSERT_TRUE(ReflectableUtil<>::equal(ReflectableFactory::getInt8(INT8_MIN),
            ReflectableFactory::getInt8(INT8_MIN)));
    ASSERT_TRUE(ReflectableUtil<>::equal(ReflectableFactory::getInt8(INT8_MAX),
            ReflectableFactory::getInt8(INT8_MAX)));
    ASSERT_FALSE(ReflectableUtil<>::equal(ReflectableFactory::getInt8(-1),
            ReflectableFactory::getInt8(0)));
    ASSERT_FALSE(ReflectableUtil<>::equal(ReflectableFactory::getInt8(INT8_MIN),
            ReflectableFactory::getInt8(INT8_MAX)));

    ASSERT_TRUE(ReflectableUtil<>::equal(ReflectableFactory::getInt16(INT16_MIN),
            ReflectableFactory::getInt16(INT16_MIN)));
    ASSERT_TRUE(ReflectableUtil<>::equal(ReflectableFactory::getInt16(INT16_MAX),
            ReflectableFactory::getInt16(INT16_MAX)));
    ASSERT_FALSE(ReflectableUtil<>::equal(ReflectableFactory::getInt16(-1),
            ReflectableFactory::getInt16(0)));
    ASSERT_FALSE(ReflectableUtil<>::equal(ReflectableFactory::getInt16(INT16_MIN),
            ReflectableFactory::getInt16(INT16_MAX)));

    ASSERT_TRUE(ReflectableUtil<>::equal(ReflectableFactory::getInt32(INT32_MIN),
            ReflectableFactory::getInt32(INT32_MIN)));
    ASSERT_TRUE(ReflectableUtil<>::equal(ReflectableFactory::getInt32(INT32_MAX),
            ReflectableFactory::getInt32(INT32_MAX)));
    ASSERT_FALSE(ReflectableUtil<>::equal(ReflectableFactory::getInt32(-1),
            ReflectableFactory::getInt32(0)));
    ASSERT_FALSE(ReflectableUtil<>::equal(ReflectableFactory::getInt32(INT32_MIN),
            ReflectableFactory::getInt32(INT32_MAX)));

    ASSERT_TRUE(ReflectableUtil<>::equal(ReflectableFactory::getInt64(INT64_MIN),
            ReflectableFactory::getInt64(INT64_MIN)));
    ASSERT_TRUE(ReflectableUtil<>::equal(ReflectableFactory::getInt64(INT64_MAX),
            ReflectableFactory::getInt64(INT64_MAX)));
    ASSERT_FALSE(ReflectableUtil<>::equal(ReflectableFactory::getInt64(-1),
            ReflectableFactory::getInt64(0)));
    ASSERT_FALSE(ReflectableUtil<>::equal(ReflectableFactory::getInt64(INT64_MIN),
            ReflectableFactory::getInt64(INT64_MAX)));
}

TEST(ReflectableUtilTest, equalUnsignedIntegrals)
{
    ASSERT_TRUE(ReflectableUtil<>::equal(ReflectableFactory::getUInt8(0),
            ReflectableFactory::getUInt8(0)));
    ASSERT_TRUE(ReflectableUtil<>::equal(ReflectableFactory::getUInt8(UINT8_MAX),
            ReflectableFactory::getUInt8(UINT8_MAX)));
    ASSERT_FALSE(ReflectableUtil<>::equal(ReflectableFactory::getUInt8(0),
            ReflectableFactory::getUInt8(1)));
    ASSERT_FALSE(ReflectableUtil<>::equal(ReflectableFactory::getUInt8(0),
            ReflectableFactory::getUInt8(UINT8_MAX)));

    ASSERT_TRUE(ReflectableUtil<>::equal(ReflectableFactory::getUInt16(0),
            ReflectableFactory::getUInt16(0)));
    ASSERT_TRUE(ReflectableUtil<>::equal(ReflectableFactory::getUInt16(UINT16_MAX),
            ReflectableFactory::getUInt16(UINT16_MAX)));
    ASSERT_FALSE(ReflectableUtil<>::equal(ReflectableFactory::getUInt16(0),
            ReflectableFactory::getUInt16(1)));
    ASSERT_FALSE(ReflectableUtil<>::equal(ReflectableFactory::getUInt16(0),
            ReflectableFactory::getUInt16(UINT16_MAX)));

    ASSERT_TRUE(ReflectableUtil<>::equal(ReflectableFactory::getUInt32(0),
            ReflectableFactory::getUInt32(0)));
    ASSERT_TRUE(ReflectableUtil<>::equal(ReflectableFactory::getUInt32(UINT32_MAX),
            ReflectableFactory::getUInt32(UINT32_MAX)));
    ASSERT_FALSE(ReflectableUtil<>::equal(ReflectableFactory::getUInt32(0),
            ReflectableFactory::getUInt32(1)));
    ASSERT_FALSE(ReflectableUtil<>::equal(ReflectableFactory::getUInt32(0),
            ReflectableFactory::getUInt32(UINT32_MAX)));

    ASSERT_TRUE(ReflectableUtil<>::equal(ReflectableFactory::getUInt64(0),
            ReflectableFactory::getUInt64(0)));
    ASSERT_TRUE(ReflectableUtil<>::equal(ReflectableFactory::getUInt64(UINT64_MAX),
            ReflectableFactory::getUInt64(UINT64_MAX)));
    ASSERT_FALSE(ReflectableUtil<>::equal(ReflectableFactory::getUInt64(0),
            ReflectableFactory::getUInt64(1)));
    ASSERT_FALSE(ReflectableUtil<>::equal(ReflectableFactory::getUInt64(0),
            ReflectableFactory::getUInt64(UINT64_MAX)));
}

TEST(ReflectableUtilTest, equalFloatingPoints)
{
    ASSERT_TRUE(ReflectableUtil<>::equal(ReflectableFactory::getFloat16(0.0f),
            ReflectableFactory::getFloat16(0.0f)));
    ASSERT_FALSE(ReflectableUtil<>::equal(ReflectableFactory::getFloat16(-1.0f),
            ReflectableFactory::getFloat16(1.0f)));

    ASSERT_TRUE(ReflectableUtil<>::equal(ReflectableFactory::getFloat32(0.0f),
            ReflectableFactory::getFloat32(0.0f)));
    ASSERT_FALSE(ReflectableUtil<>::equal(ReflectableFactory::getFloat32(-1.0f),
            ReflectableFactory::getFloat32(1.0f)));

    ASSERT_TRUE(ReflectableUtil<>::equal(ReflectableFactory::getFloat64(0.0),
            ReflectableFactory::getFloat64(0.0)));
    ASSERT_FALSE(ReflectableUtil<>::equal(ReflectableFactory::getFloat64(-1.0),
            ReflectableFactory::getFloat64(1.0)));

    ASSERT_TRUE(ReflectableUtil<>::equal(ReflectableFactory::getFloat64(static_cast<double>(NAN)),
            ReflectableFactory::getFloat64(static_cast<double>(NAN))));
    ASSERT_TRUE(ReflectableUtil<>::equal(ReflectableFactory::getFloat64(static_cast<double>(INFINITY)),
            ReflectableFactory::getFloat64(static_cast<double>(INFINITY))));
    ASSERT_TRUE(ReflectableUtil<>::equal(ReflectableFactory::getFloat64(-static_cast<double>(INFINITY)),
            ReflectableFactory::getFloat64(-static_cast<double>(INFINITY))));

    ASSERT_FALSE(ReflectableUtil<>::equal(ReflectableFactory::getFloat64(static_cast<double>(NAN)),
            ReflectableFactory::getFloat64(static_cast<double>(INFINITY))));
    ASSERT_FALSE(ReflectableUtil<>::equal(ReflectableFactory::getFloat64(static_cast<double>(INFINITY)),
            ReflectableFactory::getFloat64(-static_cast<double>(INFINITY))));
}

TEST(ReflectableUtilTest, equalStrings)
{
    ASSERT_TRUE(ReflectableUtil<>::equal(ReflectableFactory::getString(""),
            ReflectableFactory::getString("")));
    ASSERT_TRUE(ReflectableUtil<>::equal(ReflectableFactory::getString("test"),
            ReflectableFactory::getString("test")));
    ASSERT_FALSE(ReflectableUtil<>::equal(ReflectableFactory::getString(""),
            ReflectableFactory::getString("a")));
    ASSERT_FALSE(ReflectableUtil<>::equal(ReflectableFactory::getString("1"),
            ReflectableFactory::getString("")));
    ASSERT_FALSE(ReflectableUtil<>::equal(ReflectableFactory::getString("test"),
            ReflectableFactory::getString("test2")));
}

TEST(ReflectableUtilTest, equalBitBuffers)
{
    ASSERT_TRUE(ReflectableUtil<>::equal(ReflectableFactory::getBitBuffer(BitBuffer()),
            ReflectableFactory::getBitBuffer(BitBuffer())));
    ASSERT_TRUE(ReflectableUtil<>::equal(ReflectableFactory::getBitBuffer(BitBuffer({0xAB}, 8)),
            ReflectableFactory::getBitBuffer(BitBuffer({0xAB}, 8))));
}

TEST(ReflectableUtilTest, equalEnums)
{
    const DummyEnum one = DummyEnum::ONE;
    const DummyEnum two = DummyEnum::TWO;

    ASSERT_TRUE(ReflectableUtil<>::equal(enumReflectable(one), enumReflectable(one)));
    ASSERT_TRUE(ReflectableUtil<>::equal(enumReflectable(two), enumReflectable(two)));
    ASSERT_FALSE(ReflectableUtil<>::equal(enumReflectable(one), enumReflectable(two)));
}

TEST(ReflectableUtilTest, equalBitmasks)
{
    const DummyBitmask read = DummyBitmask::Values::READ;
    const DummyBitmask write = DummyBitmask::Values::WRITE;

    ASSERT_TRUE(ReflectableUtil<>::equal(read.reflectable(), read.reflectable()));
    ASSERT_TRUE(ReflectableUtil<>::equal(write.reflectable(), write.reflectable()));
    ASSERT_FALSE(ReflectableUtil<>::equal(write.reflectable(), read.reflectable()));
}

TEST(ReflectableUtilTest, equalCompounds)
{
    DummyObject dummy1 = DummyObject();
    dummy1.setTextArray({{{"one"}, {"two"}}});
    dummy1.initializeChildren();

    DummyObject dummy2 = DummyObject(); // larger array
    dummy2.setTextArray({{{"one"}, {"two"}, {"three"}}});
    dummy2.initializeChildren();

    DummyObject dummy3 = DummyObject();
    dummy3.setTextArray({{{"one"}, {"something else"}}}); // difference in array element
    dummy3.initializeChildren();

    DummyObject dummy4 = DummyObject(); // extern array set
    dummy4.setTextArray({{{"one"}, {"two"}}});
    dummy4.setExternArray({{}});
    dummy4.initializeChildren();

    DummyObject dummy5 = DummyObject(); // optional bool set
    dummy5.setTextArray({{{"one"}, {"two"}}});
    dummy5.setOptionalBool(true);
    dummy5.initializeChildren();

    DummyObject dummy6 = DummyObject(); // optional nested set
    dummy6.setTextArray({{{"one"}, {"two"}}});
    dummy6.setOptionalNested(DummyNested());
    dummy6.initializeChildren();

    ASSERT_TRUE(ReflectableUtil<>::equal(dummy1.reflectable(), dummy1.reflectable()));
    ASSERT_TRUE(ReflectableUtil<>::equal(dummy2.reflectable(), dummy2.reflectable()));
    ASSERT_TRUE(ReflectableUtil<>::equal(dummy3.reflectable(), dummy3.reflectable()));
    ASSERT_TRUE(ReflectableUtil<>::equal(dummy4.reflectable(), dummy4.reflectable()));
    ASSERT_TRUE(ReflectableUtil<>::equal(dummy5.reflectable(), dummy5.reflectable()));
    ASSERT_TRUE(ReflectableUtil<>::equal(dummy6.reflectable(), dummy6.reflectable()));

    ASSERT_FALSE(ReflectableUtil<>::equal(dummy1.reflectable(), dummy2.reflectable()));
    ASSERT_FALSE(ReflectableUtil<>::equal(dummy1.reflectable(), dummy3.reflectable()));
    ASSERT_FALSE(ReflectableUtil<>::equal(dummy1.reflectable(), dummy4.reflectable()));
    ASSERT_FALSE(ReflectableUtil<>::equal(dummy1.reflectable(), dummy5.reflectable()));
    ASSERT_FALSE(ReflectableUtil<>::equal(dummy1.reflectable(), dummy6.reflectable()));

    DummyNested nested1 = DummyNested();
    nested1.initialize(0);

    DummyNested nested2 = DummyNested();
    nested2.initialize(1);

    ASSERT_FALSE(ReflectableUtil<>::equal(nested1.reflectable(), nested2.reflectable()));

    // array and non array
    ASSERT_FALSE(ReflectableUtil<>::equal(dummy1.reflectable()->getField("textArray"),
            dummy1.reflectable()->getField("textArray")->at(0)));
}

} // namespace zserio
