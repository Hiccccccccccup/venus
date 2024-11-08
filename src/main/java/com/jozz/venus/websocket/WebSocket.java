package com.jozz.venus.websocket;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.jozz.venus.config.CustomSpringConfigurator;
import com.jozz.venus.domain.GroupMsg;
import com.jozz.venus.domain.GroupMsgPushOffset;
import com.jozz.venus.domain.Payload;
import com.jozz.venus.domain.PrivateMsgDelay;
import com.jozz.venus.mapper.GroupMsgDao;
import com.jozz.venus.mapper.GroupMsgPushOffsetDao;
import com.jozz.venus.mapper.PrivateMsgDelayDao;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;

/**
 * websocket的端点类
 * 作用相当于HTTP请求中的controller
 */
@Slf4j
@Component
@Scope("prototype")
@ServerEndpoint(value = "/ws/{userId}", configurator = CustomSpringConfigurator.class)
public class WebSocket {

    /**
     * 与客户端的连接会话，需要通过它来给客户端发送数据
     */
    private Session session;
    private Integer userId;

    @Autowired
    private PrivateMsgDelayDao privateMsgDelayDao;
    @Autowired
    private GroupMsgDao groupMsgDao;
    @Autowired
    private GroupMsgPushOffsetDao groupMsgPushOffsetDao;

    /**
     * 连接建立成
     * 功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") Integer userId) {
        this.session = session;
        this.userId = userId;
        //加入set中
        WebSocketHolder.put(userId, this);
        log.info("用户:" + userId + "上线,当前在线人数为:" + WebSocketHolder.getOnlineCount());
        sendMessage("登入成功~");

        //离线消息推送,包括私聊消息和群聊消息
        //私聊消息
        this.pullPrivateOfflineMsg(userId);
        //群聊消息
        this.pullGroupOfflineMsg(userId);
    }

    private void pullGroupOfflineMsg(Integer userId) {
        List<GroupMsg> offlineMsg = groupMsgPushOffsetDao.selectOfflineMsg(userId);
        if (!CollectionUtils.isEmpty(offlineMsg)) {
            offlineMsg.forEach(x -> {
                //推送
                sendMessage(x.getPayload());
                //推送完更新offset
                groupMsgPushOffsetDao.updateOffset(userId, x.getGroupId(), x.getId());
            });
        }
    }

    private void pullPrivateOfflineMsg(Integer userId) {
        List<PrivateMsgDelay> privateMsgDelays = privateMsgDelayDao.selectByUserId(Long.valueOf(userId));
        if (!CollectionUtils.isEmpty(privateMsgDelays)) {
            privateMsgDelays.forEach(x -> {
                //推送
                sendMessage(x.getPayload());
                //推送完删除
                privateMsgDelayDao.deleteById(x.getId());
            });
        }
    }

    /**
     * 连接关闭
     * 调用的方法
     */
    @OnClose
    public void onClose() {
        WebSocketHolder.remove(userId, this);
        log.info("用户:" + userId + "下线,当前在线人数为:" + WebSocketHolder.getOnlineCount());
    }

    /**
     * 收到客户端消
     * 息后调用的方法
     *
     * @param payload 客户端发送过来的消息
     **/
    @OnMessage
    public void onMessage(String payload, Session session) {
        log.info("用户消息:" + userId + ",报文:" + payload);
        //可以群发消息
        //消息保存到数据库、redis
        if (StringUtils.isNotBlank(payload)) {
            try {
                //解析发送的报文
                Payload payloadDto = JSON.parseObject(payload, Payload.class);
                //追加发送人(防篡改)
                payloadDto.setFromId(this.userId);
                //当场景为私聊时:toId为接收者的userId,当场景为群聊时:toId为群聊的groupId
                Integer toId = payloadDto.getToId();
                Integer type = payloadDto.getType();
                Integer messageId = null;
                List<Integer> userIds = new ArrayList<>();
                if (0 == type) {
                    //点对点发送
                    userIds.add(toId);
                } else {
                    //消息保存到数据库,并返回messageId
                    GroupMsg groupMsg = new GroupMsg();
                    groupMsg.setFromId(this.userId);
                    groupMsg.setGroupId(toId);
                    groupMsg.setPayload(payload);
                    groupMsg.setCreateTime(new Date());
                    groupMsgDao.insert(groupMsg);
                    messageId = groupMsg.getId();
                    //群发,根据toId作为群ID获取群成员id
                    userIds = this.getUserIdsByGroupId(toId);
                }
                //离线用户消息处理
                this.handleForOffline(JSON.toJSONString(payloadDto), toId, type, userIds);
                //在线用户消息处理
                this.handleForOnline(payloadDto, toId, type, messageId, userIds);
            } catch (Exception e) {
                e.printStackTrace();
                //todo 异常则保存payload到待处理表
            }
        }
    }

