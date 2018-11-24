package com.joham.springbootdemo.pathmatcher;

import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

/**
 * <p>
 * SpringMVC路径匹配规则AntPathMatcher
 * ? 匹配1个字符
 * * 匹配0个或多个字符
 * ** 匹配路径中的0个或多个目录
 * {spring:[a-z]+} 将正则表达式[a-z]+匹配到的值,赋值给名为 spring 的路径变量.(PS:必须是完全匹配才行,在SpringMVC中只有完全匹配才
 * 会进入controller层的方法)
 * </p>
 *
 * @author joham
 */
public class PathMatcherTest {
    public static void main(String[] args) {
        PathMatcher matcher = new AntPathMatcher();
        //请求路径
        String requestPath = "/customers/addresses/default/{addressId}";
        //路径匹配模式
        String patternPath = "/customers/**";
        boolean result = matcher.match(patternPath, requestPath);
        System.out.println(result);
    }
}
