package com.omi.socketiochat.compresser;

import android.content.Context;
import android.graphics.Bitmap;

import java.io.File;
import java.io.IOException;

import io.reactivex.Observable;


public class Compressor {
    //max width and height values of the compressed image is taken as 612x816
    private int maxWidth = 612;
    private int maxHeight = 816;
    private int maxWidthBlur = 153;
    private int maxHeightBlur = 204;
    private boolean keepMetaData = true;
    private Bitmap.CompressFormat compressFormat = Bitmap.CompressFormat.JPEG;
    private int quality = 80;
    private float radius = 25;
    private String destinationDirectoryPath;
    private Context context;
    public Compressor(Context context) {
        destinationDirectoryPath = context.getCacheDir().getPath() + File.separator + "images";
        this.context = context;
    }

    public Compressor setMaxWidth(int maxWidth) {
        this.maxWidth = maxWidth;
        return this;
    }

    public Compressor setMaxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
        return this;
    }

    public Compressor setMaxWidthBlur(int maxWidthBlur) {
        this.maxWidthBlur = maxWidthBlur;
        return this;
    }

    public Compressor setMaxHeightBlur(int maxHeightBlur) {
        this.maxHeightBlur = maxHeightBlur;
        return this;
    }

    public Compressor setRadius(float radius) {
        this.radius = radius;
        return this;
    }

    public Compressor setKeepMetaData(boolean keepMetaData) {
        this.keepMetaData = keepMetaData;
        return this;
    }

    public Compressor setCompressFormat(Bitmap.CompressFormat compressFormat) {
        this.compressFormat = compressFormat;
        return this;
    }

    public Compressor setQuality(int quality) {
        this.quality = quality;
        return this;
    }

    public Compressor setDestinationDirectoryPath(String destinationDirectoryPath) {
        this.destinationDirectoryPath = destinationDirectoryPath;
        return this;
    }

    public Observable<File[]> compressToFile(File imageFile) throws IOException {
        return compressToFile(imageFile, imageFile.getName());
    }

    public Observable<File[]> compressToFile(File imageFile, String compressedFileName) throws IOException {
        return ImageUtil.compressImage(imageFile, maxWidth, maxHeight, maxWidthBlur, maxHeightBlur, keepMetaData, radius, compressFormat, quality, context,
                destinationDirectoryPath + File.separator + compressedFileName,
                destinationDirectoryPath + File.separator + "blur" +compressedFileName);
    }
}
