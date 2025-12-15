import { ref, onUnmounted } from 'vue'
import SockJS from 'sockjs-client'
import Stomp from 'stompjs'
import { ElMessage } from 'element-plus'

let stompClient = null
let isConnected = ref(false)
let messageCallbacks = []

// 连接WebSocket
const connect = () => {
  if (isConnected.value) return
  
  try {
    const token = localStorage.getItem('token')
    if (!token) {
      ElMessage.error('请先登录')
      return
    }
    
    const socket = new SockJS('/api/ws')
    stompClient = Stomp.over(socket)
    
    // 设置连接头，包含JWT token
    const headers = {
      Authorization: `Bearer ${token}`
    }
    
    stompClient.connect(headers, () => {
      isConnected.value = true
      ElMessage.success('WebSocket连接成功')
      
      // 订阅自己的消息队列
      const userId = JSON.parse(localStorage.getItem('user')).id
      stompClient.subscribe(`/queue/messages/${userId}`, (message) => {
        const parsedMessage = JSON.parse(message.body)
        // 调用所有注册的消息回调
        messageCallbacks.forEach(callback => callback(parsedMessage))
      })
      
      // 可以在这里添加更多订阅，比如系统通知、好友请求等
    }, (error) => {
      isConnected.value = false
      ElMessage.error('WebSocket连接失败: ' + error)
      console.error('WebSocket连接失败:', error)
    })
  } catch (error) {
    isConnected.value = false
    ElMessage.error('WebSocket连接异常: ' + error)
    console.error('WebSocket连接异常:', error)
  }
}

// 断开WebSocket连接
const disconnect = () => {
  if (stompClient && isConnected.value) {
    stompClient.disconnect()
    isConnected.value = false
    messageCallbacks = []
    ElMessage.info('WebSocket连接已断开')
  }
}

// 发送消息
const sendMessage = (destination, message) => {
  if (!stompClient || !isConnected.value) {
    ElMessage.error('WebSocket未连接')
    return false
  }
  
  try {
    stompClient.send(destination, {}, JSON.stringify(message))
    return true
  } catch (error) {
    ElMessage.error('发送消息失败: ' + error)
    console.error('发送消息失败:', error)
    return false
  }
}

// 注册消息回调
const registerMessageCallback = (callback) => {
  if (typeof callback === 'function') {
    messageCallbacks.push(callback)
  }
}

// 取消注册消息回调
const unregisterMessageCallback = (callback) => {
  const index = messageCallbacks.indexOf(callback)
  if (index !== -1) {
    messageCallbacks.splice(index, 1)
  }
}

// 组件卸载时断开连接
onUnmounted(() => {
  disconnect()
})

export const useWebSocket = () => {
  return {
    connect,
    disconnect,
    sendMessage,
    isConnected,
    registerMessageCallback,
    unregisterMessageCallback
  }
}
