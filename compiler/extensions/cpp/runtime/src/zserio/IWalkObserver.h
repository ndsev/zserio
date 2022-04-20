#ifndef ZSERIO_I_WALK_OBSERVER_H_INC
#define ZSERIO_I_WALK_OBSERVER_H_INC

#include <memory>

#include "zserio/WalkerConst.h"

namespace zserio
{

class IWalkObserver
{
public:
    virtual ~IWalkObserver() {}

    virtual void beginRoot(const IReflectablePtr& compound) = 0;
    virtual void endRoot(const IReflectablePtr& compound) = 0;

    virtual void beginArray(const IReflectablePtr& array, const FieldInfo& fieldInfo) = 0;
    virtual void endArray(const IReflectablePtr& array, const FieldInfo& fieldInfo) = 0;

    virtual void beginCompound(const IReflectablePtr& compound, const FieldInfo& fieldInfo,
            size_t elementIndex = WALKER_NOT_ELEMENT) = 0;
    virtual void endCompound(const IReflectablePtr& compound, const FieldInfo& fieldInfo,
            size_t elementIndex = WALKER_NOT_ELEMENT) = 0;

    virtual void visitValue(const IReflectablePtr& value, const FieldInfo& fieldInfo,
            size_t elementIndex = WALKER_NOT_ELEMENT) = 0;
};

using IWalkObserverPtr = std::shared_ptr<IWalkObserver>;

} // namespace zserio

#endif // ZSERIO_I_WALK_OBSERVER_H_INC
