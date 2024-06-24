package ro.bogdan_mierloiu.authserver.util.constant;

import java.util.List;

public final class IpAddressUtils {

    public static final List<String> POSSIBLE_IP_HEADERS = List.of(
            "X-Forwarded-For",
            "HTTP_FORWARDED",
            "HTTP_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_CLIENT_IP",
            "HTTP_VIA",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "REMOTE_ADDR");

    public static final String IP_ADDRESS_BLOCKED_60 = "Ip address blocked for 60 minutes after too many failed attempts";
    public static final String IP_ADDRESS_BLOCKED_15 = "Ip address blocked for 15 minutes after too many failed attempts";

    public static final int MAX_ATTEMPT = 15;
    public static final int FIRST_BLOCKED_PERIOD = 15;
    public static final int SECOND_BLOCKED_PERIOD = 60;
    public static final int RECENT_ACTIONS_PERIOD = 10;

    private IpAddressUtils() {
    }
}
