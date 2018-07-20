package none_optional_with_auto_optional_params_error;

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
    optional uint8              numBlackTones;

    // This should not compile. The field is not optional but its parameter is.
    ColorTones(numBlackTones)   blackTones;
};
