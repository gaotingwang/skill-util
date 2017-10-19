//package com.gtw.util.transactional.factory;
//
//import java.lang.reflect.InvocationHandler;
//import java.lang.reflect.Method;
//import java.lang.reflect.Proxy;
//
//public class ProxyFactory {
//    @SuppressWarnings("unchecked")
//    public static <T> T create(Class<T> classzz) {
//        ImplProxy<T> proxy = new ImplProxy<>();
//        return (T) proxy.bind(classzz);
//    }
//
//    private static class ImplProxy<T> implements InvocationHandler {
//        private Object bind(Class<T> classzz) {
//            return Proxy.newProxyInstance(classzz.getClassLoader(), classzz.getInterfaces(),
//                    this);
//        }
//
//        @Override
//        public Object invoke(Object proxy, Method method, Object[] args)
//                throws Throwable {
//            return "这个东东能用吗？";
//        }
//    }
//}
