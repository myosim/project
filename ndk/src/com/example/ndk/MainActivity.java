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

	 private final String SERVER_ADDRESS = "http://www.learnsdk.com/network/user/"; //���� �ּ�(php������ ����Ǿ��ִ� ��α���, ����� 127.0.0.1�̳� localhost�� ���� �ȵȴ�!! �׷��Ƿ� �����Ǵ� �ڱ� �����Ƿ�.....)
     
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
        btninsert.setOnClickListener(new View.OnClickListener() { //�Է� ��ư�� ������ ��
             
            public void onClick(View v) {
                // TODO Auto-generated method stub

            	Log.d("filter", "Click Event");
                if( edtname.getText().toString().equals("") ||
                        edtprice.getText().toString().equals("") ) { //�̸��̳� �����߿� �ϳ��� �Է��� �ȵ�������
                    Toast.makeText(MainActivity.this,
                            "�̸��̳� ������ �Է��ϼ���", Toast.LENGTH_SHORT).show();
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
                                        + "&price=" + URLEncoder.encode(price, "UTF-8")); //�������� UTF-8�� ���ڵ��ϱ� ���� URLEncoder�� �̿��Ͽ� ���ڵ���
                                url.openStream(); //������ DB�� �Է��ϱ� ���� �������� insert.php���Ͽ� �Էµ� �̸��� ������ �ѱ�
                                 
                                String result = getXmlData("insertresult.xml", "result"); //�Է� ��������
                                 
                                if(result.equals("1")) { //result �±װ��� 1�϶� ����
                                    Toast.makeText(MainActivity.this,
                                            "DB insert ����", Toast.LENGTH_SHORT).show();
                                     
                                    edtname.setText("");
                                    edtprice.setText("");
                                }
                                else //result �±װ��� 1�� �ƴҶ� ����
                                    Toast.makeText(MainActivity.this,
                                            "DB insert ����", Toast.LENGTH_SHORT).show();
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
                                "�ҷ�������.....", "��ø� ��ٷ��ּ���.");
                         
                        handler.post(new Runnable() {
                             
                            public void run() {
                                // TODO Auto-generated method stub
                                try {
                                    data.clear(); //�ݺ������� ������� �Ȱ��� ���� ������ ���� �����ϱ� ���� data�� Ŭ������
                                    URL url = new URL(SERVER_ADDRESS + "/login.php");
                                    url.openStream(); //������ serarch.php������ ������
                         
                                    ArrayList<String> namelist = getXmlDataList("searchresult.xml", "name");//name �±װ��� �о� namelist ����Ʈ�� ����
                                    ArrayList<String> pricelist = getXmlDataList("searchresult.xml", "price"); //price �±װ��� �о� prica ����Ʈ�� ����
                                     
                                    if(namelist.isEmpty())
                                        data.add("�ƹ��͵� �˻����� �ʾҽ��ϴ�.");
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
	   private String getXmlData(String filename, String str) { //�±װ� �ϳ��� �޾ƿ������� String�� �Լ�
	        String rss = SERVER_ADDRESS + "/";
	        String ret = "";
	         
	        try { //XML �Ľ��� ���� ����
	            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
	            factory.setNamespaceAware(true);
	            XmlPullParser xpp = factory.newPullParser();
	            URL server = new URL(rss + filename);
	            InputStream is = server.openStream();
	            xpp.setInput(is, "UTF-8");
	             
	            int eventType = xpp.getEventType();
	             
	            while(eventType != XmlPullParser.END_DOCUMENT) {
	                if(eventType == XmlPullParser.START_TAG) {
	                    if(xpp.getName().equals(str)) { //�±� �̸��� str ���ڰ��� ���� ���
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
	   
	   private ArrayList<String> getXmlDataList(String filename, String str) { //�±װ� �������� �޾ƿ������� ArrayList<string>�� ����
	        String rss = SERVER_ADDRESS + "/";
	        ArrayList<String> ret = new ArrayList<String>();
	         
	        try { //XML �Ľ��� ���� ����
	            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
	            factory.setNamespaceAware(true);
	            XmlPullParser xpp = factory.newPullParser();
	            URL server = new URL(rss + filename);
	            InputStream is = server.openStream();
	            xpp.setInput(is, "UTF-8");
	             
	            int eventType = xpp.getEventType();
	             
	            while(eventType != XmlPullParser.END_DOCUMENT) {
	                if(eventType == XmlPullParser.START_TAG) {
	                    if(xpp.getName().equals(str)) { //�±� �̸��� str ���ڰ��� ���� ���
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
