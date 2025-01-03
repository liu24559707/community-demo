import request from "@/request/request";

// 查看新闻类型列表
export const getNewsTypes = () => {
   return request.get("/news/listType");
}

// 查看新闻列表
export const getNewsList = (typeId: string,search: string, page: number, pageSize: number) => {
    return request.get("/news/listAll", {params: {typeId, search, page, pageSize} });
}

// 查看新闻详情
export const getNewsDetail = (nid: number) => {
    return request.get(`/news/queryDetail/${nid}`);
}