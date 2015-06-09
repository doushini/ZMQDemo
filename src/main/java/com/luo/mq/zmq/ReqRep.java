package com.luo.mq.zmq;

import org.zeromq.ZMQ;
public class ReqRep {

	public static void main(String[] args) {
		for (int i = 0; i < 5; i++) {
			new Request().start();
		}
		new Response().start();
	}
	
	static class Request extends Thread{
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
	static class Response extends Thread{
		@Override
		public void run() {
			//response不能多线程，否则端口占用
			for (int i = 0; i < 5; i++) {
				new Thread(new Runnable() {
					public void run() {
						ZMQ.Context context = ZMQ.context(1);
						ZMQ.Socket responder = context.socket(ZMQ.REP);
						responder.bind("tcp://localhost:5559");
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
				}).start();
			}
		}
	}
}
