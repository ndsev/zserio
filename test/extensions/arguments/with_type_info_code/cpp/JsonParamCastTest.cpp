#include "gtest/gtest.h"
#include "json_param_cast/Holder.h"
#include "zserio/DebugStringUtil.h"
#include "zserio/JsonWriter.h"

namespace json_param_cast
{

using allocator_type = Holder::allocator_type;
using string_type = zserio::string<allocator_type>;

const string_type JSON_NAME_PARAM_CAST = "json_param_cast.json";

TEST(JsonParamCastTest, toJsonFile)
{
    Holder holder(10, Int8Param(1), Int16Param(2), Int32Param(3), Int64Param(4), Float16Param(5),
            Float32Param(6), Float64Param(7), Int32Param(8));
    holder.initializeChildren();
    zserio::toJsonFile(holder, JSON_NAME_PARAM_CAST);

    Holder readHolder = zserio::fromJsonFile<Holder>(JSON_NAME_PARAM_CAST, allocator_type());
    readHolder.initializeChildren();
    ASSERT_EQ(holder, readHolder);
}

} // namespace json_param_cast
