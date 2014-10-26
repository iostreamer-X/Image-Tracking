package com.example.killinit

import android.util.Log
import android.view.WindowManager
import java.io.DataOutputStream
import java.net.Socket
import org.opencv.android.BaseLoaderCallback
import org.opencv.android.CameraBridgeViewBase
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2
import org.opencv.android.LoaderCallbackInterface
import org.opencv.android.NativeCameraView
import org.opencv.android.OpenCVLoader
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.MatOfPoint2f
import org.opencv.core.Point
import org.opencv.core.Rect
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc
import org.xtendroid.app.AndroidActivity
import org.xtendroid.app.OnCreate

@AndroidActivity(R.layout.activity_main)
class MainActivity implements CvCameraViewListener2 {
	private int mCameraIndex;
	private CameraBridgeViewBase mCameraView;
    static var tracked=false
    static var trackedarea=0.0
    var ctr=0
    var last=new Point
	var mLoaderCallback = new BaseLoaderCallback(this) {

		override onManagerConnected(int status) {
			if (status == LoaderCallbackInterface.SUCCESS) {
				mCameraView.enableView
				setContentView(mCameraView)
			} else
				super.onManagerConnected(status)
		}

	}

	@OnCreate
	def init() {
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
		mCameraIndex =0
		mCameraView = new NativeCameraView(this, mCameraIndex)
		mCameraView.enableFpsMeter
		mCameraView.setCvCameraViewListener(this)

	}

	override protected onResume() {

		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_9, this, mLoaderCallback)
		super.onResume()
	}

	override onCameraViewStarted(int width, int height) {
		Log.e("start", "start")
	}

	override onCameraViewStopped() {
		Log.e("end", "end")
	}

	override onCameraFrame(CvCameraViewFrame inputFrame) {
		
		var mat = inputFrame.rgba
		//Core.flip(mat,mat,1)
		if(!tracked)		
		{Core.line(mat, new Point(240-80,184-50),new Point(240+80,184-50), new Scalar(0, 255, 255),3)
		Core.line(mat,new Point(240+80,184-50),new Point(240+80,184+50), new Scalar(0, 255, 255),3)
		Core.line(mat, new Point(240+80,184+50), new Point(240-80,184+50), new Scalar(0, 255, 255),3)
		Core.line(mat, new Point(240-80,184+50),new Point(240-80,184-50), new Scalar(0, 255, 255),3)
		}
		var grey = new Mat
		Imgproc.cvtColor(mat, grey, Imgproc.COLOR_RGB2GRAY)
		Imgproc.threshold(grey, grey, 75, 255, Imgproc.THRESH_BINARY);
		var cors = newArrayList()
		var hier = new Mat
		Imgproc.findContours(grey, cors, hier, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_NONE)
		var vertices = new MatOfPoint2f
		var fours = newArrayList()
		for (var i = 0; i < cors.size; i++) {

			Imgproc.approxPolyDP(new MatOfPoint2f(cors.get(i).toArray), vertices,
			Imgproc.arcLength(new MatOfPoint2f(cors.get(i).toArray), true) * 0.02, true)
			var area = Imgproc.contourArea(cors.get(i))
			if (vertices.size.height == 4 && area > 1000 && area < 30000) {
				if (priorities(vertices, mat))
					fours.add(i)
			}

		}
		return mat
	}

	def priorities(MatOfPoint2f test, Mat mat) {
		var x1 = test.get(0, 0).get(0)
		var y1 = test.get(0, 0).get(1)
		var x2 = test.get(1, 0).get(0)
		var y2 = test.get(1, 0).get(1)
		var x3 = test.get(2, 0).get(0)
		var y3 = test.get(2, 0).get(1)
		var x4 = test.get(3, 0).get(0)
		var y4 = test.get(3, 0).get(1)

		
		var p1 = new Point(new Scalar(x1, y1).^val)
		var p2 = new Point(new Scalar(x2, y2).^val)
		var p3 = new Point(new Scalar(x3, y3).^val)
		var p4 = new Point(new Scalar(x4, y4).^val)

		if(p1.x-p4.x>100)
		{
			var temp=p4
			p4=p1
			p1=p2
			p2=temp
		}
		
		var js = Math.min(Math.min(p2.x, p4.x), Math.min(p1.x, p3.x)) as int
		var je = Math.max(Math.max(p2.x, p4.x), Math.max(p1.x, p3.x)) as int
		var ks = Math.min(Math.min(p2.y, p4.y), Math.min(p1.y, p3.y)) as int
		var ke = Math.max(Math.max(p2.y, p4.y), Math.max(p1.y, p3.y)) as int
		var mid = new Point((js + je) / 2, (ks + ke) / 2)

		if ((Math.abs(mid.x - 240) < 80 && Math.abs(mid.y - 184) < 50)||tracked) {
			
				var black = 0

				var sub=new Rect(p2,p4)
				var temp = mat.submat(sub)
				for (var j = 0; j < temp.rows; j++)
					for (var k = 0; k < temp.cols; k++) {
						if (temp.get(j, k) != null)
							if ((temp.get(j, k).get(0) + temp.get(j, k).get(1) + temp.get(j, k).get(2)) < 700)
								black++
					}
				var area=temp.size.area
				if (black / (area) > 0.85) 
				{
					if(!tracked)
					{
					ctr++
					Core.line(mat, p1, p2, new Scalar(255, 255, 255))
					Core.line(mat, p2, p3, new Scalar(255, 255, 255))
					Core.line(mat, p3, p4, new Scalar(255, 255, 255))
					Core.line(mat, p4, p1, new Scalar(255, 255, 255))
					if(ctr>=15)
					{
						tracked=true
					    trackedarea=temp.size.area
					    last=mid
					    Log.e("shitz","tracked:"+last.toString)
					}
					}
					if(tracked&&Math.abs(area-trackedarea)<600)
					{
					Core.line(mat, p1, p2, new Scalar(255, 255, 255))
					Core.line(mat, p2, p3, new Scalar(255, 255, 255))
					Core.line(mat, p3, p4, new Scalar(255, 255, 255))
					Core.line(mat, p4, p1, new Scalar(255, 255, 255))
					  	if(mid.x>last.x)
					  	{Core.line(mat, p2, p3, new Scalar(255, 255, 255),3)
					  	 //messageServer("x:"+((mid.x-last.x)as int).toString)
					  	}
					  	if(mid.x<last.x)
					  	{Core.line(mat, p4, p1, new Scalar(255, 255, 255),3)
					  	 //messageServer("x:"+((mid.x-last.x)as int).toString)
					  	}
					  	if(mid.y>last.y)
					  	{Core.line(mat, p3, p4, new Scalar(255, 255, 255),3)
					  		//messageServer("y:"+((mid.y-last.y)as int).toString)
					  	}
					  	if(mid.y<last.y)
					  	{Core.line(mat, p1, p2, new Scalar(255, 255, 255),3)
					  		//messageServer("y:"+((mid.y-last.y)as int).toString)
					  	}
					  	
					  	Core.line(mat, new Point(mid.x-3,mid.y),new Point(mid.x+3,mid.y), new Scalar(127, 255,12),5)
					  	Core.line(mat, new Point(last.x-3,last.y),new Point(last.x+3,last.y), new Scalar(255, 255, 255),3)
					  	last=mid
					}
					return true
				} else
					return false
			
		}

	}
	/*def messageServer(String arg) {
		new Thread{
			
		override run() {
		var ip = "192.168.137.1"
		var client = new Socket(ip,1236)
		println("Just connected to " + client.getRemoteSocketAddress())
		var outToServer = client.getOutputStream
		var out = new DataOutputStream(outToServer)
		out.writeUTF(arg)
		client.close}
		}.start
		true
	}
	
	override onBackPressed() {
	messageServer("exit")	
	super.onBackPressed()
	}*/
	
}
