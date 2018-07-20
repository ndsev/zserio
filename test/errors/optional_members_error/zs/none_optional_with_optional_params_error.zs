package none_optional_with_optional_params_error;

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
    uint8                       numBlackTones if basicColor == BasicColor.BLACK;

    // This should not compile. The field is not optional but its parameter is.
    ColorTones(numBlackTones)   blackTones;
};
