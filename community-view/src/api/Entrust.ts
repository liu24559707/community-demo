import request from "@/request/request";

export const getEntrustListApi = (selected: number) => {
    return request.get(`/entrust/query?selected=${selected}`);
}

export const AcceptApi = (id: number) => {
    return request.post(`/entrust/accept/${id}`);
}

export const RefreshApi = (id: number) => {
    return request.get(`/entrust/refresh/${id}`);
}

export const addEntrustApi = (data: any) => {
    return request.post("/entrust/publisher",data);   
}

export const delApi = (id: number) => {
    return request.delete(`/entrust/del/${id}`);
}