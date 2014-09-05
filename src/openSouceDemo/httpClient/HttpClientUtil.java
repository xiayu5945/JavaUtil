package openSouceDemo.httpClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.util.IdleConnectionTimeoutThread;
import org.dom4j.Document;
/**
 * HttpClient 在线文档  ：http://hc.apache.org/httpclient-legacy/apidocs/overview-summary.html
 * 
 * 使用HttpClient 需要使用到的依赖包有 commons-httpclient-3.1 、commons-codec-1.5.jar、commons-logging-1.1.1.jar
 * 1.设置连接策略   MultiThreadedHttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager()
 * 2.设置HttpClient 对象 HttpClient httpClient = new HttpClient(connectionManager)
 * 		设置是否忽略Cookie：  httpClient.getParams().setCookiePolicy(CookiePolicy.IGNORE_COOKIES)
 * 		设置连接超时时间： httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(1000 * 15)
 * 		设置请求参数的编码方式： client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET,"utf-8");
 * 		设置读超时时间：client.getHttpConnectionManager().getParams().setSoTimeout(1000 * 10)
 * 		设置最大连接数：client.getHttpConnectionManager().getParams().setMaxTotalConnections(200)
 * 					   client.getHttpConnectionManager().getParams().setDefaultMaxConnectionsPerHost(100)
 * 		
 *3.避免大量请求等待
 *		IdleConnectionTimeoutThread idleThread = new IdleConnectionTimeoutThread();
 *		idleThread.setTimeoutInterval(1000 * 15);
 *		idleThread.setConnectionTimeout(1000 * 15);
 *		idleThread.addConnectionManager(connectionManager);
 *		idleThread.start();	
 *4.使用GetMethod 或者 PostMethod
 *		1)GetMethod get = new GetMethod(url)
 *			执行请求：int status = client.executeMethod(get);
 *			判断执行结果：if (status == HttpStatus.SC_OK)  为ture 连接成功，否则连接失败。
 *			若连接成为可得到返回结果：response = get.getResponseBodyAsString()
 *			关闭连接：get.releaseConnection()
 *		2)PostMethod post = new PostMethod(url);
 *			设置请求参数：NameValuePair pair = new NameValuePair（StringKey, StringValue）
 *						 NameValuePair[] pairs = new NameValuePair[]{pair1,pair2,pair3,...}
 *						 post.setQueryString(nameValuePairs)       //post.getQueryString()可取得请求path
 *			指定请求内容的类型:post.setRequestHeader("Content-type", "text/xml; charset=utf-8")
 *			设置请求内容的类型：method.setRequestEntity(new StringRequestEntity(StringBody,"text/xml", "utf-8"));
 *						 还有ByteArrayRequestEntity, FileRequestEntity, InputStreamRequestEntity, MultipartRequestEntity
 *
 */

public class HttpClientUtil {

	private static final String RESPONSE_ERROR = "{code:'A00001'}";
	private static final String RESPONSE_ERROR_CODE = "A00001";

	// http默认连接超时以及读超时时间
	public static final int DEFAULT_CONNECTION_TIMEOUT = 1000 * 15;
	public static final int DEFAULT_READ_TIMEOUT = 1000 * 10;
	public static final int MAX_TOTAL_CONNECTIONS = 200;
	public static final int DEFAULT_MAX_CONNECTIONSPERHOST = 100;
	public static final int IDLETHREAD_TIMEOUT_INTERVAL = 1000 * 15;
	public static final int IDLETHREAD_CONNECTION_TIMEOUT = 1000 * 15;

