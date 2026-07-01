
<template>
  <div class="chat-container" style="margin: 0 auto;width: 60%">
    <!-- 左侧联系人列表 -->
    <div class="contacts-panel">
      <div class="panel-header">
        <h3>联系人</h3>
      </div>
      <div class="contact-list">
        <div
          v-for="contact in contacts"
          :key="contact.id"
          class="contact-item"
          :class="{ active: currentContact.id === contact.id }"
          @click="selectContact(contact)"
        >
          <div class="avatar">
            <img :src="contact.avatar" :alt="contact.name" v-if="contact.avatar">
            <span v-else>{{ contact.name.charAt(0) }}</span>
          </div>
          <div class="contact-info">
            <div class="contact-name">{{ contact.name }}</div>
            <div class="last-message">{{ contact.lastMessage }}</div>
          </div>
          <div class="contact-status">
            <span class="status-indicator" :class="contact.status"></span>
          </div>
        </div>
      </div>
    </div>

    <!-- 右侧聊天区域 -->
    <div class="chat-panel">
      <div class="chat-header" v-if="currentContact.name">
        <div class="avatar">
          <img :src="currentContact.avatar" :alt="currentContact.name" v-if="currentContact.avatar">
          <span v-else>{{ currentContact.name.charAt(0) }}</span>
        </div>
        <div class="contact-details">
          <div class="contact-name">{{ currentContact.name }}</div>
          <div class="contact-status">{{ currentContact.status === 'online' ? '在线' : '离线' }}</div>
        </div>
      </div>

      <div class="chat-messages" ref="messageContainer" v-if="currentContact.name">
        <div
          v-for="message in messages"
          :key="message.id"
          class="message"
          :class="{ 'sent': message.sender === 'me', 'received': message.sender !== 'me' }"
        >
          <div class="message-content">
            {{ message.content }}
            <div class="message-time">{{ formatTime(message.timestamp) }}</div>
          </div>
        </div>
      </div>

      <div class="chat-input" v-if="currentContact.name">
        <input
          type="text"
          v-model="newMessage"
          placeholder="输入消息..."
          @keyup.enter="sendMessage"
        />
        <button @click="sendMessage">发送</button>
      </div>

      <!-- 默认提示 -->
      <div class="no-contact-selected" v-if="!currentContact.name">
        <p>请选择一个联系人开始聊天</p>
      </div>
    </div>
  </div>
</template>

<script>
import {mapState} from 'vuex'

