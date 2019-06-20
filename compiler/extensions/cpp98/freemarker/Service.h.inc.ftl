<#macro stub_interface_header_public rpc>
    <#if rpc.noStreaming>
        virtual ::grpc::Status ${rpc.name}(::grpc::ClientContext* context,
                const ${rpc.requestTypeFullName}& request, ${rpc.responseTypeFullName}* response) = 0;

        typedef ::grpc::ClientAsyncResponseReaderInterface<${rpc.responseTypeFullName}> <#rt>
                <#lt>IClientAsync${rpc.name}Reader;
        typedef std::unique_ptr<IClientAsync${rpc.name}Reader> IClientAsync${rpc.name}ReaderPtr;

        IClientAsync${rpc.name}ReaderPtr Async${rpc.name}(::grpc::ClientContext* context,
                const ${rpc.requestTypeFullName}& request, ::grpc::CompletionQueue* cq)
        {
            return IClientAsync${rpc.name}ReaderPtr(Async${rpc.name}Raw(context, request, cq));
        }

        IClientAsync${rpc.name}ReaderPtr PrepareAsync${rpc.name}(::grpc::ClientContext* context,
                const ${rpc.requestTypeFullName}& request, ::grpc::CompletionQueue* cq)
        {
            return IClientAsync${rpc.name}ReaderPtr(PrepareAsync${rpc.name}Raw(context, request, cq));
        }
    <#elseif rpc.requestOnlyStreaming>
        typedef ::grpc::ClientWriterInterface<${rpc.requestTypeFullName}> IClient${rpc.name}Writer;
        typedef std::unique_ptr<IClient${rpc.name}Writer> IClient${rpc.name}WriterPtr;

        IClient${rpc.name}WriterPtr ${rpc.name}(
                ::grpc::ClientContext* context, ${rpc.responseTypeFullName}* response)
        {
            return IClient${rpc.name}WriterPtr(${rpc.name}Raw(context, response));
        }

        typedef ::grpc::ClientAsyncWriterInterface<${rpc.requestTypeFullName}> IClientAsync${rpc.name}Writer;
        typedef std::unique_ptr<IClientAsync${rpc.name}Writer> IClientAsync${rpc.name}WriterPtr;

        IClientAsync${rpc.name}WriterPtr Async${rpc.name}(
                ::grpc::ClientContext* context, ${rpc.responseTypeFullName}* response,
                ::grpc::CompletionQueue* cq, void* tag)
        {
            return IClientAsync${rpc.name}WriterPtr(Async${rpc.name}Raw(context, response, cq, tag));
        }

        IClientAsync${rpc.name}WriterPtr PrepareAsync${rpc.name}(
                ::grpc::ClientContext* context, ${rpc.responseTypeFullName}* response,
                ::grpc::CompletionQueue* cq)
        {
            return IClientAsync${rpc.name}WriterPtr(PrepareAsync${rpc.name}Raw(context, response, cq));
        }
    <#elseif rpc.responseOnlyStreaming>
        typedef ::grpc::ClientReaderInterface<${rpc.responseTypeFullName}> IClient${rpc.name}Reader;
        typedef std::unique_ptr<IClient${rpc.name}Reader> IClient${rpc.name}ReaderPtr;

        IClient${rpc.name}ReaderPtr ${rpc.name}(::grpc::ClientContext* context,
                const ${rpc.requestTypeFullName}& request)
        {
            return IClient${rpc.name}ReaderPtr(${rpc.name}Raw(context, request));
        }

        typedef ::grpc::ClientAsyncReaderInterface<${rpc.responseTypeFullName}> IClientAsync${rpc.name}Reader;
        typedef std::unique_ptr<IClientAsync${rpc.name}Reader> IClientAsync${rpc.name}ReaderPtr;

        IClientAsync${rpc.name}ReaderPtr Async${rpc.name}(::grpc::ClientContext* context,
                const ${rpc.requestTypeFullName}& request, ::grpc::CompletionQueue* cq, void* tag)
        {
            return IClientAsync${rpc.name}ReaderPtr(Async${rpc.name}Raw(context, request, cq, tag));
        }

        IClientAsync${rpc.name}ReaderPtr PrepareAsync${rpc.name}(::grpc::ClientContext* context,
                const ${rpc.requestTypeFullName}& request, ::grpc::CompletionQueue* cq)
        {
            return IClientAsync${rpc.name}ReaderPtr(PrepareAsync${rpc.name}Raw(context, request, cq));
        }
    <#else><#-- bidi streaming -->
        typedef ::grpc::ClientReaderWriterInterface<
                ${rpc.requestTypeFullName}, ${rpc.responseTypeFullName}> IClient${rpc.name}ReaderWriter;
        typedef std::unique_ptr<IClient${rpc.name}ReaderWriter> IClient${rpc.name}ReaderWriterPtr;

        IClient${rpc.name}ReaderWriterPtr ${rpc.name}(::grpc::ClientContext* context)
        {
            return IClient${rpc.name}ReaderWriterPtr(${rpc.name}Raw(context));
        }

        typedef ::grpc::ClientAsyncReaderWriterInterface<
                ${rpc.requestTypeFullName}, ${rpc.responseTypeFullName}> IClientAsync${rpc.name}ReaderWriter;
        typedef std::unique_ptr<IClientAsync${rpc.name}ReaderWriter> IClientAsync${rpc.name}ReaderWriterPtr;

        IClientAsync${rpc.name}ReaderWriterPtr Async${rpc.name}(::grpc::ClientContext* context,
                ::grpc::CompletionQueue* cq, void* tag)
        {
            return IClientAsync${rpc.name}ReaderWriterPtr(Async${rpc.name}Raw(context, cq, tag));
        }
        IClientAsync${rpc.name}ReaderWriterPtr PrepareAsync${rpc.name}(::grpc::ClientContext* context,
                ::grpc::CompletionQueue* cq)
        {
            return IClientAsync${rpc.name}ReaderWriterPtr(PrepareAsync${rpc.name}Raw(context, cq));
        }
    </#if>
