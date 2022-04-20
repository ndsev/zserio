#ifndef ZSERIO_I_WALK_FILTER_H_INC
#define ZSERIO_I_WALK_FILTER_H_INC

#include <memory>

#include "zserio/WalkerConst.h"

namespace zserio
{

class IWalkFilter
{
public:
    virtual ~IWalkFilter() {}

    virtual bool beforeArray(const IReflectablePtr& array, const FieldInfo& fieldInfo) = 0;
    virtual bool afterArray(const IReflectablePtr& array, const FieldInfo& fieldInfo) = 0;

    virtual bool beforeCompound(const IReflectablePtr& compound, const FieldInfo& fieldInfo,
            size_t elementIndex = WALKER_NOT_ELEMENT) = 0;
    virtual bool afterCompound(const IReflectablePtr& compound, const FieldInfo& fieldInfo,
            size_t elementIndex = WALKER_NOT_ELEMENT) = 0;

    virtual bool beforeValue(const IReflectablePtr& value, const FieldInfo& fieldInfo,
            size_t elementIndex = WALKER_NOT_ELEMENT) = 0;
    virtual bool afterValue(const IReflectablePtr& value, const FieldInfo& fieldInfo,
            size_t elementIndex = WALKER_NOT_ELEMENT) = 0;
};

using IWalkFilterPtr = std::shared_ptr<IWalkFilter>;

} // namespace zserio

#endif // ZSERIO_I_WALK_FILTER_H_INC
