package hello.exception.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.UUID;


@Slf4j
public class LogFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info(" logFilter init");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String requestURI = httpServletRequest.getRequestURI();
        String uuid = UUID.randomUUID().toString();
        DispatcherType dispatcherType = request.getDispatcherType();

        try {
            log.info("REQUEST : [{}][{}][{}]", uuid, dispatcherType, requestURI);
            chain.doFilter(request, response);
        }catch (Exception e){
            log.info("EXCEPTION : {}", e.getMessage());
            throw e;
        }finally {
            log.info("RESPONSE : [{}][{}][{}] \n",uuid, dispatcherType, requestURI);
        }
    }

    @Override
    public void destroy() {
        log.info(" logFilter destroy");
    }
}
