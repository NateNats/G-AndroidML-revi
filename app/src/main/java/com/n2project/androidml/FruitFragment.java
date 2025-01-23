package com.n2project.androidml;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import com.n2project.androidml.databinding.FragmentThirdBinding;

public class FruitFragment extends Fragment {

    private FragmentThirdBinding binding;

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

        binding.buttonGalleryF.setOnClickListener(null);
        binding.buttonCameraF.setOnClickListener(null);
        binding.buttonBack.setOnClickListener(v ->
                NavHostFragment.findNavController(FruitFragment.this).navigate(R.id.action_FruitFragment_to_HomeFragment));
    }
}
