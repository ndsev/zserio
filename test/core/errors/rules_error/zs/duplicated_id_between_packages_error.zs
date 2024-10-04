package duplicated_id_between_packages_error;

import rules.package_rules.PackageRules;

rule_group RuleGroupOne
{
    /*! Rule one. !*/
    rule "rule-one";

    /*! Rule two. !*/
    rule "rule-two";
};
