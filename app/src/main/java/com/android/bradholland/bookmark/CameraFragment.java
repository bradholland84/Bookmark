package com.android.bradholland.bookmark;

import android.app.Fragment;
import android.app.FragmentManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class CameraFragment extends Fragment {

    public static final String TAG = "CameraFragment";

    private Camera camera;
    private SurfaceView surfaceView;
    private ParseFile photoFile;
    private Button useDefault;
    private ImageButton photoButton;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_camera, parent, false);

        useDefault = (Button) v. findViewById(R.id.btn_default);
        photoButton = (ImageButton) v.findViewById(R.id.camera_photo_button);

        if (camera == null) {
            try {
                camera = Camera.open();
                photoButton.setEnabled(true);
            } catch (Exception e) {
                Log.e(TAG, "No camera with exception: " + e.getMessage());
                photoButton.setEnabled(false);
                Toast.makeText(getActivity(), "No camera detected",
                        Toast.LENGTH_LONG).show();
            }
        }

        useDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable d = getResources().getDrawable(R.drawable.default_cover);
                Bitmap bitmap = ((BitmapDrawable)d).getBitmap();
                ByteArrayOutputStream data = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, data);
                byte[] bitmapdata = data.toByteArray();
                saveScaledPhoto(bitmapdata, false);

            }
        });


        photoButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (camera == null)
                    return;

                Camera.AutoFocusCallback cb = new Camera.AutoFocusCallback() {
                    @Override
                    public void onAutoFocus(boolean success, Camera camera) {
                        camera.takePicture(new Camera.ShutterCallback() {

                            @Override
                            public void onShutter() {
                                // nothing to do
                            }

                        }, null, new Camera.PictureCallback() {

                            @Override
                            public void onPictureTaken(byte[] data, Camera camera) {
                                saveScaledPhoto(data, true);
                            }

                        });
                    }
                };
                camera.autoFocus(cb);
            }
        });

        surfaceView = (SurfaceView) v.findViewById(R.id.camera_surface_view);

        SurfaceHolder holder = surfaceView.getHolder();
        holder.addCallback(new Callback() {

            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (camera != null) {
                        camera.setDisplayOrientation(90);
                        camera.setPreviewDisplay(holder);
                        camera.startPreview();
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Error setting up preview", e);
                }
            }

            public void surfaceChanged(SurfaceHolder holder, int format,
                                       int width, int height) {
                Log.v("surface", "surface changed called");
                // nothing to do here
            }

            public void surfaceDestroyed(SurfaceHolder holder) {
                Log.v("surface", "surface destroyed called");
                if (camera != null) {
                    camera.release();
                }
            }

        });

        return v;
    }

    /*
     * ParseQueryAdapter loads ParseFiles into a ParseImageView at whatever size
     * they are saved.
     */
    private void saveScaledPhoto(byte[] data, boolean rotate) {

        // Resize photo from camera byte array
        Bitmap coverPhoto = BitmapFactory.decodeByteArray(data, 0, data.length);
        Bitmap coverPhotoScaled = resize(coverPhoto, coverPhoto.getWidth() / 2, coverPhoto.getHeight() / 2);

        // Override Android default landscape orientation and save portrait
        Matrix matrix = new Matrix();
        if (rotate) {
            matrix.postRotate(90);
        }
        Bitmap rotatedScaledMealImage = Bitmap.createBitmap(coverPhotoScaled, 0,
                0, coverPhotoScaled.getWidth(), coverPhotoScaled.getHeight(),
                matrix, true);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        rotatedScaledMealImage.compress(Bitmap.CompressFormat.JPEG, 100, bos);

        byte[] scaledData = bos.toByteArray();

        // Save the scaled image to Parse
        photoFile = new ParseFile("cover_photo.jpg", scaledData);
        photoFile.saveInBackground(new SaveCallback() {

            public void done(ParseException e) {
                if (e != null) {
                    Toast.makeText(getActivity(),
                            "Error saving: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                } else {
                    Log.v("pic", "file created");
                        addPhotoToBookAndReturn(photoFile);
                }
            }
        });
    }

    /*
     * Once the photo has saved successfully, we're ready to return to the
     * NewMealFragment. When we added the CameraFragment to the back stack, we
     * named it "NewMealFragment". Now we'll pop fragments off the back stack
     * until we reach that Fragment.
     */
    private void addPhotoToBookAndReturn(ParseFile photoFile) {
        ((AddBookActivity) getActivity()).getCurrentBook().setPhotoFile(photoFile);
        FragmentManager fm = getActivity().getFragmentManager();
        fm.popBackStack("Camera Fragment",
                FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    // Scales the image down by half to save memory
    private static Bitmap resize(Bitmap image, int maxWidth, int maxHeight) {
        if (maxHeight > 0 && maxWidth > 0) {
            int width = image.getWidth();
            int height = image.getHeight();
            float ratioBitmap = (float) width / (float) height;
            float ratioMax = (float) maxWidth / (float) maxHeight;

            int finalWidth = maxWidth;
            int finalHeight = maxHeight;
            if (ratioMax > 1) {
                finalWidth = (int) ((float)maxHeight * ratioBitmap);
            } else {
                finalHeight = (int) ((float)maxWidth / ratioBitmap);
            }
            image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true);
            return image;
        } else {
            return image;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (camera == null) {
            try {
                camera = Camera.open();
                photoButton.setEnabled(true);
            } catch (Exception e) {
                Log.i(TAG, "No camera: " + e.getMessage());
                photoButton.setEnabled(false);
                Toast.makeText(getActivity(), "No camera detected",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onPause() {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
        }
        super.onPause();
    }

}