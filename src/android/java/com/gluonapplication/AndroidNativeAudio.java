package com.gluonapplication;

import java.io.IOException;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import javafxports.android.FXActivity;
import android.media.AudioManager;

/*
 * 
 * This class intends to play sound using android's native libraries 
 */

public class AndroidNativeAudio implements NativeAudioService {

	private MediaPlayer mp;

	public AndroidNativeAudio() {
	}

	@Override
	public void play(String str) {
		AssetFileDescriptor afd;
		try {
			mp = new MediaPlayer();
			if (str.equals("fire")) {
				afd = FXActivity.getInstance().getAssets().openFd("fire.mp3");
			} else {
				afd = FXActivity.getInstance().getAssets().openFd("explosion.mp3");
			}
			mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
			mp.setAudioStreamType(AudioManager.STREAM_RING);
			mp.prepare();
			mp.start();
		} catch (IOException e) {
			System.out.println("Error playing audio resource " + e);
		}
	}

}
