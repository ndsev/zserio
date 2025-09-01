#include <type_traits>

#include "gtest/gtest.h"
#include "test_utils/Assertions.h"
#include "with_writer/Tile.h"
#include "without_writer/Tile.h"
#include "zserio/RebindAlloc.h"
#include "zserio/SerializeUtil.h"
#include "zserio/StringView.h"

namespace with_setters_code
{

class WithSettersCode : public ::testing::Test
{
protected:
};

TEST_F(WithSettersCode, checkWithoutWriter)
{
    const char* const PATH = "arguments/with_setters_code/gen/without_writer/";
    const char* type = nullptr;

    type = "ItemType";
    ASSERT_METHOD_PRESENT(PATH, type, "::without_writer::ItemType valueToEnum<::without_writer::ItemType>(",
            "::without_writer::ItemType valueToEnum(");
    ASSERT_METHOD_NOT_PRESENT(PATH, type,
            "size_t initializeOffsets<::without_writer::ItemType>(size_t bitPosition, "
            "::without_writer::ItemType value)",
            "size_t initializeOffsets(size_t bitPosition, ::without_writer::ItemType value)");
    ASSERT_METHOD_NOT_PRESENT(PATH, type,
            "void write<::without_writer::ItemType>(::zserio::BitStreamWriter& out, ::without_writer::ItemType "
            "value)",
            "void write(::zserio::BitStreamWriter& out, ::without_writer::ItemType value)");

    type = "VersionAvailability";
    ASSERT_METHOD_PRESENT(PATH, type, " VersionAvailability()", nullptr);
    ASSERT_METHOD_PRESENT(PATH, type, "VersionAvailability(Values value)", nullptr);
    ASSERT_METHOD_PRESENT(PATH, type, "VersionAvailability(underlying_type value)", nullptr);
    ASSERT_METHOD_NOT_PRESENT(
            PATH, type, "size_t initializeOffsets(", "size_t VersionAvailability::initializeOffsets(");
    ASSERT_METHOD_NOT_PRESENT(PATH, type, "void write(", "void VersionAvailability::write(");

    type = "ExtraParamUnion";
    ASSERT_METHOD_PRESENT(PATH, type, " ExtraParamUnion()", nullptr);
    ASSERT_METHOD_PRESENT(PATH, type, "ExtraParamUnion(const allocator_type& allocator)",
            "ExtraParamUnion::ExtraParamUnion(const allocator_type& allocator)");
    ASSERT_METHOD_PRESENT(PATH, type, "void setValue16(", "void ExtraParamUnion::setValue16(");
    ASSERT_METHOD_PRESENT(PATH, type, "void setValue32(", "void ExtraParamUnion::setValue32(");
    ASSERT_METHOD_NOT_PRESENT(
            PATH, type, "size_t initializeOffsets(", "size_t ExtraParamUnion::initializeOffsets(");
    ASSERT_METHOD_NOT_PRESENT(PATH, type, "void write(", "void ExtraParamUnion::write(");

    type = "Item";
    ASSERT_METHOD_PRESENT(PATH, type, " Item()", nullptr);
    ASSERT_METHOD_PRESENT(
            PATH, type, "Item(const allocator_type& allocator)", "Item::Item(const allocator_type&)");
    ASSERT_METHOD_PRESENT(PATH, type, "void setExtraParam(", "void Item::setExtraParam(");
    ASSERT_METHOD_PRESENT(PATH, type, "bool isExtraParamSet(", "bool Item::isExtraParamSet(");
    ASSERT_METHOD_PRESENT(PATH, type, "void resetExtraParam(", "void Item::resetExtraParam(");
    ASSERT_METHOD_PRESENT(PATH, type, "void setParam(", "void Item::setParam(");
    ASSERT_METHOD_NOT_PRESENT(PATH, type, "size_t initializeOffsets(", "size_t Item::initializeOffsets(");
    ASSERT_METHOD_NOT_PRESENT(PATH, type, "void write(", "void Item::write(");

    type = "ItemChoice";
    ASSERT_METHOD_PRESENT(PATH, type, " ItemChoice()", nullptr);
    ASSERT_METHOD_PRESENT(PATH, type, "ItemChoice(const allocator_type& allocator)",
            "ItemChoice::ItemChoice(const allocator_type& allocator)");
    ASSERT_METHOD_PRESENT(PATH, type, "void setItem(", "void ItemChoice::setItem(");
    ASSERT_METHOD_PRESENT(PATH, type, "void setParam(", "void ItemChoice::setParam(");
    ASSERT_METHOD_NOT_PRESENT(PATH, type, "size_t initializeOffsets(", "size_t ItemChoice::initializeOffsets(");
    ASSERT_METHOD_NOT_PRESENT(PATH, type, "void write(", "void ItemChoice::write(");

    type = "Type";
    ASSERT_METHOD_PRESENT(PATH, type, " Type()", nullptr);
    ASSERT_METHOD_PRESENT(
            PATH, type, "Type(const allocator_type& allocator)", "Type::Type(const allocator_type&)");
    ASSERT_METHOD_PRESENT(PATH, type, "void setValue(", "void Type::setValue(");
    ASSERT_METHOD_NOT_PRESENT(PATH, type, "size_t initializeOffsets(", "size_t Type::initializeOffsets(");
    ASSERT_METHOD_NOT_PRESENT(PATH, type, "void write(", "void Type::write(");

    type = "Value";
    ASSERT_METHOD_PRESENT(PATH, type, " Value()", nullptr);
    ASSERT_METHOD_PRESENT(
            PATH, type, "Value(const allocator_type& allocator)", "Value::Value(const allocator_type&)");
    ASSERT_METHOD_PRESENT(PATH, type, "void setValue(", "void Value::setValue(");
    ASSERT_METHOD_NOT_PRESENT(PATH, type, "size_t initializeOffsets(", "size_t Value::initializeOffsets(");
    ASSERT_METHOD_NOT_PRESENT(PATH, type, "void write(", "void Value::write(");
}

TEST_F(WithSettersCode, checkWithWriter)
{
    const char* const PATH = "arguments/with_setters_code/gen/with_writer/";
    const char* type = nullptr;

    type = "ItemType";
    ASSERT_METHOD_PRESENT(PATH, type, "::with_writer::ItemType valueToEnum<::with_writer::ItemType>(",
            "::with_writer::ItemType valueToEnum(");
    ASSERT_METHOD_PRESENT(PATH, type,
            "size_t initializeOffsets<::with_writer::ItemType>(size_t bitPosition, ::with_writer::ItemType "
            "value)",
            "size_t initializeOffsets(size_t bitPosition, ::with_writer::ItemType value)");
    ASSERT_METHOD_PRESENT(PATH, type,
            "void write<::with_writer::ItemType>(::zserio::BitStreamWriter& out, ::with_writer::ItemType "
            "value)",
            "void write(::zserio::BitStreamWriter& out, ::with_writer::ItemType value)");

    type = "VersionAvailability";
    ASSERT_METHOD_PRESENT(PATH, type, " VersionAvailability()", nullptr);
    ASSERT_METHOD_PRESENT(PATH, type, "VersionAvailability(Values value)", nullptr);
    ASSERT_METHOD_PRESENT(PATH, type, "VersionAvailability(underlying_type value)", nullptr);
    ASSERT_METHOD_PRESENT(
            PATH, type, "size_t initializeOffsets(", "size_t VersionAvailability::initializeOffsets(");
    ASSERT_METHOD_PRESENT(PATH, type, "void write(", "void VersionAvailability::write(");

    type = "ExtraParamUnion";
    ASSERT_METHOD_PRESENT(PATH, type, " ExtraParamUnion()", nullptr);
    ASSERT_METHOD_PRESENT(PATH, type, "ExtraParamUnion(const allocator_type& allocator)",
            "ExtraParamUnion::ExtraParamUnion(const allocator_type& allocator)");
    ASSERT_METHOD_PRESENT(PATH, type, "void setValue16(", "void ExtraParamUnion::setValue16(");
    ASSERT_METHOD_PRESENT(PATH, type, "void setValue32(", "void ExtraParamUnion::setValue32(");
    ASSERT_METHOD_PRESENT(
            PATH, type, "size_t initializeOffsets(", "size_t ExtraParamUnion::initializeOffsets(");
    ASSERT_METHOD_PRESENT(PATH, type, "void write(", "void ExtraParamUnion::write(");

    type = "Item";
    ASSERT_METHOD_PRESENT(PATH, type, " Item()", nullptr);
    ASSERT_METHOD_PRESENT(
            PATH, type, "Item(const allocator_type& allocator)", "Item::Item(const allocator_type&)");
    ASSERT_METHOD_PRESENT(PATH, type, "void setExtraParam(", "void Item::setExtraParam(");
    ASSERT_METHOD_PRESENT(PATH, type, "bool isExtraParamSet(", "bool Item::isExtraParamSet(");
    ASSERT_METHOD_PRESENT(PATH, type, "void resetExtraParam(", "void Item::resetExtraParam(");
    ASSERT_METHOD_PRESENT(PATH, type, "void setParam(", "void Item::setParam(");
    ASSERT_METHOD_PRESENT(PATH, type, "size_t initializeOffsets(", "size_t Item::initializeOffsets(");
    ASSERT_METHOD_PRESENT(PATH, type, "void write(", "void Item::write(");

    type = "ItemChoice";
    ASSERT_METHOD_PRESENT(PATH, type, " ItemChoice()", nullptr);
    ASSERT_METHOD_PRESENT(PATH, type, "ItemChoice(const allocator_type& allocator)",
            "ItemChoice::ItemChoice(const allocator_type& allocator)");
    ASSERT_METHOD_PRESENT(PATH, type, "void setItem(", "void ItemChoice::setItem(");
    ASSERT_METHOD_PRESENT(PATH, type, "void setParam(", "void ItemChoice::setParam(");
    ASSERT_METHOD_PRESENT(PATH, type, "size_t initializeOffsets(", "size_t ItemChoice::initializeOffsets(");
    ASSERT_METHOD_PRESENT(PATH, type, "void write(", "void ItemChoice::write(");

    type = "Type";
    ASSERT_METHOD_PRESENT(PATH, type, " Type()", nullptr);
    ASSERT_METHOD_PRESENT(
            PATH, type, "Type(const allocator_type& allocator)", "Type::Type(const allocator_type&)");
    ASSERT_METHOD_PRESENT(PATH, type, "void setValue(", "void Type::setValue(");
    ASSERT_METHOD_PRESENT(PATH, type, "size_t initializeOffsets(", "size_t Type::initializeOffsets(");
    ASSERT_METHOD_PRESENT(PATH, type, "void write(", "void Type::write(");

    type = "Value";
    ASSERT_METHOD_PRESENT(PATH, type, " Value()", nullptr);
    ASSERT_METHOD_PRESENT(
            PATH, type, "Value(const allocator_type& allocator)", "Value::Value(const allocator_type&)");
    ASSERT_METHOD_PRESENT(PATH, type, "void setValue(", "void Value::setValue(");
    ASSERT_METHOD_PRESENT(PATH, type, "size_t initializeOffsets(", "size_t Value::initializeOffsets(");
    ASSERT_METHOD_PRESENT(PATH, type, "void write(", "void Value::write(");
}

} // namespace with_setters_code
