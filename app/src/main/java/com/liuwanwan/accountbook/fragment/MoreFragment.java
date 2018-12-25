package com.liuwanwan.accountbook.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
//import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.liuwanwan.accountbook.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;
import android.view.View.*;
import android.content.*;
import android.app.*;


public class MoreFragment extends Fragment implements View.OnClickListener {
    private View mRootView;
    //private CircleImageView mCivPortrait;
    private TextView mTvNickname;
    private TextView mTvSetting;
    private TextView mTvSync;
    private TextView mTvHelp;
    private TextView mTvTheme;
    private TextView mTvResponse;
    private TextView mTvResume;
    //private AVUser mCurrentUser;
    private Uri mUri;
    private static String path = "/sdcard/JudyAccount/myHead/";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = View.inflate(getContext(), R.layout.fragment_more, null);
        }
        assignViews();
        initEvent();
        return mRootView;
    }

    private void initEvent() {
        //mCivPortrait.setOnClickListener(this);
        mTvSetting.setOnClickListener(this);
        mTvSync.setOnClickListener(this);
        mTvHelp.setOnClickListener(this);
        mTvTheme.setOnClickListener(this);
        mTvResponse.setOnClickListener(this);
        mTvResume.setOnClickListener(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //mCurrentUser = AVUser.getCurrentUser();
        //((HomeActivity)getActivity()).showUserDataFromNet(mCivPortrait, mTvNickname);
    }



    private void assignViews() {
        //mCivPortrait = (CircleImageView) mRootView.findViewById(R.id.civ_portrait);
        mTvNickname = (TextView) mRootView.findViewById(R.id.tv_nickname);
        mTvSetting = (TextView) mRootView.findViewById(R.id.tv_setting);
        mTvSync = (TextView) mRootView.findViewById(R.id.tv_sync);
        mTvHelp = (TextView) mRootView.findViewById(R.id.tv_help);
        mTvTheme = (TextView) mRootView.findViewById(R.id.tv_theme);
        mTvResponse = (TextView) mRootView.findViewById(R.id.tv_response);
        mTvResume = (TextView) mRootView.findViewById(R.id.tv_note);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.civ_portrait:
                showTypeDialog();
                break;
            case R.id.tv_setting:

                break;
            case R.id.tv_sync:
                syncRecordDb();
                Toast.makeText(getContext(), "上传成功!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.tv_help:

                break;
            case R.id.tv_theme:

                break;
            case R.id.tv_response:

                break;
            case R.id.tv_note:
				showNote();
                break;
        }
    }
	private void showNote(){
		String nextFuc="\t\t\t\t1.完善资产账户收支明细功能-12.25完成\n"+
			"\t\t\t\t2.完善数据同步功能\n"+
			"\t\t\t\t3.完善支出、收入流水和资产的月度、年度趋势图\n"+
		"\t\t\t\t4.完善预算设置";
		AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();//创建对话框
        dialog.setIcon(R.mipmap.ic_launcher);//设置对话框icon
        dialog.setTitle("下一步开发计划");//设置对话框标题
        dialog.setMessage(nextFuc);//设置文字显示内容
        //分别设置三个button
        dialog.setButton(DialogInterface.BUTTON_POSITIVE,"确定", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();//关闭对话框
				}
			});
        dialog.setButton(DialogInterface.BUTTON_NEUTRAL,"点我试试", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) { }
			});
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();//关闭对话框
				}
			});
        dialog.show();//显示对话框
	}
    private void syncRecordDb() {
        compareTimeAndSync();
    }

    private void compareTimeAndSync() {
        //RecordDao recordDao = new RecordDao(getContext());
        //List<RecordBean> recordBeanList = recordDao.queryAllRecord();
        //if (recordBeanList == null || recordBeanList.size() < 1) {
         //   downloadDb();
           // return;
        //}
        //Date localTime = recordBeanList.get(0).calendar.getTime();
        //Date netTime = (Date) mCurrentUser.get("netRecordDbDate");
        //if (netTime == null) {
       //     uploadDb();
       //     return;
       // }

       // if (localTime.after(netTime)) {
       //     uploadDb();
       // } else {
      //      downloadDb();
       // }

    }
