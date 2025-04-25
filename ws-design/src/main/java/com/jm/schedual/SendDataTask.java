package com.jm.schedual;

import com.jm.facade.impl.DemoFacadeImpl;
import com.zhikuntech.realtime.ws.facade.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 定时推送数据到弹窗
 *
 * @author DAI
 */
@Component
public class SendDataTask {

    @Resource
    private DemoFacadeImpl demoFacade;


    @Scheduled(cron = "0/3 * * * * ?")
    public void sendData() {
        demoFacade.sendMessage();

    }


}
