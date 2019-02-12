package edu.eci.arsw.highlandersim;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class Immortal extends Thread {

	private ImmortalUpdateReportCallback updateCallback = null;

	private int health;

	private int defaultDamageValue;

	private final List<Immortal> immortalsPopulation;

	private final String name;

	private final Random r = new Random(System.currentTimeMillis());

	private boolean pause;

	private Immortal bloqueadoPor;

	public Immortal(String name, List<Immortal> immortalsPopulation, int health, int defaultDamageValue,
			ImmortalUpdateReportCallback ucb) {
		super(name);
		this.updateCallback = ucb;
		this.name = name;
		this.immortalsPopulation = immortalsPopulation;
		this.health = health;
		this.defaultDamageValue = defaultDamageValue;
		this.pause = false;
		this.bloqueadoPor = this;
	}

	public void run() {

		while (true) {
			if (pause) {
				synchronized (this) {
					try {
						this.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			Immortal im;

			int myIndex = immortalsPopulation.indexOf(this);

			int nextFighterIndex = r.nextInt(immortalsPopulation.size());

			// avoid self-fight
			if (nextFighterIndex == myIndex) {
				nextFighterIndex = ((nextFighterIndex + 1) % immortalsPopulation.size());
			}

			im = immortalsPopulation.get(nextFighterIndex);

			this.fight(im);

			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}

	}

	public void fight(Immortal i2) {
		if (i2.getHealth() > 0) {
			if (bloqueadoPor != i2) {
				synchronized (this) {
					synchronized (i2) {
						i2.setBloqueadoPor(this);
						i2.changeHealth(i2.getHealth() - defaultDamageValue);
						this.health += defaultDamageValue;
					}
				}
				i2.setBloqueadoPor(i2);
			} 
			else {
				synchronized (i2) {
					synchronized (this) {
						this.setBloqueadoPor(i2);
						i2.changeHealth(i2.getHealth() - defaultDamageValue);
						this.health += defaultDamageValue;
					}
				}
				this.setBloqueadoPor(this);
			}
			updateCallback.processReport("Fight: " + this + " vs " + i2 + "\n");
		} else {
			updateCallback.processReport(this + " says:" + i2 + " is already dead!\n");
		}
	}

	public void changeHealth(int v) {
		health = v;
	}

	public int getHealth() {
		return health;
	}

	public void setPause(boolean pause) {
		this.pause = pause;
	}

	public void setBloqueadoPor(Immortal bloqueadoPor) {
		this.bloqueadoPor = bloqueadoPor;
	}

	public void wakeUp() {
		pause = false;
		synchronized (this) {
			this.notifyAll();
		}
	}

	@Override
	public String toString() {

		return name + "[" + health + "]";
	}

}
