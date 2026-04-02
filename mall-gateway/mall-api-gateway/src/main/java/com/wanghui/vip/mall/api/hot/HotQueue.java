package com.wanghui.vip.mall.api.hot;

import org.springframework.stereotype.Component;

/**
 * Queue operation - Seckill feature removed
 */
@Component
public class HotQueue {

    // Not hot product
    public static final Integer NOT_HOT=0;
    // Already in queue
    public static final Integer HAS_QUEUE=204;
    // Queue success
    public static final Integer QUEUE_ING=200;

    /**
     * Rush order queue - Seckill feature removed
     * username: username
     * id: product ID
     * num: quantity
     */
    public int hotToQueue(String username,String id,Integer num){
        return NOT_HOT;
    }
}
