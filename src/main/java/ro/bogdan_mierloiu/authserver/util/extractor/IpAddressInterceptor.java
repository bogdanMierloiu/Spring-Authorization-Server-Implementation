package ro.bogdan_mierloiu.authserver.util.extractor;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import ro.bogdan_mierloiu.authserver.util.constant.IpAddressUtils;

@Component
public class IpAddressInterceptor {


    public String getIpFromHeader(HttpServletRequest request) {
        final String xfHeader = getHeaderFromRequest(request);

        if (xfHeader == null || xfHeader.isEmpty()) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }

    private String getHeaderFromRequest(HttpServletRequest request) {
        for (var value : IpAddressUtils.POSSIBLE_IP_HEADERS) {
            if (request.getHeader(value) != null) {
                return request.getHeader(value);
            }
        }
        return null;
    }

}
