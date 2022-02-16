package com.kam.camera.tester.util

import kotlin.Throws
import java.lang.Exception
import kotlin.jvm.JvmStatic
import sun.misc.BASE64Encoder
import sun.misc.BASE64Decoder
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

object Base64Utils {
    /**
     * 测试
     * @param args
     * @throws Exception
     */
    @Throws(Exception::class)
    @JvmStatic
    fun main(args: Array<String>) {

        //图片--->base64
        //本地图片
        val url = "C:/Users/Administrator/Desktop/1.png"
        val str = ImageToBase64ByLocal(url)
        println(str)

        //在线图片地址
        val string = "http://bpic.588ku.com//element_origin_min_pic/17/03/03/7bf4480888f35addcf2ce942701c728a.jpg"
        val ste = ImageToBase64ByOnline(string)
        println(ste)

        //base64--->图片
        base64ToImage(str, "C:/Users/Administrator/Desktop/test1.jpg")
        base64ToImage(ste, "C:/Users/Administrator/Desktop/test2.jpg")
    }

    /**
     * 本地图片转换成base64字符串
     * @param imgFile    图片本地路径
     * @return
     *
     * @author ZHANGJL
     * @dateTime 2018-02-23 14:40:46
     */
    fun ImageToBase64ByLocal(imgFile: String?): String { // 将图片文件转化为字节数组字符串，并对其进行Base64编码处理
        var `in`: InputStream? = null
        var data: ByteArray? = null

        // 读取图片字节数组
        try {
            `in` = FileInputStream(imgFile)
            data = ByteArray(`in`.available())
            `in`.read(data)
            `in`.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        // 对字节数组Base64编码
        val encoder = BASE64Encoder()
        return encoder.encode(data) // 返回Base64编码过的字节数组字符串
    }

    /**
     * 在线图片转换成base64字符串
     *
     * @param imgURL    图片线上路径
     * @return
     *
     * @author ZHANGJL
     * @dateTime 2018-02-23 14:43:18
     */
    fun ImageToBase64ByOnline(imgURL: String?): String {
        val data = ByteArrayOutputStream()
        try {
            // 创建URL
            val url = URL(imgURL)
            val by = ByteArray(1024)
            // 创建链接
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.connectTimeout = 5000
            val `is` = conn.inputStream
            // 将内容读取内存中
            var len = -1
            while (`is`.read(by).also { len = it } != -1) {
                data.write(by, 0, len)
            }
            // 关闭流
            `is`.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        // 对字节数组Base64编码
        val encoder = BASE64Encoder()
        return encoder.encode(data.toByteArray())
    }

    /**
     * base64字符串转换成图片
     * @param imgStr        base64字符串
     * @param imgFilePath    图片存放路径
     * @return
     *
     * @author ZHANGJL
     * @dateTime 2018-02-23 14:42:17
     */
    fun base64ToImage(imgStr: String?, imgFilePath: String?): Boolean { // 对字节数组字符串进行Base64解码并生成图片
        if (isEmpty(imgStr)) // 图像数据为空
            return false

        val substringAfter = imgStr?.substringAfter(",")

        val decoder = BASE64Decoder()
        return try {
            // Base64解码
            val b = decoder.decodeBuffer(substringAfter)
            for (i in b.indices) {
                if (b[i] < 0) { // 调整异常数据
                    b[i] = (b[i] + 256).toByte()
                }
            }
            val out: OutputStream = FileOutputStream(imgFilePath)
            out.write(b)
            out.flush()
            out.close()
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * 验证字符串是否为空
     *
     * @param input
     * @return
     */
    private fun isEmpty(input: String?): Boolean {
        return input == null || input == ""
    }
}