export default {
  name: 'chat',
  computed: {
    ...mapState({
      currentUser: state => state.account.user
    })
  },
  data () {
    return {
      newMessage: '',
      currentContact: {},
      contacts: [],
      messages: []
    }
  },
  methods: {
    queryContacts () {
      this.$get(`/business/chat-record/contacts/user/${this.currentUser.userId}`).then((r) => {
        this.contacts = r.data.data.map(contact => ({
          id: contact.staffId,
          userId: contact.userId,
          name: contact.staffName,
          avatar: 'http://127.0.0.1:9527/imagesWeb/' + contact.staffImages.split(',')[0],
          lastMessage: contact.lastMessage,
          status: 'online'
        }))
      })
    },
    selectContact (contact) {
      console.log(contact)
      this.currentContact = contact
      // 这里可以加载与该联系人的历史消息
      this.loadMessages(contact.id, contact.userId)
    },
    loadMessages (hotelId, userId) {
      this.$get(`/business/chat-record/list`, {
        userId: userId,
        staffId: hotelId
      }).then((r) => {
        // 将后端返回的聊天记录转换为前端需要的格式
        this.messages = r.data.data.map(message => ({
          id: message.id,
          sender: message.senderType === '0' ? 'me' : message.staffId.toString(),
          content: message.content,
          timestamp: new Date(message.createTime)
        }))
        console.log(this.messages)
        // 滚动到底部
        this.$nextTick(() => {
          const container = this.$refs.messageContainer
          if (container) {
            container.scrollTop = container.scrollHeight
          }
        })
      })
    },
    sendMsg (teacherId, studentId) {
      if (this.newMessage.trim() === '') return
      this.$post('/business/chat-record', {
        staffId: teacherId,
        userId: studentId,
        senderType: 0,
        content: this.newMessage
      }).then((r) => {

      })
    },
    sendMessage () {
      if (this.newMessage.trim() === '') return

      // 先将消息显示在界面上
      const message = {
        id: this.messages.length + 1,
        sender: 'me',
        content: this.newMessage,
        timestamp: new Date()
      }

      this.messages.push(message)
      this.updateLastMessage(this.currentContact.id, this.newMessage)
      console.log(this.currentContact)
      // 发送消息到服务器
      this.sendMsg(this.currentContact.id, this.currentContact.userId)

      // 清空输入框
      this.newMessage = ''

      // 滚动到底部
      this.$nextTick(() => {
        const container = this.$refs.messageContainer
        if (container) {
          container.scrollTop = container.scrollHeight
        }
      })
    },

    updateLastMessage (contactId, message) {
      const contact = this.contacts.find(c => c.id === contactId)
      if (contact) {
        contact.lastMessage = message
      }
    },

    formatTime (timestamp) {
      const date = new Date(timestamp)
      const hours = date.getHours().toString().padStart(2, '0')
      const minutes = date.getMinutes().toString().padStart(2, '0')
      return `${hours}:${minutes}`
    }
  },
  mounted () {
    this.queryContacts()
    // 初始化时滚动到底部
    this.$nextTick(() => {
      const container = this.$refs.messageContainer
      if (container) {
        container.scrollTop = container.scrollHeight
      }
    })
  }
}
</script>

<style scoped>.chat-container {
  display: flex;
  height: calc(100vh - 230px);
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  overflow: hidden;
  width: 100%;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.contacts-panel {
  width: 320px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-right: 1px solid rgba(255, 255, 255, 0.2);
  display: flex;
  flex-direction: column;
}

.panel-header {
  padding: 24px 20px 20px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
}

.panel-header h3 {
  margin: 0;
  color: #2d3436;
  font-weight: 600;
  font-size: 18px;
  letter-spacing: 0.5px;
}

.contact-list {
  flex: 1;
  overflow-y: auto;
  padding: 8px 0;
}

.contact-item {
  display: flex;
  align-items: center;
  padding: 16px 20px;
  cursor: pointer;
  margin: 4px 12px;
  border-radius: 12px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

.contact-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
  opacity: 0;
  transition: opacity 0.3s ease;
}

.contact-item:hover {
  background: rgba(102, 126, 234, 0.08);
  transform: translateY(-1px);
}

.contact-item:hover::before {
  opacity: 1;
}

.contact-item.active {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.15) 0%, rgba(118, 75, 162, 0.15) 100%);
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.2);
}

.avatar {
  width: 48px;
  height: 48px;
  border-radius: 16px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: 600;
  font-size: 16px;
  margin-right: 16px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
  transition: transform 0.3s ease;
}

.contact-item:hover .avatar {
  transform: scale(1.05);
}

.avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.contact-info {
  flex: 1;
  overflow: hidden;
}

.contact-name {
  font-weight: 600;
  margin-bottom: 6px;
  color: #2d3436;
  font-size: 15px;
}

.last-message {
  font-size: 13px;
  color: #636e72;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  line-height: 1.4;
}

.contact-status {
  display: flex;
  align-items: center;
}

.status-indicator {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  display: inline-block;
  box-shadow: 0 2px 8px currentColor;
}

