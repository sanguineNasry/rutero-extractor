package es.rutero.extractor;
import android.content.Context;
import org.json.*;
import java.util.*;
class Store {
 static final String PREF="rutero_extractor", KEY="records";
 static synchronized JSONArray read(Context c){try{return new JSONArray(c.getSharedPreferences(PREF,0).getString(KEY,"[]"));}catch(Exception e){return new JSONArray();}}
 static synchronized int add(Context c,String address,String cp,String city){
  address=address.trim();cp=cp.trim();city=city.trim();if(address.length()<5)return 0;
  JSONArray a=read(c);String k=(address+"|"+cp+"|"+city).toLowerCase(Locale.ROOT).replaceAll("\\s+"," ");
  for(int i=0;i<a.length();i++){JSONObject o=a.optJSONObject(i);if(o!=null&&k.equals(o.optString("key")))return 0;}
  JSONObject o=new JSONObject();try{o.put("key",k);o.put("address",address);o.put("cp",cp);o.put("city",city);a.put(o);c.getSharedPreferences(PREF,0).edit().putString(KEY,a.toString()).apply();return 1;}catch(Exception e){return 0;}
 }
 static synchronized void clear(Context c){c.getSharedPreferences(PREF,0).edit().remove(KEY).apply();}
 static String csv(Context c){StringBuilder b=new StringBuilder("\uFEFFName,Phone,Address,Postal Code,City,Type,Zone,Notes,Status,Fail reason\r\n");JSONArray a=read(c);for(int i=0;i<a.length();i++){JSONObject o=a.optJSONObject(i);if(o!=null)b.append(",,").append(q(o.optString("address"))).append(',').append(q(o.optString("cp"))).append(',').append(q(o.optString("city"))).append(",Domicilio,,,,,\r\n");}return b.toString();}
 static String q(String s){return "\""+s.replace("\"","\"\"").replace("\r"," ").replace("\n"," ")+"\"";}
}
