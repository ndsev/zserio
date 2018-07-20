package optional_members_warning;

enum uint8 BasicColor
{
    BLACK,
    WHITE
};

struct ColorTones(uint8 diffNumBlackTones)
{
    int32                       tones[diffNumBlackTones];
};

struct Container
{
    BasicColor                  basicColor;
    optional uint8              numBlackAndWhiteTones;
    uint8                       numBlackTones if basicColor == BasicColor.BLACK;
    uint8                       numWhiteTones if basicColor == BasicColor.WHITE;

    // This is ok.
    ColorTones(numWhiteTones)   whiteTones if basicColor == BasicColor.WHITE;

    // This should produce warning.
    ColorTones(numBlackTones + numWhiteTones) blackAndWhiteTones if basicColor == BasicColor.BLACK;

    // This should produce warning as well.
    ColorTones(numBlackTones + numBlackAndWhiteTones) mixedTones if basicColor == BasicColor.BLACK;
};

// The following just disable "unused type" warning for Container type.
sql_table ContainerTable
{
    int32       id sql "PRIMARY KEY";
    Container   container;
};

sql_database ContainerDatabase
{
    ContainerTable containerTable;
};
