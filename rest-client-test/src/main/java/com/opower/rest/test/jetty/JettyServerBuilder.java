/**
 *    Copyright 2014 Opower, Inc.
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 **/
package com.opower.rest.test.jetty;

import org.eclipse.jetty.server.NCSARequestLog;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.RequestLogHandler;
import org.eclipse.jetty.webapp.WebAppContext;

/**
 * Utility class for creating a Jetty Server instance.
 */
public final class JettyServerBuilder {
    
    private JettyServerBuilder() {
        
    }
    
    /**
     * Initialize a Jetty server on the given port using the given web.xml descriptor.
     * @param port the port to listen on
     * @param descriptor the web.xml to use
     * @return the jetty Server instance. It still needs to be started.
     */
    public static Server initServer(int port, String descriptor) {
        Server s = new Server(port);
        HandlerCollection handlerCollection = new HandlerCollection();
        WebAppContext webAppContext = new WebAppContext();
        webAppContext.setDescriptor(descriptor);
        webAppContext.setResourceBase("src/test/resources");
        webAppContext.setContextPath("/");
        handlerCollection.addHandler(webAppContext);
        RequestLogHandler requestLogHandler = new RequestLogHandler();
        requestLogHandler.setRequestLog(new NCSARequestLog());
        handlerCollection.addHandler(requestLogHandler);
        s.setHandler(handlerCollection);
        return s;
    }
}
