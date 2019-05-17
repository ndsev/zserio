package optional_members.optional_array_recursion;

struct Employee
{
    string      name;
    uint16      salary;
    Title       title;
 
    // if employee is a team lead, list the team members
    Employee    teamMembers[] if title == Title.TEAM_LEAD;
};

enum uint8 Title
{
    DEVELOPER = 0,
    TEAM_LEAD = 1
};
