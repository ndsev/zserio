#include <memory>

#include "gtest/gtest.h"

#include "pubsub_allocation/GreetingPubsub.h"

#include "MemoryResources.h"
#include "TestPubsub.h"

#include "zserio/StringView.h"

using namespace zserio::literals;
using namespace utils;

namespace pubsub_allocation
{

using allocator_type = GreetingPubsub::allocator_type;
using string_type = zserio::string<zserio::RebindAlloc<allocator_type, char>>;

class PubsubAllocationTest : public ::testing::Test
{
public:
    PubsubAllocationTest() :
            m_invalidMemoryResource(),
            m_invalidMemoryResourceSetter(m_invalidMemoryResource),
            m_memoryResource("Memory resource"),
            m_allocator(&m_memoryResource),
            m_testPubsub(new TestPubsubImpl(m_allocator)),
            greetingPubsub(*m_testPubsub, m_allocator)
    {}

    ~PubsubAllocationTest()
    {
        m_testPubsub.reset();
        EXPECT_EQ(m_memoryResource.getNumDeallocations(), m_memoryResource.getNumAllocations());
    }

    const allocator_type& getAllocator()
    {
        return m_allocator;
    }

protected:
    struct CountingContext
    {
        size_t publishCount = 0;
        size_t subscribeCount = 0;
    };

    class TestPubsubImpl : public TestPubsub<allocator_type>
    {
    public:
        explicit TestPubsubImpl(const allocator_type& allocator) :
                TestPubsub(allocator)
        {}

    protected:
        virtual void processPublishContext(void* context) override
        {
            if (context == nullptr)
                return;

            static_cast<CountingContext*>(context)->publishCount++;
        }

        virtual void processSubscribeContext(void* context) override
        {
            if (context == nullptr)
                return;

            static_cast<CountingContext*>(context)->subscribeCount++;
        }
    };

    class NameCallback : public GreetingPubsub::GreetingPubsubCallback<Name>
    {
    public:
        explicit NameCallback(GreetingPubsub& pubsub, const allocator_type& allocator) :
                m_greetingPubsub(pubsub), m_allocator(allocator)
        {}

        void operator()(zserio::StringView topic, const Name& name)
        {
            ASSERT_EQ("pubsub_allocation/name_to_use_for_greeting"_sv, topic);
            Greeting greeting{prepareGreeting(name.getName()), m_allocator};
            m_greetingPubsub.publishGreeting(greeting);
        }

    private:
        string_type prepareGreeting(const string_type& name)
        {
            // use string longer than 32B to catch string allocation on most platforms
            string_type greeting{"Hello my dear ", m_allocator};
            greeting += name;
            greeting += "! I hope you are well!";
            return greeting;
        }

        GreetingPubsub& m_greetingPubsub;
        allocator_type m_allocator;
    };

    struct GreetingCallback : public GreetingPubsub::GreetingPubsubCallback<Greeting>
    {
        explicit GreetingCallback(const allocator_type& allocator) : greeting(allocator)
        {}

        void operator()(zserio::StringView topic, const Greeting& providedGreeting) override
        {
            ASSERT_EQ("pubsub_allocation/greeting_generated_for_name"_sv, topic);
            greeting = providedGreeting.getGreeting();
        }

        string_type greeting;
    };

private:
    InvalidMemoryResource m_invalidMemoryResource;
    MemoryResourceScopedSetter m_invalidMemoryResourceSetter;
    TestMemoryResource<4*1024> m_memoryResource;
    allocator_type m_allocator;
    std::unique_ptr<TestPubsubImpl> m_testPubsub;

protected: // must be behind m_allocator
    GreetingPubsub greetingPubsub;
};

TEST_F(PubsubAllocationTest, sendGreetingPubusub)
{
    auto idName = greetingPubsub.subscribeName(std::allocate_shared<NameCallback>(
            getAllocator(), greetingPubsub, getAllocator()));

    std::shared_ptr<GreetingCallback> greetingCallback =
            std::allocate_shared<GreetingCallback>(getAllocator(), getAllocator());
    auto idGreeting = greetingPubsub.subscribeGreeting(greetingCallback);

    Name name{"Zserio with long name which is longer than 32B", getAllocator()};
    greetingPubsub.publishName(name);
    ASSERT_EQ("Hello my dear Zserio with long name which is longer than 32B! I hope you are well!",
            greetingCallback->greeting);

    greetingPubsub.unsubscribe(idName);
    greetingPubsub.unsubscribe(idGreeting);
}

TEST_F(PubsubAllocationTest, sendGreetingPubsubWithContext)
{
    CountingContext countingContext;

    auto idName = greetingPubsub.subscribeName(std::allocate_shared<NameCallback>(
            getAllocator(), greetingPubsub, getAllocator()), &countingContext);
    ASSERT_EQ(1, countingContext.subscribeCount);

    std::shared_ptr<GreetingCallback> greetingCallback =
            std::allocate_shared<GreetingCallback>(getAllocator(), getAllocator());
    auto idGreeting = greetingPubsub.subscribeGreeting(greetingCallback, &countingContext);
    ASSERT_EQ(2, countingContext.subscribeCount);

    Name name{"Zserio with long name which is longer than 32B", getAllocator()};
    greetingPubsub.publishName(name, &countingContext);
    ASSERT_EQ(1, countingContext.publishCount);
    ASSERT_EQ("Hello my dear Zserio with long name which is longer than 32B! I hope you are well!",
            greetingCallback->greeting);

    greetingPubsub.unsubscribe(idName);
    greetingPubsub.unsubscribe(idGreeting);
}

} // namespace pubsub_allocation
