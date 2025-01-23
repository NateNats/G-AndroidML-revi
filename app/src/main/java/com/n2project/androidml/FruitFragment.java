package com.n2project.androidml;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
    private Uri selectedImage;

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


        binding.buttonGalleryF.setOnClickListener(v -> imageChooser());
        binding.buttonCameraF.setOnClickListener(null);
//        binding.buttonBack.setOnClickListener(v ->
//                NavHostFragment.findNavController(FruitFragment.this).navigate(R.id.action_FruitFragment_to_HomeFragment));

        binding.buttonDelete.setOnClickListener(v -> {
            binding.imageView.setImageURI(null);
            binding.textResultF.setText("");
            binding.buttonDelete.setEnabled(false);
        });
    }

    private void classifyImage(Uri img) {

    }

    private void imageChooser() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        imageChooserLauncher.launch(Intent.createChooser(i, "Select Picture"));

    }
}
