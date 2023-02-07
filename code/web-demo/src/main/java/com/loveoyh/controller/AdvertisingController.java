package com.loveoyh.controller;

import com.loveoyh.common.entity.Advertising;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 */
@Slf4j
@RestController
public class AdvertisingController {

    @PostMapping("/save")
    public Advertising saveProject(@RequestBody Advertising advertising) {
        log.info("获取内容"+ advertising);
        return advertising;
    }
}
