package com.synapse.metrics;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import io.prometheus.client.Counter;
import io.prometheus.client.exporter.HTTPServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class App {

	static final Counter requests = Counter.build()
			.name("requests_total").help("Total requests.").register();

	public static void main(String[] args) throws IOException {
		HttpServer server = HttpServer.create(new InetSocketAddress(7800), 0);
		server.createContext("/tick", new HttpHandler() {
			@Override
			public void handle(HttpExchange httpExchange) throws IOException {
				requests.inc();
				httpExchange.close();
			}
		});

		HTTPServer metricsServer = new HTTPServer(8085);
		System.out.println("HttpServer online");
	}
}
