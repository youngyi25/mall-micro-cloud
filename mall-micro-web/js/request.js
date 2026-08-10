// 创建axios实例
  const request = axios.create({
      baseURL:'http://localhost:9000/api',// 后端请求地址
      timeout: 10000, // 请求超时时间

  });
 
  // 添加请求拦截器
  request.interceptors.request.use(function (config) {
    // 在发送请求之前做些什么，例如添加token等
    config.headers['Authorization'] = sessionStorage.getItem("token");
    return config;
  }, function (error) {
    // 对请求错误做些什么
    return Promise.reject(error);
  });
 
  // 添加响应拦截器
  request.interceptors.response.use(function (response) {
    console.log('[拦截器] 响应成功:', response.config.url, response.status);
    // 对响应数据做点什么
	try {
		var token = response.headers ? response.headers['authorization'] : null;
		// 兼容后端将 token 放在响应体的情况（跨域时自定义响应头可能丢失）
		if (!token && response.data && response.data.token) {
			token = response.data.token;
		}
		if(token && typeof token === 'string' && token.split('.').length === 3){
			sessionStorage.setItem("token",token)
			try {
				const strings = token.split('.')
				const payload = JSON.parse(decodeURIComponent(escape(window.atob(strings[1].replace(/-/g, '+').replace(/_/g, '/')))))
				// 合并：保留已有的userInfo字段（如email），JWT payload补充userId等
				let existing = {};
				try { existing = JSON.parse(sessionStorage.getItem("userInfo") || '{}'); } catch(e) {}
				const merged = { ...payload, ...existing };
				// 确保userId存在：优先取existing.userId，否则从payload.userId或subject转换
				if (!merged.userId) merged.userId = payload.userId || (payload.sub ? Number(payload.sub) : null);
				sessionStorage.setItem("userInfo", JSON.stringify(merged))
			} catch(e) {
				console.warn('token解析失败:', e);
			}
		}
	} catch(e) {
		console.warn('拦截器异常:', e);
	}
    return response.data;
  }, function (error) {
    return Promise.reject(error);
  });
 