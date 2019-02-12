/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arst.concprg.prodcons;

import java.util.Queue;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hcadavid
 */
public class Producer extends Thread {

    private Queue<Integer> queue = null;

    private int dataSeed = 0;
    private Random rand = null;
    private final long stockLimit;

    public Producer(Queue<Integer> queue, long stockLimit) {
        this.queue = queue;
        rand = new Random(System.currentTimeMillis());
        this.stockLimit = stockLimit;
    }

    @Override
    public void run() {
    	
        while (true) {
        	//int capacidadRestante = ((LinkedBlockingQueue) queue).remainingCapacity();
        	int capacidadRestante = queue.size();
        	if (capacidadRestante < stockLimit && capacidadRestante >= 0) {
        		dataSeed = dataSeed + rand.nextInt(100);
                System.out.println("Producer added " + dataSeed);
                queue.add(dataSeed);
        	} else {
        		synchronized (queue) {
        			try {
        				System.out.println("VOY A DETENERME PORQUE NO PUEDO PRODUCIR MÁS: "+capacidadRestante);
        				queue.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
        		}
        		
        	}
        }
    }
}
