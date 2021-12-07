#ifndef ZSERIO_I_REFLECTABLE_H_INC
#define ZSERIO_I_REFLECTABLE_H_INC

#include <memory>

#include "zserio/ITypeInfo.h"
#include "zserio/BitBuffer.h"
#include "zserio/String.h"
#include "zserio/RebindAlloc.h"
#include "zserio/AnyHolder.h"
#include "zserio/BitStreamWriter.h"

namespace zserio
{

/**
 * Interface for reflectable view to instances of zserio objects.
 *
 * \note Users are responsible to maintain life-time of reflected objects which must exist
 * as long as the views are used.
 *
 * \note The object in this context may be also an instance of a built-in type.
 */
template <typename ALLOC = std::allocator<uint8_t>>
class IBasicReflectable
{
public:
    /** Shared pointer to the reflectable interface. */
    using Ptr = std::shared_ptr<IBasicReflectable>;

    /**
     * Destructor.
     */
    virtual ~IBasicReflectable() {}

    /**
     * Gets type info for the current zserio object.
     *
     * \return Reference to the static instance of type info.
     */
    virtual const ITypeInfo& getTypeInfo() const = 0;

    /**
     * Gets whether the reflected object is an array.
     *
     * \return True when the object is an array, false otherwise.
     */
    virtual bool isArray() const = 0;

    /**
     * Gets reflectable view to the field (i.e. member) with the given schema name.
     *
     * \note Can be called only when the reflected object is a zserio compound type.
     *
     * \param name Field schema name.
     *
     * \return Reflectable view to the requested field.
     *
     * \throw CppRuntimeException When the reflected object is not a compound type or when the field with
     *                            the given name doesn't exist or when the field getter itself throws.
     */
    virtual Ptr getField(StringView name) const = 0;

    /**
     * Sets the field (i.e. member) with the given schema name.
     *
     * \param name Field schema name.
     * \param value Value to set. The type must exactly match the type of the zserio field mapped to C++!
     *
     * \throw CppRuntimeException When the reflected object is not a compuind type or when the field with
     *                            the given name doesn't exist or when the provided value is of a wrong type
     *                            or when the field setter itself throws.
     */
    virtual void setField(StringView name, const AnyHolder<ALLOC>& value) = 0;

    /**
     * Gets reflectable view to the parameter (i.e. member) with the given schema name.
     *
     * \note Can be called only when the reflected object is a zserio compound type.
     *
     * \param name Parameter schema name.
     *
     * \return Reflectable view to the requested paramter.
     *
     * \throw CppRuntimeException When the reflected object is not a compound type or when the paramter with
     *                            the given name doesn't exist or when the parameter getter itself throws.
     */
    virtual Ptr getParameter(StringView name) const = 0;

    /**
     * Calls function with the given name on the reflected zserio object and gets reflectable view to its
     * result.
     *
     * \note Can be called only when the reflected object is a zserio compound type.
     *
     * \param name Function schema name.
     *
     * \return Reflectable view to the value returns from the called function.
     *
     * \throw CppRuntimeException When the reflected object is not a compound type or when the function with
     *                            the given name doesn't exist or the the function call itself throws.
     */
    virtual Ptr callFunction(StringView name) const = 0;

    /**
     * Gets name of the field which is active in the reflected choice type.
     *
     * \note Applicable only on zserio unions and choices.
     *
     * \return Name of the active field (i.e. currently selected choice).
     *
     * \throw CppRuntimeException When the reflected object is not a choice type (or union).
     */
    virtual StringView getChoice() const = 0;

    /**
     * Universal accessor to zserio entities within the zserio sub-tree represented by the reflected object.
     *
     * Supports dot notation corresponding to the tree defined in zserio language. Can access fields or
     * paramters or call functions within the zserio sub-tree.
     *
     * Examples:
     * * 'fieldA.param' - Gets reflectable view to parameter 'param' whithin the parameterized field 'fieldA'.
     * * 'child.getValue' - Gets reflectable view to result of the function called on field ̈́'child'.
     * * 'child.nonexisting.field' - Gets nullptr since the path doesn't represent a valid entity.
     *
     * \param path Dot notation corresponding to the zserio tree.
     *
     * \return Reflectable view to the result of the given path. Returns nullptr when the path doesn't exist
     *         or when the requested operation throws CppRuntimeException.
     */
    virtual Ptr find(StringView path) const = 0;

    /**
     * \copydoc IBasicReflectable::find
     *
     * Overloaded method provided for convenience.
     */
    virtual Ptr operator[](StringView path) const = 0;

    /**
     * Gets size of the reflected array.
     *
     * \note Can be called only when the reflected object is an array.
     *
     * \return Size of the reflected array.
     *
     * \throw CppRuntimeException When the reflected object is not an array.
     */
    virtual size_t size() const = 0;

    /**
     * Gets reflectable view to an array element.
     *
     * \note Can be called only when the reflected object is an array.
     *
     * \return Reflectable view to an array element on the given index.
     *
     * \throw CppRuntimeException When the reflected object is not an array.
     * \throw std::out_of_range When the given index is out of bounds of the underlying array.
     */
    virtual Ptr at(size_t index) const = 0;

    /**
     * \copydoc IBasicReflectable::at
     *
     * Overloaded method provided for convenience.
     */
    virtual Ptr operator[](size_t index) const = 0;

    /**
     * Gets bool value of the bool reflectable.
     *
     * \return Bool value.
     * \throw CppRuntimeException When the reflected object is not a bool type.
     */
    virtual bool getBool() const = 0;

    /**
     * Gets 8-bit signed integral value of the int8_t reflectable.
     *
     * \return 8-bit signed integral value.
     * \throw CppRuntimeException When the reflected object is not a int8_t type.
     */
    virtual int8_t getInt8() const = 0;

