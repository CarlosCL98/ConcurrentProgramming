package edu.eci.arsw.highlandersim;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class Immortal extends Thread {

	private ImmortalUpdateReportCallback updateCallback = null;

	private int health;

	private int defaultDamageValue;

	private List<Immortal> immortalsPopulation;

	private final String name;

	private final Random r = new Random(System.currentTimeMillis());

	private boolean pause;
	
	private boolean detener;

	private Immortal bloqueadoPor;
	
	private int cuenta;

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
		this.detener = false;
		this.cuenta = 0;
	}

	public void run() {

		while (!detener) {
			if (health <= 0) {
				break;
			}
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
			// Se intenta darle un orden a los bloqueos, para evitar el deadlock.
			Immortal bloqueado1;
			Immortal bloqueado2;
			// Extraemos los índices de los inmortales que van a pelear.
			int myIndex = immortalsPopulation.indexOf(this);
			int hisIndex = immortalsPopulation.indexOf(i2);
			if (myIndex < hisIndex) {// Si el índice de este inmortal es menor al del índice del inmortal con el que va a pelear, entonces este inmortal bloquea al otro.
				bloqueado1 = this;
				bloqueado2 = i2;
			} else {
				bloqueado1 = i2;
				bloqueado2 = this;
			}
			synchronized (bloqueado1) {
				synchronized (bloqueado2) {
					// Esta es nuestra región crítica, pues se hace el proceso de agregar y quitar vida al mismo tiempo.
					i2.changeHealth(i2.getHealth() - defaultDamageValue);
					this.health += defaultDamageValue;
				}
			}
			updateCallback.processReport("Fight: " + this + " vs " + i2 + "\n");
		} else {
			if (i2.getCuenta() < 1) {
				updateCallback.processReport(this + " says:" + i2 + " is already dead!\n");
			}
			i2.setCuenta();
		}
	}
	
	public int getCuenta() {
		return cuenta;
	}
	
	public void setCuenta() {
		this.cuenta = this.cuenta + 1;
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
	
	public void detener() {
		detener = true;
	}

	@Override
	public String toString() {

		return name + "[" + health + "]";
	}

}
