package com.kam.camera.tester.bean

class SocketRequest() {

    var type: String? = null
    var data: Any? = null
    var uid: String? = null
}

enum class SocketRequestType {

    ImageBase64,
    ImageClear, ;

}
