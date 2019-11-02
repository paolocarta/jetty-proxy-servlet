package me.paolocarta.proxy.servlet;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.proxy.ProxyServlet;
import org.eclipse.jetty.util.ssl.SslContextFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

@WebServlet(value = "/proxy/*", asyncSupported = true, initParams = {
		@WebInitParam(name = "maxThreads", value = "10")
})
public class JettyProxyServlet extends ProxyServlet {

	public static final String TARGET_URL = "targetUrl";

	private String placesUrl;

	@Override
	public void init() throws ServletException {
		super.init();

		ServletConfig config = getServletConfig();
        placesUrl = config.getInitParameter(TARGET_URL);
        // Allow override with system property
        try {
        	placesUrl = System.getProperty(TARGET_URL, placesUrl);
        } catch (SecurityException e) {
        }
        if (null == placesUrl) {
        	placesUrl = "https://www.google.com";
        }
	}

	@Override
	protected HttpClient newHttpClient() {
		//Adding SSL support
		SslContextFactory sslContextFactory = new SslContextFactory.Client();
		HttpClient httpClient = new HttpClient(sslContextFactory);
		return httpClient;
	}

	@Override
	protected void addProxyHeaders(HttpServletRequest clientRequest, Request proxyRequest) {
		super.addProxyHeaders(clientRequest, proxyRequest);
		//you can add some headers here.
	}

	@Override
	protected String rewriteTarget(HttpServletRequest clientRequest) {

		String query = clientRequest.getQueryString();
		return placesUrl + "?" + query;
	}
}
