#include "gtest/gtest.h"
#include "zserio/RebindAlloc.h"

#include "WithTypeInfoCodeCreator.h"

using namespace zserio::literals;

namespace with_type_info_code
{

using allocator_type = WithTypeInfoCode::allocator_type;
template <typename T>
using vector_type = zserio::vector<T, allocator_type>;

using BitBuffer = zserio::BasicBitBuffer<zserio::RebindAlloc<allocator_type, uint8_t>>;

static void fillSimpleStruct(SimpleStruct& simpleStruct)
{
    simpleStruct.setFieldOffset(0);
    simpleStruct.setFieldFloat32(4.0);
}

static void fillParameterizedStruct(ParameterizedStruct& parameterizedStruct, const SimpleStruct& simpleStruct)
{
    vector_type<uint8_t> array;
    for (uint32_t i = 0; i < simpleStruct.getFieldU32(); i++)
    {
        array.push_back(static_cast<uint8_t>(i));
    }
    parameterizedStruct.setArray(array);
}

static void fillComplexStruct(ComplexStruct& complexStruct, bool createOptionals)
{
    fillSimpleStruct(complexStruct.getSimpleStruct());

    fillSimpleStruct(complexStruct.getAnotherSimpleStruct());

    if (createOptionals)
    {
        SimpleStruct simpleStruct;
        fillSimpleStruct(simpleStruct);
        complexStruct.setOptionalSimpleStruct(simpleStruct);
    }

    const vector_type<uint32_t> array = {3, 0xABCD2, 0xABCD3, 0xABCD4, 0xABCD5};
    complexStruct.setArray(array);

    const vector_type<int8_t> arrayWithLen = {3, 2, 1};
    complexStruct.setArrayWithLen(arrayWithLen);

    if (createOptionals)
    {
        ParameterizedStruct parameterizedStruct0;
        fillParameterizedStruct(parameterizedStruct0, complexStruct.getSimpleStruct());
        ParameterizedStruct parameterizedStruct1;
        fillParameterizedStruct(parameterizedStruct1, complexStruct.getAnotherSimpleStruct());
        const vector_type<ParameterizedStruct> paramStructArray = {parameterizedStruct0, parameterizedStruct1};
        complexStruct.setParamStructArray(paramStructArray);
    }

    complexStruct.setDynamicBitField(8);

    vector_type<uint64_t> dynamicBitFieldArray;
    for (size_t i = 1; i < 65536; i += 2)
    {
        dynamicBitFieldArray.push_back(i);
    }
    complexStruct.setDynamicBitFieldArray(dynamicBitFieldArray);

    if (createOptionals)
    {
        complexStruct.setOptionalEnum(TestEnum::ItemThree);
        complexStruct.setOptionalBitmask(
                TestBitmask::Values::RED | TestBitmask::Values::_Green | TestBitmask::Values::ColorBlue);
        const vector_type<uint8_t> buffer = {0xCB, 0xF0};
        complexStruct.setOptionalExtern(BitBuffer(buffer, 12));
        complexStruct.setOptionalBytes(vector_type<uint8_t>{{0xAB, 0xCD}});
    }

    vector_type<TestEnum> enumArray;
    enumArray.push_back(TestEnum::_TWO);
    enumArray.push_back(TestEnum::ItemThree);
    complexStruct.setEnumArray(enumArray);

    vector_type<TestBitmask> bitmaskArray;
    for (size_t i = 0; i < static_cast<size_t>(TestEnum::_TWO); ++i)
    {
        bitmaskArray.push_back(TestBitmask::Values::_Green);
    }
    complexStruct.setBitmaskArray(bitmaskArray);
}

static void fillRecursiveStruct(RecursiveStruct& recursiveStruct)
{
    recursiveStruct.setFieldU32(0xDEAD1);
    recursiveStruct.setFieldRecursion(
            RecursiveStruct(0xDEAD2, zserio::NullOpt, vector_type<RecursiveStruct>()));
    const vector_type<RecursiveStruct> arrayRecursion = {
            RecursiveStruct(0xDEAD3, zserio::NullOpt, vector_type<RecursiveStruct>()),
            RecursiveStruct(0xDEAD4, zserio::NullOpt, vector_type<RecursiveStruct>())};
    recursiveStruct.setArrayRecursion(arrayRecursion);
}

static void fillRecursiveUnion(RecursiveUnion& recursiveUnion)
{
    RecursiveUnion recursiveUnionFieldU32;
    recursiveUnionFieldU32.setFieldU32(0xDEAD);
    const vector_type<RecursiveUnion> recursive = {recursiveUnionFieldU32};
    recursiveUnion.setRecursive(recursive);
}

static void fillRecursiveChoice(RecursiveChoice& recursiveChoice, bool param1, bool param2)
{
    if (param1)
    {
        RecursiveChoice recursiveChoiceFalse;
        fillRecursiveChoice(recursiveChoiceFalse, param2, false);
        const vector_type<RecursiveChoice> recursive = {recursiveChoiceFalse};
        recursiveChoice.setRecursive(recursive);
    }
    else
    {
        recursiveChoice.setFieldU32(0xDEAD);
    }
}

static void fillSimpleUnion(SimpleUnion& simpleUnion)
{
    simpleUnion.setTestBitmask(TestBitmask::Values::_Green);
}

static void fillSimpleChoice(SimpleChoice& simpleChoice, const TestEnum& testEnum)
{
    if (testEnum == TestEnum::_TWO)
    {
        SimpleUnion simpleUnion;
        fillSimpleUnion(simpleUnion);
        simpleChoice.setFieldTwo(simpleUnion);
    }
    else
    {
        simpleChoice.setFieldDefault("text");
    }
}

static void fillTS32(TS32& ts32)
{
    ts32.setField(0xDEAD);
}

static void fillTemplatedParameterizedStruct_TS32(
        TemplatedParameterizedStruct_TS32& templatedParameterizedStruct_TS32, const TS32& ts32)
{
    vector_type<uint32_t> array;
    for (uint32_t i = ts32.getField(); i > 0; --i)
    {
        array.push_back(i);
    }
    templatedParameterizedStruct_TS32.setArray(array);
}

void fillWithTypeInfoCode(WithTypeInfoCode& withTypeInfoCode, bool createOptionals)
{
    fillSimpleStruct(withTypeInfoCode.getSimpleStruct());
    fillComplexStruct(withTypeInfoCode.getComplexStruct(), createOptionals);
    fillParameterizedStruct(withTypeInfoCode.getParameterizedStruct(), withTypeInfoCode.getSimpleStruct());
    fillRecursiveStruct(withTypeInfoCode.getRecursiveStruct());
    fillRecursiveUnion(withTypeInfoCode.getRecursiveUnion());
    fillRecursiveChoice(withTypeInfoCode.getRecursiveChoice(), true, false);
    withTypeInfoCode.setSelector(TestEnum::_TWO);
    fillSimpleChoice(withTypeInfoCode.getSimpleChoice(), withTypeInfoCode.getSelector());
    fillTS32(withTypeInfoCode.getTemplatedStruct());
    fillTemplatedParameterizedStruct_TS32(
            withTypeInfoCode.getTemplatedParameterizedStruct(), withTypeInfoCode.getTemplatedStruct());
    const BitBuffer externData({0xCA, 0xFE}, 15);
    withTypeInfoCode.setExternData(externData);
    const vector_type<BitBuffer> externArray = {externData, externData};
    withTypeInfoCode.setExternArray(externArray);
    withTypeInfoCode.setBytesData(vector_type<uint8_t>{{0xAB, 0xCD}});
    withTypeInfoCode.setBytesArray(vector_type<vector_type<uint8_t>>{{
            vector_type<uint8_t>{{0xAB, 0xCD}},
            vector_type<uint8_t>{{0xAB, 0xCD}},
    }});
    const vector_type<uint32_t> implicitArray = {1, 4, 6, 4, 6, 1};
    withTypeInfoCode.setImplicitArray(implicitArray);

    withTypeInfoCode.initializeChildren();
}

} // namespace with_type_info_code
