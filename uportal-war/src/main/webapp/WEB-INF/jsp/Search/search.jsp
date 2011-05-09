<%--

    Licensed to Jasig under one or more contributor license
    agreements. See the NOTICE file distributed with this work
    for additional information regarding copyright ownership.
    Jasig licenses this file to you under the Apache License,
    Version 2.0 (the "License"); you may not use this file
    except in compliance with the License. You may obtain a
    copy of the License at:

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing,
    software distributed under the License is distributed on
    an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
    KIND, either express or implied. See the License for the
    specific language governing permissions and limitations
    under the License.

--%>

<%@ include file="/WEB-INF/jsp/include.jsp"%>

<portlet:renderURL var="formUrl"/>
<c:set var="n"><portlet:namespace/></c:set>

<!-- Portlet -->
<div class="fl-widget portlet" role="section">

  <!-- Portlet Titlebar -->
  <div class="fl-widget-titlebar titlebar portlet-titlebar" role="sectionhead">
    <h2 class="title" role="heading"><spring:message code="search"/></h2>
  </div>
  
  <!-- Portlet Body -->
  <div class="fl-widget-content portlet-body" role="main">
  
    <!-- Portlet Section -->
    <div id="${n}search" class="portlet-section" role="region">

      <div class="portlet-section-body">

        <form action="${ formUrl }" method="POST">
            <input name="query" value="${ fn:escapeXml(query )}"/> <input type="submit" value="Search"/>
        </form>

        <c:if test="${not empty query}">

            <div class="portlet-section" role="region">
          
                <div class="titlebar">
                  <h3 class="title" role="heading">
                      <spring:message code="search.results"/>
                  </h3>
                </div>
                
                <div class="content">
                    <div id="${n}searchResults">
                        <ul ${ engineCount == 1 ? 'style=\"display:none\"' : '' }>
                            <c:forEach items="${ searchEngines }" var="engine">
                                <c:choose>
                                    <c:when test="${ engine == 'directory' }">
                                        <c:set var="directoryEnabled" value="${ true }"/>
                                        <li><a href="#${n}_directory"><span><spring:message code="directory"/></span></a></li>
                                    </c:when>
                                    <c:when test="${ engine == 'campus-web' }">
                                        <c:set var="campusWebEnabled" value="${ true }"/>
                                        <li><a href="#${n}_campus"><span><spring:message code="campus.web"/></span></a></li>
                                    </c:when>
                                    <c:when test="${ engine == 'portal' }">
                                        <c:set var="portalEnabled" value="${ true }"/>
                                        <li><a href="#${n}_portal"><span><spring:message code="portal"/></span></a></li>
                                    </c:when>
                                </c:choose>
                            </c:forEach>
                        </ul>
                        
                        <c:if test="${ directoryEnabled }">
                            <div id="${n}_directory">
                                <c:if test="${ fn:length(people) == 0 }">
                                    <spring:message code="no.results"/>
                                </c:if>
                                <c:forEach items="${ people }" var="person">
                                    <div class="person-search-result">
                                        <h3><a class="person-link" href="javascript:;">${fn:escapeXml(person.attributes.displayName[0])}</a></h3>
                                        <table>
                                            <c:forEach items="${ attributeNames }" var="attribute">
                                                <c:if test="${ fn:length(person.attributes[attribute.key]) > 0 }">
                                                    <tr>
                                                        <td><spring:message code="attribute.displayName.${ attribute.key }"/></td>
                                                        <td>${fn:escapeXml(person.attributes[attribute.key][0])}</td>
                                                </tr>
                                                </c:if>
                                            </c:forEach>
                                        </table>
                                    </div>
                                </c:forEach>
                            </div>
                        </c:if>
                        
                        <c:if test="${ campusWebEnabled }">
                            <div id="${n}_campus">
                                <c:forEach items="${ gsaResults.searchResults }" var="result">
                                    <div>
                                        <a href="${ result.link }">${ fn:escapeXml(result.title )}</a>
                                    </div>
                                    <p>${ fn:escapeXml(result.snippet )}</p>
                                </c:forEach>
                            </div>
                        </c:if>
                            
                        <c:if test="${ portalEnabled }">
                            <div id="${n}_portal">
                                <div class="portlet-search-results">
                                    <div class="portlet-match">
                                        <a class="portlet-match-link"></a>
                                        <p class="portlet-match-description"></p>
                                    </div>
                                </div>
                            </div>
                        </c:if>
                    </div>
                </div>
            </div>
        </c:if>

      </div>  

    </div>
    
  </div>

</div>

<script type="text/javascript"><rs:compressJs>
 up.jQuery(function() {
    var $ = up.jQuery;
    var fluid = up.fluid;
    

    up.jQuery(document).ready(function() {
        
        <c:if test="${not empty query and engineCount > 1}">
            up.jQuery("#${n}searchResults").tabs();
        </c:if>

        <c:if test="${ not empty query and portalEnabled }">
            var registry = up.PortletRegistry(
                "#${n}_portal", 
                { 
                    portletListUrl: "<c:url value="/api/portletList"/>",
                    listeners: {
                        onLoad: function () {
                            var cutpoints, portlets, members, portletRegex;
                            cutpoints = [
                                 { id: "portlet:", selector: ".portlet-match" },
                                 { id: "portletLink", selector: ".portlet-match-link" },
                                 { id: "portletDescription", selector: ".portlet-match-description" }
                             ];
                            
                            // Build a list of all portlets that are a deep member of the
                            // currently-selected category, sorted by title
                            portlets = [];
                            members = registry.getAllPortlets();
                            portletRegex = new RegExp(up.escapeSpecialChars("${query}"), "i");
                            $(members).each(function (idx, portlet) {
                                if (portletRegex.test(portlet.title) || portletRegex.test(portlet.description)) {
                                    portlets.push(portlet);
                                }
                            });
                            portlets.sort(up.getStringPropertySortFunction("title"));
    
                            var tree = { children: [] };
    
                            $(portlets).each(function (id, portlet) {
                                tree.children.push({
                                    ID: "portlet:",
                                    children: [
                                       { 
                                           ID: "portletLink", linktext: portlet.title, target: "/uPortal/p/" + portlet.fname
                                       },
                                       {
                                           ID: "portletDescription", value: portlet.description
                                       }
                                    ]
                                });
                            });
    
                            fluid.selfRender($(".portlet-search-results"), tree, { cutpoints: cutpoints });
                            
                        }
                    } 
                }
            );
        </c:if>
        
    });
 });
</rs:compressJs></script>