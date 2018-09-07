body
{
    font-family:Arial, Helvetica;
    font-size:11pt;
}

h1
{
    font-size:1.8em;
}

h2
{
    font-size:1.6em;
}

h3
{
    font-size:1.4em;
}

a
{
    color:#333333;
}

a.fieldLink
{
    text-decoration:none;
    color:#333333;
}

a.fieldLink:hover,
a.fieldLink:focus
{
    text-decoration:underline;
}

a.enumLink
{
    text-decoration:underline;
    color:green;
}

a.subtypeLink
{
    text-decoration:underline;
    font-style:italic;
}

a.noStyle
{
    text-decoration:underline blink;
    color:red;
}

a.consttypeLink
{
    text-decoration:underline;
    color:gray;
}

a.sqlInteger,
a.sqlTableLink,
a.sqlDBLink
{
    text-decoration:underline;
    color:blue;
}

a.choiceLink,
a.unionLink,
a.structureLink,
a.instantLink,
a.arrayLink,
a.referenceLink
{
    text-decoration:underline;
}

a.serviceLink
{
    text-decoration:underline;
    color:brown;
}

.detailedDocu
{
    width:100%;
    height:100%;
    text-align:right;
    border-width:0;
    border-style:none;
    border-spacing:0px;
    border-collapse:collapse;
}

.docuTag
{
    width:98%;
    background-color:#FFFFFF;
    padding-bottom:1em;
}

.docuTag h2
{
    padding:0;
}

.docuTag#todo
{
    text-decoration:none;
    color:#DD2222;
}

.docuTag#see
{
    text-decoration:none;
    color:#222222;
}

.docuTag#param
{
    text-decoration:none;
    color:#2222DD;
}

div.msgdetail
{
    width:98%;
    padding:1ex;
    font-weight:bold;
    font-size:1.8em;
    color:blue;
    background-color:#f2f2ff;
}

h2.msgdetail
{
}

.memberItem
{
    height:1.4em;
    padding-left:1em;
}

.memberItem a
{
    font-weight:bold;
}

.memberDetail
{
    padding-bottom:0em;
}

th
{
    font-size:1.5em;
}

.docuTag span:first-child
{
    font-weight:bold;
    text-transform:capitalize;
}

.docuCode
{
    background-color:#FFFFFF;
    empty-cells:show;
    border-width:1px;
    border-style:dotted;
    border-spacing:0px;
    border-collapse:collapse;
    padding:0.5em;
    font-family:monospace;
}

.docuCode tr td
{
    border-width:0px;
    border-style:none;
    border-collapse:collapse;
    padding:0px;
}

.docuCode table tbody#tabIndent tr.codeMember td
{
    padding-top:.25em;
    padding-bottom:.25em;
}

#tabIndent
{
    padding-left:5.5ex;
}

#tabIndent2
{
    padding-left:11ex;
}

.selectedpackagelist
{
    margin-left:0em;
    text-align:left;
    padding-left:1em;
    height:0.2em;
    cursor:pointer;
    list-style-type:disc;
    font-weight:bold;
}

del
{
    color:gray;
}

.deprecated
{
    color:gray;
}

.deprecatedDetail
{
    color:blue;
    font-weight:bold;
}

.deprecatedList
{
    list-style-type:circle;
}

.packagelist
{
    margin-left:0em;
    text-align:left;
    padding-left:1em;
    height:0.2em;
    cursor:pointer;
    list-style-type:circle;
    font-weight:normal;
}

.packagelist a
{
    text-decoration:none;
}

* html .packagelist,
* html .selectedpackagelist
{  /* nur fuer Internet Explorer */
    margin-top:0em;
    margin-bottom:0em;
}

.classlist
{
    padding-left:1em;
    margin-left:0em;
    text-align:left;
}

.classlist li
{
    display:list-item;
}

table.references
{
    border-collapse:collapse;
}

table.references, th.references, td.references
{
    border:1px solid black;
    text-align:center;
}

th.references
{
    font-weight:bold;
    font-size:1em;
}

th.references, td.references
{
    padding:5px;
}
