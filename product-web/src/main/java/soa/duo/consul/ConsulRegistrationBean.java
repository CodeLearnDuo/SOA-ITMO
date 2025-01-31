package soa.duo.consul;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.agent.model.NewService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import lombok.extern.slf4j.Slf4j;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;

@Singleton
@Startup
@Slf4j
public class ConsulRegistrationBean {

    private ConsulClient consul;

    private String consulHost;
    private String serviceAddress;
    private int servicePort;
    private String serviceId;
    private static final String SERVICE_NAME = "wildfly-product-service";

    @PostConstruct
    private void init() {
        try {
            log.info("Initializing Consul client and registering service...");

            consulHost = lookup("java:/env/CONSUL_HOST");
            serviceAddress = lookup("java:/env/SERVICE_ADDRESS");
            servicePort = lookupInt("java:/env/SERVICE_PORT");

            String hostname = getHostName();
            serviceId = String.format("%s-%s-%d", SERVICE_NAME, hostname, servicePort);

            consul = new ConsulClient(consulHost);

            NewService newService = new NewService();
            newService.setId(serviceId);
            newService.setName(SERVICE_NAME);
            newService.setTags(Collections.singletonList("wildfly"));
            newService.setPort(servicePort);
            newService.setAddress(serviceAddress);

            NewService.Check serviceCheck = new NewService.Check();
            serviceCheck.setHttp("https://" + serviceAddress + ":" + servicePort + "/api/v1/products/health?status=check");
            serviceCheck.setInterval("10s");
            serviceCheck.setDeregisterCriticalServiceAfter("1m");
            serviceCheck.setTlsSkipVerify(true);

            newService.setCheck(serviceCheck);

            registerWithRetry(newService);

        } catch (Exception e) {
            log.error("Error registering service in Consul: ", e);
        }
    }

    @PreDestroy
    private void cleanup() {
        try {
            consul.agentServiceDeregister(serviceId);
            log.info("Service {} with ID={} deregistered from Consul", SERVICE_NAME, serviceId);
        } catch (Exception e) {
            log.error("Error deregistering service from Consul: ", e);
        }
    }

    /**
     * Механизм повторных попыток регистрации сервиса в Consul.
     */
    private void registerWithRetry(NewService newService) {
        int retries = 5;
        int waitTimeMs = 5000; 

        for (int i = 0; i < retries; i++) {
            try {
                consul.agentServiceRegister(newService);
                log.info("Service {} registered successfully in Consul after {} attempts",
                        SERVICE_NAME, i + 1);
                return;
            } catch (Exception e) {
                log.warn("Attempt {} to register service failed: {}", i + 1, e.getMessage());
                try {
                    Thread.sleep(waitTimeMs);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    log.error("Retry interrupted during service registration");
                    break;
                }
            }
        }

        log.error("Failed to register service in Consul after {} attempts", retries);
    }

    /**
     * Вспомогательный метод для получения строки из JNDI.
     */
    private String lookup(String jndiName) throws NamingException {
        InitialContext context = null;
        try {
            context = new InitialContext();
            Object value = context.lookup(jndiName);
            if (value instanceof String) {
                return (String) value;
            } else {
                throw new NamingException("Expected a String for JNDI name " + jndiName + ", but got " +
                        (value != null ? value.getClass().getName() : "null"));
            }
        } finally {
            if (context != null) {
                context.close();
            }
        }
    }

    /**
     * Вспомогательный метод для получения целого числа из JNDI.
     */
    private int lookupInt(String jndiName) throws NamingException {
        InitialContext context = null;
        try {
            context = new InitialContext();
            Object value = context.lookup(jndiName);
            if (value instanceof Integer) {
                return (Integer) value;
            } else if (value instanceof String) {
                return Integer.parseInt((String) value);
            } else {
                throw new NamingException("Expected an Integer or String for JNDI name " + jndiName + ", but got " +
                        (value != null ? value.getClass().getName() : "null"));
            }
        } finally {
            if (context != null) {
                context.close();
            }
        }
    }

    private String getHostName() {
        try {
            return InetAddress.getLocalHost().getHostName().replaceAll("[^a-zA-Z0-9.-]", "_");
        } catch (UnknownHostException e) {
            log.warn("Failed to get hostname. Using 'unknown-host' as fallback.");
            return "unknown-host";
        }
    }
}