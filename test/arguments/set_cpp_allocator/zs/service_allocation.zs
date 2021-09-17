package service_allocation;

struct Name
{
    string name;
};

struct Greeting
{
    string greeting;
};

service GreetingService
{
    Greeting sendGreeting(Name);
};