</#macro>

<#macro stub_interface_header_private rpc>
    <#if rpc.noStreaming>
        virtual IClientAsync${rpc.name}Reader* Async${rpc.name}Raw(::grpc::ClientContext* context,
                const ${rpc.requestTypeFullName}& request, ::grpc::CompletionQueue* cq) = 0;
        virtual IClientAsync${rpc.name}Reader* PrepareAsync${rpc.name}Raw(::grpc::ClientContext* context,
                const ${rpc.requestTypeFullName}& request, ::grpc::CompletionQueue* cq) = 0;
    <#elseif rpc.requestOnlyStreaming>
        virtual IClient${rpc.name}Writer* ${rpc.name}Raw(::grpc::ClientContext* context,
                ${rpc.responseTypeFullName}* response) = 0;
        virtual IClientAsync${rpc.name}Writer* Async${rpc.name}Raw(::grpc::ClientContext* context,
                ${rpc.responseTypeFullName}* response, ::grpc::CompletionQueue* cq, void* tag) = 0;
        virtual IClientAsync${rpc.name}Writer* PrepareAsync${rpc.name}Raw(::grpc::ClientContext* context,
                ${rpc.responseTypeFullName}* response, ::grpc::CompletionQueue* cq) = 0;
    <#elseif rpc.responseOnlyStreaming>
        virtual IClient${rpc.name}Reader* ${rpc.name}Raw(::grpc::ClientContext* context,
                const ${rpc.requestTypeFullName}& request) = 0;
        virtual IClientAsync${rpc.name}Reader* Async${rpc.name}Raw(::grpc::ClientContext* context,
                const ${rpc.requestTypeFullName}& request, ::grpc::CompletionQueue* cq, void* tag) = 0;
        virtual IClientAsync${rpc.name}Reader* PrepareAsync${rpc.name}Raw(::grpc::ClientContext* context,
                const ${rpc.requestTypeFullName}& request, ::grpc::CompletionQueue* cq) = 0;
    <#else><#-- bidi streaming -->
        virtual IClient${rpc.name}ReaderWriter* ${rpc.name}Raw(::grpc::ClientContext* context) = 0;
        virtual IClientAsync${rpc.name}ReaderWriter* Async${rpc.name}Raw(::grpc::ClientContext* context,
                ::grpc::CompletionQueue* cq, void* tag) = 0;
        virtual IClientAsync${rpc.name}ReaderWriter* PrepareAsync${rpc.name}Raw(::grpc::ClientContext* context,
                ::grpc::CompletionQueue* cq) = 0;
    </#if>
