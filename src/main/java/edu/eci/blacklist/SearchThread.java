package edu.eci.blacklist;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class SearchThread extends Thread {
	
	public int iniSegmento;
	public int finSegmento;
	public String ipaddress;
	public HostBlacklistsDataSourceFacade skds;
	public int BLACK_LIST_ALARM_COUNT;
	public AtomicInteger ocurrencesCount;
	public AtomicInteger checkedListsCount;
	public LinkedBlockingQueue<Integer> blackListOcurrences;
	public int numHilo;
	
	public SearchThread(int iniSegmento, int finSegmento, String ipaddress, int numHilo, HostBlacklistsDataSourceFacade skds, int BLACK_LIST_ALARM_COUNT, AtomicInteger ocurrencesCount, AtomicInteger checkedListsCount, LinkedBlockingQueue<Integer> blackListOcurrences) throws InterruptedException {
		super("hilo "+numHilo);
		this.iniSegmento = iniSegmento;
		this.finSegmento = finSegmento;
		this.ipaddress = ipaddress;
		this.skds = skds;
		this.BLACK_LIST_ALARM_COUNT = BLACK_LIST_ALARM_COUNT;
		this.ocurrencesCount = ocurrencesCount;
		this.checkedListsCount = checkedListsCount;
		this.blackListOcurrences = blackListOcurrences;
		this.numHilo = numHilo;
	}	

	public void run() {			
		for (int i=iniSegmento; i<=finSegmento && ocurrencesCount.get()<BLACK_LIST_ALARM_COUNT; i++) {
			checkedListsCount.incrementAndGet();
            if (skds.isInBlackListServer(i, ipaddress)){
            	try {
					blackListOcurrences.put(i);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
            	ocurrencesCount.incrementAndGet();
            }
        }
	}
	
}