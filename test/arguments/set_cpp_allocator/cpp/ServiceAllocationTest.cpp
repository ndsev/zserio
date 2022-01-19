#include "gtest/gtest.h"

#include "service_allocation/GreetingService.h"

#include "test_utils/MemoryResources.h"
#include "test_utils/LocalServiceClient.h"

using namespace zserio::literals;
using namespace test_utils;

namespace service_allocation
{

using allocator_type = GreetingService::Client::allocator_type;
using string_type = zserio::string<zserio::RebindAlloc<allocator_type, char>>;
using LocalServiceClient = test_utils::LocalServiceClient<allocator_type>;

class ServiceAllocationTest : public ::testing::Test
{
public:
    ServiceAllocationTest() :
            m_invalidMemoryResource(),
            m_invalidMemoryResourceSetter(m_invalidMemoryResource),
            m_memoryResource("Memory resource"),
            m_allocator(&m_memoryResource),
            greetingService(m_allocator),
            localServiceClient(greetingService, m_allocator),
            greetingClient(localServiceClient, m_allocator)
    {}

    ~ServiceAllocationTest()
    {
        EXPECT_EQ(m_memoryResource.getNumDeallocations(), m_memoryResource.getNumAllocations());
    }

    const allocator_type& getAllocator()
    {
        return m_allocator;
    }

protected:
    struct CountingContext
    {
        size_t greetingCount = 0;
    };

    class GreetingServiceImpl : public GreetingService::Service
    {
    public:
        explicit GreetingServiceImpl(const allocator_type& allocator) : GreetingService::Service(allocator)
        {}

        virtual Greeting sendGreetingImpl(const Name& name, void* context) override
        {
            if (context != nullptr)
                static_cast<CountingContext*>(context)->greetingCount++;

            return Greeting(prepareGreeting(name.getName()), get_allocator_ref());
        }

    private:
        string_type prepareGreeting(const string_type& name)
        {
            // use string longer than 32B to catch string allocation on most platforms
            string_type greeting{"Hello my dear ", get_allocator_ref()};
            greeting += name;
            greeting += "! I hope you are well!";
            return greeting;
        }
    };

private:
    InvalidMemoryResource m_invalidMemoryResource;
    MemoryResourceScopedSetter m_invalidMemoryResourceSetter;
    TestMemoryResource<> m_memoryResource;
    allocator_type m_allocator;

protected: // must be behind m_allocator
    GreetingServiceImpl greetingService;
    LocalServiceClient localServiceClient;
    GreetingService::Client greetingClient;
};

TEST_F(ServiceAllocationTest, sendGreeting)
{
    Name name{"Zserio with long name which is longer than 32B", getAllocator()};
    const Greeting greeting = greetingClient.sendGreetingMethod(name);
    ASSERT_EQ("Hello my dear Zserio with long name which is longer than 32B! I hope you are well!",
            greeting.getGreeting());
}

TEST_F(ServiceAllocationTest, sendGreetingWithContext)
{
    CountingContext context;

    Name name{"Zserio with long name which is longer than 32B", getAllocator()};
    const Greeting greeting = greetingClient.sendGreetingMethod(name, &context);
    ASSERT_EQ("Hello my dear Zserio with long name which is longer than 32B! I hope you are well!",
            greeting.getGreeting());
    ASSERT_EQ(1, context.greetingCount);
}

} // namespace service_allocation
