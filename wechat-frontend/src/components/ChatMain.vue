<template>
  <div class="chat-main">
    <!-- 左侧好友列表 -->
    <div class="friends-list">
      <div class="list-header">
        <h3>微信</h3>
        <el-input
          v-model="searchText"
          placeholder="搜索好友"
          :prefix-icon="Search"
          size="small"
        ></el-input>
      </div>
      <div class="list-content">
        <div
          v-for="friend in friends"
          :key="friend.id"
          class="friend-item"
          :class="{ active: selectedFriend && selectedFriend.id === friend.id }"
          @click="selectFriend(friend)"
        >
          <el-avatar :size="50" :src="friend.avatar || ''">{{ initialChar(friend) }}</el-avatar>
          <div class="friend-info">
            <div class="friend-name">{{ friend.nickname }}</div>
            <div class="last-message">{{ friend.lastMessage || '暂无消息' }}</div>
          </div>
          <div class="message-time">{{ friend.lastMessageTime || '' }}</div>
        </div>
      </div>
    </div>

    <!-- 中间聊天窗口 -->
    <div class="chat-window" v-if="selectedFriend">
      <div class="chat-header">
        <el-avatar :size="40" :src="selectedFriend.avatar || ''">{{ initialChar(selectedFriend) }}</el-avatar>
        <div class="chat-title">{{ selectedFriend.nickname }}</div>
        <div class="chat-actions">
          <el-button type="text" :icon="VideoCamera"></el-button>
          <el-button type="text" :icon="Phone"></el-button>
          <el-button type="text" :icon="MoreFilled"></el-button>
        </div>
      </div>
      
      <div class="chat-content">
        <div
          v-for="(message, i) in messages"
          :key="message.id ?? i"
          class="message-item"
          :class="{ 'sent': message.sender.id === user.id, 'received': message.sender.id !== user.id }"
        >
          <el-avatar :size="30" :src="message.sender.avatar || ''">{{ initialChar(message.sender) }}</el-avatar>
          <div class="message-content">{{ message.content }}</div>
          <div class="message-time">{{ formatTime(message.timestamp) }}</div>
        </div>
      </div>
      
      <div class="chat-input">
        <el-button type="text" :icon="Plus"></el-button>
        <el-button type="text" :icon="Picture"></el-button>
        <el-input
          v-model="inputMessage"
          type="textarea"
          placeholder="输入消息"
          resize="none"
          @keyup.enter="sendMessage"
        ></el-input>
        <el-button type="primary" @click="sendMessage" :icon="Promotion">发送</el-button>
      </div>
    </div>
    
    <!-- 右侧用户信息 -->
    <div class="user-info" v-if="selectedFriend">
      <div class="info-header">
        <h3>资料</h3>
      </div>
      <div class="info-content">
        <el-avatar :size="100" :src="selectedFriend.avatar || ''">{{ selectedFriend.nickname.charAt(0) }}</el-avatar>
        <div class="info-name">{{ selectedFriend.nickname }}</div>
        <div class="info-username">账号: {{ selectedFriend.username }}</div>
        <div class="info-email">邮箱: {{ selectedFriend.email }}</div>
        <div class="info-phone">手机: {{ selectedFriend.phone }}</div>
        <div class="info-actions">
          <el-button type="primary" icon="el-icon-user">查看资料</el-button>
          <el-button type="success" icon="el-icon-phone-outline">语音通话</el-button>
          <el-button type="warning" icon="el-icon-video-camera">视频通话</el-button>
          {{ value }}
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Search, VideoCamera, Phone, MoreFilled, Plus, Picture, Promotion } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import axios from '../utils/axios'
import { ElMessage } from 'element-plus'
import { useWebSocket } from '../composables/useWebSocket'
//备注说明
const value = ref("")
const router = useRouter()
const searchText = ref('')
const inputMessage = ref('')
const selectedFriend = ref(null)
const user = ref(JSON.parse(localStorage.getItem('user')))
const friends = ref([])
const messages = ref([])

// 初始化WebSocket连接
const { connect, disconnect } = useWebSocket()

// 格式化时间
const formatTime = (timestamp) => {
  if (!timestamp) return ''
  const date = new Date(timestamp)
  return `${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`
}

// 选择好友
const selectFriend = async (friend) => {
  selectedFriend.value = friend
  try {
    ElMessage.success(`已选择好友：${friend.nickname || friend.username}`)
  } catch {}
  // 获取聊天记录
  await fetchMessages(friend.id)
}

// 获取聊天记录
const fetchMessages = async (friendId) => {
  try {
    const response = await axios.get(`/api/messages/history?receiverId=${friendId}`)
    messages.value = response.data
  } catch (error) {
    ElMessage.error('获取聊天记录失败')
  }
}