    /**
     * Gets 16-bit signed integral value of the int16_t reflectable.
     *
     * \return 16-bit signed integral value.
     * \throw CppRuntimeException When the reflected object is not a int16_t type.
     */
    virtual int16_t getInt16() const = 0;

    /**
     * Gets 32-bit signed integral value of the int32_t reflectable.
     *
     * \return 32-bit signed integral value.
     * \throw CppRuntimeException When the reflected object is not a int32_t type.
     */
    virtual int32_t getInt32() const = 0;

    /**
     * Gets 64-bit signed integral value of the int64_t reflectable.
     *
     * \return 64-bit signed integral value.
     * \throw CppRuntimeException When the reflected object is not a int64_t type.
     */
    virtual int64_t getInt64() const = 0;

    /**
     * Gets 8-bit unsigned integral value of the uint8_t reflectable.
     *
     * \return 8-bit unsigned integral value.
     * \throw CppRuntimeException When the reflected object is not a uint8_t type.
     */
    virtual uint8_t getUInt8() const = 0;

    /**
     * Gets 16-bit unsigned integral value of the uint16_t reflectable.
     *
     * \return 16-bit unsigned integral value.
     * \throw CppRuntimeException When the reflected object is not a uint16_t type.
     */
    virtual uint16_t getUInt16() const = 0;

    /**
     * Gets 32-bit unsigned integral value of the uint32_t reflectable.
     *
     * \return 32-bit unsigned integral value.
     * \throw CppRuntimeException When the reflected object is not a uint32_t type.
     */
    virtual uint32_t getUInt32() const = 0;

    /**
     * Gets 64-bit unsigned integral value of the uint64_t reflectable.
     *
     * \return 64-bit unsigned integral value.
     * \throw CppRuntimeException When the reflected object is not a uint64_t type.
     */
    virtual uint64_t getUInt64() const = 0;

    /**
     * Gets float value of the float reflectable.
     *
     * \return Float value.
     * \throw CppRuntimeException When the reflected object is not a float type.
     */
    virtual float getFloat() const = 0;

    /**
     * Gets double value of the double reflectable.
     *
     * \return Double value.
     * \throw CppRuntimeException When the reflected object is not a double type.
     */
    virtual double getDouble() const = 0;

    /**
     * Gets reference to the string value of the string reflectable.
     *
     * \return Reference to the string value.
     * \throw CppRuntimeException When the reflected object is not a string type.
     */
    virtual StringView getString() const = 0;

    /**
     * Gets reference to the reflected bit buffer.
     *
     * \return Reference to the bit buffer.
     * \throw CppRuntimeException When the reflected object is not a bit buffer (i.e. extern type).
     */
    virtual const BasicBitBuffer<ALLOC>& getBitBuffer() const = 0;

    /**
     * Converts any signed integral value to 64-bit singed integer.
     *
     * Works also for enum types defined with signed underlying type.
     *
     * \return 64-bit signed integral value.
     * \throw CppRuntimeException When the reflected object cannot be converted to a signed integral value.
     */
    virtual int64_t toInt() const = 0;

    /**
     * Converts any unsigned integral value to 64-bit unsigned integer.
     *
     * Works also for bitmask and enum typed defined with unsigned underlying type.
     *
     * \return 64-bit unsigned integral value.
     * \throw CppRuntimeException When the reflected object cannot be converted to
     *                            an unsigned integral value.
     */
    virtual uint64_t toUInt() const = 0;

    /**
     * Converts any numeric value to double.
     *
     * Works also for bitmask and enum types.
     *
     * \return Double value.
     * \throw CppRuntimeException When the reflected object cannot be converted to double.
     */
    virtual double toDouble() const = 0;

    /**
     * Converts an reflected object to string.
     *
     * Works for all integral types including bool, bitmask and enum types and for string types.
     *
     * \note Floating point types are not currently supported!
     * \note The conversion to string can be implemented for more types in future versions.
     *
     * \param allocator Allocator to use for the string allocation.
     *
     * \return String value representing the reflected object.
     */
    virtual string<RebindAlloc<ALLOC, char>> toString(const ALLOC& allocator = ALLOC()) const = 0;

    /**
     * Writes the reflected object to a bit stream using the given bit stream writer.
     *
     * \note Works for all reflectable types except arrays!
     *
     * \param writer Bit stream writer to use.
     *
     * \throw CppRuntimeException When the reflected object is an array.
     */
    virtual void write(BitStreamWriter& writer) = 0;

    /**
     * Gets the number of bits needed for serialization of the reflected object.
     *
     * \note Works for all reflectable types except arrays!
     *
     * \param bitPosition The bit stream position to be used for calculation.
     *
     * \throw CppRuntimeException When the reflected object is an array.
     *
     * \return The size of the serialized reflected object in bits.
     */
    virtual size_t bitSizeOf(size_t bitPosition = 0) const = 0;
};

/** Typedef to reflectable interface provided for convenience - using default std::allocator<uint8_t>. */
/** \{ */
using IReflectable = IBasicReflectable<>;
using IReflectablePtr = IReflectable::Ptr;
/** \} */

/**
 * Gets reflectable view for the given enum item.
 *
 * \param value Enum value to reflect.
 * \param allocator Allocator to use for reflectable allocation.
 *
 * \return Enum reflectable view.
 */
template <typename T, typename ALLOC = std::allocator<uint8_t>>
typename IBasicReflectable<ALLOC>::Ptr enumReflectable(T value, const ALLOC& allocator = ALLOC());

} // namespace zserio

#endif // ZSERIO_I_REFLECTABLE_H_INC