/*
    private void uploadDb() {
        File dbFile = new File(getActivity().getApplication().getDatabasePath("Records")+".db");
        if (!dbFile.exists()) {
            Toast.makeText(getContext(), "本地和云存储均没有数据！", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            final String recordDbObjectId = (String) mCurrentUser.get("recordDbObjectId");
            if (recordDbObjectId != null && !recordDbObjectId.equals("")) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final AVFile avFileDel;
                        try {
                            avFileDel = AVFile.withObjectId(recordDbObjectId);
                            avFileDel.deleteInBackground(new DeleteCallback() {
                                @Override
                                public void done(AVException e) {

                                }
                            });
                        } catch (AVException e) {
                            e.printStackTrace();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

            }


            final AVFile avFile = AVFile.withAbsoluteLocalPath("Records.db", dbFile.getAbsolutePath());
            avFile.saveInBackground(new SaveCallback() {
                @Override
                public void done(AVException e) {
                    mCurrentUser.put("recordDbObjectId", avFile.getObjectId());
                    mCurrentUser.put("netRecordDbDate", new Date());
                    mCurrentUser.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException e) {

                        }
                    });
                }
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
*/
    private void downloadDb() {

    }

    private void showTypeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final AlertDialog dialog = builder.create();
        //View view = View.inflate(getActivity(), R.layout.dialog_select_photo, null);
        //TextView tv_select_gallery = (TextView) view.findViewById(R.id.tv_select_gallery);
        //TextView tv_select_camera = (TextView) view.findViewById(R.id.tv_select_camera);
        /*tv_select_gallery.setOnClickListener(new View.OnClickListener() {// 在相册中选取
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(Intent.ACTION_PICK, null);
                intent1.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent1, 1);
                dialog.dismiss();
            }
        });
        tv_select_camera.setOnClickListener(new View.OnClickListener() {// 调用照相机
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent2.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "head.jpg")));
                startActivityForResult(intent2, 2);// 采用ForResult打开
                dialog.dismiss();
            }
        });*/
        //dialog.setView(view);
        dialog.show();
    }

    public void cropPhoto(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, 3);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    // 裁剪图片
                    mUri = data.getData();
                    cropPhoto(mUri);
                }

                break;
            case 2:
                if (resultCode == RESULT_OK) {
                    // 裁剪图片
                    File temp = new File(Environment.getExternalStorageDirectory() + "/head.jpg");
                    mUri = Uri.fromFile(temp);
                    cropPhoto(mUri);
                }

                break;
            case 3:
               /* if (data != null) {
                    Bundle extras = data.getExtras();
                    Bitmap head = extras.getParcelable("data");
                    if (head != null) {
                        setPicToView(head);
                        mCivPortrait.setImageBitmap(head);// 用ImageView显示出来
                        ///上传服务器代码

                        try {
                            final String headObjectId = (String) mCurrentUser.get("headObjectId");
                            if (headObjectId != null && !headObjectId.equals("")) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        final AVFile avFileDel;
                                        try {
                                            avFileDel = AVFile.withObjectId(headObjectId);
                                            avFileDel.deleteInBackground(new DeleteCallback() {
                                                @Override
                                                public void done(AVException e) {

                                                }
                                            });
                                        } catch (AVException e) {
                                            e.printStackTrace();
                                        } catch (FileNotFoundException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }).start();

                            }
                            final AVFile avFile = AVFile.withAbsoluteLocalPath("head.png", Environment.getExternalStorageDirectory()+"/JudyAccount/myHead/head.jpg" );
                            avFile.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(AVException e) {
                                    final String url = avFile.getUrl();
                                    if (url != null && !url.equals("")) {
                                        mCurrentUser.put("portraitUrl", url);
                                        mCurrentUser.put("headObjectId", avFile.getObjectId());
                                        mCurrentUser.saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(AVException e) {

                                            }
                                        });
                                    }
                                }
                            });
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }*/
                break;
            default:
                break;

        }
    }

    private void setPicToView(Bitmap mBitmap) {
        String sdStatus = Environment.getExternalStorageState();
        if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
            return;
        }
        FileOutputStream b = null;
        File file = new File(path);
        file.mkdirs();// 创建文件夹
        String fileName = path + "head.jpg";// 图片名字
        try {
            b = new FileOutputStream(fileName);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                // 关闭流
                b.flush();
                b.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
