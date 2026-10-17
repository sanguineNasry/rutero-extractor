package es.rutero.extractor;
import android.accessibilityservice.AccessibilityService;import android.view.accessibility.*;import android.os.*;import android.content.pm.*;import java.util.*;import java.util.regex.*;
public class ExtractService extends AccessibilityService {
 static volatile boolean running=false;static ExtractService instance;static String target="";
 final Handler h=new Handler(Looper.getMainLooper());long last=0;int scrolls=0;String previous="";int unchanged=0;
 static void reset(){target="";if(instance!=null){instance.scrolls=0;instance.previous="";instance.unchanged=0;}}
 @Override public void onServiceConnected(){instance=this;}
 @Override public void onDestroy(){running=false;instance=null;super.onDestroy();}
 @Override public void onInterrupt(){running=false;}
 @Override public void onAccessibilityEvent(AccessibilityEvent e){if(!running)return;long now=System.currentTimeMillis();if(now-last<1100)return;last=now;h.postDelayed(this::scan,500);}
 boolean isLds(String pkg){try{PackageManager pm=getPackageManager();CharSequence label=pm.getApplicationLabel(pm.getApplicationInfo(pkg,0));String n=label.toString().toLowerCase(Locale.ROOT);return n.contains("lds")||n.contains("epod");}catch(Exception ex){return false;}}
 public void scan(){if(!running)return;AccessibilityNodeInfo root=getRootInActiveWindow();if(root==null)return;String pkg=String.valueOf(root.getPackageName());if(target.isEmpty()){if(!isLds(pkg))return;target=pkg;}if(!target.equals(pkg))return;
 ArrayList<String> lines=new ArrayList<>();collect(root,lines,0);String signature=String.join("\n",lines);if(signature.equals(previous))unchanged++;else unchanged=0;previous=signature;
 int count=0;Pattern cp=Pattern.compile("(?<!\\d)(?:0[1-9]|[1-4]\\d|5[0-2])\\d{3}(?!\\d)");
 for(int i=0;i<lines.size();i++){String s=lines.get(i);Matcher m=cp.matcher(s);while(m.find()){String postal=m.group(),before=s.substring(0,m.start()).replaceAll("[,\\s-]+$","").trim(),city=s.substring(m.end()).replaceAll("^[,\\s-]+","").trim();String address=before;
 if(address.length()<5&&i>0)address=lines.get(i-1);if(address.length()<5)continue;
 // Evita confundir encabezados, identificadores y textos sin dirección con paradas.
 if(!Pattern.compile("(?i)\\b(calle|c/|avenida|avda|plaza|paseo|camino|carretera|ctra|ronda|barrio|urbanizaci[oó]n|traves[ií]a|carril|vereda|diseminado|pol[ií]gono|paraje)\\b|\\d{1,4}\\s*[,º°]?").matcher(address).find())continue;
 count+=Store.add(this,address,postal,city);}}
 // Desplazamiento limitado y detenido al alcanzar el final o dejar de cambiar la pantalla.
 if(scrolls>=150||unchanged>=3){running=false;return;}
 AccessibilityNodeInfo scroll=findScroll(root,0);if(scroll!=null&&scroll.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD)){scrolls++;h.postDelayed(this::scan,1250);}else{running=false;}
 }
 void collect(AccessibilityNodeInfo n,ArrayList<String> out,int depth){if(n==null||depth>35||out.size()>1500)return;CharSequence t=n.getText();if(t!=null){String s=t.toString().trim();if(!s.isEmpty()&&!out.contains(s))out.add(s);}CharSequence d=n.getContentDescription();if(d!=null){String s=d.toString().trim();if(!s.isEmpty()&&!out.contains(s))out.add(s);}for(int i=0;i<n.getChildCount();i++)collect(n.getChild(i),out,depth+1);}
 AccessibilityNodeInfo findScroll(AccessibilityNodeInfo n,int depth){if(n==null||depth>35)return null;for(int i=0;i<n.getChildCount();i++){AccessibilityNodeInfo r=findScroll(n.getChild(i),depth+1);if(r!=null)return r;}return n.isScrollable()?n:null;}
}
