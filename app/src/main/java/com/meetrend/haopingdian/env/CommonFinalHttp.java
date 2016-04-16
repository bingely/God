package com.meetrend.haopingdian.env;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import net.tsz.afinal.FinalHttp;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HttpContext;
import android.util.Log;
import com.meetrend.haopingdian.App;
import com.meetrend.haopingdian.util.MD5;

/**
 * 网络请求发起公共类
 * 
 *
 */
public class CommonFinalHttp extends FinalHttp{
	
	private final static String TAG = CommonFinalHttp.class.getName();
	private final static String ETAG = "ETag";
	private final static String REPONSE = "result";
	
	public String md5Url = "";
	
	public CommonFinalHttp(){
		super();
		
		DefaultHttpClient httpClient = (DefaultHttpClient) this.getHttpClient();
		
		httpClient.addRequestInterceptor(new HttpRequestInterceptor() {
			
			@Override
			public void process(HttpRequest request, HttpContext context)
					throws HttpException, IOException {
				
				org.apache.http.RequestLine requestLine = request.getRequestLine();
				
				//本次只处理"GET"请求，每次在请求之前必须检测容器中是否有该url
				if (requestLine.getMethod().equals("GET")) {
					
				   String mNetUrl = requestLine.getUri();
				   Log.i(TAG+"  请求路径", mNetUrl);
				   if (mNetUrl.contains("&token=")) {
					  
					   int startIndex = mNetUrl.lastIndexOf("&token=");
					   String tokenValue = mNetUrl.substring(startIndex + 7, 39 + startIndex);
					   mNetUrl = mNetUrl.replace(tokenValue, "");
				   }
				   
				   md5Url = MD5.hashKeyForDisk(mNetUrl);
				   HashMap<String, Object> hashMap = App.getLruCacheInstance().get(md5Url);
				   
				   if (null != hashMap) {
					   //服务器会将请求所需要的数据转成etag值，和客户端传递的etag值进行对比
					   request.addHeader("If-None-Match", (String)hashMap.get(ETAG)); //添加etag头字段value
				   }
				}
			}
		});
		
		
		httpClient.addResponseInterceptor(new HttpResponseInterceptor() {
			
			@Override
			public void process(HttpResponse response, HttpContext context)throws HttpException, IOException {
				
			     int reponseCode = response.getStatusLine().getStatusCode();
			     
    			 if (reponseCode == 304) {
    				 
    				 Log.i(TAG+" 结果码：result code is == ", reponseCode+"");
    				 
    				 //拿缓存数据
    				 HashMap<String, Object> hashMap = App.getLruCacheInstance().get(md5Url);
    				 if (null == hashMap) {
						throw new NullPointerException(TAG+"响应数据 entity is null");
					 }
    				 
    				 byte[] array = (byte[]) hashMap.get(REPONSE);
    				 
    				 response.setStatusCode(200);
    				 setEntity(array, response);
    				 
				 }else if (reponseCode == 200) {
					 
					 Log.i(TAG+" 结果码：result code is == ", reponseCode+"");
					 
					  String etagValue = null;
					  Header[] headers = response.getAllHeaders();
					   
					  for (Header header : headers) {
						if (header.getName().equals("ETag")) {
						  etagValue = header.getValue();
						  break;
						}
					  }
					
					 HttpEntity httpEntity = response.getEntity();
					 
					 byte[] byteArrays = httpEntity2ByteArray(httpEntity);
					 
					 HashMap<String, Object> hashMap = new HashMap<String, Object>();
					 hashMap.put(ETAG, etagValue);
					 hashMap.put(REPONSE, byteArrays);
					 
					 App.getLruCacheInstance().put(md5Url, hashMap);
        			 
        			 setEntity(byteArrays, response);
        			 
				 }
		  }
		
	  });
		
	}
	
	/**
	 * 设置响应数据内容
	 * @param response
	 * @throws IllegalStateException
	 * @throws IOException
	 */
	private void setEntity(byte[] byteArray,HttpResponse response) throws IllegalStateException, IOException{
		
     /*		 InputStream is = httpEntity.getContent();
		 byte[] buffer=new byte[1024];
		 ByteArrayOutputStream bos = new ByteArrayOutputStream();
		 
		 int length=0;
		 do{
			 
			 length=is.read(buffer);
			 if (length > 0) {
				 bos.write(buffer,0,length);
			 }
			 
		 }while(length> 0);*/
		  
		 
		 //byte[] byteArray = bos.toByteArray();
		 ByteArrayInputStream bis=new ByteArrayInputStream(byteArray);
		 InputStreamEntity inputStreamEntity = new InputStreamEntity(bis, byteArray.length);
		 response.setEntity(inputStreamEntity);
	}
	
	private byte[] httpEntity2ByteArray(HttpEntity httpEntity) throws IllegalStateException, IOException{
		
		/* InputStream is = httpEntity.getContent();
		 byte[] buffer=new byte[1024];
		 ByteArrayOutputStream bos = new ByteArrayOutputStream();
		
		 int length=is.read(buffer);
		
		 while(length> 0){
			 bos.write(buffer);
			 length=is.read(buffer);
		 }*/
		
		 InputStream is = httpEntity.getContent();
		 ByteArrayOutputStream bos = new ByteArrayOutputStream();
		 byte[] buffer=new byte[1024];
		 
		 int length = -1;
		 while ((length = is.read(buffer)) != -1) {
			bos.write(buffer, 0, length);
		 }
		 
		return bos.toByteArray();
		
	}
 }