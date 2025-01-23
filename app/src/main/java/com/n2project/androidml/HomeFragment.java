package com.n2project.androidml;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import com.n2project.androidml.databinding.FragmentFirstBinding;

public class HomeFragment extends Fragment {

    private FragmentFirstBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonCnd.setOnClickListener(v ->
                NavHostFragment.findNavController(HomeFragment.this).navigate(R.id.action_HomeFragment_to_CNDFragment));

        binding.buttonFruit.setOnClickListener(v ->
                NavHostFragment.findNavController(HomeFragment.this).navigate(R.id.action_HomeFragment_to_FruitFragment));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
