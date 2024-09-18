#include <functional>
#include <string>
#include <type_traits>

#include "gtest/gtest.h"
#include "test_object/std_allocator/ReflectableBitmask.h"
#include "test_object/std_allocator/ReflectableEnum.h"
#include "test_object/std_allocator/ReflectableNested.h"
#include "test_object/std_allocator/ReflectableObject.h"
#include "zserio/ArrayTraits.h"
#include "zserio/BitSizeOfCalculator.h"
#include "zserio/BitStreamReader.h"
#include "zserio/BitStreamWriter.h"
#include "zserio/Reflectable.h"
#include "zserio/Vector.h"

using test_object::std_allocator::ReflectableBitmask;
using test_object::std_allocator::ReflectableEnum;
using test_object::std_allocator::ReflectableNested;
using test_object::std_allocator::ReflectableObject;

using namespace std::placeholders;

namespace zserio
{

namespace
{

ReflectableObject createInitializedReflectableObject(
        const string<>& stringField, uint32_t reflectableNestedValue)
{
    ReflectableObject reflectableObject(stringField, ReflectableNested{reflectableNestedValue});
    reflectableObject.initializeChildren();
    return reflectableObject;
}

} // namespace

namespace detail
{

template <typename FUNC>
struct return_type;

template <typename R, typename CLASS, typename... ARGS>
struct return_type<R (CLASS::*)(ARGS...) const>
{
    using type = R;
};

} // namespace detail

class ReflectableTest : public ::testing::Test
{
protected:
    template <typename RAW_ARRAY>
    void checkArrayAnyValue(const RAW_ARRAY& rawArray, const IReflectableConstPtr& reflectable)
    {
        ASSERT_EQ(rawArray,
                reflectable->getAnyValue().template get<std::reference_wrapper<const RAW_ARRAY>>().get());
    }

    template <typename RAW_ARRAY>
    void checkArrayAnyValue(const RAW_ARRAY& rawArray, const IReflectablePtr& reflectable)
    {
        ASSERT_EQ(rawArray, reflectable->getAnyValue().template get<std::reference_wrapper<RAW_ARRAY>>().get());
    }

    template <typename RAW_ARRAY, typename REFLECTABLE_PTR, typename ELEMENT_CHECKER>
    void checkArray(const RAW_ARRAY& rawArray, const REFLECTABLE_PTR& reflectable,
            const ELEMENT_CHECKER& elementChecker)
    {
        ASSERT_TRUE(reflectable->isArray());
        ASSERT_EQ(rawArray.size(), reflectable->size());
        for (size_t i = 0; i < rawArray.size(); ++i)
        {
            if (i % 2 == 0)
            {
                elementChecker(rawArray[i], reflectable->at(i));
            }
            else
            {
                elementChecker(rawArray[i], (*reflectable)[i]);
            }
        }

        ASSERT_THROW(reflectable->at(rawArray.size()), CppRuntimeException);
        ASSERT_THROW((*reflectable)[rawArray.size()], CppRuntimeException);

        checkArrayAnyValue(rawArray, reflectable);

        ASSERT_THROW(reflectable->getBool(), CppRuntimeException);
        ASSERT_THROW(reflectable->getInt8(), CppRuntimeException);
        ASSERT_THROW(reflectable->getInt16(), CppRuntimeException);
        ASSERT_THROW(reflectable->getInt32(), CppRuntimeException);
        ASSERT_THROW(reflectable->getInt64(), CppRuntimeException);
        ASSERT_THROW(reflectable->getUInt8(), CppRuntimeException);
        ASSERT_THROW(reflectable->getUInt16(), CppRuntimeException);
        ASSERT_THROW(reflectable->getUInt32(), CppRuntimeException);
        ASSERT_THROW(reflectable->getUInt64(), CppRuntimeException);
        ASSERT_THROW(reflectable->getFloat(), CppRuntimeException);
        ASSERT_THROW(reflectable->getDouble(), CppRuntimeException);
        ASSERT_THROW(reflectable->getBytes(), CppRuntimeException);
        ASSERT_THROW(reflectable->getStringView(), CppRuntimeException);
        ASSERT_THROW(reflectable->getBitBuffer(), CppRuntimeException);

        ASSERT_THROW(reflectable->toInt(), CppRuntimeException);
        ASSERT_THROW(reflectable->toUInt(), CppRuntimeException);
        ASSERT_THROW(reflectable->toDouble(), CppRuntimeException);
        ASSERT_THROW(reflectable->toString(), CppRuntimeException);

        ASSERT_THROW(reflectable->bitSizeOf(0), CppRuntimeException);

        BitBuffer bitBuffer(0);
        BitStreamWriter writer(bitBuffer);
        ASSERT_THROW(reflectable->write(writer), CppRuntimeException);

        checkNonCompound(reflectable);
    }

    template <typename T, typename REFLECTABLE_PTR, typename READ_FUNC>
    void checkWriteRead(
            T value, const REFLECTABLE_PTR& reflectable, const READ_FUNC& readFunc, size_t bitBufferSize)
    {
        BitBuffer bitBuffer(bitBufferSize);
        BitStreamWriter writer(bitBuffer);
        reflectable->write(writer);
        const size_t bitSizeOfValue = reflectable->bitSizeOf();
        ASSERT_EQ(bitSizeOfValue, writer.getBitPosition());

        BitStreamReader reader(bitBuffer);
        ASSERT_EQ(value, readFunc(reader));
        ASSERT_EQ(bitSizeOfValue, reader.getBitPosition());
    }

    void checkNonArray(const IReflectableConstPtr& reflectable)
    {
        ASSERT_FALSE(reflectable->isArray());
        ASSERT_THROW(reflectable->size(), CppRuntimeException);
        ASSERT_THROW(reflectable->at(0), CppRuntimeException);
        ASSERT_THROW((*reflectable)[0], CppRuntimeException);
    }

    void checkNonArray(const IReflectablePtr& reflectable)
    {
        checkNonArray(static_cast<IReflectableConstPtr>(reflectable));

        ASSERT_THROW(reflectable->at(0), CppRuntimeException);
        ASSERT_THROW((*reflectable)[0], CppRuntimeException);
        ASSERT_THROW(reflectable->resize(1), CppRuntimeException);
        ASSERT_THROW(reflectable->setAt(AnyHolder<>(), 0), CppRuntimeException);
    }

    template <typename REFLECTABLE_PTR>
    void checkNonCompoundConstMethods(const REFLECTABLE_PTR& reflectable)
    {
        ASSERT_THROW(reflectable->getField("field"), CppRuntimeException);
        ASSERT_THROW(reflectable->getParameter("parameter"), CppRuntimeException);
        ASSERT_THROW(reflectable->callFunction("function"), CppRuntimeException);
        ASSERT_THROW(reflectable->getChoice(), CppRuntimeException);

        ASSERT_EQ(nullptr, reflectable->find("some.field"));
        ASSERT_EQ(nullptr, (*reflectable)["some.field"]);
    }

    void checkNonCompound(const IReflectablePtr& reflectable)
    {
        ASSERT_THROW(reflectable->initializeChildren(), CppRuntimeException);
        ASSERT_THROW(reflectable->initialize(vector<AnyHolder<>>()), CppRuntimeException);
        ASSERT_THROW(reflectable->setField("field", AnyHolder<>{}), CppRuntimeException);
        ASSERT_THROW(reflectable->createField("field"), CppRuntimeException);
        ASSERT_THROW(reflectable->initializeOffsets(), CppRuntimeException);

        checkNonCompoundConstMethods(reflectable);
    }

    void checkNonCompound(const IReflectableConstPtr& reflectable)
    {
        checkNonCompoundConstMethods(reflectable);
    }

    template <typename T, typename REFLECTABLE_PTR, typename GETTER,
            typename std::enable_if<std::is_same<T, typename detail::return_type<GETTER>::type>::value,
                    int>::type = 0>
    void checkCppTypeGetter(T value, const REFLECTABLE_PTR& reflectable, const GETTER& getter,
            const char* testName, const char* getterName)
    {
        ASSERT_EQ(value, ((*reflectable).*getter)()) << testName << " : " << getterName;
    }

    template <typename T, typename REFLECTABLE_PTR, typename GETTER,
            typename std::enable_if<!std::is_same<T, typename detail::return_type<GETTER>::type>::value,
                    int>::type = 0>
    void checkCppTypeGetter(T, const REFLECTABLE_PTR& reflectable, const GETTER& getter, const char* testName,
            const char* getterName)
    {
        ASSERT_THROW(((*reflectable).*getter)(), CppRuntimeException) << testName << " : " << getterName;
    }

    template <typename REFLECTABLE_PTR, typename GETTER,
            typename std::enable_if<
                    std::is_same<Span<const uint8_t>, typename detail::return_type<GETTER>::type>::value,
                    int>::type = 0>
    void checkCppTypeGetter(Span<const uint8_t> value, const REFLECTABLE_PTR& reflectable, const GETTER& getter,
            const char* testName, const char* getterName)
    {
        ASSERT_EQ(value.size(), ((*reflectable).*getter)().size()) << testName << " : " << getterName;
        ASSERT_EQ(value.data(), ((*reflectable).*getter)().data()) << testName << " : " << getterName;
    }

    template <typename REFLECTABLE_PTR, typename GETTER,
            typename std::enable_if<
                    std::is_same<const BitBuffer&, typename detail::return_type<GETTER>::type>::value,
                    int>::type = 0>
    void checkCppTypeGetter(const BitBuffer& value, const REFLECTABLE_PTR& reflectable, const GETTER& getter,
            const char* testName, const char* getterName)
    {
        ASSERT_EQ(value, ((*reflectable).*getter)()) << testName << " : " << getterName;
    }

    template <typename T, typename REFLECTABLE_PTR>
    void checkCppTypeGetters(T value, const REFLECTABLE_PTR& reflectable, const char* testName)
    {
        checkCppTypeGetter(value, reflectable, &IReflectable::getBool, testName, "getBool");

        checkCppTypeGetter(value, reflectable, &IReflectable::getInt8, testName, "getInt8");
        checkCppTypeGetter(value, reflectable, &IReflectable::getInt16, testName, "getInt16");
        checkCppTypeGetter(value, reflectable, &IReflectable::getInt32, testName, "getInt32");
        checkCppTypeGetter(value, reflectable, &IReflectable::getInt64, testName, "getInt64");

        checkCppTypeGetter(value, reflectable, &IReflectable::getUInt8, testName, "getUInt8");
        checkCppTypeGetter(value, reflectable, &IReflectable::getUInt16, testName, "getUInt16");
        checkCppTypeGetter(value, reflectable, &IReflectable::getUInt32, testName, "getUInt32");
        checkCppTypeGetter(value, reflectable, &IReflectable::getUInt64, testName, "getUInt64");

        checkCppTypeGetter(value, reflectable, &IReflectable::getFloat, testName, "getFloat");
        checkCppTypeGetter(value, reflectable, &IReflectable::getDouble, testName, "getDouble");

        checkCppTypeGetter(value, reflectable, &IReflectable::getBytes, testName, "getBytes");
        checkCppTypeGetter(value, reflectable, &IReflectable::getStringView, testName, "getStringView");
        checkCppTypeGetter(value, reflectable, &IReflectable::getBitBuffer, testName, "getBitBuffer");
    }

    template <typename T, typename REFLECTABLE_PTR, typename GETTER, typename READ_FUNC>
    void checkFloatingPoint(T value, const REFLECTABLE_PTR& reflectable, const GETTER& getter,
            const READ_FUNC& readFunc, size_t bitSize = sizeof(T) * 8)
    {
        ASSERT_DOUBLE_EQ(value, ((*reflectable).*getter)());

        ASSERT_DOUBLE_EQ(value, reflectable->toDouble());
        ASSERT_THROW(reflectable->toInt(), CppRuntimeException);
        ASSERT_THROW(reflectable->toUInt(), CppRuntimeException);
        ASSERT_THROW(reflectable->toString(), CppRuntimeException); // NOT IMPLEMENTED!

        checkCppTypeGetters(value, reflectable, "checkFloatingPoint");

        checkNonCompound(reflectable);
        checkNonArray(reflectable);

        checkWriteRead(value, reflectable, readFunc, bitSize);
    }