</#macro>

<#macro stub_header_public rpc>
    <#if rpc.noStreaming>
        ::grpc::Status ${rpc.name}(::grpc::ClientContext* context,
                const ${rpc.requestTypeFullName}& request, ${rpc.responseTypeFullName}* response) override;

        typedef ::grpc::ClientAsyncResponseReader<${rpc.responseTypeFullName}> ClientAsync${rpc.name}Reader;
        typedef std::unique_ptr<ClientAsync${rpc.name}Reader> ClientAsync${rpc.name}ReaderPtr;

        ClientAsync${rpc.name}ReaderPtr Async${rpc.name}(::grpc::ClientContext* context,
                const ${rpc.requestTypeFullName}& request, ::grpc::CompletionQueue* cq)
        {
            return ClientAsync${rpc.name}ReaderPtr(Async${rpc.name}Raw(context, request, cq));
        }

        ClientAsync${rpc.name}ReaderPtr PrepareAsync${rpc.name}(::grpc::ClientContext* context,
                const ${rpc.requestTypeFullName}& request, ::grpc::CompletionQueue* cq)
        {
            return ClientAsync${rpc.name}ReaderPtr(PrepareAsync${rpc.name}Raw(context, request, cq));
        }
    <#elseif rpc.requestOnlyStreaming>
        typedef ::grpc::ClientWriter<${rpc.requestTypeFullName}> Client${rpc.name}Writer;
        typedef std::unique_ptr<Client${rpc.name}Writer> Client${rpc.name}WriterPtr;

        Client${rpc.name}WriterPtr ${rpc.name}(::grpc::ClientContext* context,
                ${rpc.responseTypeFullName}* response)
        {
            return Client${rpc.name}WriterPtr(${rpc.name}Raw(context, response));
        }

        typedef ::grpc::ClientAsyncWriter<${rpc.requestTypeFullName}> ClientAsync${rpc.name}Writer;
        typedef std::unique_ptr<ClientAsync${rpc.name}Writer> ClientAsync${rpc.name}WriterPtr;

        ClientAsync${rpc.name}WriterPtr Async${rpc.name}(::grpc::ClientContext* context,
                ${rpc.responseTypeFullName}* response, ::grpc::CompletionQueue* cq, void* tag)
        {
            return ClientAsync${rpc.name}WriterPtr(Async${rpc.name}Raw(context, response, cq, tag));
        }

        ClientAsync${rpc.name}WriterPtr PrepareAsync${rpc.name}(::grpc::ClientContext* context,
                ${rpc.responseTypeFullName}* response, ::grpc::CompletionQueue* cq)
        {
            return ClientAsync${rpc.name}WriterPtr(PrepareAsync${rpc.name}Raw(context, response, cq));
        }
    <#elseif rpc.responseOnlyStreaming>
        typedef ::grpc::ClientReader<${rpc.responseTypeFullName}> Client${rpc.name}Reader;
        typedef std::unique_ptr<Client${rpc.name}Reader> Client${rpc.name}ReaderPtr;

        Client${rpc.name}ReaderPtr ${rpc.name}(::grpc::ClientContext* context,
                const ${rpc.requestTypeFullName}& request)
        {
            return Client${rpc.name}ReaderPtr(${rpc.name}Raw(context, request));
        }

        typedef ::grpc::ClientAsyncReader<${rpc.responseTypeFullName}> ClientAsync${rpc.name}Reader;
        typedef std::unique_ptr<ClientAsync${rpc.name}Reader> ClientAsync${rpc.name}ReaderPtr;

        ClientAsync${rpc.name}ReaderPtr Async${rpc.name}(::grpc::ClientContext* context,
                const ${rpc.requestTypeFullName}& request, ::grpc::CompletionQueue* cq, void* tag)
        {
            return ClientAsync${rpc.name}ReaderPtr(Async${rpc.name}Raw(context, request, cq, tag));
        }

        ClientAsync${rpc.name}ReaderPtr PrepareAsync${rpc.name}(::grpc::ClientContext* context,
                const ${rpc.requestTypeFullName}& request, ::grpc::CompletionQueue* cq)
        {
            return ClientAsync${rpc.name}ReaderPtr(PrepareAsync${rpc.name}Raw(context, request, cq));
        }
    <#else><#-- bidi streaming -->
        typedef ::grpc::ClientReaderWriter<
                ${rpc.requestTypeFullName}, ${rpc.responseTypeFullName}> Client${rpc.name}ReaderWriter;
        typedef std::unique_ptr<Client${rpc.name}ReaderWriter> Client${rpc.name}ReaderWriterPtr;

        Client${rpc.name}ReaderWriterPtr ${rpc.name}(::grpc::ClientContext* context)
        {
            return Client${rpc.name}ReaderWriterPtr(${rpc.name}Raw(context));
        }

        typedef ::grpc::ClientAsyncReaderWriter<
                ${rpc.requestTypeFullName}, ${rpc.responseTypeFullName}> ClientAsync${rpc.name}ReaderWriter;
        typedef std::unique_ptr<ClientAsync${rpc.name}ReaderWriter> ClientAsync${rpc.name}ReaderWriterPtr;

        ClientAsync${rpc.name}ReaderWriterPtr Async${rpc.name}(::grpc::ClientContext* context,
                ::grpc::CompletionQueue* cq, void* tag)
        {
            return ClientAsync${rpc.name}ReaderWriterPtr(Async${rpc.name}Raw(context, cq, tag));
        }

        ClientAsync${rpc.name}ReaderWriterPtr PrepareAsync${rpc.name}(::grpc::ClientContext* context,
                ::grpc::CompletionQueue* cq)
        {
            return ClientAsync${rpc.name}ReaderWriterPtr(PrepareAsync${rpc.name}Raw(context, cq));
        }
    </#if>
