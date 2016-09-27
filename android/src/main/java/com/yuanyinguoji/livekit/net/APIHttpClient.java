package com.yuanyinguoji.livekit.net;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * Created by chenyabing on 16/8/29.
 */

public class APIHttpClient {


    // 接口地址
    private static String apiURL = "http://192.168.3.67:8080/lkgst_manager/order/order";
    private static final String pattern = "yyyy-MM-dd HH:mm:ss:SSS";
//    private HttpClient httpClient = null;
//    private HttpPost method = null;
    private long startTime = 0L;
    private long endTime = 0L;
    private int status = 0;

    /**
     * 接口地址
     *
     * @param url
     */
    public APIHttpClient(String url) {

        if (url != null) {
            this.apiURL = url;
        }
//        if (apiURL != null) {
//            httpClient = new DefaultHttpClient();
//            method = new HttpPost(apiURL);
//
//        }
    }

    /**
     * 调用 API
     *
     * @param parameters
     * @return
     */
//    public String post(String parameters) {
//        String body = null;
//        LogUtils.i("parameters:" + parameters);
//
//        if (method != null & parameters != null
//                && !"".equals(parameters.trim())) {
//            try {
//
//                // 建立一个NameValuePair数组，用于存储欲传送的参数
//                method.addHeader("Content-type", "application/json; charset=utf-8");
//                method.setHeader("Accept", "application/json");
//                method.setEntity(new StringEntity(parameters, "UTF-8"));
//                startTime = System.currentTimeMillis();
//
//                HttpResponse response = httpClient.execute(method);
//
//                endTime = System.currentTimeMillis();
//                int statusCode = response.getStatusLine().getStatusCode();
//
//                LogUtils.i("statusCode:" + statusCode);
//                LogUtils.i("调用API 花费时间(单位：毫秒)：" + (endTime - startTime));
//                if (statusCode != HttpStatus.SC_OK) {
//                    LogUtils.e("Method failed:" + response.getStatusLine());
//                    status = 1;
//                }else {
//                    body = EntityUtils.toString(response.getEntity());
//                    LogUtils.i("调用接口状态：======" + body.toString());
//                }
//
//                // Read the response body
//
//            } catch (IOException e) {
//                // 网络错误
//                status = 3;
//            } finally {
//                LogUtils.i("调用接口状态：" + status);
//            }
//
//        }
//        return body;
//    }


    public static final MediaType JSON=MediaType.parse("application/json; charset=utf-8");

    public String postJson(String parameters) {
        String body = null;

        //申明给服务端传递一个json串
        //创建一个OkHttpClient对象
        OkHttpClient okHttpClient = new OkHttpClient();
        //创建一个RequestBody(参数1：数据类型 参数2传递的json串)
        RequestBody requestBody = RequestBody.create(JSON, parameters);
        //创建一个请求对象
        Request request = new Request.Builder()
                .url(apiURL)
                .post(requestBody)
                .build();
        //发送请求获取响应
        try {
            Response response=okHttpClient.newCall(request).execute();
            //判断请求是否成功
            if(response.isSuccessful()){
                //打印服务端返回结果
                body = response.body().string();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return body;
    }
}