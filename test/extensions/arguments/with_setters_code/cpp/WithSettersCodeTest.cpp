#include <type_traits>

#include "gtest/gtest.h"
#include "test_utils/Assertions.h"
#include "setters_code_wow/Tile.h"
#include "setters_code_ww/Tile.h"
#include "setters_code_wow_ws/Tile.h"
#include "zserio/RebindAlloc.h"
#include "zserio/SerializeUtil.h"
#include "zserio/StringView.h"

namespace with_setters_code
{

class WithSettersCode : public ::testing::Test
{
protected:
};

TEST_F(WithSettersCode, checkWOW)
{
    const char* const PATH = "arguments/with_setters_code/gen_wow/setters_code_wow/";
    const char* type = "Item";

    ASSERT_METHOD_NOT_PRESENT(PATH, type, " Item()", "Item::Item()");
    ASSERT_METHOD_NOT_PRESENT(PATH, type, "Item(const allocator_type& allocator)", "Item::Item(const allocator_type&)");
    ASSERT_METHOD_NOT_PRESENT(PATH, type, "void setExtraParam(", "void Item::setExtraParam(");
    ASSERT_METHOD_NOT_PRESENT(PATH, type, "bool isExtraParamSet(", "bool Item::isExtraParamSet(");
    ASSERT_METHOD_NOT_PRESENT(PATH, type, "void resetExtraParam(", "void Item::resetExtraParam(");
    ASSERT_METHOD_NOT_PRESENT(PATH, type, "void setParam(", "void Item::setParam(");
    ASSERT_METHOD_NOT_PRESENT(PATH, type, "size_t initializeOffsets(", "size_t Item::initializeOffsets(");
    ASSERT_METHOD_NOT_PRESENT(PATH, type, "void write(", "void Item::write(");
}

TEST_F(WithSettersCode, checkWW)
{
    const char* const PATH = "arguments/with_setters_code/gen_ww/setters_code_ww/";
    const char* type = "Item";

    ASSERT_METHOD_PRESENT(PATH, type, " Item()", "");
    ASSERT_METHOD_PRESENT(PATH, type, "Item(const allocator_type& allocator)", "Item::Item(const allocator_type&)");
    ASSERT_METHOD_PRESENT(PATH, type, "void setExtraParam(", "void Item::setExtraParam(");
    ASSERT_METHOD_PRESENT(PATH, type, "bool isExtraParamSet(", "bool Item::isExtraParamSet(");
    ASSERT_METHOD_PRESENT(PATH, type, "void resetExtraParam(", "void Item::resetExtraParam(");
    ASSERT_METHOD_PRESENT(PATH, type, "void setParam(", "void Item::setParam(");
    ASSERT_METHOD_PRESENT(PATH, type, "size_t initializeOffsets(", "size_t Item::initializeOffsets(");
    ASSERT_METHOD_PRESENT(PATH, type, "void write(", "void Item::write(");
}

TEST_F(WithSettersCode, checkWOW_WS)
{
    const char* const PATH = "arguments/with_setters_code/gen_wow_ws/setters_code_wow_ws/";
    const char* type = "Item";

    ASSERT_METHOD_PRESENT(PATH, type, " Item()", "");
    ASSERT_METHOD_PRESENT(PATH, type, "Item(const allocator_type& allocator)", "Item::Item(const allocator_type&)");
    ASSERT_METHOD_PRESENT(PATH, type, "void setExtraParam(", "void Item::setExtraParam(");
    ASSERT_METHOD_PRESENT(PATH, type, "bool isExtraParamSet(", "bool Item::isExtraParamSet(");
    ASSERT_METHOD_PRESENT(PATH, type, "void resetExtraParam(", "void Item::resetExtraParam(");
    ASSERT_METHOD_PRESENT(PATH, type, "void setParam(", "void Item::setParam(");
    ASSERT_METHOD_NOT_PRESENT(PATH, type, "size_t initializeOffsets(", "size_t Item::initializeOffsets(");
    ASSERT_METHOD_NOT_PRESENT(PATH, type, "void write(", "void Item::write(");
}

} // namespace with_setters_code
