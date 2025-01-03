import request from "@/request/request";

export const queryAllPaymentApi = () => {
    return request.get("/payment/query");
}

export const queryNoticeApi = () => {
    return request.get("/notice/queryAll");
}