</#macro>

<#macro stub_header_private rpc>
    <#if rpc.noStreaming>
        ClientAsync${rpc.name}Reader* Async${rpc.name}Raw(::grpc::ClientContext* context,
                const ${rpc.requestTypeFullName}& request, ::grpc::CompletionQueue* cq) override;
        ClientAsync${rpc.name}Reader* PrepareAsync${rpc.name}Raw(::grpc::ClientContext* context,
                const ${rpc.requestTypeFullName}& request, ::grpc::CompletionQueue* cq) override;
    <#elseif rpc.requestOnlyStreaming>
        Client${rpc.name}Writer* ${rpc.name}Raw(::grpc::ClientContext* context,
                ${rpc.responseTypeFullName}* response) override;
        ClientAsync${rpc.name}Writer* Async${rpc.name}Raw(::grpc::ClientContext* context,
                ${rpc.responseTypeFullName}* response, ::grpc::CompletionQueue* cq, void* tag) override;
        ClientAsync${rpc.name}Writer* PrepareAsync${rpc.name}Raw(::grpc::ClientContext* context,
                ${rpc.responseTypeFullName}* response, ::grpc::CompletionQueue* cq) override;
    <#elseif rpc.responseOnlyStreaming>
        Client${rpc.name}Reader* ${rpc.name}Raw(::grpc::ClientContext* context,
                const ${rpc.requestTypeFullName}& request) override;
        ClientAsync${rpc.name}Reader* Async${rpc.name}Raw(::grpc::ClientContext* context,
                const ${rpc.requestTypeFullName}& request, ::grpc::CompletionQueue* cq, void* tag) override;
        ClientAsync${rpc.name}Reader* PrepareAsync${rpc.name}Raw(::grpc::ClientContext* context,
                const ${rpc.requestTypeFullName}& request, ::grpc::CompletionQueue* cq) override;
    <#else><#-- bidi streaming -->
        Client${rpc.name}ReaderWriter* ${rpc.name}Raw(::grpc::ClientContext* context) override;
        ClientAsync${rpc.name}ReaderWriter* Async${rpc.name}Raw(::grpc::ClientContext* context,
                ::grpc::CompletionQueue* cq, void* tag) override;
        ClientAsync${rpc.name}ReaderWriter* PrepareAsync${rpc.name}Raw(::grpc::ClientContext* context,
                ::grpc::CompletionQueue* cq) override;
    </#if>
</#macro>

