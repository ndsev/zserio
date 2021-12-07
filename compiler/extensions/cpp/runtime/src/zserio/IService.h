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
 * Service response data wrapper used on the server side.
 *
 * When reflectable interface is available, holds the reflectable object, otherwise holds the serialized data.
 */
template <typename ALLOC = std::allocator<uint8_t>>
class IBasicResponseData
{
public:
    /** Destructor. */
    virtual ~IBasicResponseData() = default;

    /**
     * Gets reflectable representing the response.
     *
     * Null when -withoutTypeInfo is used.
     *
     * \return Reflectable response or null.
     */
    virtual IBasicReflectablePtr<ALLOC> getReflectable() = 0;

    /**
     * Gets response data.
     *
     * Empty when -withTypeInfo is used.
     *
     * \return Response data or empty span.
     */
    virtual Span<const uint8_t> getData() const = 0;
};

/** Typedef to response data smart pointer needed for convenience in generated code. */
template <typename ALLOC = std::allocator<uint8_t>>
using IBasicResponseDataPtr = std::shared_ptr<IBasicResponseData<ALLOC>>;

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
    virtual IBasicResponseDataPtr<ALLOC> callMethod(
            StringView methodName,
            Span<const uint8_t> requestData,
            void* context = nullptr) = 0;
};

/**
 * Service request data wrapper to be used on the client side.
 *
 * When reflectable interface is available, holds reference to the reflectable object,
 * otherwise holds the reference to the serialized data.
 */
template <typename ALLOC = std::allocator<uint8_t>>
class BasicRequestData
{
public:
    /**
     * Constructor from reflectable, used when -withTypeInfo is used.
     *
     * Reflectable created from zserio request object.
     */
    explicit BasicRequestData(const IBasicReflectablePtr<ALLOC>& reflectable) :
            m_reflectable(reflectable)
    {}

    /**
     * Constructor from bit buffer, used when -withoutTypeInfo is used.
     *
     * \param bitBuffer Serialized request data as bit buffer.
     */
    explicit BasicRequestData(const BasicBitBuffer<ALLOC>& bitBuffer) :
            m_data(bitBuffer.getBuffer(), bitBuffer.getByteSize())
    {}

    /**
     * Gets reflectable.
     *
     * Null when -withoutTypeInfo is used.
     *
     * \return Reflectable to use as request data or null.
     */
    const IBasicReflectablePtr<ALLOC>& getReflectable() const
    {
        return m_reflectable;
    }

    /**
     * Gets request data.
     *
     * Empty when -withTypeInfo is used.
     *
     * \return Request data or empty span.
     */
    Span<const uint8_t> getData() const
    {
        return m_data;
    }

private:
    Span<const uint8_t> m_data;
    IBasicReflectablePtr<ALLOC> m_reflectable;
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
     * \return Created response data.
     *
     * \throw ServiceException if the call fails.
     */
    virtual std::vector<uint8_t, ALLOC> callMethod(
            StringView methodName,
            const BasicRequestData<ALLOC>& requestData,
            void* context = nullptr) = 0;
};

/** Typedef to service interface provided for convenience - using default std::allocator<uint8_t>. */
/** \{ */
using IResponseData = IBasicResponseData<>;
using IResponseDataPtr = IBasicResponseDataPtr<>;
using IService = IBasicService<>;

using RequestData = BasicRequestData<>;
using IServiceClient = IBasicServiceClient<>;
/** \} */

} // namespace zserio

#endif // ifndef ZSERIO_ISERVICE_H_INC
