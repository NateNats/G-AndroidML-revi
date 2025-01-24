package com.n2project.androidml;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.n2project.androidml.databinding.FragmentThirdBinding;



public class FruitFragment extends Fragment {

    private FragmentThirdBinding binding;
    private ActivityResultLauncher<Intent> imageChooserLauncher;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private static final int CAMERA_REQUEST = 100;
    private Uri selectedImage;
    private Bitmap photo;

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
                }
            }
        });

        binding.buttonGalleryF.setOnClickListener(v -> imageChooser());
        binding.buttonCameraF.setOnClickListener(v -> openCamera());
//        binding.buttonBack.setOnClickListener(v ->
//                NavHostFragment.findNavController(FruitFragment.this).navigate(R.id.action_FruitFragment_to_HomeFragment));

        binding.buttonDelete.setOnClickListener(v -> {
            binding.imageView.setImageBitmap(null);
            binding.imageView.setImageURI(null);
            binding.textResultF.setText("");
            binding.buttonDelete.setEnabled(false);
        });
    }

    private void classifyImage(Uri img) {
//        try {
//            Model model = Model.newInstance(context);
//
//            // Creates inputs for reference.
//            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);
//            inputFeature0.loadBuffer(byteBuffer);
//
//            // Runs model inference and gets result.
//            Model.Outputs outputs = model.process(inputFeature0);
//            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();
//
//            // Releases model resources if no longer used.
//            model.close();
//        } catch (IOException e) {
//            // TODO Handle the exception
//        }
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