	private static final MultiThreadedHttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
	private static final HttpClient client = new HttpClient(connectionManager);
	static {
		client.getParams().setCookiePolicy(CookiePolicy.IGNORE_COOKIES);
		
		client.getHttpConnectionManager().getParams().setConnectionTimeout(
				DEFAULT_CONNECTION_TIMEOUT);
		client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET,"utf-8");
		client.getHttpConnectionManager().getParams().setSoTimeout(
				DEFAULT_READ_TIMEOUT);
		client.getHttpConnectionManager().getParams().setMaxTotalConnections(
				MAX_TOTAL_CONNECTIONS);
		client.getHttpConnectionManager().getParams().setDefaultMaxConnectionsPerHost(DEFAULT_MAX_CONNECTIONSPERHOST);
		// 检查 空闲连接线程
		IdleConnectionTimeoutThread idleThread = new IdleConnectionTimeoutThread();
		idleThread.setTimeoutInterval(IDLETHREAD_TIMEOUT_INTERVAL);
		idleThread.setConnectionTimeout(IDLETHREAD_CONNECTION_TIMEOUT);
		idleThread.addConnectionManager(connectionManager);
		idleThread.start();
	}

	public static void main(String[] args){
		
		try {
			httpGetForDocument("http://m.weather.com.cn/data/101010100.html");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 以HTTP post方式访问指定URL
	 * 
	 * @param url
	 *            访问URL
	 * @param params
	 *            请求参数
	 * @return 服务器响应内容
	 * @throws Exception
	 */
	public static String httpPost(String url, Map<String, String> params)
			throws Exception {
		String response = "";
		PostMethod post = new PostMethod(url);
		NameValuePair[] nameValuePairs = buildNameValuePairs(params);
		if (nameValuePairs != null) {
			post.setRequestBody(nameValuePairs);
		}
		try {
			int status = client.executeMethod(post);
			if (status == HttpStatus.SC_OK) {
				response = post.getResponseBodyAsString();
			}
		} catch (Exception ex) {
			System.err.println(url + ":   " + ex);
			throw ex;
		} finally {
			post.releaseConnection();
		}
		return response;
	}

	public static String httpGet(String url) throws Exception {
		String response = "";
		GetMethod get = new GetMethod(url);
		try {
			int status = client.executeMethod(get);
			if (status == HttpStatus.SC_OK) {
				response = get.getResponseBodyAsString();
			}
		} catch (Exception ex) {
			System.err.println(url + ":   " + ex);
			throw ex;
		} finally {
			get.releaseConnection();
		}
		return response;
	}
	public static Document httpGetForDocument(String url) throws Exception {
		GetMethod get = new GetMethod(url);
		try {
			int status = client.executeMethod(get);
			if (status == HttpStatus.SC_OK) {
				//SAXReader reader = new SAXReader();
				String response = get.getResponseBodyAsString();
				if(RESPONSE_ERROR.equals(response) || (response != null && response.contains(RESPONSE_ERROR_CODE))){
					System.err.println(url + "=   " + response);
					return null;
				}
				System.out.println(response);
				Document readDoc = org.dom4j.DocumentHelper.parseText(response);
				System.out.println(readDoc.toString());
				//Document readDoc = reader.read(get.getResponseBodyAsStream());
				return readDoc;
			}
		} catch (Exception ex) {
			System.err.println(url + ":   " + ex);
			throw ex;
		} finally {
			get.releaseConnection();
		}
		return null;
	}
	
	public static String httpPost(String url, Map<String, String> params,
			String body) throws Exception {
		return httpPost(url, params, body, null, null);
	}

	public static String httpPost(String url, Map<String, String> params,
			String body, String contentType, String charset) throws Exception {
		String response = "";
		PostMethod post = new PostMethod(url);
		NameValuePair[] nameValuePairs = buildNameValuePairs(params);
		if (nameValuePairs != null) {
			post.setQueryString(nameValuePairs);
			post.getQueryString();
		}
		try {
			post.setRequestEntity(new StringRequestEntity(body, contentType,
					charset));
			int status = client.executeMethod(post);
			if (status == HttpStatus.SC_OK) {
				response = post.getResponseBodyAsString();
			}
		} catch (Exception ex) {
			System.err.println(url + ":   " + ex);
			throw ex;
		} finally {
			post.releaseConnection();
		}
		return response;
	}

	/**
	 * @see #httpPost(String, Map)
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public static String httpPost(String url) throws Exception {
		return httpPost(url, null);
	}

	// 辅助方法，根据给定键值对，构造请求参数数组
	private static NameValuePair[] buildNameValuePairs(
			Map<String, String> params) {
		NameValuePair[] data = null;
		if (params != null && params.size() > 0) {
			Set<Entry<String, String>> entrySet = params.entrySet();
			data = new NameValuePair[entrySet.size()];

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			for (Entry<String, String> entry : entrySet) {
				nameValuePairs.add(new NameValuePair(entry.getKey(), entry
						.getValue()));
			}
			nameValuePairs.toArray(data);
		}
		return data;
	}

	// POST请求
	public static String xmlHttpPost(String xmlRequest, String url)
			throws Exception {
		String resXml = "";
		// 创建PostMethod方法实例
		PostMethod method = new PostMethod(url);
		try {
			// method.setHttp11(true);
			 //指定请求内容的类型
			method.setRequestHeader("Content-type", "text/xml; charset=utf-8");
			// 添加请求参数
			method.setRequestEntity(new StringRequestEntity(xmlRequest,
					"text/xml", "utf-8"));
			// 使用系统提供的默认的恢复策略
			method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
					new DefaultHttpMethodRetryHandler());

			int status = client.executeMethod(method);
			// 判断是否返回成功
			if (status == HttpStatus.SC_OK) {
				resXml = method.getResponseBodyAsString();
			}
		} catch (Exception e) {
			System.err.println(url + ":   " + e);
			throw e;
		} finally {
			method.releaseConnection();
			// 关闭连接。
		}
		return resXml;
	}
}