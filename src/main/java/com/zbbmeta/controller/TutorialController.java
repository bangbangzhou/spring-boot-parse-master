package com.zbbmeta.controller;

import com.zbbmeta.entity.Tutorial;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
public class TutorialController {




    @PostMapping("/save")
    //此处request对象就是通过Springmvc提供的参数解析器帮我们注入的
    public String saveTutorial(HttpServletRequest request){
        return "success";
    }



    @GetMapping("/tutorial")
    public Tutorial getTutorial(Tutorial tutorial){


        return tutorial;
    }
}