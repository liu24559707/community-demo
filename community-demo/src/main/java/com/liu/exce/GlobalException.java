package com.liu.exce;

import com.liu.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;


/**
 * 全局异常处理器
 * 唯一性异常
 */
@ControllerAdvice(annotations = {RestController.class,Controller.class})
@ResponseBody
@Slf4j
public class GlobalException {
    /**
     * 违反数据库完整性约束的异常
     * 这里主要应用它的唯一性异常
     */
      @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public Result sqlIntegrityConstraintViolationException(SQLIntegrityConstraintViolationException e) {
          log.error("违反数据库完整性约束异常");
          log.error(e.getMessage());
          //如果为唯一性异常，则进行特别处理
          if (e.getMessage().contains("Duplicate entry")) {
              String[] split = e.getMessage().split(" ");
              String message = "该内容"+"已存在";
              if (split[2].length()<15) {
               message = split[2]+"已存在";
              }
              return Result.build(null,500,message);
          }
          return Result.build(null,500,e.getMessage());
      }
}
