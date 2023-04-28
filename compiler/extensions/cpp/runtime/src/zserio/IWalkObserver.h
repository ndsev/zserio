#ifndef ZSERIO_I_WALK_OBSERVER_H_INC
#define ZSERIO_I_WALK_OBSERVER_H_INC

#include "zserio/WalkerConst.h"
#include "zserio/IReflectable.h"
#include "zserio/ITypeInfo.h"

namespace zserio
{

/**
 * Interface for observers which are called by the walker.
 */
template <typename ALLOC = std::allocator<uint8_t>>
class IBasicWalkObserver
{
public:
    /** Destructor. */
    virtual ~IBasicWalkObserver() = default;

    /**
     * Called for the root compound zserio object which is to be walked-through.
     *
     * \param compound Reflectable root compound zserio object.
     */
    virtual void beginRoot(const IBasicReflectableConstPtr<ALLOC>& compound) = 0;

    /**
     * Called at the end of just walked root compound zserio object.
     *
     * \param compound Reflectable root compound zserio object.
     */
    virtual void endRoot(const IBasicReflectableConstPtr<ALLOC>& compound) = 0;

    /**
     * Called at the beginning of an array.
     *
     * Note that for unset arrays (i.e. non-present optionals) the visitValue method with nullptr is called
     * instead!
     *
     * \param array Reflectable zserio array.
     * \param fieldInfo Array field info.
     */
    virtual void beginArray(const IBasicReflectableConstPtr<ALLOC>& array,
            const BasicFieldInfo<ALLOC>& fieldInfo) = 0;

    /**
     * Called at the end of an array.
     *
     * \param array Reflectable zserio array.
     * \param fieldInfo Array field info.
     */
    virtual void endArray(const IBasicReflectableConstPtr<ALLOC>& array,
            const BasicFieldInfo<ALLOC>& fieldInfo) = 0;

    /**
     * Called at the beginning of an compound field object.
     *
     * Note that for unset compounds (i.e. non-present optionals) the visitValue method with nullptr is called
     * instead!
     *
     * \param compound Reflectable compound zserio object.
     * \param fieldInfo Compound field info.
     * \param elementIndex Element index in array or WALKER_NOT_ELEMENT if the compound is not in array.
     */
    virtual void beginCompound(const IBasicReflectableConstPtr<ALLOC>& compound,
            const BasicFieldInfo<ALLOC>& fieldInfo, size_t elementIndex) = 0;

    /**
     * Called at the end of just walked compound object.
     *
     * \param compound Reflectable compound zserio object.
     * \param fieldInfo Compound field info.
     * \param elementIndex Element index in array or WALKER_NOT_ELEMENT if the compound is not in array.
     */
    virtual void endCompound(const IBasicReflectableConstPtr<ALLOC>& compound,
            const BasicFieldInfo<ALLOC>& fieldInfo, size_t elementIndex) = 0;

    /**
     * Called when a simple (or an unset compound or array - i.e. nullptr) value is reached.
     *
     * \param value Reflectable simple value.
     * \param fieldInfo Field info.
     * \param elementIndex Element index in array or WALKER_NOT_ELEMENT if the value is not in array.
     */
    virtual void visitValue(const IBasicReflectableConstPtr<ALLOC>& value,
            const BasicFieldInfo<ALLOC>& fieldInfo, size_t elementIndex) = 0;
};

/** Typedefs to walk observer interface provided for convenience - using default std::allocator<uint8_t>. */
/** \{ */
using IWalkObserver = IBasicWalkObserver<>;
/** \} */

} // namespace zserio

#endif // ZSERIO_I_WALK_OBSERVER_H_INC
