#ifndef ZSERIO_I_WALK_FILTER_H_INC
#define ZSERIO_I_WALK_FILTER_H_INC

#include <memory>

#include "zserio/WalkerConst.h"
#include "zserio/IReflectable.h"

namespace zserio
{

/**
 * Interface for filters which can influence the walking.
 */
template <typename ALLOC = std::allocator<uint8_t>>
class IBasicWalkFilter
{
public:
    /** Shared pointer to the walk filter interface. */
    using Ptr = std::shared_ptr<IBasicWalkFilter>;

    /** Destructor. */
    virtual ~IBasicWalkFilter() {}

    /**
     * Called before an array.
     *
     * Note that for unset arrays (i.e. non-present optionals) the beforeValue method with nullptr is called
     * instead!
     *
     * \param array Reflectable zserio array.
     * \param fieldInfo Array field info.
     *
     * \return True when the walking should continue to the array.
     */
    virtual bool beforeArray(const IReflectablePtr& array, const FieldInfo& fieldInfo) = 0;

    /**
     * Called after an array.
     *
     *
     * \param array Reflectable zserio array.
     * \param fieldInfo Array field info.
     *
     * \return True when the walking should continue to a next sibling, false to return to the parent.
     */
    virtual bool afterArray(const IReflectablePtr& array, const FieldInfo& fieldInfo) = 0;

    /**
     * Called before a compound object.
     *
     * Note that for unset compounds (i.e. non-present optionals) the beforeValue method with nullptr is called
     * instead!
     *
     * \param compound Reflectable compound zserio object.
     * \param fieldInfo Compound field info.
     * \param elementIndex Element index in array or WALKER_NOT_ELEMENT if the compound is not in array.
     *
     * \return True when the walking should continue into the compound object, false otherwise.
     */
    virtual bool beforeCompound(const IReflectablePtr& compound, const FieldInfo& fieldInfo,
            size_t elementIndex = WALKER_NOT_ELEMENT) = 0;

    /**
     * Called after a compound object.
     *
     * \param compound Reflectable compound zserio object.
     * \param fieldInfo Compound field info.
     * \param elementIndex Element index in array or WALKER_NOT_ELEMENT if the compound is not in array.
     *
     * \return True when the walking should continue to a next sibling, false to return to the parent.
     */
    virtual bool afterCompound(const IReflectablePtr& compound, const FieldInfo& fieldInfo,
            size_t elementIndex = WALKER_NOT_ELEMENT) = 0;

    /**
     * Called before a simple (or an unset compound or array - i.e. nullptr) value.
     *
     * \param value Reflectable simple value.
     * \param fieldInfo Field info.
     * \param elementIndex Element index in array or WALKER_NOT_ELEMENT if the value is not in array.

     * \return True when the walking should continue to the simple value, false otherwise.
     */
    virtual bool beforeValue(const IReflectablePtr& value, const FieldInfo& fieldInfo,
            size_t elementIndex = WALKER_NOT_ELEMENT) = 0;

    /**
     * Called after a simple (or an unset compound or array - i.e. nullptr) value.
     *
     * \param value Reflectable simple value.
     * \param fieldInfo Field info.
     * \param elementIndex Element index in array or None if the value is not in array.
     *
     * \return True when the walking should continue to a next sibling, false to return to the parent.
     */
    virtual bool afterValue(const IReflectablePtr& value, const FieldInfo& fieldInfo,
            size_t elementIndex = WALKER_NOT_ELEMENT) = 0;
};

/** Typedef to IWalkFilter smart pointer. */
template <typename ALLOC = std::allocator<uint8_t>>
using IBasicWalkFilterPtr = typename IBasicWalkFilter<ALLOC>::Ptr;

/** Typedefs to walk filter interface provided for convenience - using default std::allocator<uint8_t>. */
/** \{ */
using IWalkFilter = IBasicWalkFilter<>;
using IWalkFilterPtr = IBasicWalkFilterPtr<>;
/** \} */

} // namespace zserio

#endif // ZSERIO_I_WALK_FILTER_H_INC
