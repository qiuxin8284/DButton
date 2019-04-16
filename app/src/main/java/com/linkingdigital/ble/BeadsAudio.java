package com.linkingdigital.ble;

public class BeadsAudio {
	/* raw pcm
	 *
	 * adpcm_data
	 *   the raw 128 bytes read from audio attributes
	 * return
	 *   496 bytes raw PCM data (16 bits signed little endian, 16000 sample rate)
	 *   null when error
	 * */
	public native static byte[] adpcm_to_pcm(byte[] adpcm_data);

	/* encode with mp3
	 *
	 * adpcm_data
	 *   the raw 128 bytes read from audio attributes
	 * return
	 *    mp3 encoded data (length might be 0)
	 *    null when error
	 * must call mp3_stop to flush buffer and release resources after recording stopped
	 * return
	 *   mp3 encoded data
	 * */
	public native static byte[] mp3_stop();
	public native static void   mp3_init(int sr); // set sample rate, default sample rate is 8000
	public native static byte[] adpcm_to_mp3(byte[] adpcm_data);

	static {
		System.loadLibrary("beads_jni");
	}
}

// test codes
/*
try {
	FileOutputStream fos1 = new FileOutputStream(new File("/mnt/sdcard/1.mp3"));
	FileOutputStream fos2 = new FileOutputStream(new File("/mnt/sdcard/1.pcm"));

	File file = new File("/mnt/sdcard/aa");
	FileInputStream fis = new FileInputStream(file);
	byte[] buffer=new byte[128];

	int len;
	while ((len=fis.read(buffer))!=-1){
		byte[] c = BeadsAudio.ldpcm_to_mp3(buffer);
		if (c != null) {
			fos1.write(c);
		}
		byte[] d = BeadsAudio.ldpcm_to_pcm(buffer);
		if (d != null) {
			fos2.write(d);
		}
	}
	byte[] l = BeadsAudio.mp3_stop();
	if (l != null)
		fos1.write(l);

	fis.close();
	fos1.flush();
	fos1.close();
	fos2.flush();
	fos2.close();
}
catch (Exception e) {
}
*/