    private void handleForOnline(Payload payloadDto, Integer toId, Integer type, Integer messageId, List<Integer> userIds) {
        for (Integer userId : userIds) {
            Set<WebSocket> webSockets = WebSocketHolder.get(userId);
            if (!CollectionUtils.isEmpty(webSockets)) {
                //遍历发送
                for (WebSocket webSocket : webSockets) {
                    //跳过自己
                    if (webSocket != this) {
                        webSocket.sendMessage(JSON.toJSONString(payloadDto));
                    }
                    //群聊消息场景,则保存messageId和userId,toId(groupId)到群消息已推送offset表
                    if (1 == type) {
                        //先执行更新,如果没有更新到则执行插入
                        int rows = groupMsgPushOffsetDao.updateOffset(userId, toId, messageId);
                        //todo 该逻辑需要优化,可以在第一次入群时就插入一条记录,LastMsgId为t_group_msg表的maxId
                        if (rows == 0) {
                            GroupMsgPushOffset groupMsgPushOffset = new GroupMsgPushOffset();
                            groupMsgPushOffset.setGroupId(toId);
                            groupMsgPushOffset.setUserId(userId);
                            groupMsgPushOffset.setLastMsgId(messageId);
                            groupMsgPushOffset.setCreateTime(new Date());
                            groupMsgPushOffsetDao.insert(groupMsgPushOffset);
                        }
                    }
                }
            } else {
                //否则对应session不在当前服务器上，将message广播发送到MQ或者redis
                log.error("请求的userId:" + userIds + "不在该服务器上");
            }
        }
    }

    private void handleForOffline(String payload, Integer toId, Integer type, List<Integer> userIds) {
        //遍历userIds判断用户是否在线,可以使用redis bitmap判断是否在线
        Iterator<Integer> iterator = userIds.iterator();
        while (iterator.hasNext()) {
            if (!isOnline(iterator.next())) {
                if (0 == type) {
                    //todo 改为异步处理
                    this.savePrivateMessage(payload, this.userId, toId);
                }
                //移除不在线的用户
                iterator.remove();
            }
        }
    }


    /**
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("用户错误:" + this.userId, error);
    }

    private void savePrivateMessage(String payload, Integer userId, Integer toId) {
        //保存消息到数据库
        PrivateMsgDelay privateMsgDelay = new PrivateMsgDelay();
        privateMsgDelay.setFromId(userId);
        privateMsgDelay.setToId(toId);
        privateMsgDelay.setPayload(payload);
        privateMsgDelay.setCreateTime(new Date());
        privateMsgDelayDao.insert(privateMsgDelay);
    }

    public boolean isOnline(Integer userId) {
        return WebSocketHolder.get(userId).size() != 0;
        //todo 使用redis bitmap判断是否在线
//        return false;
    }

    public List<Integer> getUserIdsByGroupId(Integer groupId) {
        //todo 根据groupId获取群成员id
        return Lists.newArrayList(1,2,3);
    }

    /**
     * 实现服务
     * 器主动推送
     */
    public void sendMessage(String message) {
        try {
            this.session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            log.error("向用户userId={} sendMessage异常:", e);
            //异常则保存message到待推送表
        }
    }

    /**
     * 发送自定
     * 义消息
     **/
    public static void sendInfo(String message, Integer userId) {
        log.info("发送消息到:" + userId + "，报文:" + message);
        if (userId != null){
            Set<WebSocket> webSockets = WebSocketHolder.get(userId);
            webSockets.forEach(x->{
                x.sendMessage(message);
            });
        } else {
            log.error("用户" + userId + ",不在线！");
        }
    }

    public void close() {
        try {
            this.session.close();
        } catch (IOException e) {
            log.error("session 关闭失败: {}", e);
        }
    }
}


