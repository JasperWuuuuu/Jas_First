package com.jas.takeaway.filter;

import com.alibaba.fastjson.JSON;
import com.jas.takeaway.common.BaseContext;
import com.jas.takeaway.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//檢查用戶是否完成登陸
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {
    //路徑匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        //進行向下強制轉型
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        //1 獲取本次請求的URI
        String requestURI = request.getRequestURI();
        log.info("攔截到請求：{}",requestURI);
        // 存放一些不需要處理的請求，直接放行
        String urls[] = new String[]{
                "/employee/login",
                "/employee/logout",
                //backend下頁面靜態資源（JS，CSS，圖片等）予以放行，只攔截controller的請求
                //（但是不包括index.html，所以要通過通配符來匹配）
                "/backend/**",
                "/front/**",
                "/user/sendMsg",  //移動端發送短信
                "/user/login"  //移動端登陸
        };
        //2 判斷本次請求是否需要處理
        boolean check = check(urls,requestURI);
        //3 如果不需要處理，則直接放行
        if (check){
            log.info("本次請求{}不需要處理",requestURI);
            filterChain.doFilter(request,response);
            return;
        }
        //4-1（後台員工系統） 請求需要處理，判斷登陸狀態，若已經登陸，則直接放行
        if(request.getSession().getAttribute("employee") != null){
            log.info("用戶已登陸，用戶id為:{}",request.getSession()
                    .getAttribute("employee") );

            Long empId = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);

            long id = Thread.currentThread().getId();
            log.info("線程id為：{}",id);

            filterChain.doFilter(request,response);
            return;
        }

        //4-2（移動端用戶） 請求需要處理，判斷登陸狀態，若已經登陸，則直接放行
        if(request.getSession().getAttribute("user") != null){
            log.info("用戶已登陸，用戶id為:{}",request.getSession()
                    .getAttribute("user") );

            Long userId = (Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);

            long id = Thread.currentThread().getId();
            log.info("線程id為：{}",id);

            filterChain.doFilter(request,response);
            return;
        }

        log.info("用戶未登陸");
        //5 請求需要處理，判斷登陸狀態，未登陸則返回未登陸結果，通過輸出流方式向客戶端響應數據
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }

    //路徑匹配，檢查此次請求是否需要放行
    public boolean check(String urls[],String requestURI){
        for(String url:urls){
            boolean match = PATH_MATCHER.match(url,requestURI);
            if(match){
                return true;
            }
        }
        return false;
    }
}
