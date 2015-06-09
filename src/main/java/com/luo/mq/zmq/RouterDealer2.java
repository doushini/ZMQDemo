package com.luo.mq.zmq;

import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
/**
 * REQ==ROUTER/DEALER==REP（多个client请求，一个broker代理，多个worker响应）
 * 并使用内置的装置proxy代替Poller
 * 另外还有一些常用的装置：QUEUE装置应使用ROUTER/DEALER套接字、FORWARDER应使用SUB/PUB、STREAMER应使用PULL/PUSH
 * 本例子中，多线程之间的通信采用tcp，也也可以用inproc（进程内）协议通信
 * @author hui.luo
 *
 */
public class RouterDealer2 {

	public static void main(String[] args) {
		for (int i = 0; i < 5; i++) {
			new rrclient().start();
		}
		ZMQ.Context context = ZMQ.context(1);
		for (int i = 0; i < 5; i++) {
			new rrworker(context).start();
		}
		new rrbroker().start();
	}
	
	static class rrclient extends Thread{
		@Override
		public void run() {
			ZMQ.Context context = ZMQ.context(1);

			ZMQ.Socket requester = context.socket(ZMQ.REQ);
	        requester.connect("tcp://localhost:5559");
	        System.out.println("client launch and connect broker.");
	        long start = System.currentTimeMillis();
	        for (int request_nbr = 0; request_nbr < 2; request_nbr++) {
	        	requester.send("Hello", 0);
	            String reply = requester.recvStr(0);
	            System.out.println("Received reply " + request_nbr + " [" + reply + "]");
	        }
	        System.out.println("take time : "+(System.currentTimeMillis()-start)/1000);
	        requester.close();
	        context.term();
		}
	}
	static class rrworker extends Thread{
		private ZMQ.Context context;
		
		public rrworker(Context context) {
			super();
			this.context = context;
		}

		@Override
		public void run() {
			ZMQ.Socket responder = context.socket(ZMQ.REP);
			responder.connect("inproc://workers");
	        System.out.println("rrworker launch and connect broker.");

	        while (!Thread.currentThread().isInterrupted()) {
	            String string = responder.recvStr(0);
	            System.out.printf("Received request: [%s]\n", string);
	            try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
	            responder.send("World");
	        }
	        
	        responder.close();
	        context.term();
		}
	}
	
	static class rrbroker extends Thread{
		@Override
		public void run() {
			ZMQ.Context context = ZMQ.context(1);

			ZMQ.Socket frontend = context.socket(ZMQ.ROUTER);
			ZMQ.Socket backend  = context.socket(ZMQ.DEALER);
	        frontend.bind("tcp://*:5559");
	        backend.bind("inproc://workers");
	        System.out.println("broker launch and bind");
	        
	        ZMQ.proxy (frontend, backend, null);
	        
	        frontend.close();
	        backend.close();
	        context.term();
		}
	}
}
