# Rutero Extractor Android — prototipo fuente 0.1

**No es un APK compilado ni un extractor confirmado para LDS EPOD(ES).** Es un proyecto Android Studio Java listo para abrir, compilar y probar en un móvil Android (API 26+). No hay acceso a la estructura real de LDS, por lo que no es honesto prometer extracción completa.

## Qué hace esta versión
- Servicio de accesibilidad que solo comienza tras pulsar «Iniciar extracción» y solo trabaja en una aplicación cuyo nombre instalado contenga LDS o EPOD; fija el paquete identificado hasta detenerse.
- Espera 10 segundos para que abras la lista de paquetes de LDS. Lee los textos y descripciones accesibles de la pantalla y busca CP españoles junto a direcciones; intenta desplazar automáticamente la lista hasta que no cambie o tras 150 desplazamientos.
- Deduplica localmente, permite detener, borrar y guardar CSV mediante el selector de documentos Android. No necesita internet, root, capturas, permisos de almacenamiento ni acceso al portapapeles.
- El CSV usa exactamente las columnas que acepta Rutero web: `Name,Phone,Address,Postal Code,City,Type,Zone,Notes,Status,Fail reason`.

## Limitación importante
**No pulsa automáticamente «Copiar dirección» ni entra/sale de las fichas de cada paquete en esta primera versión.** Sin ver la jerarquía de accesibilidad real de LDS, automatizar esos botones a ciegas podría seleccionar paquetes equivocados. Si la lista solo muestra una parte de la dirección, el CSV estará incompleto o vacío. El extractor puede fallar en interfaces WebView, texto no accesible o formatos diferentes. Verifica manualmente cada dirección y código postal antes de repartir. Los campos nombre/teléfono quedan vacíos, porque el botón descrito solo copia dirección, CP y localidad.

## Compilación
1. Abre esta carpeta con Android Studio (JDK 17, Android SDK 35) y deja sincronizar Gradle.
2. `Build > Build APK(s)` e instala el APK generado en `app/build/outputs/apk/debug/app-debug.apk`.
3. Abre Rutero Extractor, activa su servicio en Ajustes > Accesibilidad y vuelve a la app.
4. Abre LDS y deja su lista de paquetes visible. En Extractor pulsa Iniciar y vuelve a LDS durante los 10 segundos de espera.
5. Vuelve a Extractor, detén si procede y exporta CSV. Abre Rutero web y usa `Más > Importar CSV`.

No introduzcas contraseñas en Extractor. Desactiva su permiso de accesibilidad al terminar. Trata el CSV como dato personal y no lo compartas sin autorización. El servicio solo almacena datos en el dispositivo y no tiene permiso INTERNET.

## Para completar la automatización por paquete
Se necesita la estructura accesible de la pantalla de lista y detalle, o una descripción precisa de sus controles, para añadir navegación automática por cada paquete y pulsar «Copiar dirección» de manera fiable. No es necesario compartir nombres ni direcciones reales.
