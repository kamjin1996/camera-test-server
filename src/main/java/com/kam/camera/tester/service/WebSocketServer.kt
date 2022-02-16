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
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import javax.websocket.*

@Component
@ServerEndpoint("/websocket/{uid}")
class WebSocketServer {

    val log: Logger = LoggerFactory.getLogger(this.javaClass)

    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    lateinit var session: Session

    //接收uid
    lateinit var uid: String

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    fun onOpen(session: Session, @PathParam("uid") uid: String) {
        this.session = session
        this.uid = uid

        UidServersHolder.add(uid, this)
        log.info("【uid: ${uid} 加入】当前在线人数为${UidServersHolder.countOnline()}")
        SocketResponseType.ConnectSuccess.response("连接成功", uid)
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    fun onClose(session: Session, @PathParam("uid") uid: String) {
        UidServersHolder.remove(uid)
        log.info("【uid: ${uid} 离线】当前在线人数为${UidServersHolder.countOnline()}")
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
        SocketRequestType.valueOf(type).handle(request)
    }

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

    fun sendMessageJson(type: SocketResponseType, data: Any) {
        SocketResponse()
            .apply {
                this.type = type.name
                this.data = data
            }.also { sendMessage(JSON.toJSONString(it)) }
    }

    @OnError
    fun onError(session: Session?, error: Throwable) {
        log.error("发生错误", error)
    }

    companion object {

        private val log: Logger = LoggerFactory.getLogger(WebSocketServer::javaClass.name)

        /**
         * 群发自定义消息
         */
        @Throws(IOException::class)
        fun sendInfo(message: String, @PathParam("uid") uid: String?) {
            log.info("推送消息到窗口$uid，推送内容:$message")
            if (uid == null) {
                UidServersHolder.webSocketMap.values.forEach { it.sendMessage(message) }
            } else {
                UidServersHolder.webSocketMap[uid]?.sendMessage(message)
            }
        }

    }

}