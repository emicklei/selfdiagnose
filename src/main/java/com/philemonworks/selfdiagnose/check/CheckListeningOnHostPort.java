package com.philemonworks.selfdiagnose.check;

import com.philemonworks.selfdiagnose.*;
import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.*;

public class CheckListeningOnHostPort extends PatternMatchingTask {
    private final static Logger log = Logger.getLogger(CheckListeningOnHostPort.class);

    private static final int TIMEOUT = 1000;
    private String url;
    private String hostName;
    private int port;
    private String description;

    private CheckListeningOnHostPort() {
    }

    public CheckListeningOnHostPort(String url, String description) {
        this.url = url;
        this.description = description;
        if (StringUtils.hasText(url)) {
            try {
                URI uri = new URI(url);
                if (uri.getHost() == null) {
                    uri = new URI("http://" + url);
                }
                if (uri.getPort() == -1) {
                    String scheme = uri.getScheme();
                    if (StringUtils.countOccurrencesOf(scheme, "https") > 0) {
                        port = 443;
                    } else if (StringUtils.countOccurrencesOf(scheme, "http") > 0) {
                        port = 80;
                    }
                } else {
                    port = uri.getPort();
                }
                    hostName = uri.getHost();
                } catch(URISyntaxException e){
                    log.error(String.format("Url %s given is not valid, Exception message is : %s", url, e.getMessage()));
                }
            }
        }

        @Override
        public String getDescription () {
            return "Check if hostname and port is reachable";
        }

        @Override
        public void run (ExecutionContext ctx, DiagnosticTaskResult result) throws DiagnoseException {
            Socket socket = new Socket();
            String template = DiagnoseUtil.format("Connection check [ {2} = {0}:{1}] ", hostName, "" + port, description);
            try {
                SocketAddress socketAddress = new InetSocketAddress(hostName, port);
                socket.connect(socketAddress, TIMEOUT);
            } catch (UnknownHostException e) {
                result.setFailedMessage(template + "Host cannot be found");
            } catch (SocketTimeoutException e) {
                result.setFailedMessage(template + "Timeout while trying to connect");
            } catch (IOException e) {
                result.setFailedMessage(template + "Port is closed");
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    log.error(String.format("Exception while trying to close socket connection with message : %s", e.getMessage()));
                }
                if (result.getStatus().equalsIgnoreCase(DiagnosticTaskResult.STATUS_UNKNOWN)) {
                    result.setPassedMessage(template + "passed");
                }
            }
        }
    }
