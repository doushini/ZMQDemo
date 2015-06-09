package com.luo.mq.zmq;

import org.zeromq.ZMQ;

/**
 * 一个客户端同时处理PULL和SUB
 * 
 * @author hui.luo
 *
 */
public class mspoller {

	public static void main(String[] args) {
		ZMQ.Context context = ZMQ.context(1);
		
		ZMQ.Socket receiver = context.socket(ZMQ.PULL);
		receiver.connect("tcp://localhost:5557");
		
		ZMQ.Socket subscriber = context.socket(ZMQ.SUB);
		subscriber.connect("tcp://localhost:5556");
		subscriber.subscribe("10001 ".getBytes());
		
        ZMQ.Poller items = new ZMQ.Poller(2);
        items.register(receiver, ZMQ.Poller.POLLIN);
        items.register(subscriber, ZMQ.Poller.POLLIN);
        
        while (!Thread.currentThread ().isInterrupted ()) {
            byte[] message;
            items.poll();
            if (items.pollin(0)) {
                message = receiver.recv(0);
                System.out.println("Process task");
            }
            if (items.pollin(1)) {
                message = subscriber.recv(0);
                System.out.println("Process weather update");
            }
        }
        receiver.close();
        subscriber.close();
        context.term();
	}

}
