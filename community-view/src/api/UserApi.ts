import request from "@/request/request";

export const loginApi = (data: any) => {
    return request.post('/user/login', data)
}

export const getCodeApi = (phone: string) => {
    return request.get(`/user/getCode/${phone}`)
}

export const registerApi = (data: any) => {
    return request.post('/user/register', data)
}

export const loginOutApi = () => {
    return request.get('/user/loginOut')
}

export const getUserInfoApi = () => {
    return request.get('/user/getUserInfo')
}

export const updatePasswordApi = (data: any) => {
    return request.put('/user/updatePassword', data)
}

export const bindPhoneApi = (data: String) => {
    return request.post('/user/bindPhone', { phone: data })
}

export const changeBindApi = (data: any) => {
    return request.put('/user/changeBind', data)
}

export const delUserApi = () => {
    return request.delete('/user/delUser')
}