package cn.skyjilygao.util.request;

import com.alibaba.fastjson.JSON;
import lombok.Data;

import javax.servlet.http.HttpServletRequest;

/**
 * 网络请求时，获取ServletRequest信息
 *
 * @author skyjilygao
 * @date 20200821
 */
@Data
public class ServletRequestPojo {
    private StringBuffer requestUrl;
    private String requestUri;
    private String queryString;
    private String remoteAddr;
    private String remoteHost;
    private int remotePort;
    private String localAddr;
    private String localName;
    private String method;

    public ServletRequestPojo(HttpServletRequest request) {
        this.requestUrl = request.getRequestURL();
        this.requestUri = request.getRequestURI();
        this.queryString = request.getQueryString();
        this.remoteAddr = request.getRemoteAddr();
        this.remoteHost = request.getRemoteHost();
        this.remotePort = request.getRemotePort();
        this.localAddr = request.getLocalAddr();
        this.localName = request.getLocalName();
        this.method = request.getMethod();
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
