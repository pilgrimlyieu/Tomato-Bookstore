import axios from 'axios'

const instance = axios.create({
  baseURL: '/api', // 可配代理或实际接口地址
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 响应拦截（统一处理返回结构）
instance.interceptors.response.use(
  res => res.data,
  err => {
    console.error('[Axios Error]', err)
    return Promise.reject(err)
  }
)

export { instance as axios }