    template <typename T, typename REFLECTABLE_PTR, typename GETTER, typename READ_FUNC>
    void checkIntegral(T value, const REFLECTABLE_PTR& reflectable, const GETTER& getter,
            const READ_FUNC& readFunc, size_t bitSize, const char* testName)
    {
        ASSERT_EQ(value, ((*reflectable).*getter)());

        ASSERT_EQ(value, reflectable->getAnyValue().template get<T>());

        ASSERT_DOUBLE_EQ(static_cast<double>(value), reflectable->toDouble());
        ASSERT_EQ(zserio::toString(value), reflectable->toString());

        checkCppTypeGetters(value, reflectable, testName);

        checkNonCompound(reflectable);
        checkNonArray(reflectable);

        checkWriteRead(value, reflectable, readFunc, bitSize);
    }

    template <typename T, typename REFLECTABLE_PTR, typename GETTER, typename READ_FUNC>
    void checkSignedIntegral(T value, const REFLECTABLE_PTR& reflectable, const GETTER& getter,
            const READ_FUNC& readFunc, size_t bitSize = sizeof(T) * 8)
    {
        ASSERT_EQ(value, reflectable->toInt());
        ASSERT_THROW(reflectable->toUInt(), CppRuntimeException);

        checkIntegral(value, reflectable, getter, readFunc, bitSize, "checkSignedIntegral");
    }

    template <typename T, typename REFLECTABLE_PTR, typename GETTER, typename READ_FUNC>
    void checkUnsignedIntegral(T value, const REFLECTABLE_PTR& reflectable, const GETTER& getter,
            const READ_FUNC& readFunc, size_t bitSize = sizeof(T) * 8)
    {
        ASSERT_EQ(value, reflectable->toUInt());
        ASSERT_THROW(reflectable->toInt(), CppRuntimeException);

        checkIntegral(value, reflectable, getter, readFunc, bitSize, "checkUnsignedIntegral");
    }

    template <typename REFLECTABLE_PTR>
    void checkString(StringView value, const REFLECTABLE_PTR& reflectable)
    {
        ASSERT_EQ(value, reflectable->getAnyValue().template get<StringView>());

        ASSERT_EQ(toString(value), reflectable->toString());
        ASSERT_THROW(reflectable->toInt(), CppRuntimeException);
        ASSERT_THROW(reflectable->toUInt(), CppRuntimeException);
        ASSERT_THROW(reflectable->toDouble(), CppRuntimeException);

        checkCppTypeGetters(value, reflectable, "checkString");

        checkNonCompound(reflectable);
        checkNonArray(reflectable);

        checkWriteRead(toString(value), reflectable,
                std::bind(&BitStreamReader::readString<>, _1, std::allocator<uint8_t>()),
                bitSizeOfVarSize(convertSizeToUInt32(value.size())) + value.size() * 8);
    }

    template <typename REFLECTABLE_PTR>
    void checkBitBuffer(const BitBuffer& value, const REFLECTABLE_PTR& reflectable)
    {
        ASSERT_EQ(value,
                reflectable->getAnyValue().template get<std::reference_wrapper<const BitBuffer>>().get());

        ASSERT_THROW(reflectable->toString(), CppRuntimeException);
        ASSERT_THROW(reflectable->toDouble(), CppRuntimeException);
        ASSERT_THROW(reflectable->toInt(), CppRuntimeException);
        ASSERT_THROW(reflectable->toUInt(), CppRuntimeException);

        checkCppTypeGetters(value, reflectable, "checkBitBuffer");

        checkNonCompound(reflectable);
        checkNonArray(reflectable);

        checkWriteRead(value, reflectable,
                std::bind(&BitStreamReader::readBitBuffer<>, _1, std::allocator<uint8_t>()),
                bitSizeOfVarSize(convertSizeToUInt32(value.getBitSize())) + value.getBitSize());
    }

    template <typename REFLECTABLE_PTR>
    void checkBytes(const std::vector<uint8_t>& value, const REFLECTABLE_PTR& reflectable)
    {
        ASSERT_EQ(value.data(), reflectable->getBytes().data());
        ASSERT_EQ(value.size(), reflectable->getBytes().size());
        auto anyValue = reflectable->getAnyValue().template get<Span<const uint8_t>>();
        ASSERT_EQ(value.data(), anyValue.data());
        ASSERT_EQ(value.size(), anyValue.size());

        ASSERT_THROW(reflectable->toString(), CppRuntimeException);
        ASSERT_THROW(reflectable->toDouble(), CppRuntimeException);
        ASSERT_THROW(reflectable->toInt(), CppRuntimeException);
        ASSERT_THROW(reflectable->toUInt(), CppRuntimeException);

        checkCppTypeGetters(Span<const uint8_t>(value), reflectable, "checkBytes");

        checkNonCompound(reflectable);
        checkNonArray(reflectable);

        const size_t bitSize = value.size() * 8;
        checkWriteRead(value, reflectable,
                std::bind(&BitStreamReader::readBytes<>, _1, std::allocator<uint8_t>()),
                bitSizeOfVarSize(convertSizeToUInt32(bitSize)) + bitSize);
    }

    template <typename REFLECTABLE_PTR>
    void checkBitmask(ReflectableBitmask bitmask, const REFLECTABLE_PTR& reflectable)
    {
        ASSERT_EQ(bitmask.getValue(), reflectable->getUInt8());

        ASSERT_EQ(bitmask, reflectable->getAnyValue().template get<ReflectableBitmask>());

        ASSERT_EQ(bitmask.getValue(), reflectable->toUInt());
        ASSERT_EQ(bitmask.getValue(), reflectable->toDouble());
        ASSERT_EQ(bitmask.toString(), reflectable->toString());
        ASSERT_THROW(reflectable->toInt(), CppRuntimeException);

        checkCppTypeGetters(bitmask.getValue(), reflectable, "checkBitmask");

        checkNonCompound(reflectable);
        checkNonArray(reflectable);

        checkWriteRead(
                bitmask, reflectable, [](BitStreamReader& reader) { return ReflectableBitmask(reader); }, 8);
    }

    template <typename REFLECTABLE_PTR>
    void checkEnum(ReflectableEnum enumeration, const REFLECTABLE_PTR& reflectable)
    {
        ASSERT_EQ(enumToValue(enumeration), reflectable->getInt8());

        ASSERT_EQ(enumeration, reflectable->getAnyValue().template get<ReflectableEnum>());

        ASSERT_EQ(enumToValue(enumeration), reflectable->toInt());
        ASSERT_EQ(enumToValue(enumeration), reflectable->toDouble());
        ASSERT_EQ(enumToString(enumeration), reflectable->toString());
        ASSERT_THROW(reflectable->toUInt(), CppRuntimeException);

        checkCppTypeGetters(enumToValue(enumeration), reflectable, "checkEnum");

        checkNonCompound(reflectable);
        checkNonArray(reflectable);

        checkWriteRead(
                enumeration, reflectable,
                [](BitStreamReader& reader) { return zserio::read<ReflectableEnum>(reader); }, 8);
    }

    void checkCompoundAnyValue(
            const ReflectableObject& reflectableObject, const IReflectableConstPtr& reflectable)
    {
        ASSERT_EQ(reflectableObject,
                reflectable->getAnyValue()
                        .template get<std::reference_wrapper<const ReflectableObject>>()
                        .get());
        ASSERT_EQ(reflectableObject.getReflectableNested(),
                reflectable->getField("reflectableNested")
                        ->getAnyValue()
                        .template get<std::reference_wrapper<const ReflectableNested>>()
                        .get());
    }

    void checkCompoundAnyValue(const ReflectableObject& reflectableObject, const IReflectablePtr& reflectable)
    {
        ASSERT_EQ(reflectableObject,
                reflectable->getAnyValue().template get<std::reference_wrapper<ReflectableObject>>().get());
        ASSERT_EQ(reflectableObject.getReflectableNested(),
                reflectable->getField("reflectableNested")
                        ->getAnyValue()
                        .template get<std::reference_wrapper<ReflectableNested>>()
                        .get());
    }

    template <typename REFLECTABLE_PTR>
    void checkCompoundConstMethods(
            const ReflectableObject& reflectableObject, const REFLECTABLE_PTR& reflectable)
    {
        ASSERT_TRUE(TypeInfoUtil::isCompound(reflectable->getTypeInfo().getSchemaType()));

        // field getter
        ASSERT_EQ(reflectableObject.getReflectableNested().getValue(),
                reflectable->getField("reflectableNested")->getField("value")->getUInt32());
        ASSERT_THROW(reflectable->getField("nonexistent"), CppRuntimeException);
        ASSERT_THROW(reflectable->getField("reflectableNested")->getField("nonexistent"), CppRuntimeException);

        // find field
        ASSERT_EQ(reflectableObject.getReflectableNested().getValue(),
                reflectable->find("reflectableNested.value")->toUInt());
        ASSERT_EQ(reflectableObject.getReflectableNested().getValue(),
                (*reflectable)["reflectableNested.value"]->toDouble());

        // find parameter
        ASSERT_NO_THROW(reflectable->getField("reflectableNested")->getParameter("dummyParam"));
        ASSERT_EQ(13, (*reflectable)["reflectableNested.dummyParam"]->getInt32());
        ASSERT_NO_THROW(reflectable->getField("reflectableNested")->getParameter("stringParam"));
        ASSERT_EQ(reflectableObject.getStringField(),
                (*reflectable)["reflectableNested.stringParam"]->toString());
        ASSERT_THROW(
                reflectable->getField("reflectableNested")->getParameter("nonexistent"), CppRuntimeException);

        // find function
        ASSERT_EQ(reflectableObject.getReflectableNested().getValue(),
                (*reflectable)["reflectableNested.getValue"]->getUInt32());
        ASSERT_THROW(
                reflectable->getField("reflectableNested")->callFunction("nonexistent"), CppRuntimeException);

        // find failed
        ASSERT_EQ(nullptr, reflectable->find("reflectableNested.nonexistent"));
        ASSERT_EQ(nullptr, reflectable->find("nonexistent"));
        ASSERT_EQ(nullptr, reflectable->find("reflectableNested.value.nonexistent"));
        ASSERT_EQ(nullptr, reflectable->find("reflectableNested.dummyParam.nonexistent"));
        ASSERT_EQ(nullptr, reflectable->find("reflectableNested.getValue.nonexistent"));
        // find failed because the underlying code throws
        ASSERT_EQ(nullptr, reflectable->find("reflectableNested.throwingFunction.nonexistent"));

        checkCompoundAnyValue(reflectableObject, reflectable);

        ASSERT_THROW(reflectable->getBool(), CppRuntimeException);
        ASSERT_THROW(reflectable->getInt8(), CppRuntimeException);
        ASSERT_THROW(reflectable->getInt16(), CppRuntimeException);
        ASSERT_THROW(reflectable->getInt32(), CppRuntimeException);
        ASSERT_THROW(reflectable->getInt64(), CppRuntimeException);
        ASSERT_THROW(reflectable->getUInt8(), CppRuntimeException);
        ASSERT_THROW(reflectable->getUInt16(), CppRuntimeException);
        ASSERT_THROW(reflectable->getUInt32(), CppRuntimeException);
        ASSERT_THROW(reflectable->getUInt64(), CppRuntimeException);
        ASSERT_THROW(reflectable->getFloat(), CppRuntimeException);
        ASSERT_THROW(reflectable->getDouble(), CppRuntimeException);
        ASSERT_THROW(reflectable->getBytes(), CppRuntimeException);
        ASSERT_THROW(reflectable->getStringView(), CppRuntimeException);
        ASSERT_THROW(reflectable->getBitBuffer(), CppRuntimeException);

        ASSERT_THROW(reflectable->toInt(), CppRuntimeException);
        ASSERT_THROW(reflectable->toUInt(), CppRuntimeException);
        ASSERT_THROW(reflectable->toDouble(), CppRuntimeException);
        ASSERT_THROW(reflectable->toString(), CppRuntimeException);

        checkNonArray(reflectable);

        checkWriteRead(
                reflectableObject, reflectable,
                [](BitStreamReader& reader) { return ReflectableObject(reader); },
                reflectableObject.bitSizeOf());
    }

    void checkCompound(const ReflectableObject& reflectableObject, const IReflectableConstPtr& reflectable)
    {
        checkCompoundConstMethods(reflectableObject, reflectable);
    }

