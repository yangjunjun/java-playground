package com.learn.reflection;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 动态代理：Proxy.newProxyInstance + InvocationHandler。
 * 运行时生成接口实现，统一做增强（日志、耗时、事务等 AOP 雏形）。
 * 注意：只能代理接口，代理类需要 CGLIB 等第三方库。
 */
public class ProxyReflect {

    public interface UserService {
        String findName(int id);

        void save(String name);
    }

    /** 真实实现 */
    static class UserServiceImpl implements UserService {
        @Override
        public String findName(int id) {
            return "用户" + id;
        }

        @Override
        public void save(String name) {
            // 保存逻辑（演示略）
        }
    }

    public static void demo() {
        UserService target = new UserServiceImpl();

        // InvocationHandler：拦截所有接口方法调用
        InvocationHandler handler = new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                System.out.println("  [代理] 调用前: " + method.getName()
                        + ", 参数=" + java.util.Arrays.toString(args));
                try {
                    Object result = method.invoke(target, args);
                    System.out.println("  [代理] 调用后: " + method.getName() + " -> " + result);
                    return result;
                } catch (java.lang.reflect.InvocationTargetException e) {
                    throw e.getCause();   // 解包真实异常，避免调用方看到包装异常
                }
            }
        };

        UserService proxy = (UserService) Proxy.newProxyInstance(
                UserService.class.getClassLoader(),
                new Class<?>[] { UserService.class },
                handler);

        System.out.println("代理类: " + proxy.getClass().getName());
        String name = proxy.findName(1001);
        System.out.println("业务得到: " + name);
        proxy.save("张三");
    }
}
