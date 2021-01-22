import com.google.gson.Gson;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URL;

public class GatosService {
    public static void verGatos() throws IOException {
        //Vamos a traer datos de la api
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://api.thecatapi.com/v1/images/search")
                .method("GET", null)
                .build();
        Response response = client.newCall(request).execute();

        String elJson = response.body().string();

        //cortar los corchetes
        elJson = elJson.substring(1, elJson.length());
        elJson = elJson.substring(0, elJson.length()-1);

        // crear un objeto de la clase gato
        Gson gson = new Gson ();
        Gatos gatos = gson.fromJson(elJson, Gatos.class);

        //redimencionar en caso de necesitar
        Image image = null;
        try {
            URL url = new URL(gatos.getUrl());
            image = ImageIO.read(url);

            ImageIcon fondoGato = new ImageIcon(image);
            if (fondoGato.getIconWidth() > 800) {
                //Redimensionamos
                Image fondo = fondoGato.getImage();
                Image modificada = fondo.getScaledInstance(800, 600, Image.SCALE_SMOOTH);
                fondoGato = new ImageIcon(modificada);
            }

            String menu = "Opciones: \n"
                    + "1. Ver otra imagen \n"
                    + "2. Agregar Favorito \n"
                    + "3. Volver";
            String[] botones = {"Ver otra imagen", "Agregar Favorito", "Volver"};
            String id_gato = gatos.getId();
            String opcion = (String) JOptionPane.showInputDialog(null, menu, id_gato, JOptionPane.INFORMATION_MESSAGE, fondoGato, botones, botones[1]);
            
            int seleccion = -1;
            for(int i=0;i<botones.length;i++){
                if(opcion.equals(botones[i])){
                    seleccion = i;
                }
            }

            switch (seleccion) {
                case 0:
                    verGatos();
                    break;
                case 1:
                    favoritoGato(gatos);
                    break;
                default:
                    break;
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public static void favoritoGato (Gatos gato) {
        try {
            OkHttpClient client = new OkHttpClient();
            MediaType mediaType = MediaType.parse("application/json,application/json");
            RequestBody body = RequestBody.create(mediaType, "{\n  \"image_id\": \""+gato.getId()+"\"\n}");
            Request request = new Request.Builder()
              .url("https://api.thecatapi.com/v1/favourites")
              .method("POST", body)
              .addHeader("Content-Type", "application/json")
              .addHeader("x-api-key", gato.getApikey())
              .addHeader("Content-Type", "application/json")
              .build();
            Response response = client.newCall(request).execute();
        } catch (IOException e) {
            System.out.println(e);
        }
    }
    
    public static void verFatorito(String apiKey) throws IOException {
        OkHttpClient client = new OkHttpClient();
          Request request = new Request.Builder()
            .url("https://api.thecatapi.com/v1/favourites")
            .method("GET", null)
            .addHeader("x-api-key", apiKey)
            .build();
          Response response = client.newCall(request).execute();
          
          String elJson = response.body().string();
          
          //creamos el objeto gson
          Gson gson = new Gson();
          GatosFav[] gatosArray = gson.fromJson(elJson, GatosFav[].class);
          
          if (gatosArray.length > 0 ){
              int min = 1;
              int max = gatosArray.length;
              int aleatorio = (int) Math.random() * ((max-min)-1) + min;
          }
    }
}
