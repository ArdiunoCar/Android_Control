package wificar;
/**
 * 这是控制界面
 * 包括上下左右，
 * 视频
 * 拍照
* */
 
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;


import my.wificar.R;
import android.R.color;
import android.R.integer;
import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MyVideo extends Activity
{
    private ImageView TakePhotos;
	private ImageView ViewPhotos;
	private ImageView BtnForward,BtnBackward,BtnLeft,BtnRight,BtnStop,update;
	private TextView receiveDataText;
    URL videoUrl;
	public static String CameraIp;
	public static String CtrlIp;
	public static String CtrlPort;
    MySurfaceView r;
    private  Socket socket;
    OutputStream socketWriter ;
	InputStream socketReader ;
	Handler handler;//主线程接收“接收数据线程”
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//隐去标题（应用的名字必须要写在setContentView之前，否则会有异常）
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.myvideo);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        r = (MySurfaceView)findViewById(R.id.mySurfaceViewVideo);
        TakePhotos = (ImageView)findViewById(R.id.TakePhoto);
        ViewPhotos = (ImageView) findViewById(R.id.ViewPhoto);
        
        BtnForward = (ImageView)findViewById(R.id.button_forward);
        BtnBackward = (ImageView)findViewById(R.id.button_backward);
        BtnLeft = (ImageView)findViewById(R.id.button_left);
        BtnRight = (ImageView)findViewById(R.id.button_right);
        BtnStop= (ImageView)findViewById(R.id.button_stop);
//        update = (Button)findViewById(R.id.updata);
		receiveDataText = (TextView)findViewById(R.id.receive_data_text);

		Intent intent = getIntent();
		//从Intent当中根据key取得value
		CameraIp = "http://192.168.1.1:8080/?action=stream";
		CtrlIp= "192.168.1.1";
		CtrlPort="2001";
		Log.d("wifirobot", "control is :++++"+CtrlIp);
		Log.d("wifirobot", "CtrlPort is :++++"+CtrlPort);
		r.GetCameraIP(CameraIp);
		InitSocket();

		GetLogTask task = new GetLogTask();
		task.execute(null,null,null);

		handler= new Handler(){
			@Override
			public void handleMessage(Message msg){
				receiveDataText.setText(receiveDataText.getText().toString() + (String)msg.obj);
			}
		};
//
//		update.setOnClickListener(new OnClickListener(){
//			public void onClick(View arg0) {
//				// TODO Auto-generated method stub
//				try
//				{
//					handler= new Handler(){
//						@Override
//						public void handleMessage(Message msg){
//							receiveDataText.setText("");
//						}
//					};
//				}
//				catch (Exception e)
//				{
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//
//		});


		BtnForward.setOnClickListener(new OnClickListener(){

			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				  try 
			        {
                         String order ="$1,0,0,0,0,0,0,0,0,0,0,100,4200#";
			             socketWriter.write(order.getBytes());
			             socketWriter.flush();
					} 
			         catch (Exception e) 
			        {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
		});
		
		BtnBackward.setOnClickListener(new OnClickListener(){

			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				try 
		        {
                     String order ="$2,0,0,0,0,0,0,0,0,0,0,100,4200#";
                     socketWriter.write(order.getBytes());
		             socketWriter.flush();
				} 
		         catch (Exception e) 
		        {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		});
		BtnRight.setOnClickListener(new OnClickListener(){

			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				try 
		        {
                     String order ="$4,0,0,0,0,0,0,0,0,0,0,100,4200#";
                     socketWriter.write(order.getBytes());
		             socketWriter.flush();
				} 
		         catch (Exception e) 
		        {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			
		});
		BtnLeft.setOnClickListener(new OnClickListener(){

			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				try 
		        {
                     String order ="$3,0,0,0,0,0,0,0,0,0,0,100,4200#";
                     socketWriter.write(order.getBytes());
		             socketWriter.flush();
				} 
		         catch (Exception e) 
		        {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		});
		BtnStop.setOnClickListener(new OnClickListener(){

			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				try 
		        {
                     String order ="$0,0,0,0,0,0,0,0,0,0,0,100,4200#";
                     socketWriter.write(order.getBytes());
		             socketWriter.flush();
				} 
		         catch (Exception e) 
		        {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			
		});
		TakePhotos.setOnClickListener(new OnClickListener(){

			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(null!=Constant.handler)
				{
				 Message message = new Message();      
		            message.what = 1;      
		            Constant.handler.sendMessage(message);  
				}
			}
			
		});
		
		ViewPhotos.setOnClickListener(new OnClickListener() 
		{

			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent=new Intent();
				intent.setClass(MyVideo.this, BgPictureShowActivity.class);
    			//通过Intent对象启动另外一个Activity
				MyVideo.this.startActivity(intent);

			}
			
		});
		
	}

  	public void InitSocket()
  	{
	  
			 try {
				socket = new Socket(InetAddress.getByName(CtrlIp),Integer.parseInt(CtrlPort));
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 try {
				 socketWriter = socket.getOutputStream();
				 socketReader = socket.getInputStream();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    	//Toast.makeText(this,"初始化网络失败！"+e.getMessage(),Toast.LENGTH_LONG).show();
  	}


	public class GetLogTask extends AsyncTask<Void,Void,String>
	{
//		@Override
//		protected String doInBackground(Void...param){
//			try {
//				DataInputStream input = new DataInputStream(socketReader);
//				byte[] b = new byte[10000];
//				while(true)
//				{
//					int length = input.read(b);
//					String Msg = new String(b, 0, length, "gb2312");
//					Log.v("data",Msg);
//
//					Message msg = new Message();
//					msg.obj = Msg;
//					(MyVideo.this).handler.sendMessage(msg);
//				}
//			}catch(Exception ex)
//			{
//				ex.printStackTrace();
//			}
//			return "";
//		}

		@Override
		protected String doInBackground(Void...param){
			try {
				byte[] bt ;
				while(true)
				{
					bt = readStream(socketReader);
					String Msg = new String(bt);
//					receiveDataText.setText(receiveDataText.getText().toString() + str);
					Log.v("data",Msg);

					Message msg = new Message();
					msg.obj = Msg;
					(MyVideo.this).handler.sendMessage(msg);
				}
			}catch(Exception ex)
			{
				ex.printStackTrace();
			}
			return "";
		}

		public  byte[] readStream(InputStream inStream) throws Exception {
			int count = 0;
			while (count == 0) {
				count = inStream.available();
			}
			byte[] b = new byte[count];
			inStream.read(b);
			return b;
		}
	}




	public void onDestroy() 
	{
		super.onDestroy();
	}

	private long exitTime = 0;
	@Override  
    public boolean onKeyDown(int keyCode, KeyEvent event)   
    {  
		 if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN)  
		 {	//点击了后退键按钮
		         if((System.currentTimeMillis()-exitTime) > 2500)  //System.currentTimeMillis()无论何时调用，肯定大于2500   
				 {  
					 Toast.makeText(getApplicationContext(), "再按一次退出程序",Toast.LENGTH_SHORT).show();                                  
		        	 exitTime = System.currentTimeMillis();  
				 }  
		         else  
		         {  
		             finish();  
		             System.exit(0);  
		         }  
		                   
		         return true;  
		 }  
		 return super.onKeyDown(keyCode, event);  
    }  

}


