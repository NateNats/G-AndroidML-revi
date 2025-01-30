package com.n2project.androidml;

import android.content.Context;

import org.tensorflow.lite.Interpreter;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class TFLiteModel {
    private Interpreter interpreter;

    public TFLiteModel(Context context) throws IOException {
        Interpreter.Options options = new Interpreter.Options();
        options.setNumThreads(4);
        interpreter = new Interpreter(loadModelFile(context), options);
    }

    private MappedByteBuffer loadModelFile(Context context) throws IOException {
        try (FileInputStream fis = new FileInputStream(context.getAssets().openFd("model.tflite").getFileDescriptor())) {
            FileChannel fileChannel = fis.getChannel();
            long startOffset = context.getAssets().openFd("model.tflite").getStartOffset();
            long declaredLength = context.getAssets().openFd("model.tflite").getDeclaredLength();
            return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
        }
    }

    public void predict(float[][][][] input, float[][] output) {
        interpreter.run(input, output);
    }

    public void close() {
        interpreter.close();
    }
}
