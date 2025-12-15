<template>
  <div class="login-container">
    <el-card class="login-card">
      <template #header>
        <div class="login-header">
          <h2>微信登录</h2>
        </div>
      </template>
      <el-form ref="loginForm" :model="loginForm" :rules="loginRules" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="loginForm.username" placeholder="请输入用户名或邮箱" ref="usernameRef"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="loginForm.password" :type="passwordVisible ? 'text' : 'password'" placeholder="请输入密码" ref="passwordRef"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="login" :loading="loading" style="width: 100%">登录</el-button>
        </el-form-item>
        <el-form-item>
          <el-button @click="register" style="width: 100%">注册新账号</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <!-- 注册对话框 -->
    <el-dialog v-model="registerDialogVisible" title="注册新账号">
      <el-form ref="registerForm" :model="registerForm" :rules="registerRules" label-width="100px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="registerForm.username" placeholder="请输入用户名"></el-input>
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="registerForm.email" placeholder="请输入邮箱"></el-input>
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="registerForm.phone" placeholder="请输入手机号"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="registerForm.password" type="password" placeholder="请输入密码"></el-input>
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="registerForm.confirmPassword" type="password" placeholder="请确认密码"></el-input>
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="registerForm.nickname" placeholder="请输入昵称"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="registerDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitRegister">注册</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import axios from '../utils/axios'
import { useRouter } from 'vue-router'

const router = useRouter()
const loading = ref(false)
const registerDialogVisible = ref(false)
const usernameRef = ref(null)
const passwordRef = ref(null)
const passwordVisible = ref(true)

// 登录表单
const loginForm = reactive({
  username: 'testuser2',
  password: 'test123'
})

onMounted(async () => {
  await nextTick()
  loginForm.username = 'testuser2'
  loginForm.password = 'test123'
  try {
    usernameRef.value && usernameRef.value.focus && usernameRef.value.focus()
  } catch {}
})

// 登录表单验证规则
const loginRules = {
  username: [{ required: true, message: '请输入用户名或邮箱', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

// 注册表单
const registerForm = reactive({
  username: '',
  email: '',
  phone: '',
  password: '',
  confirmPassword: '',
  nickname: ''
})

// 注册表单验证规则
const registerRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号格式', trigger: 'blur' }
  ],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }, { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: (rule, value, callback) => {
        if (value !== registerForm.password) {
          callback(new Error('两次输入密码不一致'))
        } else {
          callback()
        }
      }, trigger: 'blur' }
  ],
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }]
}

// 登录
const login = async () => {
  loading.value = true
  try {
    const response = await axios.post('/api/auth/login', loginForm)
    const { token, user } = response.data
    
    // 保存token和用户信息到localStorage
    localStorage.setItem('token', token)
    localStorage.setItem('user', JSON.stringify(user))
    
    ElMessage.success('登录成功')
    router.push('/chat')
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '登录失败')
  } finally {
    loading.value = false
  }
}

// 注册
const submitRegister = async () => {
  try {
    const response = await axios.post('/api/auth/register', registerForm)
    ElMessage.success('注册成功，请登录')
    registerDialogVisible.value = false
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '注册失败')
  }
}

// 打开注册对话框
const register = () => {
  registerDialogVisible.value = true
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f5f5f5;
  color: #213547;
}

.login-card {
  width: 100%;
  max-width: 420px;
}

.login-header {
  text-align: center;
}

@media (max-width: 768px) {
  .login-container {
    padding: 16px;
    align-items: flex-start;
  }
  .login-card {
    max-width: 100%;
  }
}

/* 修复在暗色根色设置下，Element Plus 输入框文字不可见问题 */
:deep(.el-input__inner) {
  color: #213547;
  caret-color: #213547;
  -webkit-text-fill-color: #213547;
  line-height: 1.2;
  letter-spacing: 0;
  transition: none;
}
:deep(.el-input__inner::placeholder) {
  color: #86909c;
}
:deep(.el-input__wrapper) {
  color: #213547;
  background-color: #ffffff;
  --el-input-text-color: #213547;
  --el-input-bg-color: #ffffff;
}
</style>
