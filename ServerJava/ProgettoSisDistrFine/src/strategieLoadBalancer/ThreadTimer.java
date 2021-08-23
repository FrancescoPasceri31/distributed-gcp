package strategieLoadBalancer;

import java.util.concurrent.TimeUnit;

public class ThreadTimer extends Thread {

	private int TIME_SLICE_SECONDS;
	private RoundRobinTime rrt;

	public ThreadTimer(RoundRobinTime rrt, int time_slice_seconds) {
		this.rrt = rrt;
		this.TIME_SLICE_SECONDS = time_slice_seconds;
	}

	@Override
	public void run() {
		while (true) {
			try {
				TimeUnit.SECONDS.sleep(TIME_SLICE_SECONDS);
				rrt.incr();
			} catch (InterruptedException e) {
//				e.printStackTrace();
				System.out.println("ThreadTimer non esegue sleep.");
			}
		}
	}

}
