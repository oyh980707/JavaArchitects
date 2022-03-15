package com.loveoyh.common.log;

import com.loveoyh.common.context.ActionContext;
import com.loveoyh.common.context.CommonRequestModel;
import com.loveoyh.common.entity.OperationLog;
import com.loveoyh.common.utils.JSONUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 *后续集成异步输出日志
 */
@Component
@Slf4j
public class LogStrategy  {
    private static Logger access_logger = LoggerFactory.getLogger("ACCESS_LOGGER");
    private static String HEARTBEAT_URL = "/heartbeat";

    public void doLog(ActionContext context) {
        //心跳检测不记录日志
        if(context.getUrl().contains(HEARTBEAT_URL)){
            return ;
        }
        OperationLog operationLog = new OperationLog();
        CommonRequestModel model = context.getModel();
        if(model==null){
            model = new CommonRequestModel();
        }
        operationLog.setIp(context.getIp());
        operationLog.setRequestPath(context.getUrl());
        operationLog.setOperationTime(new Date());
        operationLog.setTimeConsuming(String.valueOf(context.getTimeCost()));
        operationLog.setErrorMsg(context.getErrorMsg());
        operationLog.setResponseResult(context.getResponseResult());
        operationLog.setClient(context.getClient());
        operationLog.setClientParams(context.getClientParams());
        operationLog.setHeaderMap(context.getHeaders());

        // 输出日志
        access_logger.info(JSONUtils.toJSON(operationLog));

    }
}