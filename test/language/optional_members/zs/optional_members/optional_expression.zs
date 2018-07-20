package optional_members.optional_expression;

struct BlackColor(uint8 numBlackTones)
{
    int32                       tones[numBlackTones];
};

struct Container
{
    BasicColor                  basicColor;
    uint8                       numBlackTones if basicColor == BasicColor.BLACK;
    BlackColor(numBlackTones)   blackColor if basicColor == BasicColor.BLACK;
};

// check that enum can be defined behind the Container
enum uint8 BasicColor
{
    BLACK,
    WHITE
};
