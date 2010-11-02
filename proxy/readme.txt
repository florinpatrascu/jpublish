Tools I found in various places, some of them unmaintained, tools I had to modify (and improve sometimes). 

2010-09-15; Added a proxy support, the ProxyServlet original code taken from http://edwardstx.net/wiki/attach/HttpProxyServlet/ProxyServlet.java Optimized and modified the original for allowing unsigned SSL certificates (based on the EasySSLProtocolSocket factory, an HTTPClient contributed code, included in source). The trust manager mentioned above SHOULD NOT be used for productive systems due to security reasons, unless it is a conscious decision and you are perfectly aware of security implications of accepting self-signed certificates!

Check this: http://edwardstx.net/wiki/Wiki.jsp?page=HttpProxyServlet, for more details about the proxy usage.

To configure the servlet, put this in your web.xml and customize the init-param values to suit your needs:

  <servlet>
    <servlet-name>ProxyServlet</servlet-name>
    <servlet-class>proxy.ProxyServlet</servlet-class>
    <init-param>
      <param-name>proxyHost</param-name>
      <param-value>localhost</param-value>
    </init-param>
    <init-param>
      <param-name>proxyPort</param-name>
      <param-value>80</param-value>
    </init-param>
    <init-param>
      <param-name>proxyPath</param-name>
      <param-value></param-value>
    </init-param>
    <init-param>
      <param-name>maxFileUploadSize</param-name>
      <param-value></param-value>
    </init-param>
  </servlet>

...

  <servlet-mapping>
    <servlet-name>ProxyServlet</servlet-name>
    <url-pattern>/*</url-pattern>
  </servlet-mapping>

