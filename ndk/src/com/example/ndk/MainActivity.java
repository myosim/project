package com.example.ndk;

import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.R;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

	 private final String SERVER_ADDRESS = "http://www.learnsdk.com/network/user/"; //서버 주소(php파일이 저장되어있는 경로까지, 절대로 127.0.0.1이나 localhost를 쓰면 안된다!! 그러므로 아이피는 자기 아이피로.....)
     
	    EditText edtname;
	    EditText edtprice;
	    Button btninsert;
	    Button btnsearch;
	     
	    ListView list;
	    ArrayList<String> data;
	    ArrayAdapter<String> adapter;
	    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		edtname = (EditText )findViewById(R.id.editText1);
        edtprice = (EditText )findViewById(R.id.editText2);
        btninsert = (Button )findViewById(R.id.button1);
        btnsearch = (Button )findViewById(R.id.button2);
         
        list = (ListView )findViewById(R.id.listView1);
        data = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, data);
        list.setAdapter(adapter);
         //
        btninsert.setOnClickListener(new View.OnClickListener() { //입력 버튼을 눌렀을 때
             
            public void onClick(View v) {
                // TODO Auto-generated method stub

            	Log.d("filter", "Click Event");
                if( edtname.getText().toString().equals("") ||
                        edtprice.getText().toString().equals("") ) { //이름이나 가격중에 하나라도 입력이 안돼있을때
                    Toast.makeText(MainActivity.this,
                            "이름이나 가격을 입력하세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                     
                    runOnUiThread(new Runnable() {
                         
                        public void run() {
                            // TODO Auto-generated method stub
                            String name = edtname.getText().toString();
                            String price = edtprice.getText().toString();
                             
                            try {
                                URL url = new URL(SERVER_ADDRESS + "/login.php?"
                                        + "name=" + URLEncoder.encode(name, "UTF-8")
                                        + "&price=" + URLEncoder.encode(price, "UTF-8")); //변수값을 UTF-8로 인코딩하기 위해 URLEncoder를 이용하여 인코딩함
                                url.openStream(); //서버의 DB에 입력하기 위해 웹서버의 insert.php파일에 입력된 이름과 가격을 넘김
                                 
                                String result = getXmlData("insertresult.xml", "result"); //입력 성공여부
                                 
                                if(result.equals("1")) { //result 태그값이 1일때 성공
                                    Toast.makeText(MainActivity.this,
                                            "DB insert 성공", Toast.LENGTH_SHORT).show();
                                     
                                    edtname.setText("");
                                    edtprice.setText("");
                                }
                                else //result 태그값이 1이 아닐때 실패
                                    Toast.makeText(MainActivity.this,
                                            "DB insert 실패", Toast.LENGTH_SHORT).show();
                            } catch(Exception e) {
                                Log.e("Error", e.getMessage());
                            }
                        }
                    });
                }
            });
        
        btnsearch.setOnClickListener(new View.OnClickListener() {
            
            public void onClick(View arg0) {
            	Log.d("filter", "Click Event");
                // TODO Auto-generated method stub
                final Handler handler = new Handler();
                runOnUiThread(new Runnable() {
                     
                    public void run() {
                        // TODO Auto-generated method stub
                        final ProgressDialog dialog = ProgressDialog.show(
                                MainActivity.this,
                                "불러오는중.....", "잠시만 기다려주세요.");
                         
                        handler.post(new Runnable() {
                             
                            public void run() {
                                // TODO Auto-generated method stub
                                try {
                                    data.clear(); //반복적으로 누를경우 똑같은 값이 나오는 것을 방지하기 위해 data를 클리어함
                                    URL url = new URL(SERVER_ADDRESS + "/login.php");
                                    url.openStream(); //서버의 serarch.php파일을 실행함
                         
                                    ArrayList<String> namelist = getXmlDataList("searchresult.xml", "name");//name 태그값을 읽어 namelist 리스트에 저장
                                    ArrayList<String> pricelist = getXmlDataList("searchresult.xml", "price"); //price 태그값을 읽어 prica 리스트에 저장
                                     
                                    if(namelist.isEmpty())
                                        data.add("아무것도 검색되지 않았습니다.");
                                    else {
                                        for(int i = 0; i < namelist.size(); i++) {
                                            String str = namelist.get(i) + " - " + pricelist.get(i);
                                            data.add(str);
                                        }
                                    }
                                } catch(Exception e) {
                                    Log.e("Error", e.getMessage());
                                } finally{
                                    dialog.dismiss();
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        });
                    }
                });
                 
            }
        });
	}
	   private String getXmlData(String filename, String str) { //태그값 하나를 받아오기위한 String형 함수
	        String rss = SERVER_ADDRESS + "/";
	        String ret = "";
	         
	        try { //XML 파싱을 위한 과정
	            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
	            factory.setNamespaceAware(true);
	            XmlPullParser xpp = factory.newPullParser();
	            URL server = new URL(rss + filename);
	            InputStream is = server.openStream();
	            xpp.setInput(is, "UTF-8");
	             
	            int eventType = xpp.getEventType();
	             
	            while(eventType != XmlPullParser.END_DOCUMENT) {
	                if(eventType == XmlPullParser.START_TAG) {
	                    if(xpp.getName().equals(str)) { //태그 이름이 str 인자값과 같은 경우
	                        ret = xpp.nextText();
	                    }
	                }
	                eventType = xpp.next();
	            }
	        } catch(Exception e) {
	            Log.e("Error", e.getMessage());
	        }
	         
	        return ret;
	    }
	   
	   private ArrayList<String> getXmlDataList(String filename, String str) { //태그값 여러개를 받아오기위한 ArrayList<string>형 변수
	        String rss = SERVER_ADDRESS + "/";
	        ArrayList<String> ret = new ArrayList<String>();
	         
	        try { //XML 파싱을 위한 과정
	            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
	            factory.setNamespaceAware(true);
	            XmlPullParser xpp = factory.newPullParser();
	            URL server = new URL(rss + filename);
	            InputStream is = server.openStream();
	            xpp.setInput(is, "UTF-8");
	             
	            int eventType = xpp.getEventType();
	             
	            while(eventType != XmlPullParser.END_DOCUMENT) {
	                if(eventType == XmlPullParser.START_TAG) {
	                    if(xpp.getName().equals(str)) { //태그 이름이 str 인자값과 같은 경우
	                        ret.add(xpp.nextText());
	                    }
	                }
	                eventType = xpp.next();
	            }
	        } catch(Exception e) {
	            Log.e("Error", e.getMessage());
	        }
	         
	        return ret;
	    }
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
