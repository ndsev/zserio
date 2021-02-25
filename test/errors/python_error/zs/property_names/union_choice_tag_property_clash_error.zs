package property_names.union_choice_tag_property_clash_error;

union TestUnion
{
    string value1;
    uint32 choiceTag; // clashes with property in generated API
};
