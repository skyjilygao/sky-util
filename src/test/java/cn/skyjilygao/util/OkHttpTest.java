package cn.skyjilygao.util;

import okhttp3.*;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 测试okhttp
 * 参考：https://mp.weixin.qq.com/s?__biz=MzIxNjA5MTM2MA==&mid=2652436860&idx=1&sn=482517b1c749b07686851300834dd5a6&chksm=8c6206f3bb158fe5e36a687c9937aab16a36e1ab11e829c8738ef7b127ac94d7b620903c1d44&scene=0&xtrack=1&key=e105728d74a8474415300356dab9f23bbfdebb9da209777e4acec87cd771553388afa8a56351050566cb1e79ee79f5e8a962e4667009710f1388ed5e575fa6098502f38f5faa2a936111b60baba66e0c&ascene=14&uin=OTMyNDYxMzQw&devicetype=Windows+10&version=62080079&lang=zh_CN&exportkey=AcTzGn5Z7T9m4MSHP1l3kcI%3D&pass_ticket=l9Zc%2BPK4666C%2BDC1uQYqgv70CFE8f5HT%2FEzrqEmorFVI04jAedlVtK2abp89UZmO
 * @author skyjilygao
 * @date 20200402
 */
public class OkHttpTest {
    private OkHttpClient client = new OkHttpClient();
    String BASE_URL = "https://graph.facebook.com/v6.0";
    public static void main(String[] args) {
        OkHttpTest test = new OkHttpTest();
        try {
            test.testGet();
            System.out.println("3-----");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void testGet() throws IOException {
        String api = "https://graph.facebook.com/v6.0/xxx/insights";
        String url = String.format("%s", api);
        Map<String, Object> p = new LinkedHashMap<>();
        p.put("level","campaign");
        p.put("date_preset","lifetime");
        p.put("fields","campaign_id,results,cost_per_result,spend,cost_per_action_type");
        p.put("access_token","xx");
        p.put("limit","2");
        HttpUrl.Builder urlBuilder = HttpUrl.get(api).newBuilder();

        urlBuilder.addQueryParameter("level","campaign");
        urlBuilder.addQueryParameter("date_preset","lifetime");
        urlBuilder.addQueryParameter("fields","campaign_id,results,cost_per_result,spend,cost_per_action_type");
        urlBuilder.addQueryParameter("access_token","xxx");
        urlBuilder.addQueryParameter("limit","2");
        Request.Builder builder = new Request.Builder();
        Request request1 = builder.url(urlBuilder.build()).build();

//        RequestBody requestBody = RequestBody.create(JSONObject.toJSONString(p),MediaType.get("application/json; charset=utf-8"));
       /* Request request = new Request.Builder()
                .url(url).put(requestBody)
                .get()
                .build();*/
        client = new OkHttpClient().newBuilder()
                .callTimeout(1000, TimeUnit.MINUTES)
                .connectTimeout(1000, TimeUnit.MINUTES)
                .readTimeout(1000, TimeUnit.MINUTES)
                .writeTimeout(1000, TimeUnit.MINUTES)
                .build();
        final Call call = client.newCall(request1);
        // 异步
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                System.out.println("2---"+response.body().string());
            }
        });
        System.out.println("1-------------");
        // 同步
        /*Response response = call.execute();
        System.out.println(response.body().string());*/
    }

    private String getUrl(String id){
        return BASE_URL + "/" + id;
    }
}
