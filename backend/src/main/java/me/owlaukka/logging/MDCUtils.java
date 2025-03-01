package me.owlaukka.logging;

import org.slf4j.MDC;

public class MDCUtils {
    public static final String REQUEST_ID = "requestId";

    public static void setRequestId(String requestId) {
        MDC.put(REQUEST_ID, requestId);
    }

    public static void clearRequestId() {
        MDC.remove(REQUEST_ID);
    }
}
