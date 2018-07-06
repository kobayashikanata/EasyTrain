package com.per.epx.easytrain.helpers;


import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

//Request grant permission after device Android.M
public class GrantHandler {
    private static final String PERMISSION_FRAGMENT_TAG = "permission.fragment";
    private static final int REQUEST_EXTERNAL_STORAGE = 11;

    public static void requestStoragePermission(final Activity activity, final Callback onGranted) {
        requestPermission(activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, REQUEST_EXTERNAL_STORAGE, new Callback() {
            @Override
            public void onGrantResults(int[] grantResults) {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    System.out.println("permission granted.");
                    if (onGranted != null)
                        onGranted.onGrantResults(grantResults);
                } else Toast.makeText(activity, "grant permission fail", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void requestPermission(final Activity activity, String permission, int requestCode, Callback callback) {
        int grant = ActivityCompat.checkSelfPermission(activity, permission);
        if (grant != PackageManager.PERMISSION_GRANTED) {
            FragmentManager fm = activity.getFragmentManager();
            //Ensure remove old request
            Fragment old = null;
            ;
            if ((old = fm.findFragmentByTag(PERMISSION_FRAGMENT_TAG)) != null) {
                fm.beginTransaction().remove(old).commit();
            }
            //Set new request fragment
            GrantRequireFragment frag = new GrantRequireFragment();
            frag.setRequestParams(new String[]{permission}, requestCode);
            frag.setCallback(callback);
            fm.beginTransaction().add(frag, PERMISSION_FRAGMENT_TAG).commitAllowingStateLoss();
            //ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        } else {
            if (callback != null)
                callback.onGrantResults(new int[]{PackageManager.PERMISSION_GRANTED});
        }
    }

    public interface Callback {
        void onGrantResults(int[] grantResults);
    }

    public static class GrantRequireFragment extends Fragment {
        private String[] permissions;
        private int requestCode;
        private Callback callback;

        public GrantRequireFragment() {
            super();
        }

        @Override
        @RequiresApi(api = Build.VERSION_CODES.M)
        public void onAttach(Context context) {
            super.onAttach(context);
            System.out.println("GrantRequireFragment.attach");
            requestPermissions(permissions, requestCode);
        }

        @Override
        public void onDetach() {
            super.onDetach();
            System.out.println("GrantRequireFragment.detach");
        }

        public GrantRequireFragment setRequestParams(String[] permissions, int requestCode) {
            this.permissions = permissions;
            this.requestCode = requestCode;
            return this;
        }

        public GrantRequireFragment setCallback(Callback callback) {
            this.callback = callback;
            return this;
        }

        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            if (requestCode == REQUEST_EXTERNAL_STORAGE) {
                System.out.println("GrantRequireFragment.onRequestPermissionsResult");
                //Remove self
                getFragmentManager().beginTransaction().remove(this).commitAllowingStateLoss();
                if (callback != null)
                    callback.onGrantResults(grantResults);
            }
        }
    }

}

