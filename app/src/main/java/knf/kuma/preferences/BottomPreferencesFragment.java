package knf.kuma.preferences;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import knf.kuma.BottomFragment;
import knf.kuma.R;

public class BottomPreferencesFragment extends BottomFragment {
    public BottomPreferencesFragment() {
    }

    public static BottomPreferencesFragment get(){
        return new BottomPreferencesFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_preferences,container,false);
    }

    @Override
    public void onReselect() {

    }
}
