package com.luo.mq.zmq;

import org.zeromq.ZMQ;

/**
 *========不推荐的写法，见mspoller==========
 * 一个客户端同时PULL和SUBSCRIBE
 * 这种方式的缺点之一是，在收到第一条消息之前会有1毫秒的延迟，这在高压力的程序中还是会构成问题的。此外，你还需要翻阅诸如nanosleep()的函数，不会造成循环次数的激增。
 * @author hui.luo
 *
 */
public class MsReader {
	public static void main(String[] args) throws Exception {
		ZMQ.Context context = ZMQ.context(1);
		
		ZMQ.Socket receiver = context.socket(ZMQ.PULL);
		receiver.connect("tcp://localhost:5557");
		
		ZMQ.Socket subscriber = context.socket(ZMQ.SUB);
		subscriber.connect("tcp://localhost:5556");
		subscriber.subscribe("10001 ".getBytes());
		
		 while (!Thread.currentThread ().isInterrupted ()) {
			 byte[] task;
			 while((task=receiver.recv(ZMQ.DONTWAIT))!=null){
				 System.out.println("process task");
			 }
			 byte[] update;
			 while ((update = subscriber.recv(ZMQ.DONTWAIT)) != null) {
                System.out.println("process weather update");
			 }
			 Thread.sleep(1);
		 }
		 receiver.close();
         subscriber.close();
         context.term ();
	}
}
