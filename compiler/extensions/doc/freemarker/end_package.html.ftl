<#ftl output_format="HTML">
<#include "html_common.inc.ftl">
<#include "doc_comment.inc.ftl">
<#if hasDocComments(docComments)>

          <@doc_comments_all docComments, 5, false/>
</#if>
        </main>
        <script>
          if (isTocHidden()) {
            let main = document.getElementById("main");
            main.classList.remove("col-8");
            main.classList.add("col-10");
          }
        </script>

      </div><!-- row -->
    </div><!-- container -->

    <script src="<@html_path jsDirectory/>/jquery-3.5.1.slim.min.js"></script>
    <script src="<@html_path jsDirectory/>/bootstrap.bundle.min.js"></script>
    <script src="<@html_path jsDirectory/>/anchor.min.js"></script>
    <script>
      // anchorjs setup
      anchors.add(".anchor");
      anchors.add(".anchor-md");

      <@html_js_search_functions "symbol_overview", "package", "symbol"/>

      function scrollParentToChild(parent, child) {
        let parentRect = parent.getBoundingClientRect();
        let parentViewableArea = {
          height: parent.clientHeight,
          width: parent.clientWidth
        };

        let childRect = child.getBoundingClientRect();
        let isViewable = (childRect.top >= parentRect.top) &&
                         (childRect.top <= parentRect.top + parentViewableArea.height);

        if (!isViewable) {
          parent.scrollTop = (childRect.top + parent.scrollTop) - parentRect.top
        }
      }

      function toggleToc() {
        let toc = $("#toc");
        toc.toggle();
        toggleTocIcon();
        $("main").toggleClass("col-8");
        $("main").toggleClass("col-10");

        let hidden = toc.is(":hidden");
        setItemToStorage(localStorage, "tocHidden", hidden);
      }

      function toggleDoc(button) {
        // find all doc rows within the button's table
        $(button).parents("table").find("tbody tr.doc").toggle();
        $(button).toggleClass("comments-hidden");
        let icon = $(button).find("use").attr("xlink:href");
        icon = (icon == "#chat-left") ? "#chat-left-text" : "#chat-left";
        $(button).find("use").attr("xlink:href", icon);
      }

      // custom hooks
      $(document).ready(function() {
        <@html_js_search_setup "symbolOverviewSearchValue"/>

        // re-apply current left panel scroll
        let symbolOverviewScrollTop = getItemFromStorage(sessionStorage, "symbolOverviewScrollTop");
        if (symbolOverviewScrollTop) {
          $('#symbol_overview').scrollTop(symbolOverviewScrollTop);
        }

        // remember current symbol overview scroll position
        $("#symbol_overview .nav-link").on("click", function() {
          setItemToStorage(sessionStorage, "symbolOverviewScrollTop", $('#symbol_overview').scrollTop());
        });

        $("#header .nav-link").on("click", function() {
          setItemToStorage(sessionStorage, "symbolOverviewScrollTop", 0);
        });

        // toc toggleable by the button
        $("#toc_button").click(toggleToc);

        // bootstrap's scrollspy setup
        var scrollMarginTop = parseInt($('h1.anchor').css("scroll-margin-top"));
        $('body').scrollspy({
          target: '#toc',
          offset: scrollMarginTop
        });

        // allow to collapse active package in the overview
        $(".nav-link-package.active").on("click", function(e) {
          e.preventDefault();
          $(this).siblings(".nav-symbols").toggleClass("collapsed");
        })

        // catch scrollspy's event that the active item has changed
        $(window).on("activate.bs.scrollspy", function() {
          scrollParentToChild($('#toc')[0], $('#toc .active')[0]);
        })
      });
    </script>
  </body>
</html>
