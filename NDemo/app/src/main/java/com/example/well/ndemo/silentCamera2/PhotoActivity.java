//package com.example.well.ndemo.silentCamera2;
//
//import android.app.Activity;
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.Matrix;
//import android.hardware.Camera;
//import android.hardware.Camera.PreviewCallback;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Environment;
//import android.os.Handler;
//import android.os.Message;
//import android.view.SurfaceHolder;
//import android.view.SurfaceView;
//import android.widget.Toast;
//
//import com.example.well.ndemo.R;
//
//import java.io.ByteArrayInputStream;
//import java.io.ByteArrayOutputStream;
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.lang.reflect.Field;
//import java.lang.reflect.Method;
//import java.util.ArrayList;
//import java.util.List;
//
//
//public class PhotoActivity extends Activity implements PreviewCallback {
//
//	private SurfaceView mSurfaceView = null;
//	private SurfaceHolder mSurfaceHolder = null;
//	private Camera mCamera = null;
//	private List<PictureBean> listBuffer = new ArrayList<PictureBean>();
//	private String filePath;
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_photo);
//		try {
//			initData();
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}
//	}
//
//	public void initData() throws FileNotFoundException {
//		mSurfaceView = (SurfaceView) this.findViewById(R.id.surfaceview);
//		mSurfaceHolder = mSurfaceView.getHolder();
//		mSurfaceHolder.addCallback(new SurfaceHolderCallback());
//		mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
//
//		boolean result = Environment.getExternalStorageState().equals(
//				Environment.MEDIA_MOUNTED);
//		if (result) {
//			// sdcard���ڵ������
//			filePath = Environment.getExternalStorageDirectory()
//					.getAbsolutePath();
//			filePath += "/pic.jpg";
//		} else {
//			// sdcard�����ڵ������
//			this.openFileOutput("pic.jpg", Context.MODE_WORLD_WRITEABLE
//					| Context.MODE_WORLD_READABLE);
//			File file = this.getFilesDir();
//			file = new File(file, "pic.jpg");
//			filePath = file.getAbsolutePath();
//		}
//
//	}
//
//	@Override
//	public void onDestroy() {
//		super.onDestroy();
//	}
//
//	/**
//	 * ��Ƭ�������ϴ�
//	 */
//	private void doPhotographics() {
//		new Thread() {
//			public void run() {
//				for (int i = 0; i < listBuffer.size(); i++) {
//					PictureBean bean = listBuffer.get(i);
//					byte[] buffer = imageZoom(bean);
//					if (buffer != null) {
//						bean.setBuffer(buffer);
//						listBuffer.set(i, bean);
//					}
//				}
//				PictureBean selectBean = choosebitMap(listBuffer);
//				byte[] data = selectBean.getBuffer();
//				try {
//					FileOutputStream fout = new FileOutputStream(filePath);
//					fout.write(data);
//					fout.close();
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//				handler.sendEmptyMessage(0);
//			}
//		}.start();
//	}
//
//	private Handler handler = new Handler() {
//
//		@Override
//		public void handleMessage(Message msg) {
//			if (msg.what == 0) {
//				Toast.makeText(PhotoActivity.this, "������ɣ�", Toast.LENGTH_LONG)
//						.show();
//			}
//			super.handleMessage(msg);
//		}
//
//	};
//
//	/**
//	 * ��ѡͼƬ
//	 *
//	 * @param beans
//	 * @return
//	 */
//
//	private PictureBean choosebitMap(List<PictureBean> beans) {
//		int max = 0;
//		PictureBean pictureBean = null;
//		for (int i = 0; i < beans.size(); i++) {
//			Bitmap bitmap = BitmapFactory.decodeByteArray(beans.get(i)
//					.getBuffer(), 0, beans.get(i).getBuffer().length);
//			int grade = grayByPixels(bitmap);
//			if (grade > max) {
//				max = grade;
//				pictureBean = beans.get(i);
//			}
//		}
//		return max >= 16 ? pictureBean : null;
//	}
//
//	/**
//	 * ��ɫrgbֵת��
//	 *
//	 * @param colorValue
//	 * @return
//	 */
//	private int getGrayNumColor(int colorValue) {
//		return (Color.red(colorValue) * 19595 + Color.green(colorValue) * 38469 + Color
//				.blue(colorValue) * 7472) >> 16;
//	}
//
//	/**
//	 * ��������ƽ��ֵ
//	 *
//	 * @param bitMap
//	 * @return
//	 */
//	private int grayByPixels(Bitmap bitMap) {
//		int totalNum = 0;
//		int totalGrayPix = 0;
//		for (int i = 0; i < bitMap.getWidth(); i++) {
//			for (int j = 0; j < bitMap.getHeight(); j++) {
//				int tmpValue = getGrayNumColor(bitMap.getPixel(i, j));
//				totalNum += 1;
//				totalGrayPix += tmpValue;
//			}
//		}
//		// ȡ�������ص�ƽ��ֵ
//		int avgGray = totalGrayPix / totalNum;
//		return avgGray;
//	}
//
//	/**
//	 * �޷���ֵ����
//	 *
//	 * @param packageName
//	 * @param methodName
//	 * @param parasObj
//	 * @param isStatic
//	 * @return
//	 */
//	private void setupMethod(String packageName, String methodName,
//							 @SuppressWarnings("rawtypes") Class[] parasClass,
//							 Object[] parasObj, boolean isStatic) {
//		try {
//			@SuppressWarnings("rawtypes")
//			Class myClass = Class.forName(packageName);
//			Method method = myClass.getMethod(methodName, parasClass);
//			if (method != null)
//				method.invoke(myClass, parasObj);
//		} catch (Exception e) {
//		}
//	}
//
//	/**
//	 * ���䷽��
//	 *
//	 * @param packageName
//	 * @param methodName
//	 * @param parasClass
//	 * @param parasObj
//	 * @return
//	 */
//	private Object getValueOfMethod(String packageName, String methodName,
//									@SuppressWarnings("rawtypes") Class[] parasClass,
//									Object[] parasObj, boolean isStatic) {
//		try {
//			Class<?> myClass = Class.forName(packageName);
//			Object resultObj = null;
//			if (parasClass != null || parasObj != null) {
//				Method method = myClass.getMethod(methodName, parasClass);
//				resultObj = method.invoke(
//						isStatic ? myClass : myClass.newInstance(), parasObj);
//			} else {
//				Method method = myClass.getMethod(methodName);
//				resultObj = method.invoke(isStatic ? myClass : myClass
//						.newInstance());
//			}
//			return resultObj;
//		} catch (Exception e) {
//		}
//		return null;
//	}
//
//	/**
//	 * �����ֶ�
//	 *
//	 * @param packageName
//	 * @param fieldName
//	 * @return
//	 */
//	private Object reflectLayoutID(String packageName, String fieldName,
//								   boolean isStatic) {
//		Object layoutID = null;
//		try {
//			@SuppressWarnings("rawtypes")
//			Class myClass = null;
//			myClass = Class.forName(packageName);
//			/*******/
//			Field field = myClass.getField(fieldName);
//			layoutID = field.get(isStatic ? myClass : myClass.newInstance());
//		} catch (Exception e) {
//		}
//		return layoutID;
//	}
//
//	/**
//	 * �����ֶ�1
//	 * @param fieldName
//	 * @return
//	 */
//	private Object reflectLayoutID(Object object, String fieldName) {
//		Object layoutID = null;
//		try {
//			@SuppressWarnings("rawtypes")
//			Class myClass = null;
//			myClass = object.getClass();
//			Field field = myClass.getField(fieldName);
//			layoutID = field.getInt(object);
//		} catch (Exception e) {
//		}
//		return layoutID;
//	}
//
//	/**
//	 * ͼƬ����
//	 * @return
//	 */
//	private byte[] imageZoom(PictureBean bean) {
//		Matrix matrix = new Matrix();
//		int j = 0;
//		try {
//			Object objId = reflectLayoutID("android.os.Build$VERSION_CODES",
//					"GINGERBREAD", true);
//			boolean result = objId != null;
//			if (result) {
//				int currenCodes = (Integer) objId;
//				if (Build.VERSION.SDK_INT >= currenCodes) {
//					int numbers = (Integer) getValueOfMethod(
//							"android.hardware.Camera", "getNumberOfCameras",
//							null, null, true);
//					int CAMERA_FACING_FRONT = (Integer) reflectLayoutID(
//							"android.hardware.Camera$CameraInfo",
//							"CAMERA_FACING_FRONT", true);
//					for (int i = 0; i < numbers; i++) {
//						Object cameraInfo = Class.forName(
//								"android.hardware.Camera$CameraInfo")
//								.newInstance();
//						setupMethod(
//								"android.hardware.Camera",
//								"getCameraInfo",
//								new Class[] { int.class, cameraInfo.getClass() },
//								new Object[] { i, cameraInfo }, true);
//						int facing = (Integer) reflectLayoutID(cameraInfo,
//								"facing");
//						if (facing == CAMERA_FACING_FRONT) {
//							j = 1;
//							break;
//						}
//					}
//				}
//			}
//		} catch (Exception e) {
//		}
//		if (j == 0) {
//			matrix.setRotate(90);
//		} else {
//			matrix.setRotate(-90);
//		}
//		BitmapFactory.Options options = new BitmapFactory.Options();
//		options.inDither = false; /* ������ͼƬ�������� */
//		options.inPreferredConfig = null; /* �����ý���������ѷ�ʽ���� */
//		// Bitmap mBitmap = BitmapFactory.decodeByteArray(buffer, 0,
//		// buffer.length, options);
//		Bitmap mBitmap = PictureBean.beanToBitmap(bean, true);
//		if (mBitmap == null)
//			return null;
//		Bitmap bitMap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(),
//				mBitmap.getHeight(), matrix, true);
//		ByteArrayOutputStream baos = new ByteArrayOutputStream();
//		bitMap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//		if (bean.getBuffer().length / 1024 > 1024) {// �ж����ͼƬ����1M,����ѹ������������ͼƬ��BitmapFactory.decodeStream��ʱ���
//			baos.reset();// ����baos�����baos
//			bitMap.compress(Bitmap.CompressFormat.JPEG, 50, baos);// ����ѹ��50%����ѹ��������ݴ�ŵ�baos��
//		}
//		int w = options.outWidth;
//		int h = options.outHeight;
//		// ���������ֻ��Ƚ϶���800*480�ֱ��ʣ����ԸߺͿ���������Ϊ
//		float hh = 800f;// �������ø߶�Ϊ800f
//		float ww = 480f;// �������ÿ��Ϊ480f
//		// ���űȡ������ǹ̶��������ţ�ֻ�ø߻��߿�����һ�����ݽ��м��㼴��
//		int be = 1;// be=1��ʾ������
//		if (w > h && w > ww) {// �����ȴ�Ļ����ݿ�ȹ̶���С����
//			be = (int) (options.outWidth / ww);
//		} else if (w < h && h > hh) {// ����߶ȸߵĻ����ݿ�ȹ̶���С����
//			be = (int) (options.outHeight / hh);
//		}
//		if (be <= 0)
//			be = 1;
//		options.inJustDecodeBounds = false;
//		options.inSampleSize = be;// �������ű���
//		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
//		bitMap = BitmapFactory.decodeStream(isBm, null, options);
//		return compressImage(bitMap);// ѹ���ñ�����С���ٽ�������ѹ��
//
//	}
//
//	/***
//	 * ����ͼƬ��������50kb����
//	 *
//	 * @return
//	 */
//	private byte[] compressImage(Bitmap image) {
//		// ��bitmap���������У�����bitmap�Ĵ�С����ʵ�ʶ�ȡ��ԭ�ļ�Ҫ��
//		ByteArrayOutputStream baos = new ByteArrayOutputStream();
//		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//
//		int options = 100;
//		while (baos.toByteArray().length / 1024 > 50) { // ѭ���ж����ѹ����ͼƬ�Ƿ����50kb,���ڼ���ѹ��
//			baos.reset();// ����baos�����baos
//			options -= 10;// ÿ�ζ�����10
//			image.compress(Bitmap.CompressFormat.JPEG, options, baos);// ����ѹ��options%����ѹ��������ݴ�ŵ�baos��
//
//			if (options == 10)
//				break;// ����optionsΪ0����ɱ�����
//		}
//		image.recycle();
//		return baos.toByteArray();
//	}
//
//	@Override
//	public void onPreviewFrame(byte[] data, Camera camera) {
//		int w = camera.getParameters().getPreviewSize().width;
//		int h = camera.getParameters().getPreviewSize().height;
//		listBuffer.add(new PictureBean(data, w, h));
//		if (listBuffer.size() >= 5) {
//			mCamera.stopPreview();
//			PhotoActivity.this.finish();
//			doPhotographics();
//		}
//	}
//
//	public class SurfaceHolderCallback implements SurfaceHolder.Callback {
//
//		public void surfaceChanged(SurfaceHolder holder, int format, int width,
//								   int height) {
//			Bitmap getpage;
//			getpage = Bitmap.createBitmap(800, 380, Bitmap.Config.ARGB_8888);
//			Canvas canvas = new Canvas(getpage);
//			canvas.drawColor(Color.LTGRAY);// ������Խ����κλ�ͼ����
//			canvas.save(Canvas.ALL_SAVE_FLAG);
//			canvas.restore();
//		}
//
//		public void surfaceCreated(SurfaceHolder holder) {
//			/*************/
//			int SDK_INT = Build.VERSION.SDK_INT;
//			Object objId = reflectLayoutID("android.os.Build$VERSION_CODES",
//					"GINGERBREAD", true);
//			boolean result = objId != null;
//			if (result) {
//				int currenCodes = (Integer) objId;
//				if (SDK_INT >= currenCodes) {
//					int numbers = (Integer) getValueOfMethod(
//							"android.hardware.Camera", "getNumberOfCameras",
//							null, null, true);
//					int CAMERA_FACING_FRONT = (Integer) reflectLayoutID(
//							"android.hardware.Camera$CameraInfo",
//							"CAMERA_FACING_FRONT", true);
//					for (int i = 0; i < numbers; i++) {
//						try {
//							Object cameraInfo = Class.forName(
//									"android.hardware.Camera$CameraInfo")
//									.newInstance();
//							setupMethod("android.hardware.Camera",
//									"getCameraInfo", new Class[] { int.class,
//											cameraInfo.getClass() },
//									new Object[] { i, cameraInfo }, true);
//							int facing = (Integer) reflectLayoutID(cameraInfo,
//									"facing");
//							if (CAMERA_FACING_FRONT == facing) {
//								mCamera = (Camera) getValueOfMethod(
//										"android.hardware.Camera", "open",
//										new Class[] { int.class },
//										new Object[] { i }, true);
//							}
//						} catch (Exception e) {
//							e.printStackTrace();
//						}
//					}
//				}
//			}
//			/*********************/
//			try {
//				if (mCamera == null)
//					mCamera = Camera.open();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//			try {
//				mCamera.setPreviewDisplay(mSurfaceHolder);
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//			mCamera.setDisplayOrientation(90);
//			try {
//				mCamera.reconnect();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//			mCamera.setPreviewCallback(PhotoActivity.this);
//			mCamera.startPreview();
//			try {
//				Thread.sleep(800);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//
//			// /*****/
//			// mCamera.takePicture(null, null, pictureCallback);
//		}
//
//		public void surfaceDestroyed(SurfaceHolder holder) {
//			if (mCamera != null) {
//				mCamera.stopPreview();
//				mCamera.unlock();
//				mCamera.release();
//				mCamera = null;
//			}
//		}
//	}
//}
