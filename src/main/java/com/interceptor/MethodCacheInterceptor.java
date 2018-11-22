package com.interceptor;

import com.util.RedisUtil;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;


public class MethodCacheInterceptor implements MethodInterceptor {
    private Logger logger = LoggerFactory.getLogger(MethodCacheInterceptor.class);
    private RedisUtil redisUtil;

    private List<String> targetNameList; //不加入缓存的service名称
    private List<String> methodNameList; //不加入缓存的方法名称
    private Long defaultCacheExpireTime; //缓存默认的过期时间
    private Long xxxRecordManagerTime;
    private Long xxxSetRecordManagerTime;

    /**
     * 初始化读取不需要加入缓存的类名和方法名称
     */
    public MethodCacheInterceptor() {
        try {
            //分割字符串
            String[] targetName = {};
            String[] methodName = {};

            //加载过期时间的设置
            defaultCacheExpireTime = 3600L;
            xxxRecordManagerTime = 60L;
            xxxSetRecordManagerTime = 60L;

            //创建list
            targetNameList = new ArrayList<String>(targetName.length);
            methodNameList = new ArrayList<String>(methodName.length);
            Integer maxLen = targetName.length > methodName.length ? targetName.length : methodName.length;

            //将不需要缓存的类型添加到list中
            for (int i = 0; i < maxLen; i++) {
                if (i < targetName.length) {
                    targetNameList.add(targetName[i]);
                }
                if (i < methodName.length) {
                    methodNameList.add(methodName[i]);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Object invoke(MethodInvocation invocation) throws Throwable {
        Object value = null;

        String targetName = invocation.getThis().getClass().getName();
        String methodName = invocation.getMethod().getName();

        logger.info("即将执行" + targetName + "." + methodName);
        //不需要缓存的内容
        if (!isAddCache(targetName, methodName)) {
            //执行方法返回结果
            return invocation.proceed();
        }
        Object[] arguments = invocation.getArguments();
        final String key = getCacheKey(targetName, methodName, arguments);
        logger.debug("redisKey:" + key);
        try {
            //判断是否有缓存
            if (redisUtil.exists(key)) {
                logger.info("缓存中存在key:" + key);
                return redisUtil.get(key);
            }
            //没有缓存写入缓存
            //执行方法
            logger.info("缓存不存在,从数据库中查询");
            value = invocation.proceed();
            if (value != null) {
                final String tkey = key;
                final Object tvalue = value;
                new Thread(new Runnable() {
                    public void run() {
                        if (tkey.startsWith("com.service.impl.xxxRecordManager")) {
                            redisUtil.set(tkey, tvalue, xxxRecordManagerTime);
                        } else if (tkey.startsWith("com.service.impl.xxxSetRecorderManager")) {
                            redisUtil.set(tkey, tvalue, xxxSetRecordManagerTime);
                        } else {
                            logger.info("成功为key" + key + "添加缓存");
                            redisUtil.set(tkey, tvalue, defaultCacheExpireTime);
                        }
                    }
                }).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (value == null) {
                return invocation.proceed();
            }
        }

        return value;
    }


    /**
     * 是否加入缓存
     *
     * @param targetName
     * @param methodName
     * @return
     */
    private Boolean isAddCache(String targetName, String methodName) {
        boolean flag = true;
        if (targetNameList.contains(targetName) || methodNameList.contains(methodName)) {
            flag = false;
        }
        return flag;
    }


    /**
     * 创建缓存key
     *
     * @param targetName
     * @param methodName
     * @param arguments
     * @return
     */
    private String getCacheKey(String targetName, String methodName, Object[] arguments) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(targetName).append("_").append(methodName);

        if ((arguments != null) && (arguments.length != 0)) {
            for (int i = 0; i < arguments.length; i++) {
                stringBuffer.append("_").append(arguments[i]);
            }
        }
        return stringBuffer.toString();
    }

    public void setRedisUtil(RedisUtil redisUtil) {
        this.redisUtil = redisUtil;
    }
}