// 发送消息
const sendMessage = async () => {
  if (!inputMessage.value || !selectedFriend.value) return
  
  try {
    const message = {
      sender: { id: user.value.id },
      receiver: { id: selectedFriend.value.id },
      content: inputMessage.value,
      type: 'TEXT',
      timestamp: new Date(),
      isRead: false
    }
    
    // 通过WebSocket发送消息
    await axios.post('/api/messages/send', message)
    
    inputMessage.value = ''
    await fetchMessages(selectedFriend.value.id)
  } catch (error) {
    ElMessage.error('发送消息失败')
  }
}

// 获取好友列表
const fetchFriends = async () => {
  try {
    const response = await axios.get('/api/friends/list')
    friends.value = response.data
  } catch (error) {
    ElMessage.error('获取好友列表失败')
    router.push('/')
  }
}

// 监听WebSocket消息
const handleWebSocketMessage = (message) => {
  messages.value.push(message)
  // 更新好友列表中的最后一条消息
  const friendIndex = friends.value.findIndex(f => f.id === message.sender.id || f.id === message.receiver.id)
  if (friendIndex !== -1) {
    friends.value[friendIndex].lastMessage = message.content
    friends.value[friendIndex].lastMessageTime = formatTime(message.timestamp)
  }
}

const initialChar = (obj) => {
  const s = (obj?.nickname || obj?.username || '').toString()
  return s ? s.charAt(0) : ''
}

onMounted(() => {
  if (!user.value) {
    router.push('/')
    return
  }
  
  fetchFriends()
  connect()
})
</script>

<style scoped>
.chat-main {
  display: flex;
  height: 100vh;
  background-color: #f5f5f5;
}

.friends-list {
  width: 300px;
  background-color: white;
  border-right: 1px solid #e0e0e0;
  display: flex;
  flex-direction: column;
}

.list-header {
  padding: 15px;
  border-bottom: 1px solid #e0e0e0;
}

.list-header h3 {
  margin: 0 0 10px 0;
}

.list-content {
  flex: 1;
  overflow-y: auto;
}

.friend-item {
  padding: 10px 15px;
  display: flex;
  align-items: center;
  cursor: pointer;
  border-bottom: 1px solid #f0f0f0;
}

.friend-item:hover {
  background-color: #f5f5f5;
}

.friend-item.active {
  background-color: #e3f2fd;
}

.friend-info {
  flex: 1;
  margin-left: 10px;
  min-width: 0;
}

.friend-name {
  font-weight: bold;
  margin-bottom: 5px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.last-message {
  font-size: 12px;
  color: #999;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.message-time {
  font-size: 12px;
  color: #999;
}

.chat-window {
  flex: 1;
  display: flex;
  flex-direction: column;
  background-color: white;
}

.chat-header {
  padding: 15px;
  border-bottom: 1px solid #e0e0e0;
  display: flex;
  align-items: center;
}

.chat-title {
  font-weight: bold;
  margin-left: 10px;
  flex: 1;
}

.chat-actions {
  margin-left: auto;
}

.chat-content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  background-color: #fafafa;
}

.message-item {
  display: flex;
  margin-bottom: 20px;
  align-items: flex-end;
}

.message-item.sent {
  flex-direction: row-reverse;
}

.message-item.received .message-content {
  background-color: white;
  border-radius: 10px 10px 10px 0;
}

.message-item.sent .message-content {
  background-color: #9eea6a;
  border-radius: 10px 10px 0 10px;
}

.message-content {
  max-width: 70%;
  padding: 10px 15px;
  margin: 0 10px;
  word-wrap: break-word;
}

.chat-input {
  padding: 15px;
  border-top: 1px solid #e0e0e0;
  display: flex;
  align-items: center;
  gap: 10px;
}

.chat-input .el-input {
  flex: 1;
}

.user-info {
  width: 300px;
  background-color: white;
  border-left: 1px solid #e0e0e0;
  display: flex;
  flex-direction: column;
}

.info-header {
  padding: 15px;
  border-bottom: 1px solid #e0e0e0;
}

.info-header h3 {
  margin: 0;
}

.info-content {
  padding: 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.info-name {
  font-size: 20px;
  font-weight: bold;
  margin: 10px 0;
}

.info-username, .info-email, .info-phone {
  color: #666;
  margin: 5px 0;
  text-align: center;
}

.info-actions {
  margin-top: 20px;
  display: flex;
  flex-direction: column;
  gap: 10px;
  width: 100%;
}

@media (max-width: 768px) {
  .chat-main {
    flex-direction: column;
  }
  .friends-list {
    width: 100%;
    height: 40vh;
    border-right: none;
    border-bottom: 1px solid #e0e0e0;
  }
  .chat-window {
    height: 60vh;
  }
  .user-info {
    display: none;
  }
  .list-header h3 {
    font-size: 16px;
  }
  .friend-item {
    padding: 8px 12px;
  }
  .chat-input {
    padding: 10px;
    gap: 8px;
  }
}
</style>
