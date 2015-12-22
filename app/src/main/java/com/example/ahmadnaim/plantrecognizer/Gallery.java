package com.example.ahmadnaim.plantrecognizer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import android.database.Cursor;
import android.provider.MediaStore;

import org.neuroph.contrib.imgrec.ImageRecognitionPlugin;
import org.neuroph.contrib.imgrec.image.Image;
import org.neuroph.contrib.imgrec.image.ImageFactory;
import org.neuroph.core.NeuralNetwork;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class Gallery extends Fragment {

    public Gallery() {
        // Required empty public constructor
    }
    private Button txtAnswerGallery;
    private ImageView imgAnswerGallery;

    private ImageRecognitionPlugin imageRecognition;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FragmentActivity recognition = super.getActivity();
        RelativeLayout relative    = (RelativeLayout)    inflater.inflate(R.layout.fragment_gallery, container, false);

        txtAnswerGallery = (Button) relative.findViewById(R.id.txtAnswerGallery);
        imgAnswerGallery = (ImageView) relative.findViewById(R.id.imgAnswerGallery);
        txtAnswerGallery.setOnClickListener(onClickImage);

        loadData();

        relative.findViewById(R.id.recognitionlayout);
        return relative;
    }

    private View.OnClickListener onClickImage = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1111);
            imgAnswerGallery.setImageDrawable(null);
        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        Bitmap bmpAnswer;
        Image imgTest;
        InputStream imageStream;
        String filePath;
        Uri selectedImage;
        try
        {
            selectedImage = imageReturnedIntent.getData();
            filePath = getRealPathFromURI(selectedImage);
            imgTest = ImageFactory.getImage(filePath);
            imageStream = super.getActivity().getContentResolver().openInputStream(selectedImage);
            bmpAnswer = Bitmap.createBitmap(BitmapFactory.decodeStream(imageStream));
            imgAnswerGallery.setImageBitmap(bmpAnswer);
            txtAnswerGallery.setText(String.format("This is a %s", recognize(imgTest)));
        }
        catch (FileNotFoundException fnfe)
        {
            Log.d("Plant Gallery", "File not found");
        }
    }

    private Runnable loadDataRunnable = new Runnable() {
        public void run() {
            InputStream is = getResources().openRawResource(R.raw.animals_net);
            NeuralNetwork neuralnet = NeuralNetwork.load(is);
            imageRecognition = (ImageRecognitionPlugin) neuralnet.getPlugin(ImageRecognitionPlugin.class);
            getActivity().dismissDialog(2);
        }
    };

    private String recognize(Image image) {
        getActivity().showDialog(3);
        HashMap<String, Double> output = imageRecognition.recognizeImage(image);
        getActivity().dismissDialog(3);
        return getAnswer(output);
    }

    private void loadData() {
        getActivity().showDialog(2);
        new Thread(null, loadDataRunnable, "dataLoader", 32000).start();
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = super.getActivity().getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    private String getAnswer(HashMap<String, Double> output) {
        double highest = 0;
        String answer = "";
        for (Map.Entry<String, Double> entry : output.entrySet()) {
            if (entry.getValue() > highest) {
                highest = entry.getValue();
                answer = entry.getKey();
            }
        }
        return answer;
    }

}