.status-indicator.online {
  background: linear-gradient(135deg, #00b894 0%, #00a085 100%);
  color: rgba(0, 184, 148, 0.3);
}

.status-indicator.offline {
  background: linear-gradient(135deg, #b2bec3 0%, #95a5a6 100%);
  color: rgba(178, 190, 195, 0.3);
}

.chat-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: rgba(255, 255, 255, 0.98);
  backdrop-filter: blur(20px);
}

.chat-header {
  display: flex;
  align-items: center;
  padding: 20px 24px;
  background: linear-gradient(135deg, #ffffff 0%, #f8f9fa 100%);
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
}

.chat-header .avatar {
  width: 52px;
  height: 52px;
  margin-right: 16px;
  border-radius: 18px;
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
}

.contact-details .contact-name {
  font-weight: 600;
  font-size: 17px;
  margin-bottom: 4px;
  color: #2d3436;
}

.contact-details .contact-status {
  font-size: 13px;
  color: #636e72;
  font-weight: 500;
}

.chat-messages {
  flex: 1;
  padding: 24px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  background: linear-gradient(135deg, #f8f9fa 0%, #ffffff 100%);
}

.message {
  max-width: 75%;
  margin-bottom: 20px;
  position: relative;
  animation: messageSlideIn 0.3s ease-out;
}

@keyframes messageSlideIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.message.sent {
  align-self: flex-end;
}

.message.received {
  align-self: flex-start;
}

.message-content {
  padding: 14px 18px;
  border-radius: 20px;
  word-wrap: break-word;
  position: relative;
  line-height: 1.5;
  font-size: 14px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  transition: transform 0.2s ease;
}

.message-content:hover {
  transform: translateY(-1px);
}

.message.sent .message-content {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-bottom-right-radius: 6px;
}

.message.received .message-content {
  background: linear-gradient(135deg, #ffffff 0%, #f8f9fa 100%);
  color: #2d3436;
  border-bottom-left-radius: 6px;
  border: 1px solid rgba(0, 0, 0, 0.06);
}

.message-time {
  font-size: 11px;
  text-align: right;
  margin-top: 8px;
  opacity: 0.8;
  font-weight: 500;
}

.chat-input {
  display: flex;
  padding: 20px 24px;
  background: linear-gradient(135deg, #ffffff 0%, #f8f9fa 100%);
  border-top: 1px solid rgba(0, 0, 0, 0.06);
  gap: 12px;
}

.chat-input input {
  flex: 1;
  padding: 14px 20px;
  border: 2px solid transparent;
  border-radius: 28px;
  outline: none;
  font-size: 14px;
  background: linear-gradient(135deg, #f8f9fa 0%, #ffffff 100%);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transition: all 0.3s ease;
}

.chat-input input:focus {
  border-color: #667eea;
  background: #ffffff;
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.2);
}

.chat-input button {
  padding: 14px 28px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 28px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 600;
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.3);
  transition: all 0.3s ease;
  letter-spacing: 0.5px;
}

.chat-input button:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 24px rgba(102, 126, 234, 0.4);
}

.chat-input button:active {
  transform: translateY(0);
}

.no-contact-selected {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f8f9fa 0%, #ffffff 100%);
}

.no-contact-selected p {
  color: #636e72;
  font-size: 16px;
  font-weight: 500;
  text-align: center;
  padding: 20px;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.05);
}

/* 滚动条美化 */
.contact-list::-webkit-scrollbar,
.chat-messages::-webkit-scrollbar {
  width: 6px;
}

.contact-list::-webkit-scrollbar-track,
.chat-messages::-webkit-scrollbar-track {
  background: transparent;
}

.contact-list::-webkit-scrollbar-thumb,
.chat-messages::-webkit-scrollbar-thumb {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 3px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .contacts-panel {
    width: 80px;
  }

  .contact-info, .contact-details {
    display: none;
  }

  .contact-item {
    justify-content: center;
    padding: 16px 10px;
    margin: 4px 8px;
  }

  .contact-item .avatar {
    margin-right: 0;
    width: 44px;
    height: 44px;
  }

  .panel-header h3 {
    font-size: 14px;
  }
}
</style>
