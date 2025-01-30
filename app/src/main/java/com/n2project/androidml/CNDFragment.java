package com.n2project.androidml;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import com.n2project.androidml.databinding.FragmentSecondBinding;
import com.n2project.androidml.databinding.FragmentThirdBinding;

import java.io.IOException;

public class CNDFragment extends Fragment {

    private FragmentSecondBinding binding;
    private ActivityResultLauncher<Intent> imageChooserLauncher;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private static final int CAMERA_REQUEST = 100;
    private Uri selectedImage;
    private Bitmap photo;
    private TFLiteModel tflite;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSecondBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // open gallery
        imageChooserLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == MainActivity.RESULT_OK) {
                Intent data = result.getData();
                if (data != null && data.getData() != null) {
                    selectedImage = data.getData();
                    binding.buttonDelete2.setEnabled(true);
                    binding.imageView2.setImageURI(selectedImage);
                    classifyImage(selectedImage);
                }
            }
        });

        // open camera
        cameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == MainActivity.RESULT_OK) {
                Intent data = result.getData();
                if (data != null) {
                    photo = (Bitmap) data.getExtras().get("data");
                    binding.imageView2.setImageBitmap(photo);
                    binding.buttonDelete2.setEnabled(true);
                    classifyImage(photo);
                }
            }
        });

        binding.buttonCameraCND.setOnClickListener(v -> openCamera());
        binding.buttonGalleryCND.setOnClickListener(v -> imageChooser());
        binding.buttonDelete2.setOnClickListener(v -> {
            binding.imageView2.setImageBitmap(null);
            binding.buttonDelete2.setEnabled(false);
            binding.imageView2.setImageURI(null);
            binding.textResultCND.setText("");
        });
    }

    private void classifyImage(Uri img) {
        try {
            Bitmap map;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ImageDecoder.Source source = ImageDecoder.createSource(this.getActivity().getContentResolver(), img);
                map = ImageDecoder.decodeBitmap(source, (decoder, info, s) -> {
                    decoder.setAllocator(ImageDecoder.ALLOCATOR_SOFTWARE);
                    decoder.setMemorySizePolicy(ImageDecoder.MEMORY_POLICY_LOW_RAM);
                });

            } else {
                map = MediaStore.Images.Media.getBitmap(this.getActivity().getContentResolver(), img);
            }

            if (map != null) {
                map = map.copy(Bitmap.Config.ARGB_8888, true);
                process(map);
            } else {
                binding.textResultCND.setText("Error: Gambar tidak valid");
            }

        } catch (Exception e) {
            binding.textResultCND.setText("Error: " + e.getMessage());
            Log.e("ML_Error", "Gagal mengklasifikasikan gambar", e);
        }
    }

    private void classifyImage(Bitmap map) {
        try {
            if (map != null) {
                process(map);
            } else {
                binding.textResultCND.setText("Error: Bitmap kosong");
            }

        } catch (Exception e) {
            binding.textResultCND.setText("Error: " + e.getMessage());
            Log.e("ML_Error", "Gagal mengklasifikasikan gambar", e);
        }
    }

    private void process(Bitmap map) throws IOException {
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(map, 224, 224, true);
        float[][][][] input = new float[1][224][224][3];

        for (int y = 0; y < 224; y++) {
            for (int x = 0; x < 224; x++) {
                int pixel = scaledBitmap.getPixel(x, y);

                input[0][y][x][0] = ((pixel >> 16) & 0xFF) / 255.0f;
                input[0][y][x][1] = ((pixel >> 8) & 0xFF) / 255.0f;
                input[0][y][x][2] = (pixel & 0xFF) / 255.0f;
            }
        }

        tflite = new TFLiteModel(getContext(), "cndModel.tflite");
        float[][] output = new float[1][2];
        tflite.predict(input, output);

        binding.textResultCND.setText(indexToText(output[0]));
        tflite.close();
    }

    private String indexToText(float[] arr) {
        String[] fruits = {"cat", "dog"};
        // float[] sorted = sorting(arr);
        return fruits[0] + ": " + arr[0] + "\n" +
                fruits[1] + ": " + arr[1] + "\n";
    }

    private void openCamera() {
        if (requireContext().checkSelfPermission(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            Intent cameraIntent = new Intent();
            cameraIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraLauncher.launch(cameraIntent);
        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST);
        }
    }

    private void imageChooser() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        imageChooserLauncher.launch(Intent.createChooser(i, "Select Picture"));

    }

}
