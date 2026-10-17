package es.rutero.extractor;
import android.app.*;import android.os.*;import android.content.*;import android.provider.Settings;import android.net.Uri;import android.view.*;import android.widget.*;import java.io.*;import java.nio.charset.StandardCharsets;
public class MainActivity extends Activity {
 TextView status; static final int SAVE=41;
 @Override public void onCreate(Bundle b){super.onCreate(b);LinearLayout l=new LinearLayout(this);l.setOrientation(1);l.setPadding(28,32,28,24);ScrollView sc=new ScrollView(this);sc.addView(l);setContentView(sc);
 TextView title=new TextView(this);title.setText("📦 Rutero Extractor · prototipo 0.1");title.setTextSize(23);l.addView(title);
 TextView info=new TextView(this);info.setText("Uso autorizado: abre LDS EPOD(ES), entra en su lista de paquetes y pulsa Iniciar. El extractor lee el texto accesible en pantalla y recorre la lista mediante desplazamiento. No usa OCR, no lee otras aplicaciones ni envía datos a internet. Detén la extracción cuando quieras.\n\nIMPORTANTE: la extracción automática depende de que LDS muestre las direcciones como texto accesible; comprueba el CSV antes de repartir.");info.setPadding(0,20,0,20);l.addView(info);
 status=new TextView(this);l.addView(status);
 btn(l,"1. Activar permiso de accesibilidad",()->startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)));
 btn(l,"2. Preparar LDS EPOD(ES)",()->Toast.makeText(this,"Abre LDS EPOD(ES) manualmente y deja visible la lista de paquetes.",Toast.LENGTH_LONG).show());
 btn(l,"3. Iniciar extracción (10 s para abrir LDS)",()->{ExtractService.running=true;ExtractService.reset();new Handler(Looper.getMainLooper()).postDelayed(()->{if(ExtractService.instance!=null)ExtractService.instance.scan();},10000);Toast.makeText(this,"Abre la lista de LDS ahora. Inicio en 10 segundos.",Toast.LENGTH_LONG).show();refresh();});
 btn(l,"Detener extracción",()->{ExtractService.running=false;refresh();});
 btn(l,"Actualizar contador",this::refresh);
 btn(l,"Exportar CSV",()->{if(Store.read(this).length()==0){Toast.makeText(this,"No hay direcciones guardadas",Toast.LENGTH_LONG).show();return;}Intent i=new Intent(Intent.ACTION_CREATE_DOCUMENT);i.addCategory(Intent.CATEGORY_OPENABLE);i.setType("text/csv");i.putExtra(Intent.EXTRA_TITLE,"rutero_direcciones.csv");startActivityForResult(i,SAVE);});
 btn(l,"Borrar direcciones guardadas",()->new AlertDialog.Builder(this).setMessage("¿Eliminar las direcciones recopiladas?").setPositiveButton("Borrar",(d,w)->{ExtractService.running=false;Store.clear(this);refresh();}).setNegativeButton("Cancelar",null).show());refresh(); }
 void btn(LinearLayout l,String t,Runnable r){Button b=new Button(this);b.setText(t);l.addView(b);b.setOnClickListener(v->r.run());}
 void refresh(){status.setText("Direcciones guardadas: "+Store.read(this).length()+" | Extracción: "+(ExtractService.running?"ACTIVA":"detenida"));}
 @Override protected void onActivityResult(int req,int result,Intent data){super.onActivityResult(req,result,data);if(req==SAVE&&result==RESULT_OK&&data!=null){try(OutputStream o=getContentResolver().openOutputStream(data.getData())){o.write(Store.csv(this).getBytes(StandardCharsets.UTF_8));Toast.makeText(this,"CSV guardado",Toast.LENGTH_LONG).show();}catch(Exception e){new AlertDialog.Builder(this).setMessage("No se pudo guardar: "+e.getMessage()).setPositiveButton("OK",null).show();}}}
}
