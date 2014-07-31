package org.am.sl.language;

import org.am.sl.SignUp;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import at.vcity.androidim.R;

/**
 * @author Cindy Espínola
 *
 */
public class ListLanguage extends Activity {
	/**
	 * 
	 */
	ListView listView;
	 /**
	 * 
	 */
	String language="ENGLISH";
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	  protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_list_language);
	    setTitle(getString(R.string.language));
        // Get ListView object from xml
        listView = (ListView) findViewById(R.id.listView1);
        
        // Defined Array values to show in ListView
        String[] values = new String[] { "English", 
                                         "Spanish",
                                         "Catalán",
                                         "Arabic", 
                                         "Bulgarian", 
                                         "Chinese Simplified", 
                                         "Chinese Traditional", 
                                         "Czech",
                                         "Danish",
                                         "Dutch",
                                         "Estonian",
                                         "Finnish",
                                         "French",
                                         "German",
                                         "Greek",
                                         "Haitian Creole",
                                         "Hebrew",
                                         "Hindi",
                                         "Hmong Daw",
                                         "Hungarian",
                                         "Indonesian",
                                         "Italian",
                                         "Japanese",
                                         "Klingon",
                                         "Klingon (pIqaD)",
                                         "Korean",
                                         "Latvian",
                                         "Lithuanian",
                                         "Malay",
                                         "Maltese",
                                         "Norwegian",
                                         "Persian",
                                         "Polish",
                                         "Portuguese",
                                         "Romanian",
                                         "Russian",
                                         "Slovak",
                                         "Slovenian",
                                         "Swedish",
                                         "Thai",
                                         "Turkish",
                                         "Ukrainian",
                                         "Urdu",
                                         "Vietnamese",
                                         "Welsh"                                    
                                        };

        // Define a new Adapter
        // First parameter - Context
        // Second parameter - Layout for the row
        // Third parameter - ID of the TextView to which the data is written
        // Forth - the Array of data

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
          android.R.layout.simple_list_item_1, android.R.id.text1, values);


        // Assign adapter to ListView
        listView.setAdapter(adapter); 
        
        // ListView Item Click Listener
        listView.setOnItemClickListener(new OnItemClickListener() {



			@Override
			public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
				// ListView Clicked item index
	               int itemPosition     = position;
	              
	               // ListView Clicked item value
	              // String  itemValue    = (String) listView.getItemAtPosition(position);
	                  
	                // Show Alert 
	               if(itemPosition==0){
	            	   language="en";
	               }else if(itemPosition==1){
	            	   language="es";
	               }else if(itemPosition==2){
	            	   language="ca";
	               }else if(itemPosition==3){
	            	   language="ar";
	               }else if(itemPosition==4){
	            	   language="Bg";
	               }else if(itemPosition==5){
	            	   language="zh-CHS";
	               }else if(itemPosition==6){
	            	   language="zh-CHT";
	               } else if(itemPosition==7){
	            	   language="cs";
	               } else if(itemPosition==8){
	            	   language= "da";
	               } else if(itemPosition==9){
	            	   language="nl";
	               } else if(itemPosition==10){
	            	   language="et";
	               } else if(itemPosition==11){
	            	   language="fi";
	               } else if(itemPosition==12){
	            	   language="fr";
	               } else if(itemPosition==13){
	            	   language="de";
	               } else if(itemPosition==14){
	            	   language="el";
	               } else if(itemPosition==15){
	            	   language="ht";
	               } else if(itemPosition==16){
	            	   language="he";
	               } else if(itemPosition==17){
	            	   language="hi";
	               } else if(itemPosition==18){
	            	   language="mww";
	               } else if(itemPosition==19){
	            	   language="hu";
	               } else if(itemPosition==20){
	            	   language="id";
	               } else if(itemPosition==21){
	            	   language="it";
	               } else if(itemPosition==22){
	            	   language="ja";
	               } else if(itemPosition==23){
	            	   language="tlh";
	               } else if(itemPosition==24){
	            	   language="tlh-Qaak";
	               }else if(itemPosition==25){
	            	   language="ko";
	               }else if(itemPosition==26){
	            	   language="lv";
	               }else if(itemPosition==27){
	            	   language="lt";
	               }else if(itemPosition==28){
	            	   language= "ms";
	               }else if(itemPosition==29){
	            	   language="mt";
	               }else if(itemPosition==30){
	            	   language="no";
	               }else if(itemPosition==31){
	            	   language="fa";
	               }else if(itemPosition==32){
	            	   language="pl";
	               }else if(itemPosition==33){
	            	   language="pt";
	               }else if(itemPosition==34){
	            	   language="ro";
	               }else if(itemPosition==35){
	            	   language="ru";
	               }else if(itemPosition==36){
	            	   language="sk";
	               }else if(itemPosition==37){
	            	   language="sl";
	               }else if(itemPosition==38){
	            	   language="sv";
	               }else if(itemPosition==39){
	            	   language="th";
	               }else if(itemPosition==40){
	            	   language="tr";
	               }else if(itemPosition==41){
	            	   language="uk";
	               }else if(itemPosition==42){
	            	   language="ur";
	               }else if(itemPosition==43){
	            	   language="vi";
	               }else if(itemPosition==44){
	            	   language="cy";
	               }
 
//	                Toast.makeText(getApplicationContext(),
//	                  "Position :"+itemPosition+"  ListItem : " +itemValue , Toast.LENGTH_LONG)
//	                  .show();
//	               Toast.makeText(getApplicationContext(),
//	 	                  language, Toast.LENGTH_LONG)
//	 	                  .show();
				
			}

         }); 
    }
//	public void setLanguage(String language){
//		this.language= language;
//		
//	}
	/**
	 * @return
	 */
	public String getLanguage(){
		return language;
	}
	/**
	 * @param v
	 */
	public void onClick(View v) {
		Intent i = new Intent(this, SignUp.class);
		i.putExtra("language", this.getLanguage());
		startActivity(i); 
//		ListLanguage.this.finish();

	}
}