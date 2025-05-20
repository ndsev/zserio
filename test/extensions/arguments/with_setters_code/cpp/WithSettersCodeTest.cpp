#include <type_traits>

#include "gtest/gtest.h"
#include "test_utils/Assertions.h"
#include "with_writer/Tile.h"
#include "without_writer/Tile.h"
#include "without_writer_with_setters/Tile.h"
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
    const char* const PATH = "arguments/with_setters_code/gen_wow/without_writer/";
    const char* type = nullptr;

    type = "Item";
    ASSERT_METHOD_NOT_PRESENT(PATH, type, " Item()", nullptr);
    ASSERT_METHOD_NOT_PRESENT(
            PATH, type, "Item(const allocator_type& allocator)", "Item::Item(const allocator_type&)");
    ASSERT_METHOD_NOT_PRESENT(PATH, type, "void setExtraParam(", "void Item::setExtraParam(");
    ASSERT_METHOD_NOT_PRESENT(PATH, type, "bool isExtraParamSet(", "bool Item::isExtraParamSet(");
    ASSERT_METHOD_NOT_PRESENT(PATH, type, "void resetExtraParam(", "void Item::resetExtraParam(");
    ASSERT_METHOD_NOT_PRESENT(PATH, type, "void setParam(", "void Item::setParam(");
    ASSERT_METHOD_NOT_PRESENT(PATH, type, "size_t initializeOffsets(", "size_t Item::initializeOffsets(");
    ASSERT_METHOD_NOT_PRESENT(PATH, type, "void write(", "void Item::write(");

    type = "ItemChoice";
    ASSERT_METHOD_NOT_PRESENT(PATH, type, " ItemChoice()", nullptr);
    ASSERT_METHOD_NOT_PRESENT(PATH, type, "ItemChoice(const allocator_type& allocator)",
            "ItemChoice::ItemChoice(const allocator_type& allocator)");
    ASSERT_METHOD_NOT_PRESENT(PATH, type, "void setItem(", "void ItemChoice::setItem(");
    ASSERT_METHOD_NOT_PRESENT(PATH, type, "void setParam(", "void ItemChoice::setParam(");
    ASSERT_METHOD_NOT_PRESENT(PATH, type, "size_t initializeOffsets(", "size_t ItemChoice::initializeOffsets(");
    ASSERT_METHOD_NOT_PRESENT(PATH, type, "void write(", "void ItemChoice::write(");

    type = "ExtraParamUnion";
    ASSERT_METHOD_NOT_PRESENT(PATH, type, " ExtraParamUnion()", nullptr);
    ASSERT_METHOD_NOT_PRESENT(PATH, type, "ExtraParamUnion(const allocator_type& allocator)",
            "ExtraParamUnion::ExtraParamUnion(const allocator_type& allocator)");
    ASSERT_METHOD_NOT_PRESENT(PATH, type, "void setValue16(", "void ExtraParamUnion::setValue16(");
    ASSERT_METHOD_NOT_PRESENT(PATH, type, "void setValue32(", "void ExtraParamUnion::setValue32(");
    ASSERT_METHOD_NOT_PRESENT(
            PATH, type, "size_t initializeOffsets(", "size_t ExtraParamUnion::initializeOffsets(");
    ASSERT_METHOD_NOT_PRESENT(PATH, type, "void write(", "void ExtraParamUnion::write(");
}

TEST_F(WithSettersCode, checkWithWriter)
{
    const char* const PATH = "arguments/with_setters_code/gen_ww/with_writer/";
    const char* type = nullptr;

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

    type = "ExtraParamUnion";
    ASSERT_METHOD_PRESENT(PATH, type, " ExtraParamUnion()", nullptr);
    ASSERT_METHOD_PRESENT(PATH, type, "ExtraParamUnion(const allocator_type& allocator)",
            "ExtraParamUnion::ExtraParamUnion(const allocator_type& allocator)");
    ASSERT_METHOD_PRESENT(PATH, type, "void setValue16(", "void ExtraParamUnion::setValue16(");
    ASSERT_METHOD_PRESENT(PATH, type, "void setValue32(", "void ExtraParamUnion::setValue32(");
    ASSERT_METHOD_PRESENT(
            PATH, type, "size_t initializeOffsets(", "size_t ExtraParamUnion::initializeOffsets(");
    ASSERT_METHOD_PRESENT(PATH, type, "void write(", "void ExtraParamUnion::write(");
}

TEST_F(WithSettersCode, checkWithoutWriterWithSetters)
{
    const char* const PATH = "arguments/with_setters_code/gen_wow_ws/without_writer_with_setters/";
    const char* type = nullptr;

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

    type = "ExtraParamUnion";
    ASSERT_METHOD_PRESENT(PATH, type, " ExtraParamUnion()", nullptr);
    ASSERT_METHOD_PRESENT(PATH, type, "ExtraParamUnion(const allocator_type& allocator)",
            "ExtraParamUnion::ExtraParamUnion(const allocator_type& allocator)");
    ASSERT_METHOD_PRESENT(PATH, type, "void setValue16(", "void ExtraParamUnion::setValue16(");
    ASSERT_METHOD_PRESENT(PATH, type, "void setValue32(", "void ExtraParamUnion::setValue32(");
    ASSERT_METHOD_NOT_PRESENT(
            PATH, type, "size_t initializeOffsets(", "size_t ExtraParamUnion::initializeOffsets(");
    ASSERT_METHOD_NOT_PRESENT(PATH, type, "void write(", "void ExtraParamUnion::write(");
}

} // namespace with_setters_code
