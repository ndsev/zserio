package simpletrace;

struct SimpleTrace
{
    Point trace[];
};

struct Point
{
    varint longitude;
    varint latitude;
};
