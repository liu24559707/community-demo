import request from "@/request/request";

export const findAll = () => {
  return request.get("/complain/find");  
}

export const add = (data: any) => {
  return request.post("/complain/publishComplain", data);
}
