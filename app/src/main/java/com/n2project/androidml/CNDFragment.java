package com.n2project.androidml;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import com.n2project.androidml.databinding.FragmentSecondBinding;

public class CNDFragment extends Fragment {

    private FragmentSecondBinding binding;

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

        binding.buttonCameraCND.setOnClickListener(null);
        binding.buttonGalleryCND.setOnClickListener(null);
        binding.buttonBack.setOnClickListener(v ->
                NavHostFragment.findNavController(CNDFragment.this).navigate(R.id.action_CNDFragment_to_HomeFragment));
    }

}
