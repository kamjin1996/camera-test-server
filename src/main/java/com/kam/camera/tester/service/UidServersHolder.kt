package com.kam.camera.tester.service

import java.util.concurrent.ConcurrentHashMap

class UidServersHolder {
    companion object {

        val webSocketMap = ConcurrentHashMap<String, WebSocketServer>()

        val imageHandleMap = ConcurrentHashMap<String, ImageHandleServer>()

        fun countOnline(): Int {
            return webSocketMap.size
        }

        fun remove(uid: String) {
            webSocketMap.remove(uid)
            imageHandleMap.remove(uid)
        }

        fun add(uid: String, ws: WebSocketServer) {
            webSocketMap[uid] = ws
        }

        fun add(uid: String, imageHandleServer: ImageHandleServer) {
            imageHandleMap[uid] = imageHandleServer
        }
    }
}