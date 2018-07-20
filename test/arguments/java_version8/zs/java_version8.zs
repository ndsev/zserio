package java_version8;

struct MainStruct
{
    bool        hasExtra;
    uint8       numOfElements;
    int32       extra if hasExtra == true;
};
