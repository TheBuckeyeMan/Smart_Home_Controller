package net.smart.home.controller.Controller.GET;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RequestMapping("/prod")
@Slf4j
@RestController
public class GetController {
    
    @GetMapping("/testec2")
    public void testEc2(){
        log.info("The Endpoint /testec2 was called and is functioning as expected");
    }

    @GetMapping("/testpi")
    public void testPi(){
        log.info("Attempting to Trigger Rasberi Pi Device");
    }



}
