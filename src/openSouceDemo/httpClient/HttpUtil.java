package openSouceDemo.httpClient;


import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.json.JSONException;
import org.json.JSONObject;

import web.util.IOUtil;
import web.util.URLConnectionUtil;


public class HttpUtil {
	
	public static String RESP_CODE = "resp_code";
	public static String RESP_BODY = "resp_body";
	/**
	 * 连接默认超时时间
	 */
	private static final int DEFAULT_CONNECTION_MANAGER_TIMEOUT = 1000 * 3;
	/**
	 * http socket 连接超时时间
	 */
	private static final int DEFAULT_SOCKET_TIMEOUT = 1000 * 3;
	
	public static Map<String, Object> req(HttpMethodBase method)
			throws Exception {
		return req(method, DEFAULT_CONNECTION_MANAGER_TIMEOUT,
				DEFAULT_SOCKET_TIMEOUT);
	}	
	
	public static Map<String, Object> req(HttpMethodBase method,
			long conn_manager_time_out, int socket_time_out) throws Exception {
		long start=System.currentTimeMillis();
		HttpClient httpClient = new HttpClient();
		HttpClientParams httpClientParams = httpClient.getParams();
		
		httpClientParams.setCookiePolicy(CookiePolicy.IGNORE_COOKIES);

		httpClientParams.setConnectionManagerTimeout(conn_manager_time_out);
		httpClientParams.setSoTimeout(socket_time_out);
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout((int)conn_manager_time_out);  

		Map<String, Object> result = new HashMap<String, Object>();		
		try {
			int respCode = httpClient.executeMethod(method);
			InputStream is = method.getResponseBodyAsStream();
			String resp = IOUtil.toString(is);

			method.releaseConnection();

			result.put(RESP_CODE, respCode);
			result.put(RESP_BODY, resp);
		} catch (Exception e) {
			e.printStackTrace();
			String url = method.getURI() == null ? "" : method.getURI().toString();
			System.err.println(e.getMessage() + " URL: " + url);
			result.put(RESP_CODE, 500);
		} finally {
			if (method != null) {
				try {
					method.releaseConnection();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		long end=System.currentTimeMillis()-start;
		if(end>500){
			System.out.println("[httputil cost time]"+end+" url="+method.getURI().toString());
		}
		return result;
	}
	
	/**
	 * this method just sends an request and will ignore the response 
	 * @param method
	 * @param conn_manager_time_out
	 * @param socket_time_out
	 * @return
	 * @throws Exception
	 */
	
	public static Map<String, Object> reqWithNoInputStream(HttpMethodBase method,
			long conn_manager_time_out, int socket_time_out) throws Exception {
		long start=System.currentTimeMillis();
		HttpClient httpClient = new HttpClient();
		HttpClientParams httpClientParams = httpClient.getParams();
		
		httpClientParams.setCookiePolicy(CookiePolicy.IGNORE_COOKIES);

		httpClientParams.setConnectionManagerTimeout(conn_manager_time_out);
		httpClientParams.setSoTimeout(socket_time_out);
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout((int)conn_manager_time_out);  

		Map<String, Object> result = new HashMap<String, Object>();		
		//String url = method.getURI().toString();
		//LOG.info(" URL: " + url);
		try {
			int respCode = httpClient.executeMethod(method);
			//InputStream is = method.getResponseBodyAsStream();
			//String resp = IOUtil.toString(is);
			method.releaseConnection();
			result.put(RESP_CODE, respCode);
			//result.put(RESP_BODY, resp);
		} catch (Exception e) {
			e.printStackTrace();
			String url = method.getURI() == null ? "" : method.getURI().toString();
			System.err.println(e.getMessage() + " URL: " + url);
			result.put(RESP_CODE, 500);
		} finally {
			if (method != null) {
				try {
					method.releaseConnection();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		long end=System.currentTimeMillis()-start;
		if(end>500){
			System.out.println("[httputil cost time]"+end+" url="+method.getURI().toString());
		}
		return result;
	}
	
	/**
	 * 代理处理
	 * 
	 * @param method
	 * @param mClient
	 * @return
	 * @throws HttpException
	 * @throws IOException
	 * @throws JSONException
	 */
	public JSONObject localProxyResultByUrlConnetion(String url,Map params)
			throws Exception {
		long _ts = System.currentTimeMillis(), _te = _ts;
		
		Map<String, Object> respMap =URLConnectionUtil.senPostRequest(url,DEFAULT_CONNECTION_MANAGER_TIMEOUT,DEFAULT_SOCKET_TIMEOUT,params);
		_te = System.currentTimeMillis();
		if ((_te - _ts) > 100) {
			System.out.println("initLogin localProxyResultByUrlConnetion :" + (_te - _ts) + " >100ms url="+url );
		}
		String resp = (String) respMap.get(HttpUtil.RESP_BODY);
		if (resp != null) {
			resp = resp.replaceFirst("^[^{]*", "");
		}

		JSONObject root = new JSONObject();
		JSONObject response = new JSONObject();
		root.put("response", response);
		JSONObject header = new JSONObject();
		response.put("header", header);

		if (200 != (Integer) respMap.get(HttpUtil.RESP_CODE)) {
			header.put("respcode", "PROXY_ERROR");
			System.err.println("initLogin access passport : " + resp);
		} else {
			header.put("respcode", "SUCCESS");
			JSONObject result = new JSONObject(resp);
			response.put("result", result);
		}
		return root;
	}
	
	public static void main(String args[]) throws Exception {
		GetMethod method = new GetMethod("http://m.weather.com.cn/data/101010100.html");
		NameValuePair[] params = new NameValuePair[]{
				new NameValuePair("a", ""),				
				};
		method.setQueryString(params);
		Map<String, Object> show_result = HttpUtil.req(method, 1000, 1000);
		System.out.print(show_result);
	}
}

