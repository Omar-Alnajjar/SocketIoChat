package com.omi.socketiochat.main_activity.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;

public class FileDownloadManager {
	private static final String TAG = "FileDownloadManager";

	private File mFile;
	private BufferedInputStream stream;
	private String mData;
	private OutputStream fop;
	private ByteArrayOutputStream byteBuffer;
	private String username;

	public boolean prepare(String fileName) throws IOException {
		String filePath = Environment.getExternalStorageDirectory()+File.separator+"ChatApp"+File.separator+"images";
		File file = new File(filePath);
		if (!file.exists()) {
			file.mkdirs();
		}
		mFile = new File(file+File.separator+fileName);


		fop = new FileOutputStream(mFile);
		byteBuffer = new ByteArrayOutputStream();

		
		return true;
	}

	public String getFilePath(){
		return mFile.getAbsolutePath();
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void write(String data) throws IOException {
		byte[] decodedBytes = Base64.decode(data, Base64.DEFAULT);

		byteBuffer.write(decodedBytes);
	}
	
	public void close(){
		try {
			byteBuffer.writeTo(fop);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