    void checkCompound(const ReflectableObject& reflectableObject, const IReflectablePtr& reflectable)
    {
        checkCompoundConstMethods(reflectableObject, reflectable);
        checkCompoundConstMethods(reflectableObject, static_cast<IReflectableConstPtr>(reflectable));

        // setter
        reflectable->getField("reflectableNested")->setField("value", AnyHolder<>(static_cast<uint32_t>(11)));
        ASSERT_EQ(11, reflectableObject.getReflectableNested().getValue());
        ASSERT_THROW(reflectable->setField("nonexistent", AnyHolder<>()), CppRuntimeException);
        ASSERT_THROW(reflectable->find("reflectableNested")->setField("nonexistent", AnyHolder<>()),
                CppRuntimeException);

        // any value
        ASSERT_EQ(reflectableObject.getReflectableNested(),
                reflectable->find("reflectableNested")
                        ->getAnyValue()
                        .template get<std::reference_wrapper<ReflectableNested>>()
                        .get());

        reflectable->createField("reflectableNested");
        ASSERT_EQ(uint32_t(), reflectableObject.getReflectableNested().getValue());

        reflectable->setField("reflectableNested", AnyHolder<>(ReflectableNested{42}));
        ASSERT_EQ(42, reflectableObject.getReflectableNested().getValue());
        reflectable->initializeChildren(); // keep the reflectable initialized for following tests
    }
};

TEST_F(ReflectableTest, boolReflectable)
{
    const bool value = true;
    auto reflectable = ReflectableFactory::getBool(value);
    checkUnsignedIntegral(
            value, reflectable, &IReflectable::getBool, std::bind(&BitStreamReader::readBool, _1));
}

TEST_F(ReflectableTest, int8Reflectable)
{
    const int8_t value = -12;
    auto reflectable = ReflectableFactory::getInt8(value);
    checkSignedIntegral(
            value, reflectable, &IReflectable::getInt8, std::bind(&BitStreamReader::readSignedBits, _1, 8));
}

TEST_F(ReflectableTest, int16Reflectable)
{
    const int16_t value = -1234;
    auto reflectable = ReflectableFactory::getInt16(value);
    checkSignedIntegral(
            value, reflectable, &IReflectable::getInt16, std::bind(&BitStreamReader::readSignedBits, _1, 16));
}

TEST_F(ReflectableTest, int32Reflectable)
{
    const int32_t value = -123456;
    auto reflectable = ReflectableFactory::getInt32(value);
    checkSignedIntegral(
            value, reflectable, &IReflectable::getInt32, std::bind(&BitStreamReader::readSignedBits, _1, 32));
}

TEST_F(ReflectableTest, int64Reflectable)
{
    const int64_t value = -1234567890;
    auto reflectable = ReflectableFactory::getInt64(value);
    checkSignedIntegral(
            value, reflectable, &IReflectable::getInt64, std::bind(&BitStreamReader::readSignedBits64, _1, 64));
}

TEST_F(ReflectableTest, uint8Reflectable)
{
    const uint8_t value = 0xFF;
    auto reflectable = ReflectableFactory::getUInt8(value);
    checkUnsignedIntegral(
            value, reflectable, &IReflectable::getUInt8, std::bind(&BitStreamReader::readBits, _1, 8));
}

TEST_F(ReflectableTest, uint16Reflectable)
{
    const uint16_t value = 0xFFFF;
    auto reflectable = ReflectableFactory::getUInt16(value);
    checkUnsignedIntegral(
            value, reflectable, &IReflectable::getUInt16, std::bind(&BitStreamReader::readBits, _1, 16));
}

TEST_F(ReflectableTest, uint32Reflectable)
{
    const uint32_t value = 0xFFFFFFFF;
    auto reflectable = ReflectableFactory::getUInt32(value);
    checkUnsignedIntegral(
            value, reflectable, &IReflectable::getUInt32, std::bind(&BitStreamReader::readBits, _1, 32));
}

TEST_F(ReflectableTest, uint64Reflectable)
{
    const uint64_t value = 0xFFFFFFFFFFFF;
    auto reflectable = ReflectableFactory::getUInt64(value);
    checkUnsignedIntegral(
            value, reflectable, &IReflectable::getUInt64, std::bind(&BitStreamReader::readBits64, _1, 64));
}

TEST_F(ReflectableTest, fixedSignedBitField5) // mapped to int8_t
{
    const uint8_t numBits = 5;
    const int8_t value = 15;
    auto reflectable = ReflectableFactory::getFixedSignedBitField(value, numBits);
    checkSignedIntegral(value, reflectable, &IReflectable::getInt8,
            std::bind(&BitStreamReader::readSignedBits, _1, numBits), numBits);

    ASSERT_THROW(ReflectableFactory::getFixedSignedBitField(value, 10), CppRuntimeException);
}

TEST_F(ReflectableTest, fixedSignedBitField15) // mapped to int16_t
{
    const uint8_t numBits = 15;
    const int16_t value = -15;
    auto reflectable = ReflectableFactory::getFixedSignedBitField(value, numBits);
    checkSignedIntegral(value, reflectable, &IReflectable::getInt16,
            std::bind(&BitStreamReader::readSignedBits, _1, numBits), numBits);

    ASSERT_THROW(ReflectableFactory::getFixedSignedBitField(value, 5), CppRuntimeException);
    ASSERT_THROW(ReflectableFactory::getFixedSignedBitField(value, 17), CppRuntimeException);
}

TEST_F(ReflectableTest, fixedSignedBitField31) // mapped to int32_t
{
    const uint8_t numBits = 31;
    const int32_t value = -12345678;
    auto reflectable = ReflectableFactory::getFixedSignedBitField(value, numBits);
    checkSignedIntegral(value, reflectable, &IReflectable::getInt32,
            std::bind(&BitStreamReader::readSignedBits, _1, numBits), numBits);

    ASSERT_THROW(ReflectableFactory::getFixedSignedBitField(value, 16), CppRuntimeException);
    ASSERT_THROW(ReflectableFactory::getFixedSignedBitField(value, 33), CppRuntimeException);
}

TEST_F(ReflectableTest, fixedSignedBitField60) // mapped to int64_t
{
    const uint8_t numBits = 60;
    const int64_t value = 1234567890;
    auto reflectable = ReflectableFactory::getFixedSignedBitField(value, numBits);
    checkSignedIntegral(value, reflectable, &IReflectable::getInt64,
            std::bind(&BitStreamReader::readSignedBits64, _1, numBits), numBits);

    ASSERT_THROW(ReflectableFactory::getFixedSignedBitField(value, 31), CppRuntimeException);
    ASSERT_THROW(ReflectableFactory::getFixedSignedBitField(value, 65), CppRuntimeException);
}

TEST_F(ReflectableTest, fixedUnsignedBitField7) // mapped to uint8_t
{
    const uint8_t numBits = 7;
    const uint8_t value = 0x2F;
    auto reflectable = ReflectableFactory::getFixedUnsignedBitField(value, numBits);
    checkUnsignedIntegral(value, reflectable, &IReflectable::getUInt8,
            std::bind(&BitStreamReader::readBits, _1, numBits), numBits);

    ASSERT_THROW(ReflectableFactory::getFixedUnsignedBitField(value, 9), CppRuntimeException);
}

TEST_F(ReflectableTest, fixedUnsignedBitField9) // mapped to uint16_t
{
    const uint8_t numBits = 9;
    const uint16_t value = 0x1FF;
    auto reflectable = ReflectableFactory::getFixedUnsignedBitField(value, numBits);
    checkUnsignedIntegral(value, reflectable, &IReflectable::getUInt16,
            std::bind(&BitStreamReader::readBits, _1, numBits), numBits);

    ASSERT_THROW(ReflectableFactory::getFixedUnsignedBitField(value, 8), CppRuntimeException);
    ASSERT_THROW(ReflectableFactory::getFixedUnsignedBitField(value, 17), CppRuntimeException);
}

TEST_F(ReflectableTest, fixedUnsignedBitField31) // mapped to uint32_t
{
    const uint8_t numBits = 31;
    const uint32_t value = UINT32_MAX >> 1;
    auto reflectable = ReflectableFactory::getFixedUnsignedBitField(value, numBits);
    checkUnsignedIntegral(value, reflectable, &IReflectable::getUInt32,
            std::bind(&BitStreamReader::readBits, _1, numBits), numBits);

    ASSERT_THROW(ReflectableFactory::getFixedUnsignedBitField(value, 16), CppRuntimeException);
    ASSERT_THROW(ReflectableFactory::getFixedUnsignedBitField(value, 33), CppRuntimeException);
}

TEST_F(ReflectableTest, fixedUnsignedBitField33) // mapped to uint64_t
{
    const uint8_t numBits = 33;
    const uint64_t value = static_cast<uint64_t>(UINT32_MAX) << 1U;
    auto reflectable = ReflectableFactory::getFixedUnsignedBitField(value, numBits);
    checkUnsignedIntegral(value, reflectable, &IReflectable::getUInt64,
            std::bind(&BitStreamReader::readBits64, _1, numBits), numBits);

    ASSERT_THROW(ReflectableFactory::getFixedUnsignedBitField(value, 32), CppRuntimeException);
    ASSERT_THROW(ReflectableFactory::getFixedUnsignedBitField(value, 65), CppRuntimeException);
}

TEST_F(ReflectableTest, dynamicSignedBitField5) // mapped to int8_t
{
    const uint8_t maxBitSize = 8;
    const uint8_t numBits = 5;
    const int8_t value = 15;
    auto reflectable = ReflectableFactory::getDynamicSignedBitField(value, maxBitSize, numBits);
    checkSignedIntegral(value, reflectable, &IReflectable::getInt8,
            std::bind(&BitStreamReader::readSignedBits, _1, numBits), numBits);

    // maxBitSize out of bounds
    ASSERT_THROW(ReflectableFactory::getDynamicSignedBitField(value, 9, numBits), CppRuntimeException);

    // dynamicBitSize out of bounds
    ASSERT_THROW(ReflectableFactory::getDynamicSignedBitField(value, 4, numBits), CppRuntimeException);
}

TEST_F(ReflectableTest, dynamicSignedBitField15) // mapped to int16_t
{
    const uint8_t maxBitSize = 16;
    const uint8_t numBits = 15;
    const int16_t value = -15;
    auto reflectable = ReflectableFactory::getDynamicSignedBitField(value, maxBitSize, numBits);
    checkSignedIntegral(value, reflectable, &IReflectable::getInt16,
            std::bind(&BitStreamReader::readSignedBits, _1, numBits), numBits);

    // maxBitSize out of bounds
    ASSERT_THROW(ReflectableFactory::getDynamicSignedBitField(value, 8, numBits), CppRuntimeException);
    ASSERT_THROW(ReflectableFactory::getDynamicSignedBitField(value, 17, numBits), CppRuntimeException);

    // dynamicBitSize out of bounds
    ASSERT_THROW(ReflectableFactory::getDynamicSignedBitField(value, 12, numBits), CppRuntimeException);
}

TEST_F(ReflectableTest, dynamicSignedBitField31) // mapped to int32_t
{
    const uint8_t maxBitSize = 32;
    const uint8_t numBits = 31;
    const int32_t value = -12345678;
    auto reflectable = ReflectableFactory::getDynamicSignedBitField(value, maxBitSize, numBits);
    checkSignedIntegral(value, reflectable, &IReflectable::getInt32,
            std::bind(&BitStreamReader::readSignedBits, _1, numBits), numBits);

    // maxBitSize out of bounds
    ASSERT_THROW(ReflectableFactory::getDynamicSignedBitField(value, 16, numBits), CppRuntimeException);
    ASSERT_THROW(ReflectableFactory::getDynamicSignedBitField(value, 33, numBits), CppRuntimeException);

    // dynamicBitSize out of bounds
    ASSERT_THROW(ReflectableFactory::getDynamicSignedBitField(value, 29, numBits), CppRuntimeException);
}

TEST_F(ReflectableTest, dynamicSignedBitField60) // mapped to int64_t
{
    const uint8_t maxBitSize = 64;
    const uint8_t numBits = 60;
    const int64_t value = 1234567890;
    auto reflectable = ReflectableFactory::getDynamicSignedBitField(value, maxBitSize, numBits);
    checkSignedIntegral(value, reflectable, &IReflectable::getInt64,
            std::bind(&BitStreamReader::readSignedBits64, _1, numBits), numBits);

    // maxBitSize out of bounds
    ASSERT_THROW(ReflectableFactory::getDynamicSignedBitField(value, 32, numBits), CppRuntimeException);
    ASSERT_THROW(ReflectableFactory::getDynamicSignedBitField(value, 65, numBits), CppRuntimeException);

    // dynamicBitSize out of bounds
    ASSERT_THROW(ReflectableFactory::getDynamicSignedBitField(value, 58, numBits), CppRuntimeException);

    // dynamic signed bit field as a type reference (maxBitSize must be 64!)
    ASSERT_NO_THROW(ReflectableFactory::getDynamicSignedBitField(value, 64));
    ASSERT_THROW(ReflectableFactory::getDynamicSignedBitField(value, 58), CppRuntimeException);
}

TEST_F(ReflectableTest, dynamicUnsignedBitField7) // mapped to uint8_t
{
    const uint8_t maxBitSize = 8;
    const uint8_t numBits = 7;
    const uint8_t value = 0x2F;
    auto reflectable = ReflectableFactory::getDynamicUnsignedBitField(value, maxBitSize, numBits);
    checkUnsignedIntegral(value, reflectable, &IReflectable::getUInt8,
            std::bind(&BitStreamReader::readBits, _1, numBits), numBits);

    // maxBitSize out of bounds
    ASSERT_THROW(ReflectableFactory::getDynamicUnsignedBitField(value, 9, numBits), CppRuntimeException);

    // dynamicBitSize out of bounds
    ASSERT_THROW(ReflectableFactory::getDynamicUnsignedBitField(value, 6, numBits), CppRuntimeException);
}

TEST_F(ReflectableTest, dynamicUnsignedBitField9) // mapped to uint16_t
{
    const uint8_t maxBitSize = 16;
    const uint8_t numBits = 9;
    const uint16_t value = 0x1FF;
    auto reflectable = ReflectableFactory::getDynamicUnsignedBitField(value, maxBitSize, numBits);
    checkUnsignedIntegral(value, reflectable, &IReflectable::getUInt16,
            std::bind(&BitStreamReader::readBits, _1, numBits), numBits);

    // maxBitSize out of bounds
    ASSERT_THROW(ReflectableFactory::getDynamicUnsignedBitField(value, 8, numBits), CppRuntimeException);
    ASSERT_THROW(ReflectableFactory::getDynamicUnsignedBitField(value, 17, numBits), CppRuntimeException);

    // dynamicBitSize out of bounds
    ASSERT_THROW(ReflectableFactory::getDynamicUnsignedBitField(value, 9, 10), CppRuntimeException);
}

TEST_F(ReflectableTest, dynamicUnsignedBitField31) // mapped to uint32_t
{
    const uint8_t maxBitSize = 32;
    const uint8_t numBits = 31;
    const uint32_t value = UINT32_MAX >> 1;
    auto reflectable = ReflectableFactory::getDynamicUnsignedBitField(value, maxBitSize, numBits);
    checkUnsignedIntegral(value, reflectable, &IReflectable::getUInt32,
            std::bind(&BitStreamReader::readBits, _1, numBits), numBits);

    // maxBitSize out of bounds
    ASSERT_THROW(ReflectableFactory::getDynamicUnsignedBitField(value, 16, numBits), CppRuntimeException);
    ASSERT_THROW(ReflectableFactory::getDynamicUnsignedBitField(value, 33, numBits), CppRuntimeException);

    // dynamicBitSize out of bounds
    ASSERT_THROW(ReflectableFactory::getDynamicUnsignedBitField(value, 29, numBits), CppRuntimeException);
}

TEST_F(ReflectableTest, dynamicUnsignedBitField33) // mapped to uint64_t
{
    const uint8_t maxBitSize = 64;
    const uint8_t numBits = 33;
    const uint64_t value = static_cast<uint64_t>(UINT32_MAX) << 1U;
    auto reflectable = ReflectableFactory::getDynamicUnsignedBitField(value, maxBitSize, numBits);
    checkUnsignedIntegral(value, reflectable, &IReflectable::getUInt64,
            std::bind(&BitStreamReader::readBits64, _1, numBits), numBits);

    // maxBitSize out of bounds
    ASSERT_THROW(ReflectableFactory::getDynamicUnsignedBitField(value, 32, numBits), CppRuntimeException);
    ASSERT_THROW(ReflectableFactory::getDynamicUnsignedBitField(value, 65, numBits), CppRuntimeException);

    // dynamicBitSize out of bounds
    ASSERT_THROW(ReflectableFactory::getDynamicUnsignedBitField(value, 33, 34), CppRuntimeException);

    // dynamic unsigned bit field as a type reference (maxBitSize must be 64!)
    ASSERT_NO_THROW(ReflectableFactory::getDynamicUnsignedBitField(value, 64));
    ASSERT_THROW(ReflectableFactory::getDynamicUnsignedBitField(value, 58), CppRuntimeException);
}

TEST_F(ReflectableTest, varint16Reflectable)
{
    const int16_t value = -1234;
    auto reflectable = ReflectableFactory::getVarInt16(value);
    checkSignedIntegral(value, reflectable, &IReflectable::getInt16,
            std::bind(&BitStreamReader::readVarInt16, _1), bitSizeOfVarInt16(value));
}

TEST_F(ReflectableTest, varint32Reflectable)
{
    const int32_t value = 54321;
    auto reflectable = ReflectableFactory::getVarInt32(value);
    checkSignedIntegral(value, reflectable, &IReflectable::getInt32,
            std::bind(&BitStreamReader::readVarInt32, _1), bitSizeOfVarInt32(value));
}

TEST_F(ReflectableTest, varint64Reflectable)
{
    const int64_t value = -87654321;
    auto reflectable = ReflectableFactory::getVarInt64(value);
    checkSignedIntegral(value, reflectable, &IReflectable::getInt64,
            std::bind(&BitStreamReader::readVarInt64, _1), bitSizeOfVarInt64(value));
}

TEST_F(ReflectableTest, varintReflectable)
{
    const int64_t value = INT64_MAX;
    auto reflectable = ReflectableFactory::getVarInt(value);
    checkSignedIntegral(value, reflectable, &IReflectable::getInt64,
            std::bind(&BitStreamReader::readVarInt, _1), bitSizeOfVarInt(value));
}

TEST_F(ReflectableTest, varuint16Reflectable)
{
    const uint16_t value = 1234;
    auto reflectable = ReflectableFactory::getVarUInt16(value);
    checkUnsignedIntegral(value, reflectable, &IReflectable::getUInt16,
            std::bind(&BitStreamReader::readVarUInt16, _1), bitSizeOfVarUInt16(value));
}

TEST_F(ReflectableTest, varuint32Reflectable)
{
    const uint32_t value = 0x1FFFFFFF;
    auto reflectable = ReflectableFactory::getVarUInt32(value);
    checkUnsignedIntegral(value, reflectable, &IReflectable::getUInt32,
            std::bind(&BitStreamReader::readVarUInt32, _1), bitSizeOfVarUInt32(value));
}

TEST_F(ReflectableTest, varuint64Reflectable)
{
    const uint64_t value = 4242424242;
    auto reflectable = ReflectableFactory::getVarUInt64(value);
    checkUnsignedIntegral(value, reflectable, &IReflectable::getUInt64,
            std::bind(&BitStreamReader::readVarUInt64, _1), bitSizeOfVarUInt64(value));
}

TEST_F(ReflectableTest, varuintReflectable)
{
    const uint64_t value = UINT64_MAX;
    auto reflectable = ReflectableFactory::getVarUInt(value);
    checkUnsignedIntegral(value, reflectable, &IReflectable::getUInt64,
            std::bind(&BitStreamReader::readVarUInt, _1), bitSizeOfVarUInt(value));
}

TEST_F(ReflectableTest, varsizeReflectable)
{
    const uint32_t value = (UINT32_C(1) << (7 + 7 + 7 + 7 + 3)) - 1;
    auto reflectable = ReflectableFactory::getVarSize(value);
    checkUnsignedIntegral(value, reflectable, &IReflectable::getUInt32,
            std::bind(&BitStreamReader::readVarSize, _1), bitSizeOfVarSize(value));
}

TEST_F(ReflectableTest, float16Reflectable)
{
    const float value = 2.0F;
    auto reflectable = ReflectableFactory::getFloat16(value);
    checkFloatingPoint(
            value, reflectable, &IReflectable::getFloat, std::bind(&BitStreamReader::readFloat16, _1));
}

TEST_F(ReflectableTest, float32Reflectable)
{
    const float value = 1.2F;
    auto reflectable = ReflectableFactory::getFloat32(value);
    checkFloatingPoint(
            value, reflectable, &IReflectable::getFloat, std::bind(&BitStreamReader::readFloat32, _1));
}

TEST_F(ReflectableTest, float64Reflectable)
{
    const double value = 1.2;
    auto reflectable = ReflectableFactory::getFloat64(value);
    checkFloatingPoint(
            value, reflectable, &IReflectable::getDouble, std::bind(&BitStreamReader::readFloat64, _1));
}

TEST_F(ReflectableTest, stringReflectable)
{
    const std::string value = "some longer string value to have a chance that some allocation hopefully occurs";
    auto reflectable = ReflectableFactory::getString(value);
    checkString(value, reflectable);
}

TEST_F(ReflectableTest, stringViewReflectable)
{
    auto view = makeStringView("some text as a string view");
    auto reflectable = ReflectableFactory::getString(view);
    checkString(view, reflectable);
}

TEST_F(ReflectableTest, bitBufferReflectable)
{
    const BitBuffer value = BitBuffer{std::vector<uint8_t>({0xAB, 0xF0}), 12};
    auto reflectable = ReflectableFactory::getBitBuffer(value);
    checkBitBuffer(value, reflectable);
}

TEST_F(ReflectableTest, bytesReflectable)
{
    const vector<uint8_t> value{{0, 127, 128, 255}};
    auto reflectable = ReflectableFactory::getBytes(value);
    checkBytes(value, reflectable);
}

TEST_F(ReflectableTest, boolConstArray)
{
    const auto rawArray = std::vector<bool>({true, false, true, false});
    auto reflectable = ReflectableFactory::getBoolArray(rawArray);
    checkArray(rawArray, reflectable, [&](bool value, const IReflectableConstPtr& elementReflectable) {
        checkUnsignedIntegral(
                value, elementReflectable, &IReflectable::getBool, std::bind(&BitStreamReader::readBool, _1));
    });
}

TEST_F(ReflectableTest, boolArray)
{
    auto rawArray = std::vector<bool>({true, false, true, false});
    auto reflectable = ReflectableFactory::getBoolArray(rawArray);
    checkArray(rawArray, reflectable, [&](bool value, const IReflectableConstPtr& elementReflectable) {
        checkUnsignedIntegral(
                value, elementReflectable, &IReflectable::getBool, std::bind(&BitStreamReader::readBool, _1));
    });
}

TEST_F(ReflectableTest, int8ConstArray)
{
    const auto rawArray = std::vector<int8_t>({-10, -20, 30, 40});
    auto reflectable = ReflectableFactory::getInt8Array(rawArray);
    checkArray(rawArray, reflectable, [&](int8_t value, const IReflectableConstPtr& elementReflectable) {
        checkSignedIntegral(value, elementReflectable, &IReflectable::getInt8,
                std::bind(&BitStreamReader::readSignedBits, _1, 8));
    });

    auto nonConstReflectable = std::const_pointer_cast<IReflectable>(reflectable);
    ASSERT_THROW(nonConstReflectable->getAnyValue(), CppRuntimeException);
}

TEST_F(ReflectableTest, int8Array)
{
    auto rawArray = std::vector<int8_t>({-10, -20, 30, 40});
    auto reflectable = ReflectableFactory::getInt8Array(rawArray);
    checkArray(rawArray, reflectable, [&](int8_t value, const IReflectableConstPtr& elementReflectable) {
        checkSignedIntegral(value, elementReflectable, &IReflectable::getInt8,
                std::bind(&BitStreamReader::readSignedBits, _1, 8));
    });
    checkArray(rawArray, static_cast<IReflectableConstPtr>(reflectable),
            [&](int8_t value, const IReflectableConstPtr& elementReflectable) {
                checkSignedIntegral(value, elementReflectable, &IReflectable::getInt8,
                        std::bind(&BitStreamReader::readSignedBits, _1, 8));
            });

    reflectable->resize(0);
    ASSERT_EQ(0, reflectable->size());
    reflectable->append(AnyHolder<>(static_cast<int8_t>(13)));
    ASSERT_EQ(1, reflectable->size());
    ASSERT_EQ(13, reflectable->at(0)->getInt8());
    reflectable->setAt(AnyHolder<>(static_cast<int8_t>(42)), 0);
    ASSERT_EQ(1, reflectable->size());
    ASSERT_EQ(42, reflectable->at(0)->getInt8());
    reflectable->resize(2);
    ASSERT_EQ(2, reflectable->size());

    // out of range
    ASSERT_THROW(reflectable->setAt(AnyHolder<>(static_cast<int8_t>(-42)), 2), CppRuntimeException);
}

TEST_F(ReflectableTest, int16ConstArray)
{
    const auto rawArray = std::vector<int16_t>({-100, -200, 300, 400});
    auto reflectable = ReflectableFactory::getInt16Array(rawArray);
    checkArray(rawArray, reflectable, [&](int16_t value, const IReflectableConstPtr& elementReflectable) {
        checkSignedIntegral(value, elementReflectable, &IReflectable::getInt16,
                std::bind(&BitStreamReader::readSignedBits, _1, 16));
    });
}

TEST_F(ReflectableTest, int16Array)
{
    auto rawArray = std::vector<int16_t>({-100, -200, 300, 400});
    auto reflectable = ReflectableFactory::getInt16Array(rawArray);
    checkArray(rawArray, reflectable, [&](int16_t value, const IReflectableConstPtr& elementReflectable) {
        checkSignedIntegral(value, elementReflectable, &IReflectable::getInt16,
                std::bind(&BitStreamReader::readSignedBits, _1, 16));
    });
}

TEST_F(ReflectableTest, int32ConstArray)
{
    const auto rawArray = std::vector<int32_t>({-10000, -20000, 30000, 40000});
    auto reflectable = ReflectableFactory::getInt32Array(rawArray);
    checkArray(rawArray, reflectable, [&](int32_t value, const IReflectableConstPtr& elementReflectable) {
        checkSignedIntegral(value, elementReflectable, &IReflectable::getInt32,
                std::bind(&BitStreamReader::readSignedBits, _1, 32));
    });
}

TEST_F(ReflectableTest, int32Array)
{
    auto rawArray = std::vector<int32_t>({-10000, -20000, 30000, 40000});
    auto reflectable = ReflectableFactory::getInt32Array(rawArray);
    checkArray(rawArray, reflectable, [&](int32_t value, const IReflectableConstPtr& elementReflectable) {
        checkSignedIntegral(value, elementReflectable, &IReflectable::getInt32,
                std::bind(&BitStreamReader::readSignedBits, _1, 32));
    });
}

TEST_F(ReflectableTest, int64ConstArray)
{
    const auto rawArray = std::vector<int64_t>({-10000000, -20000000, 30000000, 40000000});
    auto reflectable = ReflectableFactory::getInt64Array(rawArray);
    checkArray(rawArray, reflectable, [&](int64_t value, const IReflectableConstPtr& elementReflectable) {
        checkSignedIntegral(value, elementReflectable, &IReflectable::getInt64,
                std::bind(&BitStreamReader::readSignedBits64, _1, 64));
    });
}

TEST_F(ReflectableTest, int64Array)
{
    auto rawArray = std::vector<int64_t>({-10000000, -20000000, 30000000, 40000000});
    auto reflectable = ReflectableFactory::getInt64Array(rawArray);
    checkArray(rawArray, reflectable, [&](int64_t value, const IReflectableConstPtr& elementReflectable) {
        checkSignedIntegral(value, elementReflectable, &IReflectable::getInt64,
                std::bind(&BitStreamReader::readSignedBits64, _1, 64));
    });
}

TEST_F(ReflectableTest, uint8ConstArray)
{
    const auto rawArray = std::vector<uint8_t>({10, 20, 30, 40});
    auto reflectable = ReflectableFactory::getUInt8Array(rawArray);
    checkArray(rawArray, reflectable, [&](uint8_t value, const IReflectableConstPtr& elementReflectable) {
        checkUnsignedIntegral(value, elementReflectable, &IReflectable::getUInt8,
                std::bind(&BitStreamReader::readBits, _1, 8));
    });
}

TEST_F(ReflectableTest, uint8Array)
{
    auto rawArray = std::vector<uint8_t>{{10, 20, 30, 40}};
    auto reflectable = ReflectableFactory::getUInt8Array(rawArray);
    checkArray(rawArray, reflectable, [&](uint8_t value, const IReflectablePtr& elementReflectable) {
        checkUnsignedIntegral(value, elementReflectable, &IReflectable::getUInt8,
                std::bind(&BitStreamReader::readBits, _1, 8));
    });
}

TEST_F(ReflectableTest, uint16ConstArray)
{
    const auto rawArray = std::vector<uint16_t>({100, 200, 300, 400});
    auto reflectable = ReflectableFactory::getUInt16Array(rawArray);
    checkArray(rawArray, reflectable, [&](uint16_t value, const IReflectableConstPtr& elementReflectable) {
        checkUnsignedIntegral(value, elementReflectable, &IReflectable::getUInt16,
                std::bind(&BitStreamReader::readBits, _1, 16));
    });
}

TEST_F(ReflectableTest, uint16Array)
{
    auto rawArray = std::vector<uint16_t>{{100, 200, 300, 400}};
    auto reflectable = ReflectableFactory::getUInt16Array(rawArray);
    checkArray(rawArray, reflectable, [&](uint16_t value, const IReflectablePtr& elementReflectable) {
        checkUnsignedIntegral(value, elementReflectable, &IReflectable::getUInt16,
                std::bind(&BitStreamReader::readBits, _1, 16));
    });
}

TEST_F(ReflectableTest, uint32ConstArray)
{
    const auto rawArray = std::vector<uint32_t>({10000, 20000, 30000, 40000});
    auto reflectable = ReflectableFactory::getUInt32Array(rawArray);
    checkArray(rawArray, reflectable, [&](uint32_t value, const IReflectableConstPtr& elementReflectable) {
        checkUnsignedIntegral(value, elementReflectable, &IReflectable::getUInt32,
                std::bind(&BitStreamReader::readBits, _1, 32));
    });
}

TEST_F(ReflectableTest, uint32Array)
{
    auto rawArray = std::vector<uint32_t>{{10000, 20000, 30000, 40000}};
    auto reflectable = ReflectableFactory::getUInt32Array(rawArray);
    checkArray(rawArray, reflectable, [&](uint32_t value, const IReflectablePtr& elementReflectable) {
        checkUnsignedIntegral(value, elementReflectable, &IReflectable::getUInt32,
                std::bind(&BitStreamReader::readBits, _1, 32));
    });
}

TEST_F(ReflectableTest, uint64ConstArray)
{
    const auto rawArray = std::vector<uint64_t>({10000000, 20000000, 30000000, 40000000});
    auto reflectable = ReflectableFactory::getUInt64Array(rawArray);
    checkArray(rawArray, reflectable, [&](uint64_t value, const IReflectableConstPtr& elementReflectable) {
        checkUnsignedIntegral(value, elementReflectable, &IReflectable::getUInt64,
                std::bind(&BitStreamReader::readBits64, _1, 64));
    });
}

TEST_F(ReflectableTest, uint64Array)
{
    auto rawArray = std::vector<uint64_t>{{10000000, 20000000, 30000000, 40000000}};
    auto reflectable = ReflectableFactory::getUInt64Array(rawArray);
    checkArray(rawArray, reflectable, [&](uint64_t value, const IReflectablePtr& elementReflectable) {
        checkUnsignedIntegral(value, elementReflectable, &IReflectable::getUInt64,
                std::bind(&BitStreamReader::readBits64, _1, 64));
    });
}

TEST_F(ReflectableTest, fixedSignedBitField5ConstArray)
{
    const uint8_t numBits = 5;
    const auto rawArray = std::vector<int8_t>{{-3, -1, 2, 4, 6}};
    auto reflectable = ReflectableFactory::getFixedSignedBitFieldArray(rawArray, numBits);
    checkArray(rawArray, reflectable, [&](int8_t value, const IReflectableConstPtr& elementReflectable) {
        checkSignedIntegral(value, elementReflectable, &IReflectable::getInt8,
                std::bind(&BitStreamReader::readSignedBits, _1, numBits), numBits);
    });
}

TEST_F(ReflectableTest, fixedSignedBitField5Array)
{
    const uint8_t numBits = 5;
    auto rawArray = std::vector<int8_t>{{-3, -1, 2, 4, 6}};
    auto reflectable = ReflectableFactory::getFixedSignedBitFieldArray(rawArray, numBits);
    checkArray(rawArray, reflectable, [&](int8_t value, const IReflectableConstPtr& elementReflectable) {
        checkSignedIntegral(value, elementReflectable, &IReflectable::getInt8,
                std::bind(&BitStreamReader::readSignedBits, _1, numBits), numBits);
    });
    checkArray(rawArray, static_cast<IReflectableConstPtr>(reflectable),
            [&](int8_t value, const IReflectableConstPtr& elementReflectable) {
                checkSignedIntegral(value, elementReflectable, &IReflectable::getInt8,
                        std::bind(&BitStreamReader::readSignedBits, _1, numBits), numBits);
            });

    reflectable->resize(0);
    ASSERT_EQ(0, reflectable->size());
    reflectable->append(AnyHolder<>(static_cast<int8_t>(13)));
    ASSERT_EQ(1, reflectable->size());
    ASSERT_EQ(13, reflectable->at(0)->getInt8());
    reflectable->setAt(AnyHolder<>(static_cast<int8_t>(42)), 0);
    ASSERT_EQ(1, reflectable->size());
    ASSERT_EQ(42, reflectable->at(0)->getInt8());
    reflectable->resize(2);
    ASSERT_EQ(2, reflectable->size());

    // out of range
    ASSERT_THROW(reflectable->setAt(AnyHolder<>(static_cast<int8_t>(42)), 2), CppRuntimeException);
}

TEST_F(ReflectableTest, fixedUnsignedBitField5ConstArray)
{
    const uint8_t numBits = 5;
    const auto rawArray = std::vector<uint8_t>{{3, 1, 2, 4, 6}};
    auto reflectable = ReflectableFactory::getFixedUnsignedBitFieldArray(rawArray, numBits);
    checkArray(rawArray, reflectable, [&](uint8_t value, const IReflectableConstPtr& elementReflectable) {
        checkUnsignedIntegral(value, elementReflectable, &IReflectable::getUInt8,
                std::bind(&BitStreamReader::readBits, _1, numBits), numBits);
    });
}

TEST_F(ReflectableTest, fixedUnsignedBitField5Array)
{
    const uint8_t numBits = 5;
    auto rawArray = std::vector<uint8_t>{{3, 1, 2, 4, 6}};
    auto reflectable = ReflectableFactory::getFixedUnsignedBitFieldArray(rawArray, numBits);
    checkArray(rawArray, reflectable, [&](uint8_t value, const IReflectableConstPtr& elementReflectable) {
        checkUnsignedIntegral(value, elementReflectable, &IReflectable::getUInt8,
                std::bind(&BitStreamReader::readBits, _1, numBits), numBits);
    });
}

TEST_F(ReflectableTest, dynamicSignedBitField5ConstArray)
{
    const uint8_t maxBitSize = 8;
    const uint8_t numBits = 5;
    const auto rawArray = std::vector<int8_t>{{-3, -1, 2, 4, 6}};
    auto reflectable = ReflectableFactory::getDynamicSignedBitFieldArray(rawArray, maxBitSize, numBits);
    checkArray(rawArray, reflectable, [&](int8_t value, const IReflectableConstPtr& elementReflectable) {
        checkSignedIntegral(value, elementReflectable, &IReflectable::getInt8,
                std::bind(&BitStreamReader::readSignedBits, _1, numBits), numBits);
    });

    auto nonConstReflectable = std::const_pointer_cast<IReflectable>(reflectable);
    ASSERT_THROW(nonConstReflectable->getAnyValue(), CppRuntimeException);
}

TEST_F(ReflectableTest, dynamicSignedBitField5Array)
{
    const uint8_t maxBitSize = 8;
    const uint8_t numBits = 5;
    auto rawArray = std::vector<int8_t>{{-3, -1, 2, 4, 6}};
    auto reflectable = ReflectableFactory::getDynamicSignedBitFieldArray(rawArray, maxBitSize, numBits);
    checkArray(rawArray, reflectable, [&](int8_t value, const IReflectablePtr& elementReflectable) {
        checkSignedIntegral(value, elementReflectable, &IReflectable::getInt8,
                std::bind(&BitStreamReader::readSignedBits, _1, numBits), numBits);
    });
    checkArray(rawArray, static_cast<IReflectableConstPtr>(reflectable),
            [&](int8_t value, const IReflectableConstPtr& elementReflectable) {
                checkSignedIntegral(value, elementReflectable, &IReflectable::getInt8,
                        std::bind(&BitStreamReader::readSignedBits, _1, numBits), numBits);
            });

    reflectable->resize(0);
    ASSERT_EQ(0, reflectable->size());
    reflectable->append(AnyHolder<>(static_cast<int8_t>(13)));
    ASSERT_EQ(1, reflectable->size());
    ASSERT_EQ(13, reflectable->at(0)->getInt8());
    reflectable->setAt(AnyHolder<>(static_cast<int8_t>(42)), 0);
    ASSERT_EQ(1, reflectable->size());
    ASSERT_EQ(42, reflectable->at(0)->getInt8());
    reflectable->resize(2);
    ASSERT_EQ(2, reflectable->size());

    // out of range
    ASSERT_THROW(reflectable->setAt(AnyHolder<>(static_cast<int8_t>(42)), 2), CppRuntimeException);
}

TEST_F(ReflectableTest, dynamicUnsignedBitField5ConstArray)
{
    const uint8_t maxBitSize = 8;
    const uint8_t numBits = 5;
    const auto rawArray = std::vector<uint8_t>{{3, 1, 2, 4, 6}};
    auto reflectable = ReflectableFactory::getDynamicUnsignedBitFieldArray(rawArray, maxBitSize, numBits);
    checkArray(rawArray, reflectable, [&](uint8_t value, const IReflectableConstPtr& elementReflectable) {
        checkUnsignedIntegral(value, elementReflectable, &IReflectable::getUInt8,
                std::bind(&BitStreamReader::readBits, _1, numBits), numBits);
    });
}

TEST_F(ReflectableTest, dynamicUnsignedBitField5Array)
{
    const uint8_t maxBitSize = 8;
    const uint8_t numBits = 5;
    auto rawArray = std::vector<uint8_t>{{3, 1, 2, 4, 6}};
    auto reflectable = ReflectableFactory::getDynamicUnsignedBitFieldArray(rawArray, maxBitSize, numBits);
    checkArray(rawArray, reflectable, [&](uint8_t value, const IReflectableConstPtr& elementReflectable) {
        checkUnsignedIntegral(value, elementReflectable, &IReflectable::getUInt8,
                std::bind(&BitStreamReader::readBits, _1, numBits), numBits);
    });
}

TEST_F(ReflectableTest, varint16ConstArray)
{
    const auto rawArray = std::vector<int16_t>({-10, -20, 30, 40});
    auto reflectable = ReflectableFactory::getVarInt16Array(rawArray);
    checkArray(rawArray, reflectable, [&](int16_t value, const IReflectableConstPtr& elementReflectable) {
        checkSignedIntegral(value, elementReflectable, &IReflectable::getInt16,
                std::bind(&BitStreamReader::readVarInt16, _1));
    });
}

TEST_F(ReflectableTest, varint16Array)
{
    auto rawArray = std::vector<int16_t>{{-10, -20, 30, 40}};
    auto reflectable = ReflectableFactory::getVarInt16Array(rawArray);
    checkArray(rawArray, reflectable, [&](int16_t value, const IReflectablePtr& elementReflectable) {
        checkSignedIntegral(value, elementReflectable, &IReflectable::getInt16,
                std::bind(&BitStreamReader::readVarInt16, _1));
    });
}

TEST_F(ReflectableTest, varint32ConstArray)
{
    const auto rawArray = std::vector<int32_t>({-10000, -20000, 30000, 40000});
    auto reflectable = ReflectableFactory::getVarInt32Array(rawArray);
    checkArray(rawArray, reflectable, [&](int32_t value, const IReflectableConstPtr& elementReflectable) {
        checkSignedIntegral(value, elementReflectable, &IReflectable::getInt32,
                std::bind(&BitStreamReader::readVarInt32, _1));
    });
}

TEST_F(ReflectableTest, varint32Array)
{
    auto rawArray = std::vector<int32_t>{{-10000, -20000, 30000, 40000}};
    auto reflectable = ReflectableFactory::getVarInt32Array(rawArray);
    checkArray(rawArray, reflectable, [&](int32_t value, const IReflectablePtr& elementReflectable) {
        checkSignedIntegral(value, elementReflectable, &IReflectable::getInt32,
                std::bind(&BitStreamReader::readVarInt32, _1));
    });
}

TEST_F(ReflectableTest, varint64ConstArray)
{
    const auto rawArray = std::vector<int64_t>({-10000000, -20000000, 30000000, 40000000});
    auto reflectable = ReflectableFactory::getVarInt64Array(rawArray);
    checkArray(rawArray, reflectable, [&](int64_t value, const IReflectableConstPtr& elementReflectable) {
        checkSignedIntegral(value, elementReflectable, &IReflectable::getInt64,
                std::bind(&BitStreamReader::readVarInt64, _1));
    });
}

TEST_F(ReflectableTest, varint64Array)
{
    auto rawArray = std::vector<int64_t>{{-10000000, -20000000, 30000000, 40000000}};
    auto reflectable = ReflectableFactory::getVarInt64Array(rawArray);
    checkArray(rawArray, reflectable, [&](int64_t value, const IReflectablePtr& elementReflectable) {
        checkSignedIntegral(value, elementReflectable, &IReflectable::getInt64,
                std::bind(&BitStreamReader::readVarInt64, _1));
    });
}

TEST_F(ReflectableTest, varintConstArray)
{
    const auto rawArray = std::vector<int64_t>({-10000000, -20000000, 30000000, 40000000});
    auto reflectable = ReflectableFactory::getVarIntArray(rawArray);
    checkArray(rawArray, reflectable, [&](int64_t value, const IReflectableConstPtr& elementReflectable) {
        checkSignedIntegral(value, elementReflectable, &IReflectable::getInt64,
                std::bind(&BitStreamReader::readVarInt, _1));
    });
}

TEST_F(ReflectableTest, varintArray)
{
    auto rawArray = std::vector<int64_t>{{-10000000, -20000000, 30000000, 40000000}};
    auto reflectable = ReflectableFactory::getVarIntArray(rawArray);
    checkArray(rawArray, reflectable, [&](int64_t value, const IReflectablePtr& elementReflectable) {
        checkSignedIntegral(value, elementReflectable, &IReflectable::getInt64,
                std::bind(&BitStreamReader::readVarInt, _1));
    });
}

TEST_F(ReflectableTest, varuint16ConstArray)
{
    const auto rawArray = std::vector<uint16_t>({10, 20, 30, 40});
    auto reflectable = ReflectableFactory::getVarUInt16Array(rawArray);
    checkArray(rawArray, reflectable, [&](uint16_t value, const IReflectableConstPtr& elementReflectable) {
        checkUnsignedIntegral(value, elementReflectable, &IReflectable::getUInt16,
                std::bind(&BitStreamReader::readVarUInt16, _1));
    });
}

TEST_F(ReflectableTest, varuint16Array)
{
    auto rawArray = std::vector<uint16_t>{{10, 20, 30, 40}};
    auto reflectable = ReflectableFactory::getVarUInt16Array(rawArray);
    checkArray(rawArray, reflectable, [&](uint16_t value, const IReflectablePtr& elementReflectable) {
        checkUnsignedIntegral(value, elementReflectable, &IReflectable::getUInt16,
                std::bind(&BitStreamReader::readVarUInt16, _1));
    });
}

TEST_F(ReflectableTest, varuint32ConstArray)
{
    const auto rawArray = std::vector<uint32_t>({10000, 20000, 30000, 40000});
    auto reflectable = ReflectableFactory::getVarUInt32Array(rawArray);
    checkArray(rawArray, reflectable, [&](uint32_t value, const IReflectableConstPtr& elementReflectable) {
        checkUnsignedIntegral(value, elementReflectable, &IReflectable::getUInt32,
                std::bind(&BitStreamReader::readVarUInt32, _1));
    });
}

TEST_F(ReflectableTest, varuint32Array)
{
    auto rawArray = std::vector<uint32_t>{{10000, 20000, 30000, 40000}};
    auto reflectable = ReflectableFactory::getVarUInt32Array(rawArray);
    checkArray(rawArray, reflectable, [&](uint32_t value, const IReflectablePtr& elementReflectable) {
        checkUnsignedIntegral(value, elementReflectable, &IReflectable::getUInt32,
                std::bind(&BitStreamReader::readVarUInt32, _1));
    });
}

TEST_F(ReflectableTest, varuint64ConstArray)
{
    const auto rawArray = std::vector<uint64_t>({10000, 20000, 30000, 40000});
    auto reflectable = ReflectableFactory::getVarUInt64Array(rawArray);
    checkArray(rawArray, reflectable, [&](uint64_t value, const IReflectableConstPtr& elementReflectable) {
        checkUnsignedIntegral(value, elementReflectable, &IReflectable::getUInt64,
                std::bind(&BitStreamReader::readVarUInt64, _1));
    });
}

TEST_F(ReflectableTest, varuint64Array)
{
    auto rawArray = std::vector<uint64_t>{{10000, 20000, 30000, 40000}};
    auto reflectable = ReflectableFactory::getVarUInt64Array(rawArray);
    checkArray(rawArray, reflectable, [&](uint64_t value, const IReflectablePtr& elementReflectable) {
        checkUnsignedIntegral(value, elementReflectable, &IReflectable::getUInt64,
                std::bind(&BitStreamReader::readVarUInt64, _1));
    });
}

TEST_F(ReflectableTest, varuintConstArray)
{
    const auto rawArray = std::vector<uint64_t>({10000, 20000, 30000, 40000});
    auto reflectable = ReflectableFactory::getVarUIntArray(rawArray);
    checkArray(rawArray, reflectable, [&](uint64_t value, const IReflectableConstPtr& elementReflectable) {
        checkUnsignedIntegral(value, elementReflectable, &IReflectable::getUInt64,
                std::bind(&BitStreamReader::readVarUInt, _1));
    });
}

TEST_F(ReflectableTest, varuintArray)
{
    auto rawArray = std::vector<uint64_t>{{10000, 20000, 30000, 40000}};
    auto reflectable = ReflectableFactory::getVarUIntArray(rawArray);
    checkArray(rawArray, reflectable, [&](uint64_t value, const IReflectablePtr& elementReflectable) {
        checkUnsignedIntegral(value, elementReflectable, &IReflectable::getUInt64,
                std::bind(&BitStreamReader::readVarUInt, _1));
    });
}

TEST_F(ReflectableTest, varsizeConstArray)
{
    const auto rawArray = std::vector<uint32_t>({10000, 20000, 30000, 40000});
    auto reflectable = ReflectableFactory::getVarSizeArray(rawArray);
    checkArray(rawArray, reflectable, [&](uint32_t value, const IReflectableConstPtr& elementReflectable) {
        checkUnsignedIntegral(value, elementReflectable, &IReflectable::getUInt32,
                std::bind(&BitStreamReader::readVarSize, _1));
    });
}

TEST_F(ReflectableTest, varsizeArray)
{
    auto rawArray = std::vector<uint32_t>{{10000, 20000, 30000, 40000}};
    auto reflectable = ReflectableFactory::getVarSizeArray(rawArray);
    checkArray(rawArray, reflectable, [&](uint32_t value, const IReflectablePtr& elementReflectable) {
        checkUnsignedIntegral(value, elementReflectable, &IReflectable::getUInt32,
                std::bind(&BitStreamReader::readVarSize, _1));
    });
}

TEST_F(ReflectableTest, float16ConstArray)
{
    const auto rawArray = std::vector<float>{{2.0F, 0.0F}};
    auto reflectable = ReflectableFactory::getFloat16Array(rawArray);
    checkArray(rawArray, reflectable, [&](float value, const IReflectableConstPtr& elementReflectable) {
        checkFloatingPoint(value, elementReflectable, &IReflectable::getFloat,
                std::bind(&BitStreamReader::readFloat16, _1));
    });
}

TEST_F(ReflectableTest, float16Array)
{
    auto rawArray = std::vector<float>{{2.0F, 0.0F}};
    auto reflectable = ReflectableFactory::getFloat16Array(rawArray);
    checkArray(rawArray, reflectable, [&](float value, const IReflectablePtr& elementReflectable) {
        checkFloatingPoint(value, elementReflectable, &IReflectable::getFloat,
                std::bind(&BitStreamReader::readFloat16, _1));
    });
}

TEST_F(ReflectableTest, float32ConstArray)
{
    const auto rawArray = std::vector<float>{{2.0F, 0.0F, 1.2F}};
    auto reflectable = ReflectableFactory::getFloat32Array(rawArray);
    checkArray(rawArray, reflectable, [&](float value, const IReflectableConstPtr& elementReflectable) {
        checkFloatingPoint(value, elementReflectable, &IReflectable::getFloat,
                std::bind(&BitStreamReader::readFloat32, _1));
    });
}

TEST_F(ReflectableTest, float32Array)
{
    auto rawArray = std::vector<float>{{2.0F, 0.0F, 1.2F}};
    auto reflectable = ReflectableFactory::getFloat32Array(rawArray);
    checkArray(rawArray, reflectable, [&](float value, const IReflectablePtr& elementReflectable) {
        checkFloatingPoint(value, elementReflectable, &IReflectable::getFloat,
                std::bind(&BitStreamReader::readFloat32, _1));
    });
}

TEST_F(ReflectableTest, float64ConstArray)
{
    const auto rawArray = std::vector<double>{{2.0, 0.0, 1.2}};
    auto reflectable = ReflectableFactory::getFloat64Array(rawArray);
    checkArray(rawArray, reflectable, [&](double value, const IReflectableConstPtr& elementReflectable) {
        checkFloatingPoint(value, elementReflectable, &IReflectable::getDouble,
                std::bind(&BitStreamReader::readFloat64, _1));
    });
}

TEST_F(ReflectableTest, float64Array)
{
    auto rawArray = std::vector<double>{{2.0, 0.0, 1.2}};
    auto reflectable = ReflectableFactory::getFloat64Array(rawArray);
    checkArray(rawArray, reflectable, [&](double value, const IReflectablePtr& elementReflectable) {
        checkFloatingPoint(value, elementReflectable, &IReflectable::getDouble,
                std::bind(&BitStreamReader::readFloat64, _1));
    });
}

TEST_F(ReflectableTest, stringConstArray)
{
    const auto rawArray = std::vector<std::string>{{"one", "two", "three"}};
    auto reflectable = ReflectableFactory::getStringArray(rawArray);
    checkArray(rawArray, reflectable, [&](StringView value, const IReflectableConstPtr& elementReflectable) {
        checkString(value, elementReflectable);
    });

    auto nonConstReflectable = std::const_pointer_cast<IReflectable>(reflectable);
    ASSERT_THROW(nonConstReflectable->getAnyValue(), CppRuntimeException);
}

TEST_F(ReflectableTest, stringArray)
{
    auto rawArray = std::vector<std::string>{{"one", "two", "three"}};
    auto reflectable = ReflectableFactory::getStringArray(rawArray);
    checkArray(rawArray, reflectable, [&](StringView value, const IReflectablePtr& elementReflectable) {
        checkString(value, elementReflectable);
    });
    checkArray(rawArray, static_cast<IReflectableConstPtr>(reflectable),
            [&](StringView value, const IReflectableConstPtr& elementReflectable) {
                checkString(value, elementReflectable);
            });

    reflectable->resize(0);
    ASSERT_EQ(0, reflectable->size());
    reflectable->append(AnyHolder<>(std::string("appended")));
    ASSERT_EQ(1, reflectable->size());
    ASSERT_EQ("appended"_sv, reflectable->at(0)->getStringView());
    reflectable->setAt(AnyHolder<>(std::string("set")), 0);
    ASSERT_EQ(1, reflectable->size());
    ASSERT_EQ("set"_sv, reflectable->at(0)->getStringView());
    reflectable->resize(2);
    ASSERT_EQ(2, reflectable->size());

    // out of range
    ASSERT_THROW(reflectable->setAt(AnyHolder<>(std::string("set")), 2), CppRuntimeException);
}

TEST_F(ReflectableTest, bitBufferConstArray)
{
    const auto rawArray = std::vector<BitBuffer>{{BitBuffer({0xF8}, 5), BitBuffer({0xAB, 0xCD}, 16)}};
    auto reflectable = ReflectableFactory::getBitBufferArray(rawArray);
    checkArray(
            rawArray, reflectable, [&](const BitBuffer& value, const IReflectableConstPtr& elementReflectable) {
                checkBitBuffer(value, elementReflectable);
            });
}

TEST_F(ReflectableTest, bitBufferArray)
{
    auto rawArray = std::vector<BitBuffer>{{BitBuffer({0xF8}, 5), BitBuffer({0xAB, 0xCD}, 16)}};
    auto reflectable = ReflectableFactory::getBitBufferArray(rawArray);
    checkArray(rawArray, reflectable, [&](const BitBuffer& value, const IReflectablePtr& elementReflectable) {
        checkBitBuffer(value, elementReflectable);
    });
    checkArray(rawArray, static_cast<IReflectableConstPtr>(reflectable),
            [&](const BitBuffer& value, const IReflectableConstPtr& elementReflectable) {
                checkBitBuffer(value, elementReflectable);
            });

    reflectable->resize(0);
    ASSERT_EQ(0, reflectable->size());
    reflectable->append(AnyHolder<>(BitBuffer()));
    ASSERT_EQ(1, reflectable->size());
    ASSERT_EQ(0, reflectable->at(0)->getBitBuffer().getBitSize());
    reflectable->setAt(AnyHolder<>(BitBuffer({0xA0}, 4)), 0);
    ASSERT_EQ(1, reflectable->size());
    ASSERT_EQ(4, reflectable->at(0)->getBitBuffer().getBitSize());
    reflectable->resize(2);
    ASSERT_EQ(2, reflectable->size());

    // out of range
    ASSERT_THROW(reflectable->setAt(AnyHolder<>(BitBuffer()), 2), CppRuntimeException);
}

TEST_F(ReflectableTest, bytesConstArray)
{
    const auto rawArray = std::vector<std::vector<uint8_t>>{{{{0x00, 0x01}}, {{0xFF, 0xFE}}}};
    auto reflectable = ReflectableFactory::getBytesArray(rawArray);
    checkArray(rawArray, reflectable,
            [&](const vector<uint8_t>& value, const IReflectableConstPtr& elementReflectable) {
                checkBytes(value, elementReflectable);
            });
}

TEST_F(ReflectableTest, bytesArray)
{
    auto rawArray = std::vector<std::vector<uint8_t>>{{{{0x00, 0x01}}, {{0xFF, 0xFE}}}};
    auto reflectable = ReflectableFactory::getBytesArray(rawArray);
    checkArray(rawArray, reflectable,
            [&](const vector<uint8_t>& value, const IReflectablePtr& elementReflectable) {
                checkBytes(value, elementReflectable);
            });
    checkArray(rawArray, static_cast<IReflectableConstPtr>(reflectable),
            [&](const vector<uint8_t>& value, const IReflectableConstPtr& elementReflectable) {
                checkBytes(value, elementReflectable);
            });

    reflectable->resize(0);
    ASSERT_EQ(0, reflectable->size());
    reflectable->append(AnyHolder<>(std::vector<uint8_t>()));
    ASSERT_EQ(1, reflectable->size());
    ASSERT_EQ(0, reflectable->at(0)->getBytes().size());
    reflectable->setAt(AnyHolder<>(std::vector<uint8_t>{{0xAB, 0xCD}}), 0);
    ASSERT_EQ(1, reflectable->size());
    ASSERT_EQ(2, reflectable->at(0)->getBytes().size());
    reflectable->resize(2);
    ASSERT_EQ(2, reflectable->size());

    // out of range
    ASSERT_THROW(reflectable->setAt(AnyHolder<>(std::vector<uint8_t>()), 2), CppRuntimeException);
}

TEST_F(ReflectableTest, bitmaskConstReflectable)
{
    const ReflectableBitmask bitmask = ReflectableBitmask::Values::WRITE;
    auto reflectable = bitmask.reflectable();
    checkBitmask(bitmask, reflectable);
}

TEST_F(ReflectableTest, bitmaskReflectable)
{
    ReflectableBitmask bitmask = ReflectableBitmask::Values::WRITE;
    auto reflectable = bitmask.reflectable();
    checkBitmask(bitmask, reflectable);
}

TEST_F(ReflectableTest, bitmaskConstArray)
{
    const auto rawArray = std::vector<ReflectableBitmask>{{ReflectableBitmask::Values::WRITE,
            ReflectableBitmask::Values::CREATE, ReflectableBitmask::Values::READ}};
    auto reflectable = ReflectableFactory::getBitmaskArray(rawArray);
    checkArray(rawArray, reflectable,
            [&](ReflectableBitmask value, const IReflectableConstPtr& elementReflectable) {
                checkBitmask(value, elementReflectable);
            });

    auto nonConstReflectable = std::const_pointer_cast<IReflectable>(reflectable);
    ASSERT_THROW(nonConstReflectable->getAnyValue(), CppRuntimeException);
}

TEST_F(ReflectableTest, bitmaskArray)
{
    auto rawArray = std::vector<ReflectableBitmask>{{ReflectableBitmask::Values::WRITE,
            ReflectableBitmask::Values::CREATE, ReflectableBitmask::Values::READ}};
    auto reflectable = ReflectableFactory::getBitmaskArray(rawArray);
    checkArray(rawArray, reflectable, [&](ReflectableBitmask value, const IReflectablePtr& elementReflectable) {
        checkBitmask(value, elementReflectable);
    });
    checkArray(rawArray, static_cast<IReflectableConstPtr>(reflectable),
            [&](ReflectableBitmask value, const IReflectableConstPtr& elementReflectable) {
                checkBitmask(value, elementReflectable);
            });

    reflectable->resize(0);
    ASSERT_EQ(0, reflectable->size());
    reflectable->append(AnyHolder<>(ReflectableBitmask(ReflectableBitmask::Values::READ)));
    ASSERT_EQ(1, reflectable->size());
    ASSERT_EQ(ReflectableBitmask::Values::READ, ReflectableBitmask(reflectable->at(0)->getUInt8()));
    reflectable->setAt(AnyHolder<>(ReflectableBitmask(ReflectableBitmask::Values::CREATE)), 0);
    ASSERT_EQ(1, reflectable->size());
    ASSERT_EQ(ReflectableBitmask::Values::CREATE, ReflectableBitmask(reflectable->at(0)->getUInt8()));
    reflectable->resize(2);
    ASSERT_EQ(2, reflectable->size());

    reflectable->append(AnyHolder<>(ReflectableBitmask(ReflectableBitmask::Values::WRITE).getValue()));
    ASSERT_EQ(3, reflectable->size());
    ASSERT_EQ(ReflectableBitmask::Values::WRITE, ReflectableBitmask(reflectable->at(2)->getUInt8()));
    reflectable->setAt(AnyHolder<>(ReflectableBitmask(ReflectableBitmask::Values::READ).getValue()), 2);
    ASSERT_EQ(3, reflectable->size());
    ASSERT_EQ(ReflectableBitmask::Values::READ, ReflectableBitmask(reflectable->at(2)->getUInt8()));

    // out of range
    ASSERT_THROW(reflectable->setAt(AnyHolder<>(ReflectableBitmask::Values::CREATE), 3), CppRuntimeException);
}

TEST_F(ReflectableTest, enumReflectable)
{
    const ReflectableEnum enumeration = ReflectableEnum::VALUE1;
    auto reflectable = enumReflectable(enumeration);
    checkEnum(enumeration, reflectable);
}

TEST_F(ReflectableTest, enumConstArray)
{
    const auto rawArray = std::vector<ReflectableEnum>{
            {ReflectableEnum::VALUE1, ReflectableEnum::VALUE2, ReflectableEnum::VALUE3}};
    auto reflectable = ReflectableFactory::getEnumArray(rawArray);
    checkArray(
            rawArray, reflectable, [&](ReflectableEnum value, const IReflectableConstPtr& elementReflectable) {
                checkEnum(value, elementReflectable);
            });

    auto nonConstReflectable = std::const_pointer_cast<IReflectable>(reflectable);
    ASSERT_THROW(nonConstReflectable->getAnyValue(), CppRuntimeException);
}

TEST_F(ReflectableTest, enumArray)
{
    auto rawArray = std::vector<ReflectableEnum>{
            {ReflectableEnum::VALUE1, ReflectableEnum::VALUE2, ReflectableEnum::VALUE3}};
    auto reflectable = ReflectableFactory::getEnumArray(rawArray);
    checkArray(rawArray, reflectable, [&](ReflectableEnum value, const IReflectablePtr& elementReflectable) {
        checkEnum(value, elementReflectable);
    });
    checkArray(rawArray, static_cast<IReflectableConstPtr>(reflectable),
            [&](ReflectableEnum value, const IReflectableConstPtr& elementReflectable) {
                checkEnum(value, elementReflectable);
            });

    reflectable->resize(0);
    ASSERT_EQ(0, reflectable->size());
    reflectable->append(AnyHolder<>(ReflectableEnum::VALUE3));
    ASSERT_EQ(1, reflectable->size());
    ASSERT_EQ(enumToValue(ReflectableEnum::VALUE3), reflectable->at(0)->getInt8());
    reflectable->setAt(AnyHolder<>(ReflectableEnum::VALUE2), 0);
    ASSERT_EQ(1, reflectable->size());
    ASSERT_EQ(enumToValue(ReflectableEnum::VALUE2), reflectable->at(0)->getInt8());
    reflectable->resize(2);
    ASSERT_EQ(2, reflectable->size());

    reflectable->append(AnyHolder<>(enumToValue(ReflectableEnum::VALUE1)));
    ASSERT_EQ(3, reflectable->size());
    ASSERT_EQ(enumToValue(ReflectableEnum::VALUE1), reflectable->at(2)->getInt8());
    reflectable->setAt(AnyHolder<>(enumToValue(ReflectableEnum::VALUE2)), 2);
    ASSERT_EQ(3, reflectable->size());
    ASSERT_EQ(enumToValue(ReflectableEnum::VALUE2), reflectable->at(2)->getInt8());

    // out of range
    ASSERT_THROW(reflectable->setAt(AnyHolder<>(ReflectableEnum::VALUE2), 3), CppRuntimeException);
}

TEST_F(ReflectableTest, compoundConst)
{
    {
        const ReflectableObject reflectableObjectUninitialized =
                ReflectableObject{"test", ReflectableNested{13}};
        auto reflectable = reflectableObjectUninitialized.reflectable();
        ASSERT_FALSE(reflectable->find("reflectableNested.stringParam"));
    }

    const ReflectableObject reflectableObject = createInitializedReflectableObject("test", 13);
    auto reflectable = reflectableObject.reflectable();
    checkCompound(reflectableObject, reflectable);

    IReflectablePtr nonConstReflectable = std::const_pointer_cast<IReflectable>(reflectable);
    ASSERT_THROW(nonConstReflectable->initializeChildren(), CppRuntimeException);
    ASSERT_THROW(nonConstReflectable->initialize(vector<AnyHolder<>>()), CppRuntimeException);
    ASSERT_NO_THROW(reflectable->getField("reflectableNested"));
    ASSERT_THROW(nonConstReflectable->getField("reflectableNested"), CppRuntimeException);
    ASSERT_NO_THROW(reflectable->getAnyValue());
    ASSERT_THROW(nonConstReflectable->getAnyValue(), CppRuntimeException);

    IReflectableConstPtr childReflectable = reflectable->getField("reflectableNested");
    IReflectablePtr nonConstChildReflectable = std::const_pointer_cast<IReflectable>(childReflectable);
    ASSERT_THROW(nonConstChildReflectable->setField("value", AnyHolder<>(static_cast<uint32_t>(11))),
            CppRuntimeException);
    ASSERT_NO_THROW(childReflectable->getParameter("dummyParam"));
    ASSERT_THROW(nonConstChildReflectable->getParameter("dummyParam"), CppRuntimeException);
    ASSERT_NO_THROW(childReflectable->callFunction("getValue"));
    ASSERT_THROW(nonConstChildReflectable->callFunction("getValue"), CppRuntimeException);
    ASSERT_THROW(nonConstChildReflectable->initializeOffsets(0), CppRuntimeException);
}

TEST_F(ReflectableTest, compound)
{
    {
        ReflectableObject reflectableObjectUninitialized = ReflectableObject{"test", ReflectableNested{13}};
        auto reflectable = reflectableObjectUninitialized.reflectable();
        ASSERT_FALSE(reflectable->find("reflectableNested.stringParam"));
    }

    ReflectableObject reflectableObject = ReflectableObject{"test", ReflectableNested{13}};
    auto reflectable = reflectableObject.reflectable();

    // not initialized
    ASSERT_THROW(reflectable->getField("reflectableNested")->getParameter("dummyParam"), CppRuntimeException);
    ASSERT_THROW(reflectable->getField("reflectableNested")->getParameter("stringParam"), CppRuntimeException);
    ASSERT_FALSE(static_cast<IReflectableConstPtr>(reflectable)->find("reflectableNested.stringParam"));

    reflectable->initializeChildren();
    checkCompound(reflectableObject, reflectable);
}

TEST_F(ReflectableTest, compoundConstArray)
{
    ReflectableObject reflectableObject1;
    reflectableObject1 = createInitializedReflectableObject("1", 13); // to cover assignment operator
    const auto rawArray =
            std::vector<ReflectableObject>{{reflectableObject1, createInitializedReflectableObject("2", 42)}};
    auto reflectable = ReflectableFactory::getCompoundArray(rawArray);
    checkArray(rawArray, reflectable,
            [&](const ReflectableObject& value, const IReflectableConstPtr& elementReflectable) {
                checkCompound(value, elementReflectable);
            });

    IReflectablePtr nonConstReflectable = std::const_pointer_cast<IReflectable>(reflectable);
    ASSERT_THROW(nonConstReflectable->at(0), CppRuntimeException);
    ASSERT_THROW((*nonConstReflectable)[0], CppRuntimeException);
    ASSERT_THROW(nonConstReflectable->resize(nonConstReflectable->size() + 1), CppRuntimeException);
    ASSERT_THROW(nonConstReflectable->setAt(AnyHolder<>(ReflectableObject{"test", ReflectableNested{0}}), 0),
            CppRuntimeException);
    ASSERT_THROW(nonConstReflectable->append(AnyHolder<>(ReflectableObject{"test", ReflectableNested{0}})),
            CppRuntimeException);
    ASSERT_THROW(nonConstReflectable->getAnyValue(), CppRuntimeException);
}

TEST_F(ReflectableTest, compoundArray)
{
    auto rawArray = std::vector<ReflectableObject>{
            {createInitializedReflectableObject("1", 13), createInitializedReflectableObject("2", 42)}};
    auto reflectable = ReflectableFactory::getCompoundArray(rawArray);
    checkArray(rawArray, reflectable,
            [&](const ReflectableObject& value, const IReflectablePtr& elementReflectable) {
                checkCompound(value, elementReflectable);
            });
    checkArray(rawArray, static_cast<IReflectableConstPtr>(reflectable),
            [&](const ReflectableObject& value, const IReflectableConstPtr& elementReflectable) {
                checkCompound(value, elementReflectable);
            });

    reflectable->resize(reflectable->size() + 1);
    IReflectablePtr newCompound = reflectable->at(reflectable->size() - 1);
    ASSERT_TRUE(newCompound);

    reflectable->setAt(AnyHolder<>(ReflectableObject{"test", ReflectableNested{0}}), 0);
    ASSERT_EQ(0, reflectable->at(0)->find("reflectableNested.value")->getUInt32());
    reflectable->append(AnyHolder<>(ReflectableObject{"test|", ReflectableNested{1}}));
    ASSERT_EQ(1, reflectable->at(reflectable->size() - 1)->find("reflectableNested.value")->getUInt32());

    const size_t size = reflectable->size();
    ASSERT_THROW(reflectable->setAt(AnyHolder<>(), size), CppRuntimeException); // out of range
}

TEST_F(ReflectableTest, defaultUnimplementedMethods)
{
    class Reflectable : public ReflectableBase<std::allocator<uint8_t>>
    {
    public:
        using ReflectableBase<std::allocator<uint8_t>>::ReflectableBase;
    };

    Reflectable reflectable(BuiltinTypeInfo<>::getUInt8());
    ASSERT_THROW(reflectable.bitSizeOf(0), CppRuntimeException);

    BitBuffer bitBuffer(0);
    BitStreamWriter writer(bitBuffer);
    ASSERT_THROW(reflectable.write(writer), CppRuntimeException);

    ASSERT_THROW(reflectable.getAnyValue(), CppRuntimeException);

    const Reflectable& constReflectableRef = reflectable;
    ASSERT_THROW(constReflectableRef.getAnyValue(), CppRuntimeException);

    ASSERT_THROW(reflectable.parsingInfo(), CppRuntimeException);
}

TEST_F(ReflectableTest, reflectableOwner)
{
    auto reflectable = ReflectableObject::typeInfo().createInstance();

    // must be as the first one to initialize object
    ASSERT_NO_THROW(reflectable->initializeChildren());

    IReflectableConstPtr constReflectable = reflectable;

    // same as default initialized
    ReflectableObject defaultReflectableObject;
    defaultReflectableObject.initializeChildren();
    ASSERT_EQ(defaultReflectableObject,
            reflectable->getAnyValue().template get<std::reference_wrapper<ReflectableObject>>().get());
    ASSERT_EQ(defaultReflectableObject,
            constReflectable->getAnyValue().template get<std::reference_wrapper<ReflectableObject>>().get());

    // has no type arguments
    ASSERT_THROW(reflectable->initialize(std::vector<AnyHolder<>>()), CppRuntimeException);

    ASSERT_TRUE(TypeInfoUtil::isCompound(reflectable->getTypeInfo().getSchemaType()));
    ASSERT_FALSE(reflectable->isArray());
    reflectable->setField("reflectableNested", AnyHolder<>(ReflectableNested{42}));
    ASSERT_EQ(42, reflectable->getField("reflectableNested")->getField("value")->getUInt32());
    ASSERT_EQ(42, constReflectable->getField("reflectableNested")->getField("value")->getUInt32());
    ASSERT_THROW(reflectable->createField("nonexistent"), CppRuntimeException);
    ASSERT_THROW(reflectable->getParameter("nonexistent"), CppRuntimeException);
    ASSERT_THROW(constReflectable->getParameter("nonexistent"), CppRuntimeException);
    ASSERT_THROW(reflectable->callFunction("nonexistent"), CppRuntimeException);
    ASSERT_THROW(constReflectable->callFunction("nonexistent"), CppRuntimeException);
    ASSERT_THROW(reflectable->getChoice(), CppRuntimeException);
    ASSERT_THROW(constReflectable->getChoice(), CppRuntimeException);
    ASSERT_FALSE(reflectable->find("nonexistent"));
    ASSERT_FALSE(constReflectable->find("nonexistent"));
    ASSERT_FALSE((*reflectable)["nonexistent"]);
    ASSERT_FALSE((*constReflectable)["nonexistent"]);

    ASSERT_THROW(reflectable->size(), CppRuntimeException); // not an array
    ASSERT_THROW(reflectable->resize(0), CppRuntimeException); // not an array
    ASSERT_THROW(reflectable->at(0), CppRuntimeException); // not an array
    ASSERT_THROW(constReflectable->at(0), CppRuntimeException); // not an array
    ASSERT_THROW((*reflectable)[0], CppRuntimeException); // not an array
    ASSERT_THROW((*constReflectable)[0], CppRuntimeException); // not an array
    ASSERT_THROW(reflectable->setAt(AnyHolder<>(), 0), CppRuntimeException); // not an array
    ASSERT_THROW(reflectable->append(AnyHolder<>()), CppRuntimeException); // not an array

    ASSERT_THROW(reflectable->getBool(), CppRuntimeException);
    ASSERT_THROW(reflectable->getInt8(), CppRuntimeException);
    ASSERT_THROW(reflectable->getInt16(), CppRuntimeException);
    ASSERT_THROW(reflectable->getInt32(), CppRuntimeException);
    ASSERT_THROW(reflectable->getInt64(), CppRuntimeException);
    ASSERT_THROW(reflectable->getUInt8(), CppRuntimeException);
    ASSERT_THROW(reflectable->getUInt16(), CppRuntimeException);
    ASSERT_THROW(reflectable->getUInt32(), CppRuntimeException);
    ASSERT_THROW(reflectable->getUInt64(), CppRuntimeException);
    ASSERT_THROW(reflectable->getFloat(), CppRuntimeException);
    ASSERT_THROW(reflectable->getDouble(), CppRuntimeException);
    ASSERT_THROW(reflectable->getBytes(), CppRuntimeException);
    ASSERT_THROW(reflectable->getStringView(), CppRuntimeException);
    ASSERT_THROW(reflectable->getBitBuffer(), CppRuntimeException);

    ASSERT_THROW(reflectable->toInt(), CppRuntimeException);
    ASSERT_THROW(reflectable->toUInt(), CppRuntimeException);
    ASSERT_THROW(reflectable->toDouble(), CppRuntimeException);
    ASSERT_THROW(reflectable->toString(), CppRuntimeException);

    ASSERT_EQ(reflectable->bitSizeOf(), reflectable->initializeOffsets());

    ASSERT_NO_THROW(reflectable->initializeChildren());

    const size_t bitSizeOfValue = reflectable->bitSizeOf();
    BitBuffer bitBuffer(bitSizeOfValue);
    BitStreamWriter writer(bitBuffer);
    reflectable->write(writer);
    ASSERT_EQ(bitSizeOfValue, writer.getBitPosition());

    const ParsingInfo& parsingInfo = reflectable->parsingInfo();
    ASSERT_THROW(parsingInfo.getBitPosition(), CppRuntimeException);
    ASSERT_THROW(parsingInfo.getBitSize(), CppRuntimeException);
}

} // namespace zserio
