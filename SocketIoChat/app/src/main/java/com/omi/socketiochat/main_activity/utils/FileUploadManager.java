package com.omi.socketiochat.main_activity.utils;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FileUploadManager {
	private static final String TAG = "FileUploadManager";
	
	private long mFileSize;
	private File mFile;
	private BufferedInputStream stream;
	private String mData;
	private int mBytesRead;
	private int chunkSize;

	public FileUploadManager(Context context) {
		String type = Constants.haveNetworkConnection(context);
		if(!type.equals("null")){
			if(type.equals("wifi") || type.equals("4g")){
				chunkSize = 4096;
			}else if(type.equals("3g")){
				chunkSize = 2048;
			}else {
				chunkSize = 1024;
			}
		}
	}

	public boolean prepare(String fullFilePath) {
		mFile = new File(fullFilePath);
		mFileSize = mFile.length();
		
		try {
			stream = new BufferedInputStream(new FileInputStream(mFile));
		} catch (FileNotFoundException e) {
			Log.e(TAG, "prepare err", e);
		}
		
		return true;
	}
	
	public String getFileName() {
		return mFile.getName();
	}
	
	public long getFileSize() {
		return mFileSize;
	}
	
	public long getBytesRead() {
		return mBytesRead;
	}
	
	public String getData() {
		return mData;
	}
	
	public void read() throws IOException {
	    ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
		byte[] buffer = new byte[chunkSize];
		
		mBytesRead = stream.read(buffer);
		if(mBytesRead != -1) {
			byteBuffer.write(buffer, 0, mBytesRead);
		}
		Log.v(TAG, "Read :" + mBytesRead);

		mData = Base64.encodeToString(byteBuffer.toByteArray(), Base64.DEFAULT);
	}
	
	public void close() {
		if (stream != null) {
			try {
				stream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			stream = null;
		}
	}
}
