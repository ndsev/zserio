#ifndef ZSERIO_ISERVICE_H_INC
#define ZSERIO_ISERVICE_H_INC

#include <string>
#include <vector>
#include "zserio/StringView.h"
#include "zserio/Span.h"
#include "zserio/Types.h"

#include "zserio/IReflectable.h"

namespace zserio
{

/**
 * Service data interface.
 *
 * When reflectable interface is available, holds the reflectable object, otherwise holds the serialized data.
 */
template <typename ALLOC = std::allocator<uint8_t>>
class IBasicServiceData
{
public:
    /** Destructor. */
    virtual ~IBasicServiceData() = default;

    /**
     * Gets reflectable representing the service data.
     *
     * Null when -withoutReflectionCode is used.
     *
     * \return Reflectable or null.
     */
    virtual IBasicReflectablePtr<ALLOC> getReflectable() const = 0;

    /**
     * Gets service data as bytes.
     *
     * Lazy initialized from reflectable when -withReflectionCode is used or already kept otherwise.
     *
     * \return Service data.
     */
    virtual Span<const uint8_t> getData() const = 0;
};

/** Typedef to service data smart pointer needed for convenience in generated code. */
template <typename ALLOC = std::allocator<uint8_t>>
using IBasicServiceDataPtr = std::shared_ptr<IBasicServiceData<ALLOC>>;

/**
 * Service data wrapper which can be used directly on the client side.
 *
 * When reflectable interface is available, holds reference to the reflectable object,
 * otherwise holds the reference to the serialized data.
 */
template <typename ALLOC = std::allocator<uint8_t>>
class BasicServiceData : public IBasicServiceData<ALLOC>
{
public:
    /**
     * Constructor from reflectable, used when -withReflectableCode is used.
     *
     * \param reflectable Reflectable created from zserio request object.
     * \param allocator Allocator to use for data allocation.
     */
    explicit BasicServiceData(const IBasicReflectablePtr<ALLOC>& reflectable,
                              const ALLOC& allocator = ALLOC()) :
            m_reflectable(reflectable), m_data(allocator)
    {}

    /**
     * Constructor from bit buffer r-value, used when -withoutReflectableCode is used.
     *
     * \param request Reference to request zserio object.
     * \param allocator Allocator to use for data allocation
     */
    template <typename ZSERIO_OBJECT>
    explicit BasicServiceData(ZSERIO_OBJECT& request, const ALLOC& allocator = ALLOC()) :
            m_data(request.bitSizeOf(), allocator)
    {
        BitStreamWriter writer(m_data);
        request.write(writer);
    }

    virtual IBasicReflectablePtr<ALLOC> getReflectable() const override
    {
        return m_reflectable;
    }

    virtual Span<const uint8_t> getData() const override
    {
        if (m_reflectable && m_data.getBitSize() == 0)
        {
            // lazy initialization
            m_data = BasicBitBuffer<ALLOC>(m_reflectable->bitSizeOf(), m_data.get_allocator());
            BitStreamWriter writer(m_data);
            m_reflectable->write(writer);
        }
        return { m_data.getBuffer(), m_data.getByteSize() };
    }

private:
    IBasicReflectablePtr<ALLOC> m_reflectable;
    mutable BasicBitBuffer<ALLOC> m_data;
};

/**
 * Generic interface for all Zserio services to be used on the server side.
 */
template <typename ALLOC = std::allocator<uint8_t>>
class IBasicService
{
public:
    virtual ~IBasicService() = default;

    /**
     * Calls method with the given name synchronously.
     *
     * \param methodName Name of the service method to call.
     * \param requestData Request data to be passed to the method.
     * \param context Context specific for particular service.
     *
     * \return Created response data.
     *
     * \throw ServiceException if the call fails.
     */
    virtual IBasicServiceDataPtr<ALLOC> callMethod(
            StringView methodName,
            Span<const uint8_t> requestData,
            void* context = nullptr) = 0;
};

/**
 * Generic interface for all Zserio services to be used on the client side.
 */
template <typename ALLOC = std::allocator<uint8_t>>
class IBasicServiceClient
{
public:
    virtual ~IBasicServiceClient() = default;

    /**
     * Calls method with the given name synchronously.
     *
     * \param methodName Name of the service method to call.
     * \param requestData Request data to be passed to the method.
     * \param context Context specific for particular service.
     *
     * \return Created response data as bytes.
     *
     * \throw ServiceException if the call fails.
     */
    virtual std::vector<uint8_t, ALLOC> callMethod(
            StringView methodName,
            const IBasicServiceData<ALLOC>& requestData,
            void* context = nullptr) = 0;
};

/** Typedef to service interface provided for convenience - using default std::allocator<uint8_t>. */
/** \{ */
using IServiceData = IBasicServiceData<>;
using IServiceDataPtr = IBasicServiceDataPtr<>;
using IService = IBasicService<>;
using IServiceClient = IBasicServiceClient<>;
/** \} */

/** Typedef to service data implementation provided for convenience - using default std::allocator<uint8_t>. */
using ServiceData = BasicServiceData<>;

} // namespace zserio

#endif // ifndef ZSERIO_ISERVICE_H_INC
