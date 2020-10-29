<#ftl output_format="HTML">
<#include "doc_comment.inc.ftl">

          <@doc_comments docComments 2, false/>
        </main>
        <div id="toc" class="col-2 order-3">
          <nav class="nav flex-column">
            <a class="nav-link p-0 pl-1" href="#${symbol.htmlLink.htmlAnchor}">${symbol.name}</a>
<#list packageSymbols as packageSymbol>
            <@symbol_toc_link packageSymbol.symbol packageSymbol.templateParameters/>
</#list>
          </nav>
        </div>
      </div><!-- row -->
    </div><!-- container -->

    <script src="https://code.jquery.com/jquery-3.5.1.slim.min.js" integrity="sha384-DfXdz2htPH0lsSSs5nCTpuj/zy4C+OGpamoFVy38MVBnE+IbbVYUew+OrCXaRkfj" crossorigin="anonymous"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@4.5.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-ho+j7jyWK8fNQe+A12Hb8AhRq26LrZ/JpcUGGOn+Y7RsweNrtN/tE3MoK7ZeZDyx" crossorigin="anonymous"></script>
  </body>
</html>
