package com.benbeehler.ignislang.runtime.server;

//import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
public class JettyHandler {

    //@Execute
    public void execute(int port) throws Exception {
        Runnable runnable = new Runnable() {
        	
            @Override
            public void run() {
                Server server = new Server(port);
                try {
                    server.getConnectors()[0].getConnectionFactory(HttpConnectionFactory.class);
                    server.setHandler(new HttpRequestHandler());

                    server.start();
                    server.join();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        
        new Thread(runnable).start();
    }
}