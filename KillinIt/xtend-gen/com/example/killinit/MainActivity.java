package com.example.killinit;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import com.example.killinit.MainActivity_CallBacks;
import com.example.killinit.R;
import com.google.common.base.Objects;
import java.util.ArrayList;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.NativeCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.xtendroid.app.AndroidActivity;
import org.xtendroid.app.OnCreate;

@AndroidActivity(R.layout.activity_main)
@SuppressWarnings("all")
public class MainActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2, MainActivity_CallBacks {
  private int mCameraIndex;
  
  private CameraBridgeViewBase mCameraView;
  
  private static boolean tracked = false;
  
  private static double trackedarea = 0.0;
  
  private int ctr = 0;
  
  private Point last = new Point();
  
  private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
    public void onManagerConnected(final int status) {
      if ((status == LoaderCallbackInterface.SUCCESS)) {
        MainActivity.this.mCameraView.enableView();
        MainActivity.this.setContentView(MainActivity.this.mCameraView);
      } else {
        super.onManagerConnected(status);
      }
    }
  };
  
  @OnCreate
  public void init(final Bundle savedInstanceState) {
    Window _window = this.getWindow();
    _window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    this.mCameraIndex = 0;
    NativeCameraView _nativeCameraView = new NativeCameraView(this, this.mCameraIndex);
    this.mCameraView = _nativeCameraView;
    this.mCameraView.enableFpsMeter();
    this.mCameraView.setCvCameraViewListener(this);
  }
  
  protected void onResume() {
    OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_9, this, this.mLoaderCallback);
    super.onResume();
  }
  
  public void onCameraViewStarted(final int width, final int height) {
    Log.e("start", "start");
  }
  
  public void onCameraViewStopped() {
    Log.e("end", "end");
  }
  
  public Mat onCameraFrame(final CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
    Mat mat = inputFrame.rgba();
    if ((!MainActivity.tracked)) {
      Point _point = new Point((240 - 80), (184 - 50));
      Point _point_1 = new Point((240 + 80), (184 - 50));
      Scalar _scalar = new Scalar(0, 255, 255);
      Core.line(mat, _point, _point_1, _scalar, 3);
      Point _point_2 = new Point((240 + 80), (184 - 50));
      Point _point_3 = new Point((240 + 80), (184 + 50));
      Scalar _scalar_1 = new Scalar(0, 255, 255);
      Core.line(mat, _point_2, _point_3, _scalar_1, 3);
      Point _point_4 = new Point((240 + 80), (184 + 50));
      Point _point_5 = new Point((240 - 80), (184 + 50));
      Scalar _scalar_2 = new Scalar(0, 255, 255);
      Core.line(mat, _point_4, _point_5, _scalar_2, 3);
      Point _point_6 = new Point((240 - 80), (184 + 50));
      Point _point_7 = new Point((240 - 80), (184 - 50));
      Scalar _scalar_3 = new Scalar(0, 255, 255);
      Core.line(mat, _point_6, _point_7, _scalar_3, 3);
    }
    Mat grey = new Mat();
    Imgproc.cvtColor(mat, grey, Imgproc.COLOR_RGB2GRAY);
    Imgproc.threshold(grey, grey, 75, 255, Imgproc.THRESH_BINARY);
    ArrayList<MatOfPoint> cors = CollectionLiterals.<MatOfPoint>newArrayList();
    Mat hier = new Mat();
    Imgproc.findContours(grey, cors, hier, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_NONE);
    MatOfPoint2f vertices = new MatOfPoint2f();
    ArrayList<Integer> fours = CollectionLiterals.<Integer>newArrayList();
    for (int i = 0; (i < cors.size()); i++) {
      {
        MatOfPoint _get = cors.get(i);
        Point[] _array = _get.toArray();
        MatOfPoint2f _matOfPoint2f = new MatOfPoint2f(_array);
        MatOfPoint _get_1 = cors.get(i);
        Point[] _array_1 = _get_1.toArray();
        MatOfPoint2f _matOfPoint2f_1 = new MatOfPoint2f(_array_1);
        double _arcLength = Imgproc.arcLength(_matOfPoint2f_1, true);
        double _multiply = (_arcLength * 0.02);
        Imgproc.approxPolyDP(_matOfPoint2f, vertices, _multiply, true);
        MatOfPoint _get_2 = cors.get(i);
        double area = Imgproc.contourArea(_get_2);
        if ((((vertices.size().height == 4) && (area > 1000)) && (area < 30000))) {
          boolean _priorities = this.priorities(vertices, mat);
          if (_priorities) {
            fours.add(Integer.valueOf(i));
          }
        }
      }
    }
    return mat;
  }
  
  public boolean priorities(final MatOfPoint2f test, final Mat mat) {
    double[] _get = test.get(0, 0);
    double x1 = _get[0];
    double[] _get_1 = test.get(0, 0);
    double y1 = _get_1[1];
    double[] _get_2 = test.get(1, 0);
    double x2 = _get_2[0];
    double[] _get_3 = test.get(1, 0);
    double y2 = _get_3[1];
    double[] _get_4 = test.get(2, 0);
    double x3 = _get_4[0];
    double[] _get_5 = test.get(2, 0);
    double y3 = _get_5[1];
    double[] _get_6 = test.get(3, 0);
    double x4 = _get_6[0];
    double[] _get_7 = test.get(3, 0);
    double y4 = _get_7[1];
    Scalar _scalar = new Scalar(x1, y1);
    Point p1 = new Point(_scalar.val);
    Scalar _scalar_1 = new Scalar(x2, y2);
    Point p2 = new Point(_scalar_1.val);
    Scalar _scalar_2 = new Scalar(x3, y3);
    Point p3 = new Point(_scalar_2.val);
    Scalar _scalar_3 = new Scalar(x4, y4);
    Point p4 = new Point(_scalar_3.val);
    if (((p1.x - p4.x) > 100)) {
      Point temp = p4;
      p4 = p1;
      p1 = p2;
      p2 = temp;
    }
    double _min = Math.min(p2.x, p4.x);
    double _min_1 = Math.min(p1.x, p3.x);
    double _min_2 = Math.min(_min, _min_1);
    int js = ((int) _min_2);
    double _max = Math.max(p2.x, p4.x);
    double _max_1 = Math.max(p1.x, p3.x);
    double _max_2 = Math.max(_max, _max_1);
    int je = ((int) _max_2);
    double _min_3 = Math.min(p2.y, p4.y);
    double _min_4 = Math.min(p1.y, p3.y);
    double _min_5 = Math.min(_min_3, _min_4);
    int ks = ((int) _min_5);
    double _max_3 = Math.max(p2.y, p4.y);
    double _max_4 = Math.max(p1.y, p3.y);
    double _max_5 = Math.max(_max_3, _max_4);
    int ke = ((int) _max_5);
    Point mid = new Point(((js + je) / 2), ((ks + ke) / 2));
    boolean _or = false;
    boolean _and = false;
    double _abs = Math.abs((mid.x - 240));
    boolean _lessThan = (_abs < 80);
    if (!_lessThan) {
      _and = false;
    } else {
      double _abs_1 = Math.abs((mid.y - 184));
      boolean _lessThan_1 = (_abs_1 < 50);
      _and = _lessThan_1;
    }
    if (_and) {
      _or = true;
    } else {
      _or = MainActivity.tracked;
    }
    if (_or) {
      int black = 0;
      Rect sub = new Rect(p2, p4);
      Mat temp_1 = mat.submat(sub);
      for (int j = 0; (j < temp_1.rows()); j++) {
        for (int k = 0; (k < temp_1.cols()); k++) {
          double[] _get_8 = temp_1.get(j, k);
          boolean _notEquals = (!Objects.equal(_get_8, null));
          if (_notEquals) {
            double[] _get_9 = temp_1.get(j, k);
            double _get_10 = _get_9[0];
            double[] _get_11 = temp_1.get(j, k);
            double _get_12 = _get_11[1];
            double _plus = (_get_10 + _get_12);
            double[] _get_13 = temp_1.get(j, k);
            double _get_14 = _get_13[2];
            double _plus_1 = (_plus + _get_14);
            boolean _lessThan_2 = (_plus_1 < 700);
            if (_lessThan_2) {
              black++;
            }
          }
        }
      }
      Size _size = temp_1.size();
      double area = _size.area();
      if (((black / area) > 0.85)) {
        if ((!MainActivity.tracked)) {
          this.ctr++;
          Scalar _scalar_4 = new Scalar(255, 255, 255);
          Core.line(mat, p1, p2, _scalar_4);
          Scalar _scalar_5 = new Scalar(255, 255, 255);
          Core.line(mat, p2, p3, _scalar_5);
          Scalar _scalar_6 = new Scalar(255, 255, 255);
          Core.line(mat, p3, p4, _scalar_6);
          Scalar _scalar_7 = new Scalar(255, 255, 255);
          Core.line(mat, p4, p1, _scalar_7);
          if ((this.ctr >= 15)) {
            MainActivity.tracked = true;
            Size _size_1 = temp_1.size();
            double _area = _size_1.area();
            MainActivity.trackedarea = _area;
            this.last = mid;
            String _string = this.last.toString();
            String _plus = ("tracked:" + _string);
            Log.e("shitz", _plus);
          }
        }
        boolean _and_1 = false;
        if (!MainActivity.tracked) {
          _and_1 = false;
        } else {
          double _abs_2 = Math.abs((area - MainActivity.trackedarea));
          boolean _lessThan_2 = (_abs_2 < 600);
          _and_1 = _lessThan_2;
        }
        if (_and_1) {
          Scalar _scalar_8 = new Scalar(255, 255, 255);
          Core.line(mat, p1, p2, _scalar_8);
          Scalar _scalar_9 = new Scalar(255, 255, 255);
          Core.line(mat, p2, p3, _scalar_9);
          Scalar _scalar_10 = new Scalar(255, 255, 255);
          Core.line(mat, p3, p4, _scalar_10);
          Scalar _scalar_11 = new Scalar(255, 255, 255);
          Core.line(mat, p4, p1, _scalar_11);
          if ((mid.x > this.last.x)) {
            Scalar _scalar_12 = new Scalar(255, 255, 255);
            Core.line(mat, p2, p3, _scalar_12, 3);
          }
          if ((mid.x < this.last.x)) {
            Scalar _scalar_13 = new Scalar(255, 255, 255);
            Core.line(mat, p4, p1, _scalar_13, 3);
          }
          if ((mid.y > this.last.y)) {
            Scalar _scalar_14 = new Scalar(255, 255, 255);
            Core.line(mat, p3, p4, _scalar_14, 3);
          }
          if ((mid.y < this.last.y)) {
            Scalar _scalar_15 = new Scalar(255, 255, 255);
            Core.line(mat, p1, p2, _scalar_15, 3);
          }
          Point _point = new Point((mid.x - 3), mid.y);
          Point _point_1 = new Point((mid.x + 3), mid.y);
          Scalar _scalar_16 = new Scalar(127, 255, 12);
          Core.line(mat, _point, _point_1, _scalar_16, 5);
          Point _point_2 = new Point((this.last.x - 3), this.last.y);
          Point _point_3 = new Point((this.last.x + 3), this.last.y);
          Scalar _scalar_17 = new Scalar(255, 255, 255);
          Core.line(mat, _point_2, _point_3, _scalar_17, 3);
          this.last = mid;
        }
        return true;
      } else {
        return false;
      }
    }
    return false;
  }
  
  public void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    init(savedInstanceState);
  }
}
