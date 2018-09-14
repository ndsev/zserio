package service_types.streaming_service;

/** User data. */
struct User
{
    string name;
    uint8 age;
};

/** Empty structure for empty request  or response. */
struct Empty
{};

/** Number response. */
struct Num
{
    uint32 num;
};

/** Name request. */
struct Name
{
    string name;
};

/** Age response. */
struct Age
{
    uint8 age;
};

/** User database. */
service UserDB
{
    /** Adds the user and returns the number of users. Normal RPC. */
    rpc Num addUser(User);

    /** Adds stream of users, returns number of users. Client streaming RPC. */
    rpc Num addUsers(stream User);

    /** Returns stream of users. Server streaming RPC. */
    rpc stream User getUsers(Empty);

    /** Returns stream of ages corresponding to the stream of user names. Bidirectional streaming RPC. */
    rpc stream Age getAges(stream Name);
};
