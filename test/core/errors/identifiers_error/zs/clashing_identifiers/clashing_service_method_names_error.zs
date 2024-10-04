package clashing_identifiers.clashing_service_method_names_error;

struct Data
{
    string data;
};

service Service
{
    Data x_method(Data);
    Data X_method(Data);
};
