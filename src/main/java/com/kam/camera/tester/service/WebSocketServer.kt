package com.kam.camera.tester.service

import com.alibaba.fastjson.JSON
import kotlin.Throws
import javax.websocket.server.ServerEndpoint
import javax.websocket.server.PathParam
import com.kam.camera.tester.bean.SocketResponseType
import com.kam.camera.tester.bean.SocketResponse
import java.io.IOException
import com.kam.camera.tester.bean.SocketRequest
import com.kam.camera.tester.bean.SocketRequestType
import com.kam.camera.tester.util.Base64Utils
import com.kam.camera.tester.util.FileUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.lang.Exception
import java.util.concurrent.ConcurrentHashMap
import javax.websocket.*
import kotlin.properties.Delegates

@ServerEndpoint("/websocket/{uid}")
@Component
class WebSocketServer {

    var log: Logger = LoggerFactory.getLogger(this.javaClass)

    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    lateinit var session: Session

    //接收uid
    private var uid = ""

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    fun onOpen(session: Session, @PathParam("uid") uid: String) {
        this.apply {
            this.session = session
            this.uid = uid
        }

        webSocketMap[uid] = this //加入set中

        log.info("uid: ${uid}加入 当前在线人数为${webSocketMap.size}")
        sendConnectSuccess()
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    fun onClose(session: Session) {
        webSocketMap.values.removeIf {
            val result: Boolean = it.session == session
            if (result) {
                log.info("uid: ${it.uid}连接关闭 当前在线人数为${webSocketMap.size}")
            }
            result
        }
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param jsonMessage 客户端发送过来的消息
     */
    @OnMessage
    fun onMessage(jsonMessage: String, session: Session) {
        val request = JSON.parseObject(jsonMessage, SocketRequest::class.java)
        val type = request.type ?: return
        when (SocketRequestType.valueOf(type)) {
            SocketRequestType.ImageBase64 -> {
                handleSavePhoto(request)
            }
            SocketRequestType.ImageClear ->
                // 删除本地图片文件
                handleSavedPhotoClear(request)
            SocketRequestType.Connect -> {}
        }
    }

    /**
     * 删除已经保存的所有图片
     *
     * @param request
     */
    private fun handleSavedPhotoClear(request: SocketRequest) {
        FileUtil.delAllFile("D:\\test\\")
        fileCount = 0
    }

    /**
     * 处理保存图片
     *
     * @param request
     */
    private fun handleSavePhoto(request: SocketRequest) {
        val base64: String? = (request.data as String?)?.replace("data:image/jpeg;base64,", "")
        val filepath = "D:\\test\\${fileCount}.jpeg"
        try {
            Base64Utils.Base64ToImage(base64, filepath)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        log.info("已存: ${fileCount++} 张")
    }

    private fun sendConnectSuccess() = sendMessageJson(SocketResponseType.ConnectSuccess, "连接成功")

    /**
     * 实现服务器主动推送
     */
    fun sendMessage(message: String?) {
        try {
            session.basicRemote.sendText(message)
        } catch (e: IOException) {
            log.error("websocket IO异常", e)
        }
    }

    /**
     * 响应给客户端已保存数量
     */
    private fun responseSavedPhoto() = sendMessageJson(SocketResponseType.ImageSavedCount, fileCount)

    private fun sendMessageJson(type: SocketResponseType, data: Any) {
        SocketResponse()
            .apply {
                this.type = type.name
                this.data = data
            }.also { sendMessage(JSON.toJSONString(it)) }
    }

    /**
     * @param session
     * @param error
     */
    @OnError
    fun onError(session: Session?, error: Throwable) {
        log.error("发生错误", error)
    }

    /**
     * 保存的文件数量
     */
    private var fileCount: Int by Delegates.observable(0, { property, oldValue, newValue ->
        //响应给服务器已存数量
        responseSavedPhoto()
    })

    companion object {

        var log: Logger = LoggerFactory.getLogger(WebSocketServer::javaClass.name)

        private val webSocketMap = ConcurrentHashMap<String, WebSocketServer>()

        /**
         * 群发自定义消息
         */
        @Throws(IOException::class)
        fun sendInfo(message: String, @PathParam("uid") uid: String?) {
            log.info("推送消息到窗口$uid，推送内容:$message")
            if (uid == null) {
                webSocketMap.values.forEach { it.sendMessage(message) }
            } else {
                webSocketMap[uid]?.sendMessage(message)
            }
        }

    }
}