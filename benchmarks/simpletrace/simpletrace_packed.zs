package simpletrace_packed;

struct SimpleTrace
{
    packed Point trace[];
};

struct Point
{
    varint longitude;
    varint latitude;
};
