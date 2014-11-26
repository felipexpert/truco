package miquilini.felipe.clienteSwing.som;


public class SFCoinEffect {
	private static MP3 som;
	
	static {
		som = new MP3("audios/coinEffect.mp3");
	}
	
	private SFCoinEffect() {}
	
	public static void play() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				som.play();
			}
		}).start();
	}
	
	public static void stop(){
		som.stop();
	}
}
