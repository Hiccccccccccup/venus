package com.jozz.venus.config;

import com.jozz.venus.websocket.ApplicationContextProvider;
import org.springframework.context.annotation.Configuration;

import javax.websocket.server.ServerEndpointConfig;

@Configuration
public class CustomSpringConfigurator extends ServerEndpointConfig.Configurator {

    /**
     * Endpoint通过Spring实例化,解决@ServerEndpoint注解的类中引用的spring依赖注入失败的问题
     * @param clazz
     * @return
     * @param <T>
     * @throws InstantiationException
     */
    @Override
    public <T> T getEndpointInstance(Class<T> clazz) throws InstantiationException {
        return ApplicationContextProvider.getApplicationContext().getBean(clazz);
    }
}
