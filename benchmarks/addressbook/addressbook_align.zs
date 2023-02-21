package addressbook_align;

struct AddressBook
{
    Person people[];
};

struct Person
{
    varint32 id;
    string name;
    string email;
    PhoneNumber phones[];
};

struct PhoneNumber
{
    string number;
    PhoneType type;
};

enum uint8 PhoneType // <-- use a full byte instead of 2 bit to keep alignment
{
    MOBILE = 0,
    HOME = 1,
    WORK = 2,
};
