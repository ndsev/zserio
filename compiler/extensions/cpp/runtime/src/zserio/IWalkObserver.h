#ifndef ZSERIO_I_WALK_OBSERVER_H_INC
#define ZSERIO_I_WALK_OBSERVER_H_INC

#include <memory>

#include "zserio/WalkerConst.h"
#include "zserio/IReflectable.h"

namespace zserio
{

/**
 * Interface for observers which are called by the walker.
 */
template <typename ALLOC = std::allocator<uint8_t>>
class IBasicWalkObserver
{
public:
    /** Shared pointer to the walk observer interface. */
    using Ptr = std::shared_ptr<IBasicWalkObserver>;

    /** Destructor. */
    virtual ~IBasicWalkObserver() {}

    /**
     * Called for the root compound zserio object which is to be walked-through.
     *
     * \param compound Reflectable root compound zserio object.
     */
    virtual void beginRoot(const IReflectablePtr& compound) = 0;

    /**
     * Called at the end of just walked root compound zserio object.
     *
     * \param compound Reflectable root compound zserio object.
     */
    virtual void endRoot(const IReflectablePtr& compound) = 0;

    /**
     * Called at the beginning of an array.
     *
     * Note that for unset arrays (i.e. non-present optionals) the visitValue method with nullptr is called
     * instead!
     *
     * \param array Reflectable zserio array.
     * \param fieldInfo Array field info.
     */
    virtual void beginArray(const IReflectablePtr& array, const FieldInfo& fieldInfo) = 0;

    /**
     * Called at the end of an array.
     *
     * \param array Reflectable zserio array.
     * \param fieldInfo Array field info.
     */
    virtual void endArray(const IReflectablePtr& array, const FieldInfo& fieldInfo) = 0;

    /**
     *Called at the beginning of an compound field object.
     *
     * Note that for unset compounds (i.e. non-present optionals) the visitValue method with nullptr is called
     * instead!
     *
     * \param compound Reflectable compound zserio object.
     * \param fieldInfo Compound field info.
     * \param elementIndex Element index in array or WALKER_NOT_ELEMENT if the compound is not in array.
     */
    virtual void beginCompound(const IReflectablePtr& compound, const FieldInfo& fieldInfo,
            size_t elementIndex = WALKER_NOT_ELEMENT) = 0;

    /**
     * Called at the end of just walked compound object.
     *
     * \param compound Reflectable compound zserio object.
     * \param fieldInfo Compound field info.
     * \param elementIndex Element index in array or WALKER_NOT_ELEMENT if the compound is not in array.
     */
    virtual void endCompound(const IReflectablePtr& compound, const FieldInfo& fieldInfo,
            size_t elementIndex = WALKER_NOT_ELEMENT) = 0;

    /**
     * Called when a simple (or an unset compound or array - i.e. nullptr) value is reached.
     *
     * \param value Reflectable simple value.
     * \param fieldInfo Field info.
     * \param elementIndex Element index in array or WALKER_NOT_ELEMENT if the value is not in array.
     */
    virtual void visitValue(const IReflectablePtr& value, const FieldInfo& fieldInfo,
            size_t elementIndex = WALKER_NOT_ELEMENT) = 0;
};

/** Typedef to IWalkObserver smart pointer. */
template <typename ALLOC = std::allocator<uint8_t>>
using IBasicWalkObserverPtr = typename IBasicWalkObserver<ALLOC>::Ptr;

/** Typedefs to walk observer interface provided for convenience - using default std::allocator<uint8_t>. */
/** \{ */
using IWalkObserver = IBasicWalkObserver<>;
using IWalkObserverPtr = IBasicWalkObserverPtr<>;
/** \} */

} // namespace zserio

#endif // ZSERIO_I_WALK_OBSERVER_H_INC
