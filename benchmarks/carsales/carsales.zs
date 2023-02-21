package carsales;

struct CarSales
{
    varsize numSoldCars;
    CarSale carSales[numSoldCars];
};

struct CarSale
{
    Date date;
    optional string storeName;
    Address store;
    Car car;
    varuint32 price;
};

struct Date
{
    bit:5 day;
    DayOfWeek dayOfWeek;
    Month month;
    varuint16 year;
};

struct Address
{
    string street;
    varuint16 streetNumber;
    string city;
    bit:17 zipCode;
};

struct Car
{
    CarBrand carBrand;
    Color color;
    string vin;
};

enum bit:3 DayOfWeek
{
    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY,
    SATURDAY,
    SUNDAY
};

enum bit:4 Month
{
    JANUARY,
    FEBRUARY,
    MARCH,
    APRIL,
    MAY,
    JUNE,
    JULY,
    AUGUST,
    SEPTEMBER,
    OCTOBER,
    NOVEMBER,
    DECEMBER
};

enum bit:5 CarBrand
{
    VOLKSWAGEN,
    TOYOTA,
    STELLANTIS,
    MERCEDES_BENZ,
    FORD_MOTOR,
    BMW,
    HONDA,
    GENERAL_MOTORS,
    HYUNDAI,
    NISSAN,
    KIA,
    RENAULT,
    TESLA,
    VOLVO,
    SUZUKI
};

enum bit:4 Color
{
    BLACK,
    WHITE,
    RED,
    GREEN,
    YELLOW,
    BLUE,
    PINK,
    GRAY,
    BROWN,
    ORANGE,
    PURPLE
};
