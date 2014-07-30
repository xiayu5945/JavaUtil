package web.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.dom4j.Document;
import org.dom4j.io.SAXReader;

public class URLConnectionUtil {
	
	private static final String SERVLET_POST = "POST";
	private static final String SERVLET_GET = "GET";
	private static final String URL_ENCODER = "utf-8";
	private static final String CONENT_ENCODER = "utf-8";
	private static final int CONNECT_TIMEOUT = 1000;
	private static final int READ_TIMEOUT = 1000;
	private static String RESP_CODE = "resp_code";
	private static String RESP_BODY = "resp_body";
	
	//组装GET参数。
	private static String prepareParam(Map<String, Object> paramMap) {
		if (paramMap == null || paramMap.isEmpty()) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		for(String key : paramMap.keySet()) {
			if(key == null || key.trim().length()==0){
				continue;
			}
			String value = (String)paramMap.get(key);
			if (sb.length() < 1) {
				sb.append(key).append("=").append(value);
			} else {
				sb.append("&").append(key).append("=").append(value);
			}
		}
		return sb.toString();
	}

	public static String senGetRequest(String urlStr, Map<String, Object> paramMap) throws IOException{
		try {
			String paramStr = prepareParam(paramMap);
			if(paramStr != null && paramStr.trim().length() != 0){
				paramStr = java.net.URLEncoder.encode(paramStr, URL_ENCODER);
				if (paramStr != null && paramStr.trim().length() < 1) {
					urlStr += "?" + paramStr;
				} 
			}
			return senGetRequest(urlStr);
		} catch (Exception e) {
			System.err.println(URLConnectionUtil.class.getName());
		}
		return "";
	}
	
	public static String senGetRequest(String urlStr) throws Exception{
		return senGetRequest(urlStr,CONNECT_TIMEOUT,READ_TIMEOUT);
	}
	public static String senGetRequest(String urlStr,int connectTimeout,int readTimeout) throws Exception{
		HttpURLConnection conn = null;
		OutputStream os = null;
		BufferedReader br = null;
		try {
			URL url = new URL(urlStr);
			//LOG.info(url);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(SERVLET_GET);
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			// suggest功能要求实时性较高，过长的Timeout无意义
			conn.setConnectTimeout(connectTimeout);
			conn.setReadTimeout(readTimeout);
			br = new BufferedReader(new InputStreamReader(conn.getInputStream(),CONENT_ENCODER));
			StringBuilder sb  = new StringBuilder();
			String line = "";
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			//System.out.println(line);
			return sb.toString();
		} catch (Exception e) {
			System.err.println(URLConnectionUtil.class.getName());
			throw e;
		}finally{
			if(conn != null){
				conn.disconnect();
			}
			if(os != null){
				os.close();
			}
			if(br != null){
				br.close();
			}
		} 
	}
	
	public static String senGetRequest(String urlStr,int connectTimeout,int readTimeout,String userIp) throws Exception{
		HttpURLConnection conn = null;
		OutputStream os = null;
		BufferedReader br = null;
		try {
			URL url = new URL(urlStr);
			//LOG.info(url);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(SERVLET_GET);
			conn.setRequestProperty("X-Forwarded-For", userIp);
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			// suggest功能要求实时性较高，过长的Timeout无意义
			conn.setConnectTimeout(connectTimeout);
			conn.setReadTimeout(readTimeout);
			br = new BufferedReader(new InputStreamReader(conn.getInputStream(),CONENT_ENCODER));
			StringBuilder sb  = new StringBuilder();
			String line = "";
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			//System.out.println(line);
			return sb.toString();
		} catch (Exception e) {
			System.err.println(URLConnectionUtil.class.getName());
			throw e;
		}finally{
			if(conn != null){
				conn.disconnect();
			}
			if(os != null){
				os.close();
			}
			if(br != null){
				br.close();
			}
		} 
	}
	public static Document sendQisuSearchRequest(String urlStr,int connectTimeout,int readTimeout) throws Exception{
		HttpURLConnection conn = null;
		OutputStream os = null;
		BufferedReader br = null;
		try {
			URL url = new URL(urlStr);
			System.out.println("searchURL: " + url);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(SERVLET_GET);
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setConnectTimeout(connectTimeout);
			conn.setReadTimeout(readTimeout);
			br = new BufferedReader(new InputStreamReader(conn.getInputStream(),CONENT_ENCODER));
			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				System.err.println("QisuSearch Server Error!!!");
				return null;
			}
			SAXReader reader = new SAXReader();
			Document readDoc = reader.read(br);
			//System.out.println(line);
			return readDoc;
		} catch (Exception e) {
			System.err.println(URLConnectionUtil.class.getName());
			throw e;
		}finally{
			if(conn != null){
				conn.disconnect();
			}
			if(os != null){
				os.close();
			}
			if(br != null){
				br.close();
			}
		} 
	}
	
	
	public static String senPostRequest(String urlStr, String postContent) throws IOException{
		HttpURLConnection conn = null;
		OutputStream os = null;
		BufferedReader br = null;
		try {
			URL url = new URL(urlStr);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(SERVLET_POST);
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			// suggest功能要求实时性较高，过长的Timeout无意义
			conn.setConnectTimeout(CONNECT_TIMEOUT);
			conn.setReadTimeout(READ_TIMEOUT);
			if(postContent != null && postContent.trim().length() != 0){
				os = conn.getOutputStream();
				os.write(postContent.getBytes(CONENT_ENCODER));
				os.flush();
			}
			br = new BufferedReader(new InputStreamReader(conn.getInputStream(),CONENT_ENCODER));
			StringBuilder sb  = new StringBuilder();
			String line = "";
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			//System.out.println(line);
			return sb.toString();
		} catch (Exception e) {
			System.err.println(URLConnectionUtil.class.getName());
		}finally{
			if(conn != null){
				conn.disconnect();
			}
			if(os != null){
				os.close();
			}
			if(br != null){
				br.close();
			}
		} 
		return "";
	}
	public static Map<String, Object> senPostRequest(String urlStr,int connectTimeout,int readTimeout,Map params) throws Exception{
		HttpURLConnection conn = null;
		OutputStream os = null;
		BufferedReader br = null;
		Map<String, Object> result = new HashMap<String, Object>();	
		try {
			CookieManager cm = new CookieManager();
			
			cm.setCookiePolicy(java.net.CookiePolicy.ACCEPT_NONE);
			URL url = new URL(urlStr);
			//LOG.info(url);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(SERVLET_POST);
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			
			conn.setConnectTimeout(connectTimeout);
			conn.setReadTimeout(readTimeout);
			String paStr=prepareParam(params);
			if(paStr!=null&&paStr.trim().length()>0){
				byte[] bypes = paStr.getBytes();       
				conn.getOutputStream().write(bypes);// 输入参数
			}
			br = new BufferedReader(new InputStreamReader(conn.getInputStream(),CONENT_ENCODER));
			StringBuilder sb  = new StringBuilder();
			String line = "";
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			//System.out.println(line);
			int respCode=conn.getResponseCode();
			result.put(RESP_CODE, respCode);
			result.put(RESP_BODY, sb.toString());
			
		} catch (Exception e) {
			System.err.println(URLConnectionUtil.class.getName());
			result.put(RESP_CODE, 500);
			
		}finally{
			if(conn != null){
				try{
					conn.disconnect();
				}catch (Exception e) {
					
				}
			}
			if(os != null){
				try {
					os.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(br != null){
				try {
					br.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} 
		return result;
	}
}