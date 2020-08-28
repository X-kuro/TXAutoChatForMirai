package net.kuroi.httpok.repack

import com.google.gson.Gson
import java.net.URLEncoder
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import kotlin.random.Random

class TXApiCombine(val appid:String,val appkey:String){

    fun buildMsg(msg:String):Map<String,String>{
        var time = System.currentTimeMillis()
        var timestamp = (time/1000).toString()
        var nounce = (0..2000).random().toString()
        var sign = ""
        var session = "10086"

        var map = mutableMapOf<String,String>()
        map["app_id"] = appid
        map["time_stamp"] = timestamp
        map["nonce_str"] = nounce
        map["session"] = session
        map["question"] = msg
        sign = buildSign(map)
        map["sign"] = sign
        return map
    }

    fun buildSign(map:MutableMap<String,String>):String{
        var sortedMap = map.toSortedMap()
        var sign = ""
        for(key in sortedMap.keys)
        {
            if(sortedMap[key] != "")
            {
                sign += key+"="+ URLEncoder.encode(sortedMap[key],"UTF-8")+"&"
            }
        }
        sign += "app_key=$appkey"
        sign = encode(sign).toUpperCase()
        return sign
    }

    fun encode(password: String): String {
        try {
            val  instance: MessageDigest = MessageDigest.getInstance("MD5")//获取md5加密对象
            val digest:ByteArray = instance.digest(password.toByteArray())//对字符串加密，返回字节数组
            var sb : StringBuffer = StringBuffer()
            for (b in digest) {
                var i :Int = b.toInt() and 0xff//获取低八位有效值
                var hexString = Integer.toHexString(i)//将整数转化为16进制
                if (hexString.length < 2) {
                    hexString = "0$hexString"//如果是一位的话，补0
                }
                sb.append(hexString)
            }
            return sb.toString()

        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }

        return ""
    }

    data class ChatReply(val ret:String,val msg:String,val data:Data)
    data class Data(val session:String, val answer:String)

    fun AutoChat(msg:String):String
    {
        var param = buildMsg(msg)
        val url = "https://api.ai.qq.com/fcgi-bin/nlp/nlp_textchat"
        var replymsg = getDataByPost(param,url).toString()
        val reply = Gson().fromJson(replymsg, ChatReply::class.java)
        return reply.data.answer
    }
}

