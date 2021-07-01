package pubsub_allocation;

struct Name
{
    string name;
};

struct Greeting
{
    string greeting;
};

pubsub GreetingPubsub
{
    // use strings longer than 32B to catch string allocation on most platforms
    topic("pubsub_allocation/name_to_use_for_greeting") Name name;
    topic("pubsub_allocation/greeting_generated_for_name") Greeting greeting;
};
