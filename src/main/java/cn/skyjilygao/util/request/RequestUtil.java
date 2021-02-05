package cn.skyjilygao.util.request;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 允许IP访问的限制白名单，添加方法：
 * 	1. 可以直接在静态块指定也可以
 * 	2. 使用追加方法{@link RequestUtil#addWhiteList(String)}
 * @author skyjilygao
 * @date 20200617
 */
@Slf4j
public class RequestUtil {

	public static final Set<String> WHITE_LIST = new LinkedHashSet<>();
	public static final Set<String> SELF_IP_LIST = new LinkedHashSet<>();

	static {
		// eg:
		SELF_IP_LIST.add("127.0.0.1");
		// localhost
		SELF_IP_LIST.add("0:0:0:0:0:0:0:1");
	}

	/**
	 * 追加白名单
	 * @param ip
	 * @return
	 */
	public static Set<String> addWhiteList(String ip){
		if(StringUtils.isNotBlank(ip) || !WHITE_LIST.contains(ip)){
			WHITE_LIST.add(ip);
		}
		return WHITE_LIST;
	}

	private static Set<String> getWhiteList(){
		return WHITE_LIST;
	}
	/**
	 * 当前指定IP是否在白名单
	 *
	 * @param ip
	 * @return
	 */
	public static boolean isAllow(String ip) {
		if(CollectionUtils.isNotEmpty(WHITE_LIST) && !SELF_IP_LIST.contains(ip)){
			return WHITE_LIST.contains(ip);
		}
		return true;
	}

	/**
	 * 获取请求的用户ip
	 *
	 * @param request
	 * @return
	 */
	public static String getIPAddress(HttpServletRequest request) {
		String ip = null;
		String unknown = "unknown";
		//X-Forwarded-For：Squid 服务代理
		String ipAddresses = request.getHeader("X-Forwarded-For");
		if (ipAddresses == null || ipAddresses.length() == 0 || unknown.equalsIgnoreCase(ipAddresses)) {
			//Proxy-Client-IP：apache 服务代理
			ipAddresses = request.getHeader("Proxy-Client-IP");
		}

		if (ipAddresses == null || ipAddresses.length() == 0 || unknown.equalsIgnoreCase(ipAddresses)) {
			//WL-Proxy-Client-IP：weblogic 服务代理
			ipAddresses = request.getHeader("WL-Proxy-Client-IP");
		}

		if (ipAddresses == null || ipAddresses.length() == 0 || unknown.equalsIgnoreCase(ipAddresses)) {
			//HTTP_CLIENT_IP：有些代理服务器
			ipAddresses = request.getHeader("HTTP_CLIENT_IP");
		}

		if (ipAddresses == null || ipAddresses.length() == 0 || unknown.equalsIgnoreCase(ipAddresses)) {
			//X-Real-IP：nginx服务代理
			ipAddresses = request.getHeader("X-Real-IP");
		}

		//有些网络通过多层代理，那么获取到的ip就会有多个，一般都是通过逗号（,）分割开来，并且第一个ip为客户端的真实IP
		if (ipAddresses != null && ipAddresses.length() != 0) {
			ip = ipAddresses.split(",")[0];
		}

		//还是不能获取到，最后再通过request.getRemoteAddr();获取
		if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ipAddresses)) {
			ip = request.getRemoteAddr();
		}
		log.info("Request from ip -> " + ip);
		log.info("getRequestURL -> " + request.getRequestURL());
		log.info("getRequestURI -> " + request.getRequestURI());
		log.info("getQueryString -> " + request.getQueryString());
		return ip;
	}
}
