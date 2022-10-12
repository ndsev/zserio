package comments.markdown_doc.service_comments;

/*! Structure used for service response. !*/
struct Response
{
    /*! Value which contains result of power of two calculation. !*/
    uint64 value;
};

/*! Structure used for service request. !*/
struct Request
{
    /*! Value which is used as an input for power of two calculation. !*/
    int32 value;
};

/*! Simple service interface which demonstrates calculation of power of two. !*/
service SimpleService
{
    /*! Method which calculates power of two from given value. !*/
    Response powerOfTwo(Request);
};
