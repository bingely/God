package com.meetrend.haopingdian.tool;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

public class HttpFileUpTool {
	/**
	 * 通过拼接的方式构造请求内容，实现参数传输以及文件传输
	 * 
	 * @param actionUrl
	 * @param params
	 * @return
	 * @throws IOException
	 */
	public static String post(String actionUrl, List <NameValuePair> params) throws IOException {

        HttpPost httpRequest = new HttpPost(actionUrl);   //建立HTTP POST联机
        httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));   //发出http请求
        HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequest);   //取得http响应
        if(httpResponse.getStatusLine().getStatusCode() == 200){
	          String strResult = EntityUtils.toString(httpResponse.getEntity());   //获取字符串
	          return strResult;
        }
		return null;
	}
}