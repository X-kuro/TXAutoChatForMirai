package net.kuroi.httpok.repack

import okhttp3.*
import java.io.IOException

fun getDataByGet(url:String): String? {

        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .build()

        var response = client.newCall(request).execute()
        return response.body?.string()

}

fun getDataByPost(map: Map<String,String>,url: String): String? {
    try {
        val client = OkHttpClient()
        var body =  FormBody.Builder()
        for(key in map.keys)
        {
           body.add(key, map[key].toString())
        }
        var requestBody = body.build()
        var request = Request.Builder()
                .post(requestBody)
                .url(url)
                .build()
        var response = client.newCall(request).execute()
        return response.body?.string()
    }catch (e:Exception) {
        return ""
    }
}


