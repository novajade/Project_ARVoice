package com.google.ar.sceneform.samples.hellosceneform;

import android.media.Image;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.ar.core.Frame;
import com.google.ar.core.exceptions.NotYetAvailableException;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;

import java.util.List;

import static android.support.constraint.Constraints.TAG;
import static com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions.ACCURATE_MODE;
import static com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS;
import static com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions.NO_CLASSIFICATIONS;
import static com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions.NO_LANDMARKS;

public class findface {
    public Image mimage = null;
    private int cameraWidth = 0;
    private int cameraHeight=0;
    private FirebaseVisionFaceDetector detector;
    private FirebaseVisionImageMetadata metadata;
    public static float FaceX;
    public static float FaceY;
    public Boolean isFace = false;
    Frame returnFrame;
    public static float FaceCenterX;
    public static float FacePlusY;


    public void findFace(Frame mFrame){
        HelloSceneformActivity.LOCK=true;
        returnFrame = mFrame;
        try {
            mimage = mFrame.acquireCameraImage();
        } catch (NotYetAvailableException e1) {
            e1.printStackTrace();
        }
        cameraWidth = mimage.getWidth();
        cameraHeight = mimage.getHeight();
        Log.d("이미지 크기 ", String.valueOf(mimage.getWidth()) + " , " + String.valueOf(mimage.getHeight()));
        FindFaceDetector(cameraWidth, cameraHeight, true, mimage);
    }


    public void FindFaceDetector(int cameraWidth, int cameraHeight, boolean trackingEnabled, Image mimage){
        FirebaseVisionFaceDetectorOptions detectorOptions = new FirebaseVisionFaceDetectorOptions.Builder()
                .setModeType(ACCURATE_MODE)
                .setLandmarkType(NO_LANDMARKS)
                .setClassificationType(NO_CLASSIFICATIONS)
                .setMinFaceSize(0.1f)
                .setTrackingEnabled(trackingEnabled)
                .build();
        detector = FirebaseVision.getInstance().getVisionFaceDetector(detectorOptions);

        metadata = new FirebaseVisionImageMetadata.Builder()
                .setWidth(cameraWidth)
                .setHeight(cameraHeight)
                .setFormat(FirebaseVisionImageMetadata.IMAGE_FORMAT_YV12)
                .setRotation(FirebaseVisionImageMetadata.ROTATION_90)
                .build();
        detectFromMediaImage(mimage);
        mimage.close();
    }
    public void detectFromMediaImage(Image mimage) {
        if(mimage == null){
            Log.d(TAG, "이미지값이 NULL");
        }
        detect(FirebaseVisionImage.fromMediaImage(mimage, 1));
    }


    public List<FirebaseVisionFace> face;
    private void detect(FirebaseVisionImage firebaseVisionImage) {
        //========2018.08.25============
        detector.detectInImage(firebaseVisionImage)
                .addOnSuccessListener(
                        new OnSuccessListener<List<FirebaseVisionFace>>() {
                            @Override
                            public void onSuccess(List<FirebaseVisionFace> faces) {
                                //========2018.08.25============
                                if(faces.isEmpty()){
                                    Log.d(TAG,"인식할 얼굴이 없음");
                                    isFace = false;
                                    HelloSceneformActivity.faceSuccess = false;
                                    HelloSceneformActivity.LOCK = false;
                                    return;
                                }
                                else{
                                    Log.d(TAG,"찾은 얼굴 갯수 : " + faces.size());
                                }
                                for(int i = 0; i<faces.size(); ++i) {
                                    FirebaseVisionFace face = faces.get(i);
                                    FaceX = face.getBoundingBox().centerX();
                                    FaceY = face.getBoundingBox().centerY();
                                    FaceCenterX = face.getBoundingBox().exactCenterX();
                                    FacePlusY = (float)face.getBoundingBox().top;
                                    FaceY = face.getBoundingBox().centerY();
                                    isFace = true;
                                    HelloSceneformActivity.faceSuccess = true;
                                    Log.d(TAG, "이미지 얼굴 위치" + FaceX + " , " + FaceY);
                                    HelloSceneformActivity.LOCK = false;
                                }
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Task failed with an exception
                                // ...
                                Log.e(TAG, "FAIL TO DETECT FACE IN IMAGE" + e);
                                HelloSceneformActivity.LOCK = false;
                            }
                        });
    }
    public float findxy(){
        return 0;
    }
    public Frame ReturnFrame(){
        return returnFrame;
    }

}