<#macro service_header_public rpc>
    <#if rpc.noStreaming>
        virtual ::grpc::Status ${rpc.name}(::grpc::ServerContext* context,
                const ${rpc.requestTypeFullName}* request, ${rpc.responseTypeFullName}* response);
    <#elseif rpc.requestOnlyStreaming>
        virtual ::grpc::Status ${rpc.name}(::grpc::ServerContext* context,
                ::grpc::ServerReader<${rpc.requestTypeFullName}>* reader,
                ${rpc.responseTypeFullName}* response);
    <#elseif rpc.responseOnlyStreaming>
        virtual ::grpc::Status ${rpc.name}(::grpc::ServerContext* context,
                const ${rpc.requestTypeFullName}* request,
                ::grpc::ServerWriter<${rpc.responseTypeFullName}>* writer);
    <#else><#-- bidi streaming -->
        virtual ::grpc::Status ${rpc.name}(::grpc::ServerContext* context,
                ::grpc::ServerReaderWriter<${rpc.responseTypeFullName}, ${rpc.requestTypeFullName}>* stream);
    </#if>
</#macro>

<#macro with_async_method_impl rpc index>
    <#if rpc.noStreaming>
        // disable synchronous version of this method
        ::grpc::Status ${rpc.name}(::grpc::ServerContext* context,
                const ${rpc.requestTypeFullName}* request, ${rpc.responseTypeFullName}* response) final override
        {
            abort();
            return ::grpc::Status(::grpc::StatusCode::UNIMPLEMENTED, "");
        }

        void Request${rpc.name}(::grpc::ServerContext* context,
                ${rpc.requestTypeFullName}* request,
                ::grpc::ServerAsyncResponseWriter<${rpc.responseTypeFullName}>* response,
                ::grpc::CompletionQueue* new_call_cq, ::grpc::ServerCompletionQueue* notification_cq, void *tag)
        {
            ::grpc::Service::RequestAsyncUnary(
                    ${index}, context, request, response, new_call_cq, notification_cq, tag);
        }
    <#elseif rpc.requestOnlyStreaming>
        // disable synchronous version of this method
        ::grpc::Status ${rpc.name}(::grpc::ServerContext* context,
                ::grpc::ServerReader<${rpc.requestTypeFullName}>* reader,
                ${rpc.responseTypeFullName}* response) override
        {
            abort();
            return ::grpc::Status(::grpc::StatusCode::UNIMPLEMENTED, "");
        }

        void Request${rpc.name}(::grpc::ServerContext* context,
                ::grpc::ServerAsyncReader<${rpc.responseTypeFullName}, ${rpc.requestTypeFullName}>* reader,
                ::grpc::CompletionQueue* new_call_cq, ::grpc::ServerCompletionQueue* notification_cq, void *tag)
        {
            ::grpc::Service::RequestAsyncClientStreaming(
                    ${index}, context, reader, new_call_cq, notification_cq, tag);
        }
    <#elseif rpc.responseOnlyStreaming>
        // disable synchronous version of this method
        ::grpc::Status ${rpc.name}(::grpc::ServerContext* context, const ${rpc.requestTypeFullName}* request,
                ::grpc::ServerWriter<${rpc.responseTypeFullName}>* writer) override
        {
            abort();
            return ::grpc::Status(::grpc::StatusCode::UNIMPLEMENTED, "");
        }

        void Request${rpc.name}(::grpc::ServerContext* context, ${rpc.requestTypeFullName}* request,
                ::grpc::ServerAsyncWriter<${rpc.responseTypeFullName}>* writer,
                ::grpc::CompletionQueue* new_call_cq, ::grpc::ServerCompletionQueue* notification_cq, void *tag)
        {
            ::grpc::Service::RequestAsyncServerStreaming(
                    ${index}, context, request, writer, new_call_cq, notification_cq, tag);
        }
    <#else><#-- bidi streaming -->
        // disable synchronous version of this method
        ::grpc::Status ${rpc.name}(::grpc::ServerContext* context, ::grpc::ServerReaderWriter<
                ${rpc.responseTypeFullName}, ${rpc.requestTypeFullName}>* stream) override
        {
            abort();
            return ::grpc::Status(::grpc::StatusCode::UNIMPLEMENTED, "");
        }

        void Request${rpc.name}(::grpc::ServerContext* context,
                ::grpc::ServerAsyncReaderWriter<${rpc.responseTypeFullName}, ${rpc.requestTypeFullName}>* stream,
                ::grpc::CompletionQueue* new_call_cq, ::grpc::ServerCompletionQueue* notification_cq, void *tag)
        {
            ::grpc::Service::RequestAsyncBidiStreaming(
                    ${index}, context, stream, new_call_cq, notification_cq, tag);
        }
    </#if>
