package jade.imtp.leap.nio;



// Class used for debugging purpose only
class StuckSimulator {

	private static final Object lock = new Object();
	
	static void init() {
		Thread t = new Thread() {
			public void run() {
				synchronized (lock) {
					System.err.println("LOCK acquired");
					try {
						while (true) {
							Thread.sleep(10000);
						}
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}
				System.err.println("LOCK released");
			}
		};
		t.start();
	}
	
	static void stuck() {
		System.err.println("Thread "+Thread.currentThread().getName()+" STUCK!!!!");
		synchronized (lock) {
			System.err.println("THIS IS IMPOSSIBLE!!!!!!!!!!!!");
		}
	}

}
