package com.n2project.androidml;

import android.content.Context;
import android.graphics.Bitmap;

import org.tensorflow.lite.Interpreter;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class TFLiteModel {
    private Interpreter interpreter;

    public TFLiteModel(Context context) throws IOException {
        interpreter = new Interpreter(loadModelFile(context, "app/src/main/ml/model.tflite"));
    }

    private MappedByteBuffer loadModelFile(Context context, String modelPath) throws IOException {
        FileInputStream fis = new FileInputStream(context.getAssets().openFd(modelPath).getFileDescriptor());
        FileChannel fh = fis.getChannel();
        long startOffset = context.getAssets().openFd(modelPath).getStartOffset();
        long endOffset = context.getAssets().openFd(modelPath).getDeclaredLength();
        return fh.map(FileChannel.MapMode.READ_ONLY, startOffset, endOffset);
    }

    public float[] predict(float[][] input) {
        float[][] output = new float[1][1];
        interpreter.run(input, output);
        return output[0];
    }

    public void close() {
        interpreter.close();
    }
}
