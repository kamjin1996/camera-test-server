package com.kam.camera.tester.service

import com.kam.camera.tester.bean.SocketRequest
import com.kam.camera.tester.bean.SocketResponseType.*
import com.kam.camera.tester.config.WebSocketConfig
import com.kam.camera.tester.util.Base64Utils
import com.kam.camera.tester.util.FileUtil
import com.kam.camera.tester.util.SpringContextHolder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.lang.Exception
import kotlin.properties.Delegates

/**
 * image handle
 */
class ImageHandleServer(ws: WebSocketServer) {

    private var uid: String = ws.uid

    init {
        UidServersHolder.add(ws.uid, this)
    }

    val log: Logger = LoggerFactory.getLogger(this::class.java)

    private lateinit var imageSaveDir: String

    /**
     * 保存的文件数量
     */
    private var fileCount: Int by Delegates.observable(0, { property, oldValue, newValue ->
        //响应给服务器已存数量
        ImageSavedCount.response(fileCount, this.uid)
    })

    private val subDir: Lazy<File> = lazy {
        this.imageSaveDir = SpringContextHolder.getBean(WebSocketConfig::class.java).imageSaveDir
        val path = imageSaveDir + "\\" + uid
        ImageSavePath.response(path, uid)
        File(path)
    }

    /**
     * 处理保存图片
     *
     * @param request
     */
    fun savePhoto(request: SocketRequest) {
        val imageBase64 = request.data as String? ?: return
        if (!subDir.value.exists()) {
            subDir.value.mkdirs()
        }
        val filepath = "$imageSaveDir\\$uid\\${fileCount}.${obtainImageSuffixByBase64(imageBase64)}"
        try {
            Base64Utils.base64ToImage(imageBase64, filepath)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        log.info("已存: ${fileCount++} 张")
    }

    /**
     * 删除已经保存的所有图片
     *
     * @param request
     */
    fun clearSavedPhoto(request: SocketRequest) {
        FileUtil.delAllFile(imageSaveDir)
        fileCount = 0
    }

    private fun obtainImageSuffixByBase64(imageBase64: String): String {
        return imageBase64.substringBefore(",")//data:image/jpeg;base64
            .substringBefore(";")//data:image/jpeg
            .substringAfter("/")//jpeg
    }
}