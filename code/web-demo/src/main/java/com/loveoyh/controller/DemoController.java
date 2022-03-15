package com.loveoyh.controller;

import com.loveoyh.common.entity.ResultVO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/user")
public class DemoController {

    @PostMapping("login")
    public ResultVO login() {
        return ResultVO.createSuccess();
    }

}
