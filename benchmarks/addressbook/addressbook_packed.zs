package addressbook_packed;

struct AddressBook
{
    packed Person people[];
};

struct Person
{
    varint32 id;
    string name;
    string email;
    packed PhoneNumber phones[];
};

struct PhoneNumber
{
    string number;
    PhoneType type;
};

enum bit:2 PhoneType
{
    MOBILE = 0,
    HOME = 1,
    WORK = 2,
};
