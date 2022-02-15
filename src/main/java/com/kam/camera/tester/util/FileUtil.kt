package com.kam.camera.tester.util

import org.slf4j.LoggerFactory
import kotlin.Throws
import java.lang.Exception
import sun.misc.BASE64Encoder
import sun.misc.BASE64Decoder
import java.io.*
import kotlin.jvm.JvmStatic

object FileUtil {
    private val log = LoggerFactory.getLogger(FileUtil::class.java)

    /**
     * 将文件转成base64 字符串
     *
     * @param path文件路径
     * @return *
     * @throws Exception
     */
    @Throws(Exception::class)
    fun encodeBase64File(path: String): String {
        val file = File(path)
        val inputFile = FileInputStream(file)
        val buffer = ByteArray(file.length().toInt())
        inputFile.read(buffer)
        inputFile.close()
        return BASE64Encoder().encode(buffer)
    }

    /**
     * 将字节流保存为文件
     *
     * @param targetFile
     * @param data
     * @throws Exception
     */
    fun byteToFile(targetFile: File?, data: ByteArray?) {
        if (data != null && data.isNotEmpty()) {
            try {
                FileOutputStream(targetFile).use { fos ->
                    fos.write(data, 0, data.size)
                    fos.flush()
                }
            } catch (e1: FileNotFoundException) {
                log.error("文件未找到", e1)
            } catch (e2: IOException) {
                log.error("文件写入失败", e2)
            }
        }
    }

    /**
     * 将base64字符解码保存文件
     *
     * @param base64Code
     * @param targetPath
     * @throws Exception
     */
    @Throws(Exception::class)
    fun decoderBase64File(base64Code: String?, targetPath: String) {
        val buffer = BASE64Decoder().decodeBuffer(base64Code)
        val out = FileOutputStream(targetPath)
        out.write(buffer)
        out.close()
    }

    /**
     * 将base64字符保存文本文件
     *
     * @param base64Code
     * @param targetPath
     * @throws Exception
     */
    @Throws(Exception::class)
    fun toFile(base64Code: String, targetPath: String) {
        val buffer = base64Code.toByteArray()
        val out = FileOutputStream(targetPath)
        out.write(buffer)
        out.close()
    }

    @JvmStatic
    fun main(args: Array<String>) {
        try {
            val base64Code = encodeBase64File("D:/0101-2011-qqqq.tif")
            println(base64Code)
            decoderBase64File(base64Code, "D:/2.tif")
            toFile(base64Code, "D:\\three.txt")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    /*************************删除文件夹delFolder / 删除文件夹中的所有文件delAllFile *start */
    /**
     * 删除文件夹
     * @param folderPath 文件夹完整绝对路径 ,"Z:/xuyun/save"
     */
    fun delFolder(folderPath: String) {
        try {
            delAllFile(folderPath) //删除完里面所有内容
            val myFilePath = File(folderPath)
            myFilePath.delete() //删除空文件夹
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 删除指定文件夹下所有文件
     * @param path 文件夹完整绝对路径 ,"Z:/xuyun/save"
     */
    fun delAllFile(path: String): Boolean {
        var flag = false
        val file = File(path)
        if (!file.exists()) {
            return flag
        }
        if (!file.isDirectory) {
            return flag
        }
        val tempList = file.list()
        if(tempList.isNullOrEmpty()){
            return true
        }
        var temp: File?
        for (i in tempList.indices) {
            temp = if (path.endsWith(File.separator)) {
                File(path + tempList[i])
            } else {
                File(path + File.separator + tempList[i])
            }
            if (temp.isFile) {
                temp.delete()
            }
            if (temp.isDirectory) {
                delAllFile(path + "/" + tempList[i]) //先删除文件夹里面的文件
                delFolder(path + "/" + tempList[i]) //再删除空文件夹
                flag = true
            }
        }
        return flag
    }
    /**************删除文件夹delFolder / 删除文件夹中的所有文件delAllFile *over */
}