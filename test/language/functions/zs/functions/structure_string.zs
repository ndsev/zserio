package functions.structure_string;

// Intended to check subtypes to string returning from function.
subtype string StringSubtype;

struct StringPool
{
    string field = "POOL_FIELD";

    function StringSubtype getConst()
    {
        return "POOL_CONST";
    }

    function string getField()
    {
        return field;
    }
};

const string FIELD = "FIELD";
const string CONST = "CONST";

struct TestStructure
{
    StringPool pool;
    string field = FIELD;

    function string getPoolConst()
    {
        return pool.getConst();
    }

    function string getPoolField()
    {
        return pool.getField();
    }

    function string getConst()
    {
        return CONST;
    }

    function string getField()
    {
        return field;
    }
};