</#macro>

<#macro with_generic_method_impl rpc>
    <#if rpc.noStreaming>
        // disable synchronous version of this method
        ::grpc::Status ${rpc.name}(::grpc::ServerContext* context,
                const ${rpc.requestTypeFullName}* request, ${rpc.responseTypeFullName}* response) final override
        {
            abort();
            return ::grpc::Status(::grpc::StatusCode::UNIMPLEMENTED, "");
        }
    <#elseif rpc.requestOnlyStreaming>
        // disable synchronous version of this method
        ::grpc::Status ${rpc.name}(::grpc::ServerContext* context,
                ::grpc::ServerReader<${rpc.requestTypeFullName}>* reader,
                ${rpc.responseTypeFullName}* response) override
        {
            abort();
            return ::grpc::Status(::grpc::StatusCode::UNIMPLEMENTED, "");
        }
    <#elseif rpc.responseOnlyStreaming>
        // disable synchronous version of this method
        ::grpc::Status ${rpc.name}(::grpc::ServerContext* context, const ${rpc.requestTypeFullName}* request,
                ::grpc::ServerWriter<${rpc.responseTypeFullName}>* writer) override
        {
            abort();
            return ::grpc::Status(::grpc::StatusCode::UNIMPLEMENTED, "");
        }
    <#else><#-- bidi streaming -->
        // disable synchronous version of this method
        ::grpc::Status ${rpc.name}(::grpc::ServerContext* context, ::grpc::ServerReaderWriter<
                ${rpc.responseTypeFullName}, ${rpc.requestTypeFullName}>* stream) override
        {
            abort();
            return ::grpc::Status(::grpc::StatusCode::UNIMPLEMENTED, "");
        }
    </#if>
</#macro>

<#macro with_raw_method_impl rpc index>
    <#if rpc.noStreaming>
        // disable synchronous version of this method
        ::grpc::Status ${rpc.name}(::grpc::ServerContext* context,
                const ${rpc.requestTypeFullName}* request, ${rpc.responseTypeFullName}* response) final override
        {
            abort();
            return grpc::Status(::grpc::StatusCode::UNIMPLEMENTED, "");
        }

        void Request${rpc.name}(::grpc::ServerContext* context,
                ::grpc::ByteBuffer* request, ::grpc::ServerAsyncResponseWriter<::grpc::ByteBuffer>* response,
                ::grpc::CompletionQueue* new_call_cq, ::grpc::ServerCompletionQueue* notification_cq, void *tag)
        {
            ::grpc::Service::RequestAsyncUnary(
                    ${index}, context, request, response, new_call_cq, notification_cq, tag);
        }
    <#elseif rpc.requestOnlyStreaming>
        // disable synchronous version of this method
        ::grpc::Status ${rpc.name}(::grpc::ServerContext* context,
                ::grpc::ServerReader<${rpc.requestTypeFullName}>* reader,
                ${rpc.responseTypeFullName}* response) override
        {
            abort();
            return ::grpc::Status(::grpc::StatusCode::UNIMPLEMENTED, "");
        }

        void Request${rpc.name}(::grpc::ServerContext* context,
                ::grpc::ServerAsyncReader<::grpc::ByteBuffer, ::grpc::ByteBuffer>* reader,
                ::grpc::CompletionQueue* new_call_cq, ::grpc::ServerCompletionQueue* notification_cq, void *tag)
        {
            ::grpc::Service::RequestAsyncClientStreaming(
                    ${index}, context, reader, new_call_cq, notification_cq, tag);
        }
    <#elseif rpc.responseOnlyStreaming>
        // disable synchronous version of this method
        ::grpc::Status ${rpc.name}(::grpc::ServerContext* context, const ${rpc.requestTypeFullName}* request,
                ::grpc::ServerWriter<${rpc.responseTypeFullName}>* writer) override
        {
            abort();
            return ::grpc::Status(::grpc::StatusCode::UNIMPLEMENTED, "");
        }

        void Request${rpc.name}(::grpc::ServerContext* context, ::grpc::ByteBuffer* request,
                ::grpc::ServerAsyncWriter<::grpc::ByteBuffer>* writer, ::grpc::CompletionQueue* new_call_cq,
                ::grpc::ServerCompletionQueue* notification_cq, void *tag)
        {
            ::grpc::Service::RequestAsyncServerStreaming(
                    ${index}, context, request, writer, new_call_cq, notification_cq, tag);
        }
    <#else><#-- bidi streaming -->
        // disable synchronous version of this method
        ::grpc::Status ${rpc.name}(::grpc::ServerContext* context, ::grpc::ServerReaderWriter<
                ${rpc.responseTypeFullName}, ${rpc.requestTypeFullName}>* stream) override
        {
            abort();
            return ::grpc::Status(::grpc::StatusCode::UNIMPLEMENTED, "");
        }

        void Request${rpc.name}(::grpc::ServerContext* context,
                ::grpc::ServerAsyncReaderWriter<::grpc::ByteBuffer, ::grpc::ByteBuffer>* stream,
                ::grpc::CompletionQueue* new_call_cq, ::grpc::ServerCompletionQueue* notification_cq, void *tag)
        {
            ::grpc::Service::RequestAsyncBidiStreaming(
                    ${index}, context, stream, new_call_cq, notification_cq, tag);
        }
    </#if>
