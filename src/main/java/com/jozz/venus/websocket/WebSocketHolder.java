package com.jozz.venus.websocket;

import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class WebSocketHolder {
    /**
     * 用于缓存用户的会话，value之所以是一个集合，是为了保存同一个用户多终端登录的会话
     */
    private static Map<Integer, Set<WebSocket>> holder = new ConcurrentHashMap<>();
    /**
     * 计数器，用于统计当前登录用户会话数
     */
    public static AtomicInteger counter = new AtomicInteger();

    /**
     * 存储session
     * @param sid
     * @param session
     */
    public static void put(Integer sid, WebSocket session) {
        Set<WebSocket> sessions = holder.getOrDefault(sid, new HashSet<>());
        if (sessions.size() == 0) {
            holder.put(sid, sessions);
        }
        sessions.add(session);
        // 计数
        int c = counter.incrementAndGet();
//        log.info("用户{}登录,当前在线会话为: {}", sid, c);
    }

    /**
     * 获取session
     * <p>
     * 1. sid不为空，则获取指定用户；<br/>
     * 2. sid为空，则获取所有登录用户；<br/>
     * </p>
     * @param sid
     * @return
     */
    public static Set<WebSocket> get(Integer sid) {
        Set<WebSocket> set = new HashSet<>();
        if (sid == null) {
            // sid标识为空
            holder.values().forEach(s -> set.addAll(s));
        } else {
            // sid不为空
            if (holder.containsKey(sid)) {
                set.addAll(holder.get(sid));
            }
        }
        return set;
    }


    /**
     * 移除session
     * @param sid
     */
    public static void remove(Integer sid, WebSocket socket) {
        Set<WebSocket> sockets = holder.get(sid);
        socket.close();
        sockets.remove(socket);
        if (sockets.size() == 0) {
            holder.remove(sid);
        }
        int c = counter.decrementAndGet();
//        log.info("用户{}退出,当前在线会话为: {}", sid, c);
    }

    /**
     * 获得此时的
     * 在线人数
     *
     * @return
     */
    public static int getOnlineCount() {
        return WebSocketHolder.counter.get();
    }
}
