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
import com.n2project.androidml.databinding.FragmentThirdBinding;

import java.io.IOException;

public class FruitFragment extends Fragment {

    private FragmentThirdBinding binding;
    private ActivityResultLauncher<Intent> imageChooserLauncher;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private static final int CAMERA_REQUEST = 100;
    private Uri selectedImage;
    private Bitmap photo;
    private TFLiteModel tflite;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentThirdBinding.inflate(inflater, container, false);
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
                    binding.buttonDelete.setEnabled(true);
                    binding.imageView.setImageURI(selectedImage);
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
                    binding.imageView.setImageBitmap(photo);
                    binding.buttonDelete.setEnabled(true);
                    classifyImage(photo);
                }
            }
        });

        binding.buttonGalleryF.setOnClickListener(v -> imageChooser());
        binding.buttonCameraF.setOnClickListener(v -> openCamera());

        binding.buttonDelete.setOnClickListener(v -> {
            binding.imageView.setImageBitmap(null);
            binding.imageView.setImageURI(null);
            binding.textResultF.setText("");
            binding.buttonDelete.setEnabled(false);
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
                binding.textResultF.setText("Error: Gambar tidak valid");
            }

        } catch (Exception e) {
            binding.textResultF.setText("Error: " + e.getMessage());
            Log.e("ML_Error", "Gagal mengklasifikasikan gambar", e);
        }
    }

    private void classifyImage(Bitmap map) {
        try {
            if (map != null) {
                process(map);
            } else {
                binding.textResultF.setText("Error: Bitmap kosong");
            }

        } catch (Exception e) {
            binding.textResultF.setText("Error: " + e.getMessage());
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

        tflite = new TFLiteModel(getContext(), "model.tflite");
        float[][] output = new float[1][9];
        tflite.predict(input, output);

        binding.textResultF.setText(indexToText(output[0]));
        tflite.close();
    }

    private String indexToText(float[] arr) {
        String[] fruits = {"apple", "banana", "cherry", "chickoo", "grapes", "kiwi", "mango", "orange", "strawberry"};
        // float[] sorted = sorting(arr);
        return fruits[0] + ": " + arr[0] + "\n" +
                fruits[1] + ": " + arr[1] + "\n" +
                fruits[2] + ": " + arr[2] + "\n" +
                fruits[3] + ": " + arr[3] + "\n" +
                fruits[4] + ": " + arr[4] + "\n" +
                fruits[5] + ": " + arr[5] + "\n" +
                fruits[6] + ": " + arr[6] + "\n" +
                fruits[7] + ": " + arr[7] + "\n" +
                fruits[8] + ": " + arr[8];
    }

    private float[] sorting(float[] arr) {
        int n = arr.length;

        for (int i = 0; i < n -1; i++) {
            for (int j = 0; j < n - 1 - i; j++) {
                if (arr[j] < arr[j + 1]) {
                    float temp = arr[j+1];
                    arr[j+1] = arr[j];
                    arr[j] = temp;
                }
            }
        }

        return arr;
    }

    private void openCamera() {
        if (requireContext().checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
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