</#macro>

<#macro typedef_service_with_method methodName first hasNext last>
    <#if first>
    typedef <#rt>
        <#if hasNext>
            <#lt>${methodName}<
        <#else>
            <#lt>${methodName}<<#rt>
        </#if>
    <#elseif last>
            ${methodName}<<#rt>
    <#else>
            ${methodName}<
    </#if>
</#macro>

<#macro typedef_async_service rpcList>
    <#list rpcList as rpc>
        <#local methodName>WithAsyncMethod_${rpc.name}</#local>
        <@typedef_service_with_method methodName, rpc?is_first, rpc?has_next, rpc?is_last/>
    </#list>
        Service<#t>
    <#list rpcList as rpc>
        <#lt><#if rpc?index != 0> </#if>><#rt>
    </#list>
        <#lt> AsyncService;
</#macro>

<#macro typedef_streamed_unary_service noStreamingRpcList>
    <#list noStreamingRpcList as rpc>
        <#local methodName>WithStreamedUnaryMethod_${rpc.name}</#local>
        <@typedef_service_with_method methodName, rpc?is_first, rpc?has_next, rpc?is_last/>
    </#list>
        Service<#t>
    <#list noStreamingRpcList as rpc>
        <#lt><#if rpc?index != 0> </#if>><#rt>
    </#list>
        <#lt> StreamedUnaryService;
</#macro>

<#macro typedef_split_streamed_service responseOnlyStreamingRpcList>
    <#list responseOnlyStreamingRpcList as rpc>
        <#local methodName>WithSplitStreamingMethod_${rpc.name}</#local>
        <@typedef_service_with_method methodName, rpc?is_first, rpc?has_next, rpc?is_last/>
    </#list>
        Service<#t>
    <#list responseOnlyStreamingRpcList as rpc>
        <#lt><#if rpc?index != 0> </#if>><#rt>
    </#list>
        <#lt> SplitStreamedService;
</#macro>

<#macro typedef_streamed_service noOrResponseOnlyStreamingRpcList>
    <#list noOrResponseOnlyStreamingRpcList as rpc>
        <#local methodName>
            <#if rpc.noStreaming>
                WithStreamedUnaryMethod_${rpc.name}<#t>
            <#else><#-- rpc.responseOnlyStreaming -->
                WithSplitStreamingMethod_${rpc.name}<#t>
            </#if>
        </#local>
        <@typedef_service_with_method methodName, rpc?is_first, rpc?has_next, rpc?is_last/>
    </#list>
        Service<#t>
    <#list noOrResponseOnlyStreamingRpcList as rpc>
        <#lt><#if rpc?index != 0> </#if>><#rt>
    </#list>
        <#lt> StreamedService;
</#macro